package com.f.thoth.backend.data.gdoc.metadata;

/**
 * Representa un rango de valores de metadatos
 */
public interface Range
{
   public boolean in(Object value);
}