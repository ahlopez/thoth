package com.f.thoth.backend.data.security;

/**
 * Representa un objeto que requiere protección
 */
public interface NeedsProtection
{

   public String  getKey();

   public boolean canBeAccessedBy(Integer userCategory);

   public boolean isOwnedBy( SingleUser user);

   public boolean isOwnedBy( Role role);
   
   public boolean admits( Role role);
   
   public void    grant( Permission permission);
   
   public void    revoke( Permission permission);

}//NeedsProtection