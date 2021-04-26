package com.f.thoth.backend.data.gdoc.numerator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.f.thoth.backend.data.cache.CacheManager;
import com.f.thoth.backend.data.entity.util.TextUtil;
import com.f.thoth.backend.data.security.Tenant;
import com.f.thoth.backend.service.SequenceService;

/**
 * Un Numerator administra un conjunto de secuencias de numeracion
 */
@Component
public class Numerator
{
   private CacheManager<String,Sequence>  seqs;
   private static final int         CACHE_SIZE = 100;
   private static Numerator         INSTANCE   = null;

   @Autowired
   public Numerator(SequenceService sequenceService)
   {
      this.seqs = new CacheManager<>( sequenceService, CACHE_SIZE);
      if (INSTANCE == null)
         INSTANCE = this;
   }//Numerator

   /**
    * Obtiene la única instancia del numerador
    * @return Instancia del numerador
    */
   public static Numerator getInstance()
   {
      return INSTANCE;
   }//getInstance

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
   public Sequence getSequence(final Tenant tenant, final String nombre, final String prefijo, final String sufijo, final long inicial, final int incremento, final int longitud )
   {
      if ( tenant == null )
         throw new IllegalArgumentException("Tenant al que pertenece la secuencia no puede ser nulo");

      if ( TextUtil.isEmpty(nombre) )
         throw new IllegalArgumentException("El nombre del Numerator no puede ser nulo ni vacio");

      if ( inicial < 0 )
         throw new IllegalArgumentException("Valor inicial de la secuencia["+ nombre+ "]= " + inicial + ". debe ser cero o positivo.");

      if ( incremento <= 0 )
         throw new IllegalArgumentException("Incremento de la secuencia["+ nombre+ "] = "+ incremento+ ". debe ser mayor que cero");

      String     p = ( prefijo == null? "": prefijo);
      String     s = ( sufijo  == null? "": sufijo);
      String  name = sequenceName(tenant, nombre, p, s);
      Sequence seq =  seqs.fetch( name);
      if ( seq == null )
      {
         seq = new Sequence( tenant, nombre, p, s, inicial, incremento, longitud);
         seqs.add(name, seq);
      }

      return seq;

   }//getSequence
   
   
   /**
    * Verifica si una secuencia ya existe en el numerador
    * @param tenant     Tenant al que pertenece la secuencia
    * @param nombre     Nombre publico de la secuencia
    * @param prefijo    Prefijo del numero
    * @param sufijo     Sufijo del numero
    */
   public boolean sequenceExists(final Tenant tenant, final String nombre, final String prefijo, final String sufijo)
   {
	   String seqName = sequenceName( tenant, nombre, prefijo, sufijo);
	   return  seqs.fetch( seqName) != null;
   }//sequenceExists
   

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
   public static String sequenceName( final Tenant tenant, final String nombre, String prefijo, String sufijo)
   {
	  String name =  (nombre == null)? prefijo+ "-"+ sufijo: nombre;
      return("["+ tenant.getId()+ "]"+ name).toUpperCase();
   }//sequenceName


   // ================================================================================
   // Logica de negocio
   //

   /**
    * Obtiene una secuencia creada con nombre especifico.
    * @param code Identificador (business key) de la secuencia a buscar.
    *             No es sensitivo a mayusculas/minusculas
    * @return Sequence La secuencia solicitada, si esta registrada; de otra forma retorna null
    * @throws IllegalArgumentException cuando el codigo presentado es nulo o vacio
    */
   public synchronized Sequence obtenga( final String code)
   {
      if ( TextUtil.isEmpty(code) )
         throw new IllegalArgumentException("Código de la secuencia no puede ser nulo ni vacío");

      return seqs.fetch( code);
   }//obtenga

   /**
    * Cierra la secuencia para numeracion
    * @param code  Identificador (business key) de la Secuencia a cerrar
    */
   public synchronized void close( String code)
   {
      if ( TextUtil.isEmpty( code) )
         throw new IllegalArgumentException("Código (business key) de la secuencia a cerar no puede ser nulo ni vacío");

      Sequence seq = seqs.fetch( code);
      if ( seq == null )
         throw new IllegalArgumentException("Secuencia["+ code+ "]  no existe");

      seq.close();
   }//close

   /**
    * Retorna el siguiente valor de la secuencia.
    * En la primera llamada retorna el valor inicial de la secuencia
    * @param code identificador (business key) de la Secuencia a incrementar
    * @return long Siguiente valor de la secuencia
    * @throws IllegalArgumentException si el nombre de secuencia es nulo o vacio, o
    * si la secuencia no existe
    */
   public synchronized String next(String code)
   {
      if ( TextUtil.isEmpty(code) )
         throw new IllegalArgumentException("Código (business key) de la secuencia a cerar no puede ser nulo ni vacío");

      Sequence s = null;
      s = seqs.fetch( code);

      if ( s == null )
         throw new IllegalArgumentException("Secuencia["+ code+ "] no existe");

      return s.next();
   }//next


   /**
    *  Obtiene el valor actual de la secuencia
    * @param code Identificador (business key) de la secuencia requerida
    * @throws IllegalArgumentException si el nombre de secuencia es nulo o vacio, o si la secuencia no existe
    *  @return Valor actual de la secuencia
    */
   public synchronized String get( String code)
   {
      if ( TextUtil.isEmpty(code) )
         throw new IllegalArgumentException("Nombre de secuencia a consultar no puede ser nulo ni vacÃ­o");

      Sequence s = seqs.fetch( code);
      if ( s == null )
         throw new IllegalArgumentException("Secuencia["+ code+ "] no existe");

      return s.get();
   }//get

}//Numerator
