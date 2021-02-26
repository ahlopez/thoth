package com.f.thoth.backend.data.gdoc.numerator;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.atomic.AtomicLong;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.f.thoth.backend.data.entity.util.TextUtil;
import com.f.thoth.backend.data.security.Tenant;

/** Representa una secuencia de numeracion */
@Entity
@Table(name = "SEQUENCE", indexes = { @Index(columnList = "nombre")})
public class Sequence extends Observable implements Comparable<Sequence>
{
   public enum Status { OPEN, CLOSED};

   /** persistor - Observador de persistencia de la secuencia */
   private static final Observer persistor   =  PersistProducer.getInstance();

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   protected Long        id;                                     // Primary key

   @Version
   protected int         version;                                // Record version
      
   @NotNull (message = "{evidentia.code.required}")
   protected String      code;                                   // Business id

   @NotNull (message = "{evidentia.tenant.required}")
   protected Tenant      tenant;                                 // Tenant duenno de la secuencia

   @NotNull (message = "{evidentia.name.required}")
   @NotEmpty(message = "{evidentia.name.required}")
   @Size(max = 255, message="{evidentia.code.maxlength}")
   @Column(unique = true)
   protected String      nombre;                                 // Nombre de la secuencia

   @NotNull (message = "{evidentia.prefix.required}")
   protected String      prefijo;                                // Prefijo del numero

   @NotNull (message = "{evidentia.suffix.required}")
   protected String      sufijo;                                 // Sufijo  del numero

   @NotNull (message = "{evidentia.value.required}")
   protected AtomicLong  value;                                  // Valor actual de la secuencia

   @NotNull (message = "{evidentia.increment.required}")
   protected Integer        increment;                           // Delta entre dos numeros consecutivos de la secuencia

   @NotNull (message = "{evidentia.length.required}")
   protected Integer     longitud;                               // Longitud obligatoria del secuencial. Asegurarla con padding de ceros por la izquierda

   @NotNull     (message= "{evidentia.status.required}")
   @Enumerated(EnumType.STRING)
   protected Status      status;                                 // Estado de la secuencia OPEN/CLOSED


   // ================================================================================
   // Creacion e inicializacion
   //

   /** Obtiene una instancia vacia de la secuencia */
   public Sequence()
   {
      this.nombre = null;
   }// Sequence

   /**
    *  Crea una secuencia de numeracion con valor inicial e incremento especificados
    *  @param nombre Identificador externo de la secuencia
    *  @param prefijo Prefijo del numero
    *  @param sufijo  Sufijo del numero
    *  @param initialValue El valor inferior del rango
    *  @param increment Delta entre dos numeros consecutivos de la secuencia
    *  @param longitud Longitud del secuencial
    */
   public Sequence( Tenant tenant, String nombre, String prefijo, String sufijo, final Long initialValue, final Integer increment, Integer longitud)
   {
      assert tenant != null;
      assert TextUtil.isNotEmpty( nombre);
      assert prefijo != null;
      assert sufijo  != null;
      assert initialValue >= 0L;
      assert increment > 0L;
      assert longitud > 0;

      this.tenant     = tenant;
      this.nombre     = nombre;
      this.prefijo    = prefijo;
      this.sufijo     = sufijo;
      this.value      = new AtomicLong( initialValue);
      this.increment  = increment;
      this.status     = Status.OPEN;
      this.longitud   = longitud;

      addObserver( persistor);
      setChanged( );
      notifyObservers( new CreateSequence( this));

   }//Sequence
   
   @PrePersist
   @PreUpdate
   public void prepareData()
   {
      this.code = getCode();
   }//prepareData

   // ---------------------- Getters & Setters -----------------

   public  Long        getId()                         { return id;}
   public  void        setId(Long id)                  { this.id = id;}

   public  int         getVersion()                    { return version;}
   public  void        setVersion(int version)         { this.version = version;}
   
   public  String      getCode()                       { return code;}
   public  void        setCode( String code)           { this.code = code;}

   public  Tenant      getTenant()                     { return tenant;}
   public  void        setTenant(Tenant tenant)        { this.tenant = tenant;}

