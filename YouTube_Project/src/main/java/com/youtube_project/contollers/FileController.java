package com.youtube_project.contollers;

import com.youtube_project.model.exceptions.NotFoundException;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.nio.file.Files;

@RestController
public class FileController extends MasterController{

    @GetMapping("videos/download/{fileName}")
    @SneakyThrows
    public void download(@PathVariable String fileName, HttpServletRequest req, HttpServletResponse resp){
        sessionManager.validateLogin(req);
        String filePath = "uploads" + File.separator + "videos" + File.separator + fileName;
        File f = new File(filePath);
        if(!f.exists()){
            throw new NotFoundException("File not found!");
        }
        resp.setContentType(Files.probeContentType(f.toPath()));
        Files.copy(f.toPath(),resp.getOutputStream());
    }
}
