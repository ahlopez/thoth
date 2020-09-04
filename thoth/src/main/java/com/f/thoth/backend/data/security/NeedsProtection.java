package com.f.thoth.backend.data.security;

/**
 * Representa un objeto que requiere protección
 */
public interface NeedsProtection
{

   public ObjectToProtect getObjectToProtect();

   public boolean canBeAccessedBy(Integer userCategory);

   public boolean isOwnedBy( SingleUser user);

   public boolean isOwnedBy( Role role);

   public boolean isRestrictedTo( UserGroup userGroup);

   public boolean admits( Role role);

   public void    grant( Role role);

   public void    revoke( Role role);

}//NeedsProtection