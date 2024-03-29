package com.f.thoth.backend.data;

public class Role
{
   public static final String BARISTA = "barista";
   public static final String BAKER   = "baker";  
   public static final String ADMIN   = "admin"; // This role implicitly allows access to all views.

   private Role() 
   {
      // Static methods and fields only
   }

   public static String[] getAllRoles() 
   {
      return new String[] { BARISTA, BAKER, ADMIN };
   }

}//Role
