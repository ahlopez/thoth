package com.f.thoth.backend.data.entity;

public interface HierarchicalEntity
{
   public Long    getId();
   public String  getCode();
   public String  getName();
   public Long    getParent();

}//HierarchicalEntity
