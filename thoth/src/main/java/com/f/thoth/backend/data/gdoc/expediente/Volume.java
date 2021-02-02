package com.f.thoth.backend.data.gdoc.expediente;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.f.thoth.backend.data.entity.BaseEntity;

/**
 * Representa un volumen documental (segun Moreq)
 */
@Entity
@Table(name = "VOLUME", indexes = { @Index(columnList = "code")})
public class Volume extends BaseEntity implements  Comparable<Volume>
{
	@NotNull  (message = "{evidentia.volume.required}")
	protected Long          currentVolume;                                // Index of the current volume

	@NotNull  (message = "{evidentia.expediente.required}")
	protected Expediente    expediente;                                   // Expediente that owns the volume

	@NotNull  (message = "{evidentia.repopath.required}")
	@NotBlank (message = "{evidentia.repopath.required}")
	@NotEmpty (message = "{evidentia.repopath.required}")
	@Size(max = 255)
	protected String         path;                                        //  Node path in document repository

	@NotNull(message = "{evidentia.dateopened.required}")
	protected LocalDateTime  dateOpened;                                  // Date volume was opened

	@NotNull(message = "{evidentia.dateclosed.required}")
	protected LocalDateTime  dateClosed;                                  // Date volume was closed

	@NotNull(message = "{evidentia.open.required}")
	protected Boolean        open;                                        // Is the volume currently open?

	// ---------------- Constructors -------------
	public Volume()
	{
		super();
		this.currentVolume = 0L;
		buildCode();
	}//Volume


	public Volume(Expediente expediente, Long currentVolume, LocalDateTime  dateOpened, LocalDateTime  dateClosed)
	{
		super();

		if ( expediente == null)
			throw new IllegalArgumentException("Expediente padre del volumen no puede ser nulo");

		if ( currentVolume == null)
			throw new IllegalArgumentException("Indice del volumen no puede ser nulo");

		if ( dateOpened == null)
			throw new IllegalArgumentException("Fecha de apertura del volumen no puede ser nula");

		if ( dateClosed == null)
			throw new IllegalArgumentException("Fecha de cierre del volumen no puede ser nula");

		this.expediente    = expediente;
		this.currentVolume = currentVolume;
		this.dateOpened    = dateOpened;
		this.dateClosed    = dateClosed;
		buildCode();
	}//Volume

	@PrePersist
	@PreUpdate
	public void prepareData()
	{
		buildCode();
	}

	@Override protected void buildCode()
	{ 
		if (expediente != null)
			this.path =  expediente.getCode()+ "/"+ currentVolume;
		else 
			this.path = "[expediente]/"+ currentVolume;	

		this.code = this.path;
	}//buildCode

	// ------------------ Getters & Setters ----------------------

	public Long          getCurrentVolume() {	return currentVolume; }
	public void          setCurrentVolume(Long currentVolume) {	this.currentVolume = currentVolume;}

	public Expediente    getExpediente() {	return expediente;}
	public void          setExpediente(Expediente expediente) {	this.expediente = expediente;}

	public String        getPath() { return path;}
	public void          setPath ( String path) { this.path = path;}

	public LocalDateTime getDateOpened() {	return dateOpened;}
	public void          setDateOpened(LocalDateTime dateOpened) {	this.dateOpened = dateOpened;}

	public LocalDateTime getDateClosed() {	return dateClosed;}
	public void          setDateClosed(LocalDateTime dateClosed) {	this.dateClosed = dateClosed;}

	public Boolean       getOpen() {	return open;}
	public void          setOpen(Boolean open) {	this.open = open;}

	// ------------------- Object ---------------------------------

	@Override public boolean equals( Object o)
	{
		if (this == o)
			return true;

		if (!(o instanceof Expediente ))
			return false;

		Volume that = (Volume) o;
		return this.id != null && this.id.equals(that.id);
	}//equals


	@Override public int hashCode() { return id == null? 7027: id.hashCode();}

	public String toString()
	{
		StringBuilder s = new StringBuilder();
		s.append( "Volumen{")
		.append( super.toString())
		.append( expediente.toString())
		.append( " currentVolume["+ currentVolume+ "]")
		.append( " date opened["+ dateOpened+ "]")
		.append( " date closed["+ dateClosed+ "]")
		.append( " open["+ open+ "]}\n");

		return s.toString();
	}//toString


	@Override  public int compareTo(Volume that)
	{
		if (that == null)
			return 1;
		
		int expedienteOrder = this.expediente.compareTo(that.expediente);
		return this.equals(that)?       0 :
			   expedienteOrder  != 0?  expedienteOrder:
			   this.code.compareTo(that.code);
	}// compareTo


	// ------------------- Logic  -------------------------------
	public boolean isOpen()
	{
		LocalDateTime now = LocalDateTime.now();
		return open &&
				((now.equals(dateOpened) || now.equals(dateClosed)) ||
						(now.isAfter(dateOpened) && now.isBefore(dateClosed))) ;
	}//isOpen

}//Volume