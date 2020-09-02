package com.f.thoth.backend.data.gdoc.document;

/**
 * Representa un documento
 */
public interface Document extends Comparable<Document>
{
   public int     compareTo(Document other);

   public boolean isSimple();

   public boolean isComposite();

   public boolean isReserved();

   public boolean isPublic();

   public boolean isClassified();

   public boolean isRecord();

}//Document