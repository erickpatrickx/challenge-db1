package com.challenge.service;

import com.challenge.dto.ProductRequest;
import com.challenge.exception.SerializationException;
import com.challenge.model.Category;
import com.challenge.model.Product;
import com.challenge.model.ProductProcessing;
import com.challenge.model.enums.ProcessingStatus;
import com.challenge.model.enums.Status;
import com.challenge.repository.CategoryRepository;
import com.challenge.repository.ProductProcessingRepository;
import com.challenge.repository.ProductRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductConsumerServiceTest {

    @Mock
    private SqsClient sqsClient;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductProcessingRepository productProcessingRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ProductConsumerService productConsumerService;

    private Product product;
    private ProductProcessing productProcessing;
    private Category category;

    ProductRequest productRequest;

    UUID processingId = UUID.randomUUID();

    @BeforeEach
    public void setup() {
        productRequest = new ProductRequest();
        productRequest.setName("Test Product");
        productRequest.setDescription("Test Description");
        productRequest.setPrice(BigDecimal.valueOf(100));
        productRequest.setCategoryIds(List.of(1L));
        productRequest.setProcessingId(processingId);

        category = Category.builder().id(1L).name("category").build();

        product = Product.builder()
                .id(1L)
                .name("Test Product")
                .description("Test Description")
                .price(BigDecimal.valueOf(100))
                .categories(Set.of(category))
                .status(Status.PENDING)
                .build();

        productProcessing = ProductProcessing.builder()
                .id(UUID.randomUUID())
                .status(ProcessingStatus.PROCESSING)
                .request("{\"name\":\"Test Product\", \"processingId\":\"" + processingId.toString() + "\"}")
                .build();
    }

    @Test
    public void testReceiveMessages() {
        Message message = Message.builder().body("{\"name\":\"Test Product\"}").build();
        ReceiveMessageResponse response = ReceiveMessageResponse.builder().messages(message).build();

        when(sqsClient.receiveMessage(any(ReceiveMessageRequest.class))).thenReturn(response);

        productConsumerService.receiveMessages();

        verify(sqsClient, times(1)).receiveMessage(any(ReceiveMessageRequest.class));
    }

    @Test
    public void testProcessProduct() throws JsonProcessingException {
        productProcessing.setId(processingId);

        when(objectMapper.readValue(anyString(), eq(ProductRequest.class))).thenReturn(productRequest);
        when(categoryRepository.findAllById(anyList())).thenReturn(List.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        doReturn(Optional.of(productProcessing)).when(productProcessingRepository).findById(processingId);

        productConsumerService.processProduct("{\"name\":\"Test Product\", \"processingId\":\"" + processingId.toString() + "\"}");

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(productCaptor.capture());
        Product savedProduct = productCaptor.getValue();

        assertEquals("Test Product", savedProduct.getName());
        assertEquals("Test Description", savedProduct.getDescription());
        assertEquals(BigDecimal.valueOf(100), savedProduct.getPrice());
    }
}