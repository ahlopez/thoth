package com.f.thoth.ui.views.security.permission;

import com.f.thoth.backend.data.entity.HierarchicalEntity;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.ui.components.Period;
import com.vaadin.flow.component.ComponentEvent;

public abstract class PermissionEvent<E extends HierarchicalEntity<E>>  extends ComponentEvent<AbstractPermissionView<E>> 
{
   private Role      role;
   private Period    period;
   
   protected PermissionEvent( AbstractPermissionView<E> source, Role role, Period period) 
   { 
      super(source, false);
      this.role   = role;
      this.period = period;
      
   }//PermissionEvent consructor
   
   // ------------------- Getters & Setters ---------------
   public Role getRole()      { return role;}
   public Period getPeriod()  { return period;}

}//PermitFormEvent




