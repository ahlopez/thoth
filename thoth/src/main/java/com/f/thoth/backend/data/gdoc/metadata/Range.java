package com.f.thoth.backend.data.gdoc.metadata;

/**
 * Representa un rango de valores de metadatos
 */
public interface Range<E>
{
   public boolean in(E value);
}