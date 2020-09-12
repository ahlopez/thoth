package com.f.thoth.backend.data.gdoc.metadata;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.time.LocalDateTime;

/**
 * Representa los tipos de metadatos del sistema
 */

public enum Type
{
   STRING   (String.class),
   ENUM     (String.class),
   BINARY   (Byte[].class),
   BOOLEAN  (Boolean.class),
   DECIMAL  (BigDecimal.class),
   INTEGER  (BigInteger.class),
   DATETIME (LocalDateTime.class),
   REFERENCE(String.class),
   URI      (URI.class),
   ID       (String.class),
   PATH     (String.class),
   HTML     (String.class);

   private  Class<?> javaType;
   public   Class<?> getJavaType() { return javaType;}


   private Type( Class<?> javaType)
   {
     this.javaType = javaType;
   }
   
   public Object valueof( String stringValue)
   {
      try 
      {
         if ( javaType.equals(Byte[].class))
            return  stringValue.toCharArray();
         
         Constructor<?> constructor = javaType.getConstructor(String.class);
         return constructor.newInstance(stringValue);
      }catch( Exception e)
      {
         return null;
      }
   }
   
   public String getDisplayName() { return name();}


}//Type