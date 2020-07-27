package com.f.thoth.backend.data.entity;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.f.thoth.backend.data.security.Tenant;

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

  @ManyToOne
  @NotNull (message = "{evidentia.tenant.required}")
  protected Tenant tenant;

  // ---------------- Getters y Setters -------------------

  public Long    getId() { return id; }

  public boolean isPersisted() { return id != null; }

  public int     getVersion() { return version; }

  public String  getCode() { return code; }
  public void    setCode(String code) { this.code = code; }

  public Tenant  getTenant() { return tenant;}
  public void    setTenant( Tenant tenant) { this.tenant = tenant;}

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
  public String toString() { return "id[" + id + "] code[" + code + "] version[" + version + "]"; }

}// AbstractEntity
