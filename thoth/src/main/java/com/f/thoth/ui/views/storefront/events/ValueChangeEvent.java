package com.f.thoth.ui.views.storefront.events;

import com.f.thoth.ui.views.orderedit.OrderItemsEditor;
import com.vaadin.flow.component.ComponentEvent;

public class ValueChangeEvent extends ComponentEvent<OrderItemsEditor> {

   public ValueChangeEvent(OrderItemsEditor component) {
      super(component, false);
   }
}