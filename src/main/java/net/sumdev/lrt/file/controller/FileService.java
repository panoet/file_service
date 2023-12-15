package net.sumdev.lrt.file.controller;

import lombok.extern.slf4j.Slf4j;
import net.sumdev.lrt.file.model.FileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.time.Instant;
import java.util.*;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Slf4j
@RestController
public class FileService {

    @Value("${upload.directory:./uploaded/}")
    private String uploadDirectory;

    @GetMapping(value = "/v1/file/get/{fileName}")
    @ResponseBody
    public ResponseEntity<InputStreamResource> getFile(@PathVariable String fileName) throws IOException {
        File file = new File(uploadDirectory+fileName);
        InputStream in = FileUtils.openInputStream(file);
        MediaType contentType = MediaType.parseMediaType(Files.probeContentType(file.toPath()));

        return ResponseEntity.ok()
                .contentType(contentType)
                .body(new InputStreamResource(in));
    }

    @RequestMapping(path = "/v1/file/upload", method = POST, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<Map<String, Object>> saveFile(@ModelAttribute FileUpload fileUpload) {
        log.info("Receiving request");
        Map<String, Object> map = new HashMap<>();
        ArrayList<String> savedFiles = new ArrayList<>();

            if(fileUpload.getFile().length == 0) {
                log.error("No file uploaded");
                throw new MultipartException("File was null");
            }

        Arrays.stream(fileUpload.getFile()).forEach(multipartFile -> {
//            MultipartFile multipartFile = fileUpload.getFile();
            String savedFile = uploadDirectory+ Instant.now().getEpochSecond() + "-" + Objects.requireNonNull(multipartFile.getOriginalFilename()).replace(" ", "_").toLowerCase();

            try {
                File targetFile = new File(savedFile);
                log.info("Writing file to {}", targetFile);
                FileUtils.copyInputStreamToFile(multipartFile.getInputStream(), targetFile);
                savedFiles.add(savedFile.replace(uploadDirectory, ""));
            } catch (IOException e) {
                log.error(e.getMessage());
                throw new MultipartException("File not saved");
            }

        });


        map.put("result", "success");
        map.put("uploaded", savedFiles);
/*
        map.put("fileName", multipartFile.getOriginalFilename());
        map.put("fileSize", String.valueOf(multipartFile.getSize()));
        map.put("fileContentType", multipartFile.getContentType());
*/

        // File upload is successful
        map.put("message", "File upload done");
        return ResponseEntity.ok(map);
    }
}
