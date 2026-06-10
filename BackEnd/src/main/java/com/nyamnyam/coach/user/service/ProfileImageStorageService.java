package com.nyamnyam.coach.user.service;

import com.nyamnyam.coach.global.exception.BusinessException;
import com.nyamnyam.coach.global.exception.errorcode.UserErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
public class ProfileImageStorageService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp");

    private final Path uploadDirectory;
    private final String publicPathPrefix;

    public ProfileImageStorageService(
            @Value("${app.upload.profile-image-dir:uploads/profile-images}") String uploadDirectory,
            @Value("${app.upload.profile-image-path-prefix:/uploads/profile-images}") String publicPathPrefix
    ) {
        this.uploadDirectory = Paths.get(uploadDirectory).toAbsolutePath().normalize();
        this.publicPathPrefix = normalizePublicPathPrefix(publicPathPrefix);
    }

    public String store(MultipartFile image) {
        validate(image);

        String extension = extension(image.getOriginalFilename());
        String storedFileName = UUID.randomUUID() + "." + extension;
        Path target = uploadDirectory.resolve(storedFileName).normalize();

        if (!target.startsWith(uploadDirectory)) {
            throw new BusinessException(UserErrorCode.INVALID_PROFILE_IMAGE);
        }

        try {
            Files.createDirectories(uploadDirectory);
            try (InputStream inputStream = image.getInputStream()) {
                Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
            }
            return publicPathPrefix + "/" + storedFileName;
        } catch (IOException exception) {
            throw new BusinessException(UserErrorCode.PROFILE_IMAGE_STORAGE_FAILED);
        }
    }

    public void delete(String storedPath) {
        if (storedPath == null || storedPath.isBlank() || !storedPath.startsWith(publicPathPrefix + "/")) {
            return;
        }

        String fileName = storedPath.substring(publicPathPrefix.length() + 1);
        Path target = uploadDirectory.resolve(fileName).normalize();
        if (!target.startsWith(uploadDirectory)) {
            return;
        }

        try {
            Files.deleteIfExists(target);
        } catch (IOException ignored) {
            // The DB path remains the source of truth; failed cleanup should not block profile updates.
        }
    }

    private void validate(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new BusinessException(UserErrorCode.INVALID_PROFILE_IMAGE);
        }

        String contentType = image.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BusinessException(UserErrorCode.INVALID_PROFILE_IMAGE);
        }

        extension(image.getOriginalFilename());
    }

    private String extension(String originalFilename) {
        String extension = StringUtils.getFilenameExtension(originalFilename);
        if (extension == null) {
            throw new BusinessException(UserErrorCode.INVALID_PROFILE_IMAGE);
        }

        extension = extension.toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BusinessException(UserErrorCode.INVALID_PROFILE_IMAGE);
        }
        return extension;
    }

    private String normalizePublicPathPrefix(String publicPathPrefix) {
        String normalized = publicPathPrefix == null || publicPathPrefix.isBlank()
                ? "/uploads/profile-images"
                : publicPathPrefix.trim();
        if (!normalized.startsWith("/")) {
            normalized = "/" + normalized;
        }
        if (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }
}
