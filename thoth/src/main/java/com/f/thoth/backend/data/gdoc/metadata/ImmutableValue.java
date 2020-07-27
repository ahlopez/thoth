package com.f.thoth.backend.data.gdoc.metadata;

/**
 * Representa un valor inmutable de un metadato
 */
public class ImmutableValue<E> implements Value<E>
{
   protected E     value;
   protected Type  type;


   // ------------- Constructors ------------------
   public ImmutableValue()
   {
   }

   public ImmutableValue( E value, Type type)
   {
      if ( value == null)
         throw new IllegalArgumentException( "Un valor inmutable no puede ser nulo");

      if ( type == null)
         throw new IllegalArgumentException( "El tipo de un valor inmutable no puede ser nulo");

      this.value  = value;
      this.type   = type;
   }//ImmutableValue

   // -------------- Getters & Setters ----------------

   @Override public E getValue() { return value;}

   @Override public Type  getType() { return type;}

   // --------------- Object methods ---------------------

   @Override
   public int hashCode(){ return super.hashCode()*  1023+ value.hashCode() * 247 + type.hashCode(); }

   public boolean equals(Object o)
   {
      if (this == o)
         return true;

      if (o == null || getClass() != o.getClass())
         return false;

      if (!super.equals(o))
         return false;

     @SuppressWarnings("unchecked")   // Type verified above
     ImmutableValue<E> that = (ImmutableValue<E>) o;

      return  this.value.equals(that.value) && this.type.equals(that.type);

   }// equals

   public String toString(){ return value.toString(); }

   // --------------- Logic ---------------------


}//ImmutableValue
