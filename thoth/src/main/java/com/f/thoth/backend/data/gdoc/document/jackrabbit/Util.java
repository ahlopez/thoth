package com.f.thoth.backend.data.gdoc.document.jackrabbit;

import javax.jcr.PropertyType;

import com.f.thoth.backend.data.gdoc.metadata.Type;

public class Util
{
   private static Type evidType[] = new Type[13];
   static
   {
      evidType[PropertyType.STRING]        =  Type.STRING;
      evidType[PropertyType.BINARY]        =  Type.BINARY;
      evidType[PropertyType.DATE]          =  Type.DATETIME;
      evidType[PropertyType.DOUBLE]        =  Type.DECIMAL;
      evidType[PropertyType.LONG]          =  Type.INTEGER;
      evidType[PropertyType.BOOLEAN]       =  Type.BOOLEAN;
      evidType[PropertyType.NAME]          =  Type.STRING;
      evidType[PropertyType.PATH]          =  Type.PATH;
      evidType[PropertyType.REFERENCE]     =  Type.REFERENCE;
      evidType[PropertyType.WEAKREFERENCE] =  Type.REFERENCE;
      evidType[PropertyType.URI]           =  Type.URI;
    }

   private static int jcrType[] = new int[13];
   static
   {
      jcrType[Type.STRING.ordinal()]    =    PropertyType.STRING;
      jcrType[Type.BINARY.ordinal()]    =    PropertyType.BINARY;
      jcrType[Type.DATETIME.ordinal()]  =    PropertyType.DATE;
      jcrType[Type.DECIMAL.ordinal()]   =    PropertyType.DOUBLE;
      jcrType[Type.INTEGER.ordinal()]   =    PropertyType.LONG;
      jcrType[Type.BOOLEAN.ordinal()]   =    PropertyType.BOOLEAN;
      jcrType[Type.PATH.ordinal()]      =    PropertyType.PATH;
      jcrType[Type.REFERENCE.ordinal()] =    PropertyType.REFERENCE;
      jcrType[Type.URI.ordinal()]       =    PropertyType.URI;
    }

   public Util()
   {
   }

   public static Type evidenciaType( int jcrType) { return evidType[ jcrType]; }

   public static int jcrType( Type evidenciaType) { return jcrType[ evidenciaType.ordinal()]; }

}//Util
