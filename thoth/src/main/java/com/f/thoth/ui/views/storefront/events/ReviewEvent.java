package com.f.thoth.ui.views.storefront.events;

import com.vaadin.flow.component.ComponentEvent;
import com.f.thoth.ui.views.orderedit.OrderEditor;

public class ReviewEvent extends ComponentEvent<OrderEditor> {

   public ReviewEvent(OrderEditor component) {
      super(component, false);
   }
}