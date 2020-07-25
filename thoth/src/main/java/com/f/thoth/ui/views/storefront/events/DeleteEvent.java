package com.f.thoth.ui.views.storefront.events;

import com.vaadin.flow.component.ComponentEvent;
import com.f.thoth.ui.views.orderedit.OrderItemEditor;

public class DeleteEvent extends ComponentEvent<OrderItemEditor> {
   public DeleteEvent(OrderItemEditor component) {
      super(component, false);
   }
}