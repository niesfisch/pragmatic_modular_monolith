package de.marcelsauer.order_intake.infrastructure.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static de.marcelsauer.order_intake.DomainModule.PREFIX;

@RequiredArgsConstructor
@Component(PREFIX + "WebSocketScheduler")
public class WebSocketScheduler {
    private final SimpMessagingTemplate template;
    private final JdbcTemplate jdbcTemplate;

    @Scheduled(fixedRate = 1000)
    public void greeting() {
        long failuresSoFar = jdbcTemplate.queryForObject("select count(*) from order_intake__failure", Long.class);
        long ordersSoFar = jdbcTemplate.queryForObject("select count(*) from order_intake__order", Long.class);
        this.template.convertAndSend("/topic/%s/ordersSoFar".formatted(PREFIX), new WebSocketMessage("" + ordersSoFar));
        this.template.convertAndSend("/topic/%s/failuresSoFar".formatted(PREFIX), new WebSocketMessage("" + failuresSoFar));
    }
}
