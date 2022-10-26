package com.youtube_project.services;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.youtube_project.model.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class StorageService {
    @Autowired
    AmazonS3 s3Client;
    @Value("${application.bucket.name}" + "/videos")
    String bucketName;

    public String uploadFile(MultipartFile multipartFile) {
        File file = convertMultipartFileToFIle(multipartFile);
        String fileName = System.currentTimeMillis() + "_" + multipartFile.getOriginalFilename();
        s3Client.putObject(new PutObjectRequest(bucketName, fileName, file));
        file.delete();
        System.out.println("Da");
        return fileName + " was uploaded to s3bucket";
    }

    public byte[] downloadFile(String fileName,String bucketName) {
        if (!s3Client.doesObjectExist(bucketName,fileName)){
            throw new NotFoundException("The file you are trying to download does not exist!");
        }
        S3Object s3Object = s3Client.getObject(bucketName, fileName);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        try {
            byte[] content = IOUtils.toByteArray(inputStream);
            return content;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String deleteFile(String fileName) {
        if (s3Client.doesObjectExist(bucketName, fileName)) {
            s3Client.deleteObject(bucketName, fileName);
            return fileName + " removed";
        }
        return "File not found! Deletion unsuccessful!";
    }

    private File convertMultipartFileToFIle(MultipartFile file) {
        File convertedFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            System.out.println("CIRK");
        }

        return convertedFile;
    }

}
