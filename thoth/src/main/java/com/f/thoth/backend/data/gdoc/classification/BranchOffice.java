package com.f.thoth.backend.data.gdoc.classification;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.NamedSubgraph;
import javax.persistence.Table;

import com.f.thoth.backend.data.gdoc.metadata.Schema;
import com.f.thoth.backend.data.security.ObjectToProtect;

/**
 * Representa una rama de la estructura jer�rquica de oficinas productoras
 */
@NamedEntityGraphs({
   @NamedEntityGraph(
         name = BranchOffice.BRIEF,
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
         name = BranchOffice.FULL,
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
@Table(name = "BRANCH_OFFICE", indexes = { @Index(columnList = "code") })
public class BranchOffice extends Office
{
   public static final String BRIEF = "BranchOffice.brief";
   public static final String FULL  = "BranchOffice.full";

   // ------------- Constructors ------------------
   public BranchOffice()
   {
      super();
   }//BranchOffice


   public BranchOffice( String name, Schema schema, BranchOffice owner, LocalDate dateOpened, LocalDate dateClosed) //, RetentionSchedule retentionSchedule)
   {
      super(  name, schema, owner, dateOpened, dateClosed); // , retentionSchedule

   }//BranchOffice

   // -------------- Getters & Setters ----------------

   // --------------- Object methods ---------------------

   @Override  public String toString()
   {
      StringBuilder s = new StringBuilder();
      s.append(" {BranchOffice "+ super.toString()) 
       .append("}\n");
      return s.toString();
   }//toString

   // ----------------   Logic ---------------------

}//BranchOffice