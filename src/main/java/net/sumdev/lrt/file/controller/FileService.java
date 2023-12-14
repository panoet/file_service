package net.sumdev.lrt.file.controller;

import lombok.extern.slf4j.Slf4j;
import net.sumdev.lrt.file.model.FileUpload;
import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Slf4j
@RestController
public class FileService {
    @RequestMapping(path = "/v1/file/upload", method = POST, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<Map<String, String>> saveFile(@ModelAttribute FileUpload fileUpload) {
        log.info("Receiving request");
        if(fileUpload.getFile() == null) {
            log.error("No file uploaded");
            throw new MultipartException("File was null");
        }

        MultipartFile multipartFile = fileUpload.getFile();
        String savedFile = "files/"+ Instant.now().getEpochSecond() + "-" + multipartFile.getOriginalFilename().replace(" ", "_");

        try {
            File targetFile = new File(savedFile);
            log.info("Writing file to {}", targetFile);
            FileUtils.copyInputStreamToFile(fileUpload.getFile().getInputStream(), targetFile);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new MultipartException("File not saved");
        }

        Map<String, String> map = new HashMap<>();
        map.put("result", "success");
        map.put("uploaded", "[\""+ savedFile +"\"]");
        map.put("fileName", multipartFile.getOriginalFilename());
        map.put("fileSize", String.valueOf(multipartFile.getSize()));
        map.put("fileContentType", multipartFile.getContentType());

        // File upload is successful
        map.put("message", "File upload done");
        return ResponseEntity.ok(map);
    }
}
