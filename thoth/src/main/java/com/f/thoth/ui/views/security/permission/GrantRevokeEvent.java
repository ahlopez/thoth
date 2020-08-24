package com.f.thoth.ui.views.security.permission;

import java.util.Collection;

import com.f.thoth.backend.data.entity.HierarchicalEntity;
import com.f.thoth.backend.data.security.Role;

public class GrantRevokeEvent<E extends HierarchicalEntity<E>> extends PermissionEvent<E> 
{
   Collection<E> grants;
   
   protected GrantRevokeEvent(AbstractPermissionView<E> source, Collection<E>grants, Role role) 
   {
      super(source, role);
      this.grants = grants;
   }//GrantRevokeEvent
   
   public Collection<E> getGrants() { return grants;} 
   
}//GrantRevokeEvent