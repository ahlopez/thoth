package com.f.thoth.backend.data.gdoc.metadata;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Representa un rango de valores tipo URI
 */
public class UriRange implements Range
{
   // ------------- Constructors ------------------
   public UriRange()
   {
   }

   // --------------- Logic ------------------------------

   public boolean in(Object value)
   {
      if (value == null ||  ! (value instanceof String))
         return false;

      try
      {
         URI uri = new URI( (String)value);
         return uri != null;
      } catch (URISyntaxException e)
      {
         return false;
      }

   }//in

}//UriRange