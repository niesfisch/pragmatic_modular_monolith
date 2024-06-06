package de.marcelsauer.informothersystems.infrastructure.websocket;

public class WebSocketMessage {
  private String content;

  public WebSocketMessage(String content) {
    this.content = content;
  }

  public WebSocketMessage() {}

  public String getContent() {
    return content;
  }
}
