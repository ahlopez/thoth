package com.f.thoth.backend.data.entity;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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

	  @NotNull (message = "{evidentia.code.required}")
	  @NotEmpty(message = "{evidentia.code.required}")
	  @Size(max = 255, message="{evidentia.code.maxlength}")
	  @Column(unique = true)
	  protected String code;

	  // ---------------- Getters y Setters -------------------

	  public Long    getId() { return id; }

	  public boolean isPersisted() { return id != null; }

	  public int     getVersion() { return version; }

	  public String  getCode() { return code; }
	  public void    setCode(String code) { this.code = code; }
	  
	  protected abstract void buildCode();

	  // ---------- Object methods ------------------

	  @Override
	  public boolean equals(Object o)
	  {
	    if (this == o)
	      return true;

	    if (o == null || getClass() != o.getClass())
	      return false;

	    BaseEntity that = (BaseEntity) o;
	    if (this.getId() == null || that.getId() == null)
	      return false;

	    return this.getId().equals(that.getId()) &&
	           this.getVersion() == that.getVersion();

	  }// equals

	  @Override
	  public int hashCode() { return Objects.hash(id, version); }

	  @Override
	  public String toString() { return "id[" + id + "] code[" + code + "] version[" + version + "]"; }

}// AbstractEntity
