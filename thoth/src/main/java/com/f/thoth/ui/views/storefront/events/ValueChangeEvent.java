package com.f.thoth.ui.views.storefront.events;

import com.vaadin.flow.component.ComponentEvent;
import com.f.thoth.ui.views.orderedit.OrderItemsEditor;

public class ValueChangeEvent extends ComponentEvent<OrderItemsEditor> {

   public ValueChangeEvent(OrderItemsEditor component) {
      super(component, false);
   }
}