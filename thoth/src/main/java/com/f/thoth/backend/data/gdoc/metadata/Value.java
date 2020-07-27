package com.f.thoth.backend.data.gdoc.metadata;


/**
 * Representa un valor de un metadato
 */
public interface Value<E>
{
   public E       getValue();

   public Type    getType();

}//Value
