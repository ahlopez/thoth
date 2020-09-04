package com.f.thoth.backend.data.entity;

import java.util.Objects;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

/**
 * Representa la lógica común a todas las entidades persistentes
 */

@MappedSuperclass
public abstract class AbstractEntity
{
	  @Id
	  @GeneratedValue(strategy = GenerationType.IDENTITY)
	  protected Long id;

	  @Version
	  protected int version;
	  
	  public AbstractEntity()
	  {	     
	  }

	  // ---------------- Getters y Setters -------------------

	  public Long    getId() { return id; }

	  public boolean isPersisted() { return id != null; }

	  public int     getVersion() { return version; }

	  // ---------- Object methods ------------------

	  @Override
	  public boolean equals(Object o)
	  {
	    if (this == o)
	      return true;

	    if (o == null || getClass() != o.getClass())
	      return false;

	    AbstractEntity that = (AbstractEntity) o;
	    if (this.getId() == null || that.getId() == null)
	      return false;

	    return this.getId().equals(that.getId()) &&
	           this.getVersion() == that.getVersion();

	  }// equals

	  @Override
	  public int hashCode() { return Objects.hash(id, version); }

	  @Override
	  public String toString() { return "id[" + id + ":" + version + "]"; }

}// AbstractEntity
