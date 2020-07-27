package com.f.thoth.backend.data.gdoc.expediente;

import java.util.Set;
import java.util.TreeSet;

/**
 * Representa un indice de expediente
 */
public class FileIndex
{
   public Expediente      expediente;
   public Set<IndexEntry> entries;

   public FileIndex()
   {
       entries = new TreeSet<>();
   }

   public Set<IndexEntry>  getEntries(){ return entries;}


   public int size() { return entries.size();}

}//FileIndex