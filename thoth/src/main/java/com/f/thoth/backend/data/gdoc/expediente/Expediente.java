package com.f.thoth.backend.data.gdoc.expediente;

import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.f.thoth.backend.data.entity.BaseEntity;
import com.f.thoth.backend.data.entity.HierarchicalEntity;
import com.f.thoth.backend.data.entity.util.TextUtil;
import com.f.thoth.backend.data.gdoc.classification.Classification;
import com.f.thoth.backend.data.gdoc.document.jackrabbit.NodeType;
import com.f.thoth.backend.data.gdoc.metadata.DocumentType;
import com.f.thoth.backend.data.gdoc.metadata.SchemaValues;
import com.f.thoth.backend.data.security.NeedsProtection;
import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.data.security.Permission;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.SingleUser;
import com.f.thoth.backend.data.security.ThothSession;
import com.f.thoth.backend.data.security.UserGroup;
import com.f.thoth.ui.utils.FormattingUtils;

/**
 * Representa un expediente documental
 */
@MappedSuperclass
public abstract class Expediente extends BaseEntity implements NeedsProtection, HierarchicalEntity<BranchExpediente>, Comparable<Expediente>
{
  @NotNull  (message = "{evidentia.name.required}")
  @NotBlank (message = "{evidentia.name.required}")
  @NotEmpty (message = "{evidentia.name.required}")
  @Size(max = 255)
  @Column(unique = true)
  protected String            name;                 // Expediente name (asunto)

  @NotNull(message = "{evidentia.objectToProtect.required}")
  @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
  protected ObjectToProtect   objectToProtect;      // Associated security object

  @NotNull(message = "{evidentia.nodeType.required}")
  @Enumerated(EnumType.STRING)                           //TODO: Esto es solo para el JCR Repo
  protected NodeType  type;                               //  Node type: {EXPEDIENTE}

  @NotNull(message = "{evidentia.class.required}")
  protected Classification    classificationClass;  // Classification class to which this expediente belongs

  @ManyToOne
  protected BranchExpediente  owner;                // Expediente to which this expediente belongs

  @ManyToOne
  protected SingleUser        createdBy;            // User that created this expediente

  @OneToOne(fetch = FetchType.LAZY, orphanRemoval = true)
  protected ExpedienteIndex   index;                // Expediente index

  protected DocumentType      attributes;           // Expediente optional metadata

