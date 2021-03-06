package com.maple.demo.utils;

import com.maple.demo.config.GlobalConfigs;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * 文件上传
 */
public class UploadUtils {

    /**
     * 上传单个文件
     * @param file
     * @return
     */
    public static String upload(MultipartFile file) {
        if (file.isEmpty()) {
            return "上传失败，请选择文件";
        }

        // 获取文件名
        String fileName = file.getOriginalFilename();
        // 后缀名
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        
        // 上传后的路径
        String filePath = "";
        
        String os = System.getProperty("os.name");
        if(os.toLowerCase().startsWith("win")) {
        	filePath = GlobalConfigs.WIN_UPLOAD_URL;
        }else {
        	filePath = GlobalConfigs.LINUX_UPLOAD_URL;
        }
        
        // 新文件名
        fileName = UUID.randomUUID() + suffixName;

        File dest = new File(filePath + fileName);

        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }

        String result = GlobalConfigs.BASE_FILE_URL + fileName;
        return result;
    }
}
