package de.marcelsauer.event.external.incoming;

import java.util.UUID;

// just here for reference, should be in consuming submodule if only used there
public record SomeOrderEventFromOtherTeam(
    UUID messageId, String firstname, String lastname, int articleNr, String externalId) {}
