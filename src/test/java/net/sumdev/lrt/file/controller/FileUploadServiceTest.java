package net.sumdev.lrt.file.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FileService.class)
class FileUploadServiceTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MockMultipartFile mockMultipartFile;

    void saveFile() throws Exception {
        mockMvc.perform(multipart("/v1/file/upload")
                        .file(mockMultipartFile)
                        .param("token", "jwt"))
                .andExpect(status().isOk());
    }
}