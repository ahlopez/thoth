package com.f.thoth.backend.data.gdoc.expediente;

import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.BatchSize;

import com.f.thoth.backend.data.entity.BaseEntity;

/**
 * Representa un indice de expediente
 */
@Entity
@Table(name = "FILE_INDEX", indexes = { @Index(columnList = "code") })
public class FileIndex extends BaseEntity
{  
   @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
   @JoinColumn(name="entry_id")
   @BatchSize(size = 50)
   public Set<IndexEntry> entries;

   public FileIndex()
   {
       entries = new TreeSet<>();
       buildCode();
   }
   
   public void  buildCode() { code = (tenant == null? "[Tenant]": tenant.getCode())+ "[IDX]>"+ (id == null? "---": getId());}

   public Set<IndexEntry>  getEntries(){ return entries;}
   public void             setEntries(Set<IndexEntry> entries){ this.entries = entries;}
   
   public int size() { return entries.size();}

}//FileIndex