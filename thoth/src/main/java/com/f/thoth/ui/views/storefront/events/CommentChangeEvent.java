package com.f.thoth.ui.views.storefront.events;

import com.f.thoth.ui.views.orderedit.OrderItemEditor;
import com.vaadin.flow.component.ComponentEvent;

public class CommentChangeEvent extends ComponentEvent<OrderItemEditor> {

   private final String comment;

   public CommentChangeEvent(OrderItemEditor component, String comment) {
      super(component, false);
      this.comment = comment;
   }

   public String getComment() {
      return comment;
   }

}