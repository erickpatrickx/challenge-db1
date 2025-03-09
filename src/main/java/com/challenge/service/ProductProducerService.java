package com.challenge.service;

import com.challenge.dto.ProductRequest;
import com.challenge.model.ProductProcessing;
import com.challenge.model.enums.ProcessingStatus;
import com.challenge.repository.ProductProcessingRepository;
import com.challenge.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductProducerService {

    private final SqsClient sqsClient;
    private final ProductProcessingRepository productProcessingRepository;
    private final JsonUtil jsonUtil;

    @Value("${aws.sqs.endpoint}${aws.sqs.queue-url-product}")
    private String queueUrl;

    public UUID sendProductForProcessing(ProductRequest request) {
        UUID requestId = UUID.randomUUID();
        request.setProcessingId(requestId);
        String requestJson = jsonUtil.toJson(request);

        ProductProcessing processing = productProcessingRepository.save(
                ProductProcessing.builder()
                        .id(requestId)
                        .status(ProcessingStatus.PROCESSING)
                        .request(requestJson)
                        .build()
        );

        sendMessageToQueue(requestJson);
        return processing.getId();
    }

    private void sendMessageToQueue(String messageBody) {
        SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(messageBody)
                .build();
        sqsClient.sendMessage(sendMsgRequest);
        log.info("Message sent to SQS queue: {}", messageBody);
    }
}