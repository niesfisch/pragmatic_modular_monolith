package de.marcelsauer.order_overdue.domain;

import java.util.Collection;

public interface OverdueRepository {
  Collection<Order> findAllOverdueOrders();
}
