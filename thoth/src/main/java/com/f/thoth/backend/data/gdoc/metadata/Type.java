package com.f.thoth.backend.data.gdoc.metadata;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.time.LocalDateTime;

/**
 * Representa los tipos de metadatos del sistema,
 * tomados de CMIS, JCR y adición de ENUM
 */

public enum Type
{
   STRING   (String.class),         // JCR: java.lang.String
   ENUM     (String.class),         // Evidentia specific.  Not defined in CMIS, JCR
   BINARY   (Byte[].class),         // JCR: javax.jcr.Binary.   Not defined in CMIS
   BOOLEAN  (Boolean.class),        // CMIS.   JCR: boolean primitive type
   DECIMAL  (BigDecimal.class),     // CMIS, JCR: java.math.BigDecimal
   INTEGER  (BigInteger.class),     // CMIS. Not defined in JCR.  JCR uses LONG type according to long primitive type
   DATETIME (LocalDateTime.class),  // CMIS: DATETIME type ->java.util.Date.   JCR: DATE type -> java.util.Calendar, ISO8601 formato sYYYY-MM-DDThh:mm:ss.sssTZD
   REFERENCE(String.class),         // JCR: REFERENCE pointer (ID) to a node with ref integrity. WEAKREFERENCE same, with no ref integrity 
   URI      (URI.class),            // CMIS,JCR: java.lang.String, according to RFC3986
   ID       (String.class),         // CMIS.   JCR usa REFERENCE, WEAKREFERENCE
   PATH     (String.class),         // JCR: Path sintax according to JCR spec, JSR283 pag 30, &3.4.   Not defined in CMIS
   HTML     (String.class);         // CMIS.  Not defined in JCR
                                    // JCR types not defined: NAME, LONG, DOUBLE, WEAKREFERENCE, UNDEFINED

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
   }//valueof
   
   public String getDisplayName() { return name();}


}//Type