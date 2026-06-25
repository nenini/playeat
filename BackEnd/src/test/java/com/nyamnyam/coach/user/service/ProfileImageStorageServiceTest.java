package com.nyamnyam.coach.user.service;

import com.nyamnyam.coach.global.exception.BusinessException;
import com.nyamnyam.coach.global.exception.errorcode.UserErrorCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProfileImageStorageServiceTest {

    @TempDir
    private Path tempDir;

    @Test
    void storeImageToLocalFilesystemAndReturnPublicPath() throws Exception {
        ProfileImageStorageService storageService = new ProfileImageStorageService(
                tempDir.resolve("profile-images").toString(),
                "/uploads/profile-images"
        );
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "profile.png",
                "image/png",
                "image-content".getBytes()
        );

        String storedPath = storageService.store(image);

        assertThat(storedPath).startsWith("/uploads/profile-images/");
        String storedFileName = storedPath.substring("/uploads/profile-images/".length());
        assertThat(Files.exists(tempDir.resolve("profile-images").resolve(storedFileName))).isTrue();
    }

    @Test
    void rejectNonImageFile() {
        ProfileImageStorageService storageService = new ProfileImageStorageService(
                tempDir.resolve("profile-images").toString(),
                "/uploads/profile-images"
        );
        MockMultipartFile file = new MockMultipartFile(
                "image",
                "profile.txt",
                "text/plain",
                "not-image".getBytes()
        );

        assertThatThrownBy(() -> storageService.store(file))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(UserErrorCode.INVALID_PROFILE_IMAGE);
    }
}
