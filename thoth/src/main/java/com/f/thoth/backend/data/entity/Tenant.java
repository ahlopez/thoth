package com.f.thoth.backend.data.entity;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
public class Tenant extends BasicEntity
{

   @NotBlank(message = "{bakery.name.required}")
   @Size(max = 255)
   @Column(unique = true)
   private String name;


   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   @Override
   public String toString() {
      return name;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }
      if (o == null || getClass() != o.getClass()) {
         return false;
      }
      if (!super.equals(o)) {
         return false;
      }
      Tenant that = (Tenant) o;
      return Objects.equals(name, that.name); 
   }

   @Override
   public int hashCode() {
      return Objects.hash(super.hashCode(), name);
   }
}
