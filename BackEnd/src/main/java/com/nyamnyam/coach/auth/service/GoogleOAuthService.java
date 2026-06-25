package com.nyamnyam.coach.auth.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nyamnyam.coach.auth.dto.request.GoogleOAuthLoginRequest;
import com.nyamnyam.coach.global.exception.BusinessException;
import com.nyamnyam.coach.global.exception.errorcode.AuthErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
public class GoogleOAuthService {

    private final RestClient restClient;
    private final String clientId;
    private final String clientSecret;
    private final String tokenUri;
    private final String userInfoUri;

    public GoogleOAuthService(
            RestClient.Builder restClientBuilder,
            @Value("${oauth.google.client-id:}") String clientId,
            @Value("${oauth.google.client-secret:}") String clientSecret,
            @Value("${oauth.google.token-uri}") String tokenUri,
            @Value("${oauth.google.user-info-uri}") String userInfoUri
    ) {
        this.restClient = restClientBuilder.build();
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.tokenUri = tokenUri;
        this.userInfoUri = userInfoUri;
    }

    public GoogleOAuthUserInfo authenticate(GoogleOAuthLoginRequest request) {
        validateConfiguration();

        try {
            GoogleTokenResponse token = exchangeCode(request);
            if (token == null || isBlank(token.accessToken())) {
                throw new BusinessException(AuthErrorCode.OAUTH_INVALID_RESPONSE);
            }

            GoogleUserInfoResponse userInfo = restClient.get()
                    .uri(userInfoUri)
                    .headers(headers -> headers.setBearerAuth(token.accessToken()))
                    .retrieve()
                    .body(GoogleUserInfoResponse.class);

            if (userInfo == null || isBlank(userInfo.sub()) || isBlank(userInfo.email())) {
                throw new BusinessException(AuthErrorCode.OAUTH_INVALID_RESPONSE);
            }

            return new GoogleOAuthUserInfo(
                    userInfo.sub().trim(),
                    userInfo.email().trim().toLowerCase(),
                    trimToNull(userInfo.name()),
                    trimToNull(userInfo.picture())
            );
        } catch (BusinessException exception) {
            throw exception;
        } catch (RestClientException exception) {
            throw new BusinessException(AuthErrorCode.OAUTH_GOOGLE_FAILED);
        }
    }

    private GoogleTokenResponse exchangeCode(GoogleOAuthLoginRequest request) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("code", request.code());
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);
        form.add("redirect_uri", request.redirectUri());
        form.add("grant_type", "authorization_code");

        return restClient.post()
                .uri(tokenUri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(GoogleTokenResponse.class);
    }

    private void validateConfiguration() {
        if (isBlank(clientId) || isBlank(clientSecret)) {
            throw new BusinessException(
                    AuthErrorCode.OAUTH_GOOGLE_FAILED,
                    "Google OAuth 환경변수가 설정되지 않았습니다."
            );
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String trimToNull(String value) {
        return isBlank(value) ? null : value.trim();
    }

    private record GoogleTokenResponse(
            @JsonProperty("access_token") String accessToken,
            @JsonProperty("id_token") String idToken
    ) {
    }

    private record GoogleUserInfoResponse(
            String sub,
            String email,
            String name,
            String picture
    ) {
    }
}
