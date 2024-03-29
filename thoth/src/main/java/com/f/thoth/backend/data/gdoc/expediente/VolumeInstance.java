package com.f.thoth.backend.data.gdoc.expediente;

import static com.f.thoth.Parm.CODE_SEPARATOR;

import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.f.thoth.Parm;
import com.f.thoth.backend.data.entity.AbstractEntity;
import com.f.thoth.backend.data.entity.util.TextUtil;
import com.f.thoth.backend.data.security.Tenant;

/**
 * Representa un volumen documental (segun Moreq)
 */
@Entity
@Table(name = "VOLUME_INSTANCE")
public class VolumeInstance extends AbstractEntity implements  Comparable<VolumeInstance>
{
   @NotNull  (message = "{evidentia.volume.required}")
   protected Integer        instance;                                    // Index of the current volume

   @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
   @NotNull  (message = "{evidentia.volume.required}")
   protected Volume         volume;                                      // Volume to which this instance belongs

   @NotNull  (message = "{evidentia.repopath.required}")
   @NotBlank (message = "{evidentia.repopath.required}")
   @NotEmpty (message = "{evidentia.repopath.required}")
   @Size(max = 255)
   protected String         path;                                        //  Node path in document repository

   protected String         location;                                    // Physical archive location (topographic signature)

   @NotNull(message = "{evidentia.dateopened.required}")
   protected LocalDateTime  dateOpened;                                  // Date volume instance was opened

   @NotNull(message = "{evidentia.dateclosed.required}")
   protected LocalDateTime  dateClosed;                                  // Date volume instance was closed

   @NotNull(message = "{evidentia.open.required}")
   protected Boolean        open;                                        // Is the volume instance currently open?

   // ---------------- Constructors -------------
   public VolumeInstance()
   {
      super();
      this.instance   = 0;
      this.volume     = null;
      this.path       = "";
      this.location   = "";
      this.dateOpened = LocalDateTime.MAX;
      this.dateClosed = LocalDateTime.MAX;
      this.open       = false;

   }//Volume


   public VolumeInstance(Volume volume, Integer instance, String location, LocalDateTime  dateOpened, LocalDateTime  dateClosed)
   {
      super();

      if ( volume == null)
         throw new IllegalArgumentException("Volumen dueño de la instancia no puede ser nulo");

      if ( instance == null)
         throw new IllegalArgumentException("Indice de instancia del volumen no puede ser nulo");

      if ( dateOpened == null)
         throw new IllegalArgumentException("Fecha de apertura del volumen no puede ser nula");

      if ( dateClosed == null)
         throw new IllegalArgumentException("Fecha de cierre del volumen no puede ser nula");

      this.instance      = instance;
      this.volume        = volume;
      this.path          = volume.getPath() + Parm.PATH_SEPARATOR+ instance;
      this.location      = location == null? "" : location;
      this.dateOpened    = dateOpened;
      this.dateClosed    = dateClosed;
      this.open          = false;

   }//Volume
   
   
   public String  formatCode()
   {  return volume.formatCode()+ CODE_SEPARATOR+ instance;
   }


   // ------------------ Getters & Setters ----------------------

   public Integer       getInstance()                 { return instance; }
   public void          setInstance(Integer instance) { this.instance = instance;}

   public Volume        getVolume()                   { return volume;}
   public void          setVolume(Volume volume)      { this.volume = volume;}

   public String        getPath()                     { return path;}
   public void          setPath ( String path)        { this.path = path;}

   public String        getLocation()                 { return location;}
   public void          setLocation(String location)  { this.location = location;}

   public LocalDateTime getDateOpened()               { return dateOpened;}
   public void          setDateOpened(LocalDateTime dateOpened) { this.dateOpened = dateOpened;}

   public LocalDateTime getDateClosed()               { return dateClosed;}
   public void          setDateClosed(LocalDateTime dateClosed) { this.dateClosed = dateClosed;}

   public Boolean       getOpen()                     { return open;}
   public void          setOpen(Boolean open)         { this.open = open;}
   
   public Tenant        getTenant()                   { return volume.getTenant();}

   // ------------------- Object ---------------------------------

   @Override public boolean equals( Object o)
   {
      if (this == o)
         return true;

      if (!(o instanceof VolumeInstance ))
         return false;

      VolumeInstance that = (VolumeInstance) o;
      return this.id != null && this.id.equals(that.id);
   }//equals


   @Override public int hashCode() { return id == null? 70277: id.hashCode();}

   public String toString()
   {
      StringBuilder s = new StringBuilder();
      s.append( "VolumeInstance{")
       .append( super.toString())
       .append( volume.toString())
       .append( " instance["   + instance+ "]")
       .append( " path["       + path+ "]")
       .append( " date opened["+ TextUtil.formatDateTime(dateOpened)+ "]")
       .append( " date closed["+ TextUtil.formatDateTime(dateClosed)+ "]")
       .append( " location["  + location+ "]")
       .append( " open["       + open+ "]}\n");

      return s.toString();
   }//toString


   @Override  public int compareTo(VolumeInstance other)
   {
      if (other == null)
         return 1;

      Volume otherVolume = other.getVolume();
      int compareVol = (this.volume == null? -1: this.volume.compareTo(otherVolume));
      if ( compareVol != 0)
        return compareVol;

      return this.instance == null? -1 : this.instance.compareTo(other.instance);
   }// compareTo


   // ------------------- Logic  -------------------------------
   public boolean isOpen()
   {
      LocalDateTime now = LocalDateTime.now();
      return open &&
            ((now.equals(dateOpened) || now.equals(dateClosed)) ||
                  (now.isAfter(dateOpened) && now.isBefore(dateClosed))) ;
   }//isOpen


   public void closeInstance()
   {
      if (isOpen())
      {  dateClosed = LocalDateTime.now();
      }
   }//closeInstance


}//VolumeInstance