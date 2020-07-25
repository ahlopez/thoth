package com.f.thoth.testbench.elements.components;

import com.f.thoth.testbench.elements.ui.DashboardViewElement;
import com.f.thoth.testbench.elements.ui.LoginViewElement;
import com.f.thoth.testbench.elements.ui.ProductsViewElement;
import com.f.thoth.testbench.elements.ui.StorefrontViewElement;
import com.f.thoth.testbench.elements.ui.UsersViewElement;
import com.vaadin.flow.component.html.testbench.AnchorElement;
import com.vaadin.flow.component.tabs.testbench.TabElement;
import com.vaadin.flow.component.tabs.testbench.TabsElement;
import com.vaadin.testbench.TestBenchElement;

public class AppNavigationElement extends TabsElement {

   public StorefrontViewElement navigateToStorefront() {
      return navigateTo(0, StorefrontViewElement.class);
   }

   public DashboardViewElement navigateToDashboard() {
      return navigateTo(1, DashboardViewElement.class);
   }

   public UsersViewElement navigateToUsers() {
      return navigateTo(2, UsersViewElement.class);
   }

   public ProductsViewElement navigateToProducts() {
      return navigateTo(3, ProductsViewElement.class);
   }

   public LoginViewElement logout() {
      clickLink($(TabElement.class).last());
      return $(LoginViewElement.class).onPage().waitForFirst();
   }

   private <T extends TestBenchElement> T navigateTo(int index, Class<T> landingPage) {
      clickLink($(TabElement.class).get(index));
      return $(landingPage).onPage().waitForFirst();
   }

   private static void clickLink(TabElement tab) {
      tab.$(AnchorElement.class).first().click();
   }
}
