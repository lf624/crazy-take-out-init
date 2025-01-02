package com.crazy.controller.admin;

import com.crazy.constant.MessageConstant;
import com.crazy.result.Result;
import com.crazy.utils.QcosUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/admin/common")
@Tag(name = "通用接口")
@Slf4j
public class CommonController {

    @Autowired
    private QcosUtil qcosUtil;

    @PostMapping("/upload")
    @Operation(summary = "上传文件")
    public Result<String> upload(MultipartFile file) {
        log.info("upload file: {}", file);
        try {
            // 原始文件名
            String originalFilename = file.getOriginalFilename();
            // 截取文件后缀
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            // 构造新文件名
            String objectName = UUID.randomUUID() + extension;

            // 文件请求路径
            String filePath = qcosUtil.upload(file.getBytes(), objectName);
            return Result.success(filePath);
        } catch (IOException e) {
            log.error("upload failed: ", e);
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
