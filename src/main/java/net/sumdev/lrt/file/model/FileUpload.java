package net.sumdev.lrt.file.model;


import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import org.springframework.web.multipart.MultipartFile;

@Data
public class FileUpload {
    private String token;
    private MultipartFile[] file;
}
