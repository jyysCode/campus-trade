package com.campus.trade.controller;

import com.campus.trade.pojo.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class FileController {

    @PostMapping("/image")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error(400, "文件不能为空");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return Result.error(400, "只允许上传图片文件");
        }

        long maxSize = 10 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            return Result.error(400, "图片大小不能超过10MB");
        }

        try {
            String userDir = System.getProperty("user.dir");
            Path basePath = Paths.get(userDir, "uploads");
            Files.createDirectories(basePath);

            String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            Path uploadPath = basePath.resolve(dateDir);
            Files.createDirectories(uploadPath);

            String originalName = file.getOriginalFilename();
            String extension = "";
            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(originalName.lastIndexOf("."));
            }
            String fileName = UUID.randomUUID().toString().replace("-", "") + extension;
            Path filePath = uploadPath.resolve(fileName);

            file.transferTo(filePath.toFile());

            String url = "/uploads/" + dateDir + "/" + fileName;
            return Result.success(url);
        } catch (IOException e) {
            return Result.error(500, "文件上传失败: " + e.getMessage());
        }
    }
}
