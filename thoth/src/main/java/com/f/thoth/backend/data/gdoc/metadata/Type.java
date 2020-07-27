package com.f.thoth.backend.data.gdoc.metadata;

/**
 * Representa los tipos de metadatos del sistema
 */
public enum Type
{
   STRING   ("String"),
   ENUM     ("String[]"),
   BINARY   ("byte[]"),
   BOOLEAN  ("Boolean"),
   DECIMAL  ("BigDecimal"),
   INTEGER  ("BigInteger"),
   DATETIME ("LocalDateTime"),
   REFERENCE("String"),
   URI      ("URI"),
   ID       ("String"),
   PATH     ("String"),
   HTML     ("String");

   private  String javaType;
   public String getJavaType() { return javaType;}


   private Type( String javaType)
   {
     this.javaType = javaType;
   }

}//Type