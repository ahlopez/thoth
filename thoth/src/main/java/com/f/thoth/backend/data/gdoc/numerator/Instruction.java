package com.f.thoth.backend.data.gdoc.numerator;

/**
 * Representa una instruccion a ser ejecutada
 * Implementa el patrón Command"
 */
public interface Instruction
{
   /** Ejecuta la instrucción encomendada */
   public void execute();

   /**
    * Combina instrucciones en una sola, si es posible
    * @param  other La instrucción que será combinada con esta
    * @return boolean true si esta instruccion se actualizó con la presentada
    */
   public boolean merge ( Instruction other);
}//Instruction
