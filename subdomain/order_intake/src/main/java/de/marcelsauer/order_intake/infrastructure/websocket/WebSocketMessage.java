package de.marcelsauer.order_intake.infrastructure.websocket;

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
