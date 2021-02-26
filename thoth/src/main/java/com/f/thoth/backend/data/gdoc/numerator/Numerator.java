package com.f.thoth.backend.data.gdoc.numerator;

import org.springframework.beans.factory.annotation.Autowired;

import com.f.thoth.backend.data.cache.CacheManager;
import com.f.thoth.backend.data.entity.util.TextUtil;
import com.f.thoth.backend.data.security.Tenant;
import com.f.thoth.backend.service.SequenceService;

/**
 * Un Numerator administra un conjunto de secuencias de numeracion
 */
public class Numerator
{
   private CacheManager<String,Sequence>  seqs;
   private static final int CACHE_SIZE = 100;

   @Autowired
   public Numerator(SequenceService sequenceService)
   {
      this.seqs = new CacheManager<>( sequenceService, CACHE_SIZE);
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
   public Sequence getSequence(final Tenant tenant, final String nombre, final String prefijo, final String sufijo, final long inicial, final int incremento,final int longitud )
   {
      if ( tenant == null )
         throw new IllegalArgumentException("Tenant al que pertenece la secuencia no puede ser nulo");

      if ( TextUtil.isEmpty(nombre) )
         throw new IllegalArgumentException("El nombre del Numerator no puede ser nulo ni vacio");

      if ( inicial < 0 )
         throw new IllegalArgumentException("Valor inicial de la secuencia["+ name+ "]= " + inicial + ". debe ser cero o positivo.");

      if ( incremento <= 0 )
         throw new IllegalArgumentException("Incremento de la secuencia["+ name+ "] = "+ incremento+ ". debe ser mayor que cero");

      String p = ( prefijo == null? "": prefijo);
      String s = ( sufijo  == null? "": sufijo);
      String  name = sequenceName(tenant, nombre, p, s);
      synchronized ( seqs)
      {
         Sequence seq = seqs.fetch( name);
         if ( seq == null )
         {
            seq = new Sequence( tenant, nombre, p, s, inicial, incremento, longitud);
            seqs.add(name, seq);
         }
      }
      return seq;

   }//getSequence

   /*
    * Obtiene el nombre unico de la secuencia en el sistema
    *
    * @param tenant  Tenant duenno de la secuencia
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
    * Obtiene una secuencia creada con nombre especifico.
    * @param nombre Nombre de la secuencia a buscar.
    *               No es sensitivo a mayusculas/minusculas
    * @return Sequence La secuencia solicitada, si esta registrada; de otra forma retorna null
    * @throws IllegalArgumentException cuando el nombre presentado es nulo o vacio
    */
   public Sequence obtenga( final String nombre)
   {
      if ( TextUtil.isEmpty(nombre) )
         throw new IllegalArgumentException("Nombre de la secuencia no puede ser nulo ni vacÃ­o");

      synchronized(seqs)
      {
         return seqs.get( nombre.toUpperCase());
      }

   }//obtenga

   /**
    * Cierra la secuencia para numeracion
    * @param nombre Secuencia a cerrar
    */
   public void close( String nombre)
   {
      if ( TextUtil.isEmpty( nombre) )
         throw new IllegalArgumentException("Nombre de la secuencia a cerar no puede ser nulo ni vacÃ­o");

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
    * @throws IllegalArgumentException si el nombre de secuencia es nulo o vacio, o
    * si la secuencia no existe
    */
   public String next(String nombre)
   {
      if ( TextUtil.isEmpty(nombre) )
         throw new IllegalArgumentException("Nombre de secuencia no puede ser nulo ni vacio");

      Sequence s = null;
      synchronized( seqs)
      {
         s = seqs.get( nombre.toUpperCase());

         if ( s == null )
            throw new IllegalArgumentException("Secuencia["+ nombre+ "] no existe");

         return s.next();
      }
   }//next


   /**
    *  Obtiene el valor actual de la secuencia
    * @param nombre  Nombre de la secuencia requerida
    * @throws IllegalArgumentException si el nombre de secuencia es nulo o vacio, o
    * si la secuencia no existe
    *  @return Valor actual de la secuencia
    */
   public String get( String nombre)
   {
      if ( TextUtil.isEmpty(nombre) )
         throw new IllegalArgumentException("Nombre de secuencia a consultar no puede ser nulo ni vacÃ­o");

      synchronized( seqs)
      {
         Sequence s = seqs.get( nombre.toUpperCase());
         if ( s == null )
            throw new IllegalArgumentException("Secuencia["+ nombre+ "] no existe");

         return s.get();
      }
   }//get

}//Numerator
