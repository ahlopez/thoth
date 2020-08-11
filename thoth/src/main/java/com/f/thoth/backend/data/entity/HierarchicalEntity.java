package com.f.thoth.backend.data.entity;

public interface HierarchicalEntity<T>
{
   public Long    getId();
   public String  getCode();
   public String  getName();
   public T       getOwner();

}//HierarchicalEntity
