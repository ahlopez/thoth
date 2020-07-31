package com.f.thoth.ui.utils;

import java.util.Locale;

import org.springframework.data.domain.Sort;

public class BakeryConst
{

   public static final Locale APP_LOCALE = Locale.US;
   public static final String TENANT= "TENANT";

   public static final String PAGE_ROOT = "";
   public static final String PAGE_STOREFRONT = "storefront";
   public static final String PAGE_STOREFRONT_EDIT = "storefront/edit";
   public static final String PAGE_DASHBOARD = "dashboard";
   public static final String PAGE_USERS = "users";
   public static final String PAGE_ROLES = "roles";
   public static final String PAGE_PRODUCTS = "products";
   public static final String PAGE_TENANTS = "tenants";
   public static final String PAGE_OBJECT_TO_PROTECT = "objects";

   public static final String TITLE_STOREFRONT = "Storefront";
   public static final String TITLE_DASHBOARD = "Dashboard";

   public static final String TITLE_ADMINISTRATION = "Administración";
   public static final String TITLE_USERS = "Usuarios";
   public static final String TITLE_PRODUCTS = "Products";
   public static final String TITLE_ROLES = "Roles";
   public static final String TITLE_LOGOUT = "Terminar";
   public static final String TITLE_NOT_FOUND = "No encontró la página";
   public static final String TITLE_ACCESS_DENIED = "Acceso denegado";
   public static final String TITLE_TENANTS = "Tenants";
   public static final String TITLE_OBJECT_TO_PROTECT = "Protección";

   public static final String[] ORDER_SORT_FIELDS = {"dueDate", "dueTime", "id"};
   public static final Sort.Direction DEFAULT_SORT_DIRECTION = Sort.Direction.ASC;

   public static final String VIEWPORT = "width=device-width, minimum-scale=1, initial-scale=1, user-scalable=yes, viewport-fit=cover";

   // Mutable for testing.
   public static int NOTIFICATION_DURATION = 4000;

}//BakeryConst
