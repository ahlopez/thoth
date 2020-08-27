package com.f.thoth.ui.views.security.permission;

import java.time.LocalDate;
import java.util.Collection;

import com.f.thoth.backend.data.entity.HierarchicalEntity;
import com.f.thoth.backend.data.security.Role;

public class GrantRevokeEvent<E extends HierarchicalEntity<E>> extends PermissionEvent<E> 
{
   Collection<E> grants;
   
   protected GrantRevokeEvent(AbstractPermissionView<E> source, Collection<E>grants, Role role, LocalDate from, LocalDate to) 
   {
      super(source, role, from, to);
      this.grants = grants;
   }//GrantRevokeEvent
   
   public Collection<E> getGrants() { return grants;} 
   
}//GrantRevokeEvent