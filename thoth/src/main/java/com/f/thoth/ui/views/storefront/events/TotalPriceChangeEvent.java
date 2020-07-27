package com.f.thoth.ui.views.storefront.events;

import com.f.thoth.ui.views.orderedit.OrderItemsEditor;
import com.vaadin.flow.component.ComponentEvent;

public class TotalPriceChangeEvent extends ComponentEvent<OrderItemsEditor> {

   private final Integer totalPrice;

   public TotalPriceChangeEvent(OrderItemsEditor component, Integer totalPrice) {
      super(component, false);
      this.totalPrice = totalPrice;
   }

   public Integer getTotalPrice() {
      return totalPrice;
   }

}