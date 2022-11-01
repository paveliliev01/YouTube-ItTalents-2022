package com.youtube_project.contollers;

import com.amazonaws.services.s3.AmazonS3;
import com.youtube_project.model.exceptions.NotFoundException;
import com.youtube_project.services.StorageService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.nio.file.Files;

@RestController
public class FileController extends MasterController{

    @Autowired
    private StorageService storageService;

    @Autowired
    private AmazonS3 s3Client;
    @Value("${application.bucket.name}" + "/videos")
    private String bucketName;

    @GetMapping("videos/download")
    @SneakyThrows
    public void download(@RequestParam String videoURL, HttpServletRequest req, HttpServletResponse resp){


        String videoName = videoURL.substring(videoURL.lastIndexOf('/') + 1).trim();
        File file = new File("uploads"+File.separator+"videos"+ File.separator+videoName);
        System.out.println(videoName);
        byte[] f = storageService.downloadFile(videoName,bucketName);
        ByteArrayResource resource = new ByteArrayResource(f);
        Files.copy(resource.getInputStream(),file.toPath());
        resp.setContentType(Files.probeContentType(file.toPath()));
        Files.copy(file.toPath(),resp.getOutputStream());
        //We don't want to keep files locally so we delete it after the response
        file.delete();

    }
}
