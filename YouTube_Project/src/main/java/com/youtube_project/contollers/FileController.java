package com.youtube_project.contollers;

import com.youtube_project.model.exceptions.NotFoundException;
import com.youtube_project.services.StorageService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.nio.file.Files;

@RestController
public class FileController extends MasterController{

    @Autowired
    private StorageService storageService;
    @GetMapping("videos/download/{fileName}")
    @SneakyThrows
    public ByteArrayResource download(@PathVariable String fileName, HttpServletRequest req, HttpServletResponse resp, String bucketName){
        sessionManager.validateLogin(req);
        String filePath = "uploads" + File.separator + "videos" + File.separator + fileName;
        byte[] f = storageService.downloadFile(filePath,bucketName);
        ByteArrayResource resource = new ByteArrayResource(f);

        return resource;
    }
}
