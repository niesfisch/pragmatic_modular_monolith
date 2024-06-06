package de.marcelsauer.order_overdue.infrastructure.rest;

import java.util.UUID;

public record OverdueOrder(UUID orderId, String firstname, String lastname) {}
