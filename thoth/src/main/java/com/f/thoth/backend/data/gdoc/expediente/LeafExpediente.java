package com.f.thoth.backend.data.gdoc.expediente;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.NamedSubgraph;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import com.f.thoth.backend.data.gdoc.classification.Classification;
import com.f.thoth.backend.data.gdoc.metadata.DocumentType;
import com.f.thoth.backend.data.gdoc.metadata.SchemaValues;
import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.data.security.SingleUser;

/**
 * Representa un nodo de la jerarquia de expedientes (expediente/sub-expediente/volumen
 */
@NamedEntityGraphs({
     @NamedEntityGraph(
         name = Expediente.BRIEF,
         attributeNodes = {
            @NamedAttributeNode("tenant"),
            @NamedAttributeNode("code"),           // DB human id. Includes [tenant, type, path+]
            @NamedAttributeNode("expedienteCode"), // Business id (vg. dependencia-serie-subserie-secuencial)
            @NamedAttributeNode("name"),
            @NamedAttributeNode("path"),
            @NamedAttributeNode("classificationClass"),
            @NamedAttributeNode("owner"),
            @NamedAttributeNode("open"),
            @NamedAttributeNode("admissibleTypes"),
            @NamedAttributeNode("currentDocNumber"),
            @NamedAttributeNode(value="objectToProtect", subgraph = ObjectToProtect.BRIEF)
         },
         subgraphs = @NamedSubgraph(name = ObjectToProtect.BRIEF,
            attributeNodes = {
               @NamedAttributeNode("category"),
               @NamedAttributeNode("userOwner"),
               @NamedAttributeNode("roleOwner"),
               @NamedAttributeNode("restrictedTo")
            })
         ),
     @NamedEntityGraph(
         name = Expediente.FULL,
         attributeNodes = {
            @NamedAttributeNode(value="objectToProtect", subgraph = ObjectToProtect.BRIEF),
            @NamedAttributeNode("tenant"),
            @NamedAttributeNode("code"),
            @NamedAttributeNode("expedienteCode"),
            @NamedAttributeNode("name"),
            @NamedAttributeNode("classificationClass"),
            @NamedAttributeNode("createdBy"),
            @NamedAttributeNode("owner"),
            @NamedAttributeNode("path"),
            @NamedAttributeNode("dateOpened"),
            @NamedAttributeNode("dateClosed"),
            @NamedAttributeNode("metadata"),
            @NamedAttributeNode("open"),
            @NamedAttributeNode("admissibleTypes"),
            @NamedAttributeNode("keywords"),
            @NamedAttributeNode("entries"),
            @NamedAttributeNode("mac"),
            @NamedAttributeNode(value="objectToProtect", subgraph = ObjectToProtect.FULL)
         },
         subgraphs = @NamedSubgraph(name = ObjectToProtect.FULL,
            attributeNodes = {
               @NamedAttributeNode("category"),
               @NamedAttributeNode("userOwner"),
               @NamedAttributeNode("roleOwner"),
               @NamedAttributeNode("restrictedTo"),
               @NamedAttributeNode("acl")
            })
         )
    })

@Entity
@Table(name = "LEAF_EXPEDIENTE", indexes = { @Index(columnList = "code"), @Index(columnList = "tenant,expedienteCode"), @Index(columnList= "tenant,keywords")})
public class LeafExpediente extends AbstractExpediente
{
   public static final String BRIEF = "LeafExpediente.brief";
   public static final String FULL  = "LeafExpediente.full";

   @NotNull  (message = "{evidentia.volume.required}")
   protected Boolean           volume;                     // Is the expediente a volume?

   @PositiveOrZero
   @NotNull(message = "{evidentia.documentNumber.required}")
   protected Integer           currentDocNumber;           // Number of current document created in this expediente

   @OneToMany
   @NotNull  (message = "{evidentia.types.required}")
   protected Set<DocumentType> admissibleTypes;            // Admisible document types that can be included in the expediente
                                                           //
   protected String            location;                   // Physical archive location (topographic signature)

   // ------------- Constructors ------------------
   public LeafExpediente()
   {
      super();
      this.volume               = false;
      this.currentDocNumber     = 0;
      this.admissibleTypes      = new TreeSet<>();
      this.location             = "";
   }//LeafExpediente null constructor


   // ------------- LeafExpediente ------------------




   public LeafExpediente( String expedienteCode, String path, String name, SingleUser createdBy, Classification classificationClass,
                      SchemaValues metadata, LocalDateTime dateOpened, LocalDateTime dateClosed, Expediente owner,
                      Set<IndexEntry> entries, Boolean open, Set<String> keywords, String mac,
                      Boolean volume, Integer currentDocNumber, Set<DocumentType>admissibleTypes, String location)
   {
      super( expedienteCode, path, name, createdBy,  classificationClass, metadata, dateOpened, dateClosed,
             owner, open, entries, admissibleTypes, keywords, location, mac);

      this.volume           = (volume           == null? false: volume);
      this.currentDocNumber = (currentDocNumber == null? 0  : currentDocNumber);
      this.admissibleTypes  = (admissibleTypes  == null? new TreeSet<>(): admissibleTypes);
      this.location         = (location         == null? "" : location);
   }//LeafExpediente constructor


   @PrePersist
   @PreUpdate
   public void prepareData()
   {
      super.prepareData();
   }//prepareData


   // -------------- Getters & Setters ----------------

   public Boolean           isVolume()  { return volume;}
   public Boolean           getVolume() { return volume;}
   public void              setVolume(Boolean volume) { this.volume = volume;}

   public Integer           getCurrentDocNumber() {  return currentDocNumber;}
   public void              setCurrentDocNumber(Integer currentDocNumber) { this.currentDocNumber = currentDocNumber;}

   public Set<DocumentType> getAdmissibleTypes() {  return admissibleTypes;}
   public void              setAdmissibleTypes(Set<DocumentType> admissibleTypes) { this.admissibleTypes = admissibleTypes;}

   public String            getLocation() { return location;}
   public void              setLocation(String location) { this.location = location;}

   // --------------- Object methods ---------------------

   @Override public boolean equals( Object o)
   {
      if (this == o)
         return true;

      if (!(o instanceof LeafExpediente ))
         return false;

      LeafExpediente that = (LeafExpediente) o;
      return this.id != null && this.id.equals(that.id);

   }//equals

   @Override public int hashCode() { return id == null? 47027: id.hashCode();}

   @Override public String toString()
   {
      StringBuilder s = new StringBuilder();
      s.append( "LeafExpediente{")
       .append( super.toString())
       .append( " isVolume["+ volume+ "]")
       .append( " currentDocNumber["+ currentDocNumber+ "]")
       .append( " location["+ location+ "]\nAdmissibleTypes[\n");

      for ( DocumentType docType: admissibleTypes )
         s.append( " "+ docType.getName());

      s.append("]\n     }\n");

      return s.toString();
   }//toString

   // --------------- Logic ------------------------------
   public boolean isSubExpediente() { return !isVolume();}

   public void    nextVolume()
   {
      /*
        Verifique isVolume();
        Cree el leafExpediente, con volume=true                      ;
        Cierre este leafExpediente                                   ;
        Adicione al padre el nuevo leafExpediente                                                             ;
        Abra el nuevo leafExpediente                                                                                                  ;
      */
   }//nextVolume


}//LeafExpediente
