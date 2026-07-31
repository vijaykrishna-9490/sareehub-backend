package com.sareehub.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.sareehub.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/upload")
@RequiredArgsConstructor
public class UploadController {

    private final Cloudinary cloudinary;

    @PostMapping
    public Map<String, String> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new ApiException("No file provided", HttpStatus.BAD_REQUEST);
        }
        try {
            Map<?, ?> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap("folder", "sareehub/products")
            );
            String url = (String) result.get("secure_url");
            return Map.of("url", url);
        } catch (IOException e) {
            throw new ApiException("Image upload failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
