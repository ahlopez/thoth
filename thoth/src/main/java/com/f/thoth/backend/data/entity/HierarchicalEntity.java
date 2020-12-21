package com.f.thoth.backend.data.entity;

import com.f.thoth.backend.data.security.ObjectToProtect;

public interface HierarchicalEntity<T>
{
   public Long            getId();
   public ObjectToProtect getObjectToProtect();
   public String          getName();   
   public String          getCode();
   public String          formatCode();
   public T               getOwner();

}//HierarchicalEntity
