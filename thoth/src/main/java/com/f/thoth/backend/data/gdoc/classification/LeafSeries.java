package com.f.thoth.backend.data.gdoc.classification;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.NamedSubgraph;
import javax.persistence.Table;

import com.f.thoth.backend.data.gdoc.metadata.Schema;
import com.f.thoth.backend.data.security.ObjectToProtect;

/**
 * Representa una hoja de la estructura jer�rquica de series documentales
 */
@NamedEntityGraphs({
   @NamedEntityGraph(
         name = LeafSeries.BRIEF,
         attributeNodes = {
            @NamedAttributeNode("tenant"),
            @NamedAttributeNode("code"),
            @NamedAttributeNode("name"),
            @NamedAttributeNode("owner"),
            @NamedAttributeNode("schema"),
            @NamedAttributeNode("dateOpened"),
            @NamedAttributeNode("dateClosed"),
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
         name = LeafSeries.FULL,
         attributeNodes = {
               @NamedAttributeNode("tenant"),
               @NamedAttributeNode("code"),
               @NamedAttributeNode("name"),
               @NamedAttributeNode("owner"),
               @NamedAttributeNode("schema"),
               @NamedAttributeNode("dateOpened"),
               @NamedAttributeNode("dateClosed"),
               //   @NamedAttributeNode("retentionSchedule"),
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
@Table(name = "LEAF_SERIES")
public class LeafSeries extends Series
{
   public static final String BRIEF = "LeafSeries.brief";
   public static final String FULL  = "LeafSeries.full";

   // ------------- Constructors ------------------
   public LeafSeries()
   {
      super();
   }

   public LeafSeries( String name, Schema schema, BranchSeries owner, LocalDate dateOpened, LocalDate dateClosed) //, RetentionSchedule retentionSchedule)
   {
      super(  name, schema, owner, dateOpened, dateClosed); // , retentionSchedule
   }//LeafSeries

   // --------------- Object methods ---------------------
   @Override  public String toString() { return " {LeafSeries "+ super.toString()+ "}"; }


}//LeafSeries