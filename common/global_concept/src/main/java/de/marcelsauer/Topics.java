package de.marcelsauer;

public interface Topics {

  // from external teams to us
  interface External {

    interface Outgoing {
      String TOPIC_ORDER_TO_EXTERNAL_TEAM_ONE = "order-to-external-team-one";
    }

    interface Incoming {
      // other team that notifies us about a new order
      String TOPIC_EXTERNAL_TEAM_NEW_ORDER = "order";

      // from external supplier team to us
      String TOPIC_ORDER_SHIPPED_BY_SUPPLIER = "order-shipped-by-supplier-team-one";
    }
  }

  interface Internal {
    String TOPIC_ORDER_CREATED = "order-created";
    String TOPIC_ORDER_OVERDUE = "order-overdue";
    String TOPIC_ORDER_SHIPPED = "order-shipped";
  }
}
