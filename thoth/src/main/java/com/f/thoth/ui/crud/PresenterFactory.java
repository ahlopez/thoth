/**
 *
 */
package com.f.thoth.ui.crud;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.f.thoth.app.security.CurrentUser;
import com.f.thoth.backend.data.entity.Order;
import com.f.thoth.backend.service.OrderService;
import com.f.thoth.ui.views.storefront.StorefrontView;

@Configuration
public class PresenterFactory 
{
   @Bean
   @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
   public EntityPresenter<Order, StorefrontView> orderEntityPresenter(OrderService crudService, CurrentUser currentUser) 
   {
      return new EntityPresenter<>(crudService, currentUser);
   }

}//PresenterFactory
