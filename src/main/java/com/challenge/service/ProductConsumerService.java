package com.challenge.service;

import com.challenge.dto.ProductAttributeRequest;
import com.challenge.dto.ProductRequest;
import com.challenge.exception.SerializationException;
import com.challenge.model.Category;
import com.challenge.model.Product;
import com.challenge.model.ProductAttribute;
import com.challenge.model.ProductProcessing;
import com.challenge.model.enums.ProcessingStatus;
import com.challenge.model.enums.Status;
import com.challenge.repository.CategoryRepository;
import com.challenge.repository.ProductProcessingRepository;
import com.challenge.repository.ProductRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductConsumerService {

    private final SqsClient sqsClient;
    private final ProductRepository productRepository;
    private final ProductProcessingRepository productProcessingRepository;
    private final CategoryRepository categoryRepository;
    private final ObjectMapper objectMapper;

    @Value("${aws.sqs.endpoint}${aws.sqs.queue-url-product}")
    private String queueUrl;

    @Scheduled(fixedDelay = 100)
    public void receiveMessages() {
        try {
            ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .maxNumberOfMessages(10)
                    .waitTimeSeconds(1)
                    .build();

            List<Message> messages = sqsClient.receiveMessage(request).messages();

            for (Message message : messages) {
                processMessageAsync(message);
            }
        } catch (Exception e) {
            log.error("Error receiving messages from SQS", e);
        }
    }

    @Async
    @Transactional
    public void processMessageAsync(Message message) {
        try {
            log.info("Processing message: {}", message.body());
            processProduct(message.body());
            deleteMessage(message);
        } catch (Exception e) {
            log.error("Error processing message.", e);
        }
    }

    public void processProduct(String message) {
        ProductRequest request = deserializeMessage(message);
        try {
            List<Category> categories = validateCategories(request.getCategoryIds());
            validatePrice(request.getPrice());

            Product product = saveProduct(request, categories);
            updateProcessingStatus(product,request.getProcessingId(), ProcessingStatus.SUCCESS, null);
            log.info("Product {} processed successfully!", product.getId());
        } catch (Exception e) {
            log.error("Error processing product", e);
            updateProcessingStatus(null, request.getProcessingId(), ProcessingStatus.FAILED, e.getMessage());
        }
    }

    private ProductRequest deserializeMessage(String message) {
        try {

            return objectMapper.readValue(message, ProductRequest.class);
        } catch (JsonProcessingException e) {
            throw new SerializationException("Failed to deserialize message from queue", e);
        }
    }


    private List<Category> validateCategories(List<Long> categoryIds) {
        List<Category> categories = categoryRepository.findAllById(categoryIds);
        if (categories.size() != categoryIds.size()) {
            throw new IllegalArgumentException("Some categories do not exist.");
        }
        return categories;
    }


    private void validatePrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Product price must be greater than zero.");
        }
    }


    private Product saveProduct(ProductRequest request, List<Category> categories) {
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .categories(Set.copyOf(categories))
                .status(Status.PENDING)
                .build();

        if (request.getAttributes() != null && !request.getAttributes().isEmpty()) {
            List<ProductAttribute> attributes = request.getAttributes().stream()
                    .map(attr -> convertToProductAttribute(attr, product))
                    .collect(Collectors.toList());

            product.setAttributes(Set.copyOf(attributes));
        }
        return productRepository.save(product);
    }

    private ProductAttribute convertToProductAttribute(ProductAttributeRequest attributeRequest, Product product) {
        return ProductAttribute.builder()
                .name(attributeRequest.getName())
                .type(attributeRequest.getType())
                .value(attributeRequest.getValue())
                .product(product)
                .build();
    }


    private void updateProcessingStatus(Product product, UUID requestId, ProcessingStatus status, String errorMessage) {
        Optional<ProductProcessing> processingOptional = productProcessingRepository.findById(requestId);
        if (processingOptional.isPresent()) {
            ProductProcessing processing = processingOptional.get();
            processing.setStatus(status);
            processing.setErrorMessage(errorMessage);
            processing.setProduct(product);
            productProcessingRepository.save(processing);
            log.info("Processing status updated to {}", status);
        } else {
            log.warn("Processing with ID {} not found", requestId);
        }
    }


    private void deleteMessage(Message message) {
        sqsClient.deleteMessage(DeleteMessageRequest.builder()
                .queueUrl(queueUrl)
                .receiptHandle(message.receiptHandle())
                .build());
        log.info("Message removed from queue: {}", message.messageId());
    }

}
