package com.delivery.controller;

import com.delivery.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/file")
@Tag(name = "文件上传", description = "图片上传相关接口")
public class FileUploadController {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png", ".gif", ".webp");

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/webp"
    );

    @Value("${upload.path}")
    private String uploadPath;

    @Value("${upload.url-prefix}")
    private String urlPrefix;

    @Operation(summary = "上传图片", description = "支持jpg、png、gif格式，最大10MB")
    @PostMapping("/upload")
    public Result<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("请选择要上传的文件");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            return Result.error("文件大小不能超过10MB");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !StringUtils.hasText(originalFilename)) {
            return Result.error("文件名不能为空");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            log.warn("不支持的文件类型: {}", contentType);
            return Result.error("不支持的文件类型");
        }

        int dotIndex = originalFilename.lastIndexOf(".");
        if (dotIndex < 0) {
            return Result.error("文件格式错误");
        }
        String extension = originalFilename.substring(dotIndex).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            return Result.error("不支持的文件格式，仅支持 jpg/png/gif/webp");
        }

        try {
            String fileName = UUID.randomUUID().toString().replace("-", "") + extension;
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                boolean created = uploadDir.mkdirs();
                if (!created) {
                    log.error("创建上传目录失败");
                    return Result.error("服务器内部错误");
                }
            }
            File destFile = new File(uploadDir, fileName);
            if (destFile.exists()) {
                fileName = UUID.randomUUID().toString().replace("-", "") + "_" + System.currentTimeMillis() + extension;
                destFile = new File(uploadDir, fileName);
            }
            file.transferTo(destFile);

            Map<String, String> data = new HashMap<>();
            data.put("url", urlPrefix + fileName);
            data.put("fileName", fileName);

            log.info("文件上传成功：{}, 大小: {} bytes, 类型: {}", fileName, file.getSize(), contentType);
            return Result.success(data);
        } catch (IOException e) {
            log.error("文件上传失败", e);
            return Result.error("文件上传失败，请稍后重试");
        }
    }
}