   public  String      getNombre()                     { return nombre;}
   public  void        setNombre(String nombre)        { this.nombre = nombre;}

   public  String      getPrefijo()                    { return prefijo;}
   public  void        setPrefijo(String prefijo)      { this.prefijo = prefijo;}

   public  String      getSufijo()                     { return sufijo;}
   public  void        setSufijo(String sufijo)        { this.sufijo = sufijo;}

   public  Long        getValue()                      { return value.longValue();}
   public  void        setValue(Long value)            { this.value.set(value);}

   public  Integer     getIncrement()                  { return increment;}
   public  void        setInccrement(Integer increment){ this.increment = increment;}

   public  Integer     getLongitud()                   { return longitud;}
   public  void        setLongitud(Integer longitud)   { this.longitud = longitud;}

   public  Status      getStatus()                     { return status;}
   public  void        setStatus(Status status)        { this.status = status;}

   // --------------- Object methods ---------------------

   @Override public boolean equals( Object o)
   {
      if (this == o)
         return true;

      if (!(o instanceof Sequence ))
         return false;

      Sequence that = (Sequence) o;
      return this.id != null && this.id.equals(that.id);

   }//equals

   @Override public int hashCode() { return id == null? 74027: id.hashCode();}

   @Override
   public String toString()
   {
      StringBuilder s = new StringBuilder();
      s.append("Sequence{")
       .append(" status["+ status.toString()+ "]")
       .append(" n observers["+ countObservers()+ "]")
       .append(" id["+ id+ ")")
       .append(" version["+ version+ "]")
       .append(" tenant["+ tenant.getCode()+ "]")
       .append(" nombre["+ nombre+ "]\n")
       .append(" prefijo["+ prefijo+ "]")
       .append(" sufijo["+ sufijo+ "]")
       .append(" valor actual["+ value+ "]")
       .append(" incremento["+ increment+ "]")
       .append(" longitud["+ longitud+ "] }\n");

      return s.toString();
   }//toString


   @Override  public int compareTo(Sequence that)
   {
      return this.equals(that)?  0 :
             that == null?       1 :
             this.getCode().compareTo(that.getCode());

   }// compareTo

   public String buildCode()
   {
      return("["+ tenant.getId()+ "]"+ nombre+ "_"+ prefijo+ "_"+ sufijo).toUpperCase();
   }//buildCode

   // ----------------------- Logica ---------------------------

   public  boolean     isEmpty(){return nombre == null;}

   /**
    * Retorna el siguiente valor de la secuencia.
    * En la primera llamada retorna el valor inicial de la secuencia
    * @return long Siguiente valor de la secuencia.  Thread-safe.
    */
   public  String next()
   {
      if ( status != Status.OPEN )
         throw new IllegalStateException("Secuencia ["+ tenant.getId()+ "]"+ nombre+ ","+ prefijo+ ","+ sufijo+ " está cerrada. No puede avanzar");

      long n =  value.addAndGet(increment);
      setChanged( );
      notifyObservers(  new SaveSequence( this));
      return prefijo+ TextUtil.pad(n, longitud)+ sufijo;

   }//next


   /**
    *  Obtiene el valor actual de la secuencia
    *  @return Valor actual de la secuencia
    */
   public String get() { return prefijo+ value.get()+ sufijo;}



   /** Cierra la secuencia e impide su avance posterior */
   public void close()
   {
      if ( status == Status.OPEN )
      {
         status = Status.CLOSED;
         setChanged();
         notifyObservers( new CloseSequence( this)); 
         deleteObserver(persistor);
      }

   }//close

   /**
    * Actualiza el valor de una secuencia para que tenga el mayor valor entre el actual y el presentado
    * @param otherValue El valor a comparar
    * @return boolean true si se actualizo el valor de la secuencia; false si no se actualizo
    */
   public boolean  setGreatestValue( Long otherValue)
   {
      boolean updated = false;
      synchronized(this)
      {
         if ( this.value.get() < otherValue )
         {
            this.value.set( otherValue);
            updated = true;
         }
      }
      return updated;
   }//setGreatestValue

}//Sequence
