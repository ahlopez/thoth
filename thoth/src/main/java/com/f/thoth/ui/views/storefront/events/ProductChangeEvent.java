package com.f.thoth.ui.views.storefront.events;

import com.f.thoth.backend.data.entity.Product;
import com.f.thoth.ui.views.orderedit.OrderItemEditor;
import com.vaadin.flow.component.ComponentEvent;

public class ProductChangeEvent extends ComponentEvent<OrderItemEditor> {

   private final Product product;

   public ProductChangeEvent(OrderItemEditor component, Product product) {
      super(component, false);
      this.product = product;
   }

   public Product getProduct() {
      return product;
   }

}