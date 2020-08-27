package com.f.thoth.ui.views.security.permission;

import java.time.LocalDate;

import com.f.thoth.backend.data.entity.HierarchicalEntity;
import com.f.thoth.backend.data.security.Role;
import com.vaadin.flow.component.ComponentEvent;

public abstract class PermissionEvent<E extends HierarchicalEntity<E>>  extends ComponentEvent<AbstractPermissionView<E>> 
{
   private Role      role;
   private LocalDate from;
   private LocalDate to;
   
   protected PermissionEvent( AbstractPermissionView<E> source, Role role, LocalDate from, LocalDate to) 
   { 
      super(source, false);
      this.role = role;
      this.from = from;
      this.to   = to;
      
   }//PermissionEvent consructor
   
   // ------------------- Getters & Setters ---------------
   public Role getRole()      { return role;}
   public LocalDate getFrom() { return from;}
   public LocalDate getTo()   { return to;}

}//PermitFormEvent




