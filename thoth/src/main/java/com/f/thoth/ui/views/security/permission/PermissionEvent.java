package com.f.thoth.ui.views.security.permission;

import com.f.thoth.backend.data.entity.HierarchicalEntity;
import com.f.thoth.backend.data.security.Role;
import com.vaadin.flow.component.ComponentEvent;

public abstract class PermissionEvent<E extends HierarchicalEntity<E>>  extends ComponentEvent<AbstractPermissionView<E>> 
{
   private Role  role;
   
   protected PermissionEvent( AbstractPermissionView<E> source, Role role) 
   { 
      super(source, false);
      this.role = role;
   }
   
   public Role getRole() { return role;}

}//PermitFormEvent




