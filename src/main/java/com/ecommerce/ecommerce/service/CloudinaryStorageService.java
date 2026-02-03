package com.ecommerce.ecommerce.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "storage.type", havingValue = "cloudinary")
public class CloudinaryStorageService implements ImageStorageService {

    private final Cloudinary cloudinary;

    @Value("${cloudinary.folder:ecommerce/products}")
    private String folder;

    @Override
    public String storeImage(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Cannot store empty file");
            }

            Map<String, Object> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", folder,
                            "resource_type", "image"
                    )
            );

            String secureUrl = (String) uploadResult.get("secure_url");
            log.info("Image uploaded to Cloudinary successfully: {}", secureUrl);
            return secureUrl;
        } catch (IOException e) {
            log.error("Failed to upload image to Cloudinary", e);
            throw new RuntimeException("Failed to upload image: " + e.getMessage());
        }
    }

    @Override
    public void deleteImage(String imageUrl) {
        try {
            if (imageUrl == null || imageUrl.isEmpty()) {
                return;
            }

            String publicId = extractPublicId(imageUrl);
            if (publicId != null) {
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
                log.info("Image deleted from Cloudinary: {}", publicId);
            }
        } catch (IOException e) {
            log.error("Failed to delete image from Cloudinary: {}", imageUrl, e);
        }
    }

    private String extractPublicId(String imageUrl) {
        if (imageUrl == null || !imageUrl.contains("cloudinary.com")) {
            return null;
        }

        try {
            String[] parts = imageUrl.split("/upload/");
            if (parts.length > 1) {
                String path = parts[1];
                // Remove version if present (e.g., v1234567890/)
                if (path.matches("^v\\d+/.*")) {
                    path = path.substring(path.indexOf('/') + 1);
                }
                // Remove file extension
                int lastDot = path.lastIndexOf('.');
                if (lastDot > 0) {
                    path = path.substring(0, lastDot);
                }
                return path;
            }
        } catch (Exception e) {
            log.warn("Could not extract public ID from URL: {}", imageUrl);
        }
        return null;
    }
}
