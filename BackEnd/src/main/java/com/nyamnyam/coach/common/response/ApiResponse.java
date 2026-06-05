package com.nyamnyam.coach.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 모든 API 응답을 감싸는 공통 응답 포맷.
 *
 * 성공: { "success": true,  "data": {...} }
 * 실패: { "success": false, "error": { "code": "...", "message": "..." } }
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> {

    private boolean success;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ErrorDetail error;

    // ─────────────────────────────────────────
    // 성공
    // ─────────────────────────────────────────

    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = true;
        response.data = data;
        return response;
    }

    public static ApiResponse<Void> success() {
        ApiResponse<Void> response = new ApiResponse<>();
        response.success = true;
        return response;
    }

    // ─────────────────────────────────────────
    // 실패
    // ─────────────────────────────────────────

    public static <T> ApiResponse<T> fail(String code, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = false;
        response.error = new ErrorDetail(code, message);
        return response;
    }

    // ─────────────────────────────────────────
    // 에러 상세 내부 클래스
    // ─────────────────────────────────────────

    @Getter
    public static class ErrorDetail {
        private final String code;
        private final String message;

        public ErrorDetail(String code, String message) {
            this.code = code;
            this.message = message;
        }
    }
}
