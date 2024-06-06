package de.marcelsauer.informothersystems.infrastructure.websocket;

import de.marcelsauer.informothersystems.domain.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static de.marcelsauer.informothersystems.DomainModule.PREFIX;

@RequiredArgsConstructor
@Component(PREFIX + "WebSocketScheduler")
public class WebSocketScheduler {
    private final SimpMessagingTemplate template;
    private final OrderRepository orderRepository;

    @Scheduled(fixedRate = 2000)
    public void greeting() {
        long ordersSoFar = orderRepository.countAll();
        this.template.convertAndSend(
                "/topic/%s/orders".formatted(PREFIX), new WebSocketMessage("" + ordersSoFar));
    }
}
