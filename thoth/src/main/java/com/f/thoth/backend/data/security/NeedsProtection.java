package com.f.thoth.backend.data.security;

/**
 * Representa un objeto que requiere protección.
 * Los objetos que requieren protección son abstractos,
 * pueden ser físicos tales como clases, expedientes, o documentos,
 * o pueden ser lógicos tales como operaciones
 */
public interface NeedsProtection
{

   public ObjectToProtect getObjectToProtect();

   public boolean canBeAccessedBy(Integer userCategory);

   public boolean isOwnedBy( User user);

   public boolean isOwnedBy( Role role);

   public boolean isRestrictedTo( UserGroup userGroup);

   public boolean admits( Role role);

   public void    grant( Permission role);

   public void    revoke( Permission role);

}//NeedsProtection