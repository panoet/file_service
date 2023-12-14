package net.sumdev.lrt.file.controller;

import net.sumdev.lrt.file.model.FileUpload;
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

@RestController
public class FileService {
    @RequestMapping(path = "/v1/file/upload", method = POST, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<Map<String, String>> saveFile(@ModelAttribute FileUpload fileUpload) {
        if(fileUpload.getFile() == null) throw new MultipartException("File was null");

        MultipartFile multipartFile = fileUpload.getFile();
        String savedFile = ".\\files\\"+ Instant.now().getEpochSecond() + "-" + multipartFile.getOriginalFilename().replace(" ", "_");

        try {
            File targetFile = new File(savedFile);
            if(!targetFile.exists()) {
                targetFile.createNewFile(); // create your file on the file system
            }
            OutputStream outStream = new FileOutputStream(targetFile);

            byte[] buffer = new byte[8 * 1024];
            int bytesRead;
            while ((bytesRead = multipartFile.getInputStream().read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
//            throw new MultipartException("File not saved");
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
