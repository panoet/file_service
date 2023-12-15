package net.sumdev.lrt.file.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

@RestControllerAdvice
public class ExceptionService {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<String> handleMaxUploadSizeExceeded() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File size exceeds the limit.");
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<String> handleMultipartException() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("{\n" +
                        "  \"result\": \"error\",\n" +
                        "  \"error\": \"No files were uploaded\"\n" +
                        "}");
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<String> handleNullException() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("{\n" +
                        "  \"result\": \"error\",\n" +
                        "  \"error\": \"Something happened\"\n" +
                        "}");
    }
}
