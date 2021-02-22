package com.f.thoth.backend.data.gdoc.numerator;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.f.thoth.backend.data.entity.util.TextUtil;
import com.f.thoth.backend.data.security.Tenant;

/**
 * Un Numerator administra un conjunto de secuencias de numeración
 */
public class Numerator
{
     private transient Map<String, Sequence> seqs;                          //  Cache of Sequences in use in the session.

   public Numerator()
   {
      seqs = new HashMap<String, Sequence>();
   }//Numerator

   /**
    * Obtiene una secuencia con un nombre dado <BR>
    * El nombre de cada secuencia es unico en el sistema y no es sensitivo a mayusculas <BR>
    * Si la secuencia ya existe retorna la secuencia existente
    * e ignora los parumetros de inicializacion; si no existe la crea <BR>
    *
    * @param tenant     Tenant al que pertenece la secuencia
    * @param nombre     Nombre publico de la secuencia
    * @param prefijo    Prefijo del numero
    * @param sufijo     Sufijo del numero
    * @param inicial    Valor inicial de la secuencia
    * @param incremento Delta entre numeros consecutivos de la secuencia
    * @return Sequence  La secuencia solicitada
    */
   public Sequence getSequence(final Tenant tenant, final String nombre, final String prefijo, final String sufijo, final long inicial, final long incremento,final int longitud )
   {
     if( tenant == null )
         throw new IllegalArgumentException("Tenant al que pertenece la secuencia no puede ser nulo");

      if ( TextUtil.isEmpty(nombre) )
         throw new IllegalArgumentException("El nombre del Numerator no puede ser nulo ni vacío");

      if ( prefijo == null ) prefijo = "";
      if ( sufijo  == null ) sufijo  = "";
      String  name = sequenceName(tenant, nombre, prefijo, sufijo);

      if ( inicial < 0 )
         throw new IllegalArgumentException("Valor inicial de la secuencia["+ name+ "]= " + inicial + ". debe ser cero o positivo.");

      if ( incremento <= 0 )
         throw new IllegalArgumentException("Incremento de la secuencia["+ name+ "] = "+ incremento+ ". debe ser mayor que cero");

      Sequence seq = seqs.get( name);
      if ( seq == null )
      {
         synchronized ( this)
         {
            Optional<Sequence> optSeq = sequenceRepository.findByName(name);
            seq = optSeq.isPresent()?
                  optSeq.get():
                  new Sequence( tenant, nombre, prefijo, sufijo, inicial, incremento, longitud);

            seqs.put( name, seq);
         }
      }
      return seq;

   }//getSequence

   /**
    * Obtiene el nombre unico de la secuencia en el sistema
    *
    * @param tenant  Tenant dueño de la secuencia
    * @param nombre  Nombre publico de la secuencia
    * @param prefijo Prefijo del numero
    * @param sufijo  Sufijo del numero
    *
    * @return String Identificador unico de la secuencia
    */
   private static String sequenceName( Tenant tenant, String nombre, String prefijo, String sufijo)
   {
      return("["+ tenant.getId()+ "]"+ nombre+ "_"+ prefijo+ "_"+ sufijo).toUpperCase();
   }//sequenceName


   // ================================================================================
   // Logica de negocio
   //

   /**
    * Obtiene una secuencia creada con nombre específico.
    * @param nombre Nombre de la secuencia a buscar.
    *               No es sensitivo a mayusculas/minusculas
    * @return Sequence La secuencia solicitada, si esta registrada; de otra forma retorna null
    * @throws IllegalArgumentException cuando el nombre presentado es nulo o vacío
    */
   public synchronized Sequence obtenga( final String nombre)
   {
      if ( TextUtil.isEmpty(nombre) )
         throw new IllegalArgumentException("Nombre de la secuencia no puede ser nulo ni vacío");

      return seqs.get( nombre.toUpperCase());

   }//obtenga

   /**
    * Cierra la secuencia para numeracion
    * @param nombre Secuencia a cerrar
    */
   public void close( String nombre)
   {
      if ( TextUtil.isEmpty( nombre) )
         throw new IllegalArgumentException("Nombre de la secuencia a cerar no puede ser nulo ni vacío");

      synchronized( seqs)
      {
         Sequence seq = seqs.get( nombre.toUpperCase());
         if ( seq == null )
            throw new IllegalArgumentException("Secuencia["+ nombre+ "]  no existe");

         seq.close();
      }
   }//close

   /**
    * Retorna el siguiente valor de la secuencia.
    * En la primera llamada retorna el valor inicial de la secuencia
    * @param nombre Secuencia a incrementar
    * @return long Siguiente valor de la secuencia
    * @throws IllegalArgumentException si el nombre de secuencia es nulo o vacío, o
    * si la secuencia no existe
    */
   public String next(String nombre)
   {
      if ( TextUtil.isEmpty(nombre) )
         throw new IllegalArgumentException("Nombre de secuencia no puede ser nulo ni vacío");

      Sequence s = null;
      synchronized( seqs)
      {
         s = seqs.get( nombre);
      }

      if ( s == null )
         throw new IllegalArgumentException("Secuencia["+ nombre+ "] no existe");

      return s.next();
   }//next


   /**
    *  Obtiene el valor actual de la secuencia
    * @param nombre  Nombre de la secuencia requerida
    * @throws IllegalArgumentException si el nombre de secuencia es nulo o vacío, o
    * si la secuencia no existe
    *  @return Valor actual de la secuencia
    */
   public String get( String nombre)
   {
      if ( TextUtil.isEmpty(nombre) )
         throw new IllegalArgumentException("Nombre de secuencia a consultar no puede ser nulo ni vacío");

      synchronized( seqs)
      {
         Sequence s = seqs.get( nombre);
         if ( s == null )
            throw new IllegalArgumentException("Secuencia["+ nombre+ "] no existe");

         return s.get();
      }
   }//get

}//Numerator