  @NotNull(message = "evidentia.metadata.required")    //TODO: Hacer consistente con el DocType
  @OneToOne(cascade= CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
  protected SchemaValues metadata;                        // Metadata values of the associated expediente

  @NotNull(message = "{evidentia.dateopened.required}")
  protected LocalDateTime     openingDate;          // Last date the expediente was opened

  protected LocalDateTime     closingDate;          // last date the expediente was closed

  protected boolean           open;                 // Is the expediente currently open?

  @ManyToMany
  protected Set<String>       keywords;             // Search keywords

  @ManyToOne
  protected String            location;             // Signatura topográfica

  // --------------- Constructors --------------------
  public Expediente()
  {
    this.tenant = ThothSession.getCurrentTenant();
    buildCode();
  }

  public Expediente( String name, BranchExpediente owner)
  {
    if( TextUtil.isEmpty(name))
      throw new IllegalArgumentException("Nombre del expediente no puede ser nulo ni vacío");

    this.name  = name;
    this.owner = owner;

  }//Expediente constructor

  @PrePersist
  public void prepareData()
  {
    beforeUpdate();
    buildCode();
  }//prepareData

  @PreUpdate
  public void beforeUpdate()
  {
    this.name     =  TextUtil.nameTidy(name).toLowerCase();
  }


  @Override protected void buildCode()
  {
    //TODO: Asignar el identificador de forma automática con un numerador según dependencia
    this.code = (tenant == null? "[tenant]": tenant.getCode())+"[XPE]"+
        (owner == null? ":": owner.getOwnerCode())+ ">"+
        (name == null? "[name]" : name);
  }//buildCode

  // ---------------- Getters & Setters --------------

  public void            setName(String name){ this.name = name; }

  public Classification  getClassificationClass() { return classificationClass;}
  public void            setClassificationClass( Classification classificationClass) { this.classificationClass = classificationClass;}

  public void            setOwner(BranchExpediente owner) { this.owner = owner; }

  public void            setObjectToProtect(ObjectToProtect objectToProtect) { this.objectToProtect = objectToProtect; }

  public SingleUser      getCreatedBy() { return createdBy;}
  public void            setCreatedBy( SingleUser createdBy){ this.createdBy = createdBy;}

  public ExpedienteIndex getIndex() {return index;}
  public void            setIndex(ExpedienteIndex index) {this.index = index;}

  public DocumentType    getAttributes() {return attributes;}
  public void            setAttributes(DocumentType attributes) {this.attributes = attributes;}

  public LocalDateTime   getOpeningDate() {return openingDate;}
  public void            setOpeningDate(LocalDateTime openingDate) {this.openingDate = openingDate;}

  public LocalDateTime   getClosingDate() {return closingDate;}
  public void            setClosingDate(LocalDateTime closingDate) {this.closingDate = closingDate;}

  public void            setOpen(boolean open) {this.open = open;}

  public Set<String>     getKeywords() { return keywords;}
  public void            setKeywords( Set<String> keywords) { this.keywords = keywords;}

  public String          getLocation() { return location;}
  public void            setLocation(String location) { this.location = location;}

  // --------------------------- Implements HierarchicalEntity ---------------------------------------
  @Override public String           getName()   { return name;}

  @Override public BranchExpediente getOwner()  { return owner;}

  @Override public String           formatCode()
  {
    int i = TextUtil.indexOf(code, "/", 3);
    String id = code.substring(i);
    id = TextUtil.replace(id, "/", "-");
    return id;
  }//formatCode

  protected String getOwnerCode(){ return (owner == null ? "" : owner.getOwnerCode())+ ":"+ name; }

  // -----------------  Implements NeedsProtection ----------------

  @Override public ObjectToProtect getObjectToProtect()                  { return objectToProtect;}

  @Override public boolean         canBeAccessedBy(Integer userCategory) { return objectToProtect.canBeAccessedBy(userCategory);}

  @Override public boolean         isOwnedBy( SingleUser user)           { return objectToProtect.isOwnedBy(user);}

  @Override public boolean         isOwnedBy( Role role)                 { return objectToProtect.isOwnedBy(role);}

  @Override public boolean         isRestrictedTo( UserGroup userGroup)  { return objectToProtect.isRestrictedTo(userGroup);}

  @Override public boolean         admits( Role role)                    { return objectToProtect.admits(role);}

  @Override public void            grant( Permission  permission)        { objectToProtect.grant(permission);}

  @Override public void            revoke(Permission permission)         { objectToProtect.revoke(permission);}

  // ---------------------- Object -----------------------

  @Override public boolean equals( Object o)
  {
    if (this == o)
      return true;

    if (!(o instanceof Expediente ))
      return false;

    Expediente that = (Expediente) o;
    return this.id != null && this.id.equals(that.id);

  }//equals

  @Override public int hashCode() { return id == null? 83237: id.hashCode();}

  @Override public String toString()
  {
    StringBuilder s = new StringBuilder();
    s.append(" name["+ name+ "]")
    .append(" clase["+ classificationClass.formatCode()+ "]")
    .append(" owner["+ owner.getCode()+ "]")
    .append(" created by["+ createdBy.getEmail()+ "]")
    .append(" n entries["+ index.size()+ "]")
    .append(" docType["+ attributes.getCode()+ "]")
    .append(" attributes["+ attributes == null? "]": "has atributes]")
    .append(" location["+ (location == null? " ": location)+ "]")
    .append(" dateOpen["+ openingDate.format(FormattingUtils.FULL_DATE_FORMATTER)+ "]")
    .append(" dateCloses["+ closingDate.format(FormattingUtils.FULL_DATE_FORMATTER)+ "]")
    .append(" isOpen["+ isOpen()+ "]")
    .append(" keywords[");
    for (String keyword: keywords)
      s.append(" "+ keyword);
    s.append("]");


    return s.toString();
  }//toString

  @Override  public int compareTo(Expediente that)
  {
    return this.equals(that)?  0 :
      that == null?       1 :
        this.getCode().compareTo(that.getCode());

  }// compareTo

  public abstract boolean isBranch();

  public abstract boolean isLeaf();

  public abstract boolean isVolume();

  public boolean isOpen()
  {
    LocalDateTime now = LocalDateTime.now();
    return open &&
        ((now.equals(openingDate) || now.equals(closingDate)) ||
            (now.isAfter(openingDate) && now.isBefore(closingDate))) ;
  }//isOpen

}//Expediente