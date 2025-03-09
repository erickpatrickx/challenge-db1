package com.challenge.service;

import com.challenge.dto.ProductRequest;
import com.challenge.model.ProductProcessing;
import com.challenge.model.enums.ProcessingStatus;
import com.challenge.repository.ProductProcessingRepository;
import com.challenge.util.JsonUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductProducerServiceTest {

    @Mock
    private SqsClient sqsClient;

    @Mock
    private ProductProcessingRepository productProcessingRepository;

    @Mock
    private JsonUtil jsonUtil;

    @InjectMocks
    private ProductProducerService productProducerService;

    @BeforeEach
    public void setup() {
        productProducerService = new ProductProducerService(sqsClient, productProcessingRepository, jsonUtil);
    }

    @Test
    public void testSendProductForProcessing() {
        ProductRequest request = new ProductRequest();
        UUID requestId = UUID.randomUUID();
        String requestJson = "{\"processingId\":\"" + requestId + "\"}";

        when(jsonUtil.toJson(any(ProductRequest.class))).thenReturn(requestJson);
        when(productProcessingRepository.save(any(ProductProcessing.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UUID result = productProducerService.sendProductForProcessing(request);


        ArgumentCaptor<ProductProcessing> processingCaptor = ArgumentCaptor.forClass(ProductProcessing.class);
        verify(productProcessingRepository).save(processingCaptor.capture());
        ProductProcessing savedProcessing = processingCaptor.getValue();

        assertEquals(ProcessingStatus.PROCESSING, savedProcessing.getStatus());
        assertEquals(requestJson, savedProcessing.getRequest());

        ArgumentCaptor<SendMessageRequest> messageCaptor = ArgumentCaptor.forClass(SendMessageRequest.class);
        verify(sqsClient).sendMessage(messageCaptor.capture());
        SendMessageRequest sentMessage = messageCaptor.getValue();

        assertEquals(requestJson, sentMessage.messageBody());
    }
}