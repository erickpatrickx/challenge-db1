package com.challenge.controller;

import com.challenge.dto.AssetPresignedUrlDTO;
import com.challenge.dto.AssetUploadRequestDTO;
import com.challenge.service.AssetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class AssetControllerTest {

    @Mock
    private AssetService assetService;

    @InjectMocks
    private AssetController assetController;

    private MockMvc mockMvc;


    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(assetController).build();
    }

    @Test
    public void testGeneratePreSignedUrls() throws Exception {
        AssetUploadRequestDTO request = AssetUploadRequestDTO.builder()
                .productId(1L)
                .files(Arrays.asList())
                .build();

        List<AssetPresignedUrlDTO> urls = Arrays.asList(
                new AssetPresignedUrlDTO(1L, "url1"),
                new AssetPresignedUrlDTO(2L, "url2")
        );
        when(assetService.generatePreSignedUrls(any(AssetUploadRequestDTO.class))).thenReturn(urls);

        mockMvc.perform(post("/assets/generate-presigned-urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    public void testConfirmUpload() throws Exception {
        Long productId = 1L;
        List<Long> assetIds = Arrays.asList(1L, 2L);

        doNothing().when(assetService).confirmUpload(productId, assetIds);

        mockMvc.perform(post("/assets/confirm-upload/{productId}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[1, 2]"))
                .andExpect(status().isOk());
    }
}