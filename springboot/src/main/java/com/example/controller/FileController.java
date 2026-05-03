package com.example.controller;

import cn.hutool.core.io.FileUtil;
import com.example.common.Result;
import com.example.exception.CustomException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 文件上传与下载接口。
 */
@RestController
@RequestMapping("/files")
public class FileController {

    @Value("${fileBaseUrl}")
    private String fileBaseUrl;

    @Value("${server.port}")
    private String port;

    @PostMapping("/upload")
    public Result upload(MultipartFile file) {
        String originalName = file.getOriginalFilename() == null ? "file" : file.getOriginalFilename();
        String fileName = System.currentTimeMillis() + "-" + originalName;
        Path storageDir = resolveStorageDir();
        String realFilePath = storageDir.resolve(fileName).toString();

        try {
            if (!FileUtil.isDirectory(storageDir.toString())) {
                FileUtil.mkdir(storageDir.toString());
            }
            FileUtil.writeBytes(file.getBytes(), realFilePath);
        } catch (IOException e) {
            throw new CustomException("文件上传失败");
        }

        String url = fileBaseUrl + ":" + port + "/files/download/" + fileName;
        return Result.success(url);
    }

    @GetMapping("/download/{fileName}")
    public void download(@PathVariable String fileName, HttpServletResponse response) {
        String realFilePath = resolveStorageDir().resolve(fileName).toString();
        if (!FileUtil.exist(realFilePath)) {
            throw new CustomException("文件不存在");
        }

        response.setHeader(
                "Content-Disposition",
                "attachment;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8)
        );

        try (ServletOutputStream os = response.getOutputStream()) {
            os.write(FileUtil.readBytes(realFilePath));
            os.flush();
        } catch (IOException e) {
            throw new CustomException("文件下载失败");
        }
    }

    private Path resolveStorageDir() {
        Path workDir = Paths.get(System.getProperty("user.dir"));
        Path localFilesDir = workDir.resolve("files");
        if (FileUtil.isDirectory(localFilesDir.toString())) {
            return localFilesDir;
        }

        Path parentDir = workDir.getParent();
        if (parentDir != null) {
            Path repoFilesDir = parentDir.resolve("files");
            if (FileUtil.isDirectory(repoFilesDir.toString())) {
                return repoFilesDir;
            }
        }

        return localFilesDir;
        //这里是一个注释 不影响代码运行
    }
}
