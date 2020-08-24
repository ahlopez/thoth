package com.f.thoth.ui.views.security.permission;

import com.f.thoth.backend.data.entity.HierarchicalEntity;

public class CloseEvent<E extends HierarchicalEntity<E>> extends PermissionEvent<E> 
{
   protected CloseEvent(AbstractPermissionView<E> source) 
   { 
      super(source, null); 
   }
}//CloseEvent
