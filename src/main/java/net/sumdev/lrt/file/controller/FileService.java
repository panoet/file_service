package net.sumdev.lrt.file.controller;

import lombok.extern.slf4j.Slf4j;
import net.sumdev.lrt.file.model.FileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.lang.reflect.Array;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Slf4j
@RestController
public class FileService {
    @GetMapping(value = "/v1/file/get/{fileName}")
    public @ResponseBody byte[] getFile(@PathVariable String fileName) throws IOException {
        InputStream in = getClass()
                .getResourceAsStream("uploaded/"+fileName);
        return IOUtils.toByteArray(in);
    }

    @RequestMapping(path = "/v1/file/upload", method = POST, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<Map<String, Object>> saveFile(@ModelAttribute FileUpload fileUpload) {
        log.info("Receiving request");
        if(fileUpload.getFile() == null) {
            log.error("No file uploaded");
            throw new MultipartException("File was null");
        }

        MultipartFile multipartFile = fileUpload.getFile();
        String savedFile = "uploaded/"+ Instant.now().getEpochSecond() + "-" + multipartFile.getOriginalFilename().replace(" ", "_");

        try {
            File targetFile = new File(savedFile);
            log.info("Writing file to {}", targetFile);
            FileUtils.copyInputStreamToFile(fileUpload.getFile().getInputStream(), targetFile);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new MultipartException("File not saved");
        }

        Map<String, Object> map = new HashMap<>();
        map.put("result", "success");
        map.put("uploaded", Arrays.asList(savedFile.replace("uploaded/", "")));
        map.put("fileName", multipartFile.getOriginalFilename());
        map.put("fileSize", String.valueOf(multipartFile.getSize()));
        map.put("fileContentType", multipartFile.getContentType());

        // File upload is successful
        map.put("message", "File upload done");
        return ResponseEntity.ok(map);
    }
}
