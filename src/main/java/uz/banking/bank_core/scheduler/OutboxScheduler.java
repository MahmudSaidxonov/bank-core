package uz.banking.bank_core.scheduler;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uz.banking.bank_core.config.RabbitMQConfig;
import uz.banking.bank_core.entity.OutboxMessage;
import uz.banking.bank_core.enums.OutboxStatus;
import uz.banking.bank_core.repository.OutboxRepository;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxScheduler {

    private final RabbitTemplate rabbitTemplate;
    private final OutboxRepository outboxRepository;

    @Scheduled(fixedDelay = 5000)
    public void processOutboxMessages() {
        List<OutboxMessage> messages = outboxRepository.findAndLockNewMessages();

        if  (messages.isEmpty()) {
            return;
        }

        for (OutboxMessage message : messages) {
            try {
                rabbitTemplate.convertAndSend(RabbitMQConfig.NOTIFICATION_QUEUE, message.getPayload());
                message.setStatus(OutboxStatus.SENT);
                outboxRepository.save(message);
            } catch (Exception e) {
                log.error("Failed to send OutboxMessage with ID {}. Reason: {}", message.getId(), e.getMessage(), e);
            }
        }
    }
}
