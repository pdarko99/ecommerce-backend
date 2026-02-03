package com.ecommerce.ecommerce.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageStorageService {
    String storeImage(MultipartFile file);
    void deleteImage(String imageUrl);
}
