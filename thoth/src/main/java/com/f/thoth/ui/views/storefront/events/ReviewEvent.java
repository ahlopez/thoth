package com.f.thoth.ui.views.storefront.events;

import com.f.thoth.ui.views.orderedit.OrderEditor;
import com.vaadin.flow.component.ComponentEvent;

public class ReviewEvent extends ComponentEvent<OrderEditor> {

   public ReviewEvent(OrderEditor component) {
      super(component, false);
   }
}