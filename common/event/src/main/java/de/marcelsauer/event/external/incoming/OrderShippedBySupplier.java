package de.marcelsauer.event.external.incoming;

import java.time.Instant;
import java.util.UUID;

// just here for reference, should be in consuming submodule if only used there
public record OrderShippedBySupplier(
    UUID messageId, UUID orderId, String carrier, String trackingCode, Instant shipmentDateTime) {}
