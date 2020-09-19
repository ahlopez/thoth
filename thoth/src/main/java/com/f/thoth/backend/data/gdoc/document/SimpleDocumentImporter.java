package com.f.thoth.backend.data.gdoc.document;

import java.time.LocalDateTime;

import com.f.thoth.backend.data.gdoc.metadata.DocType;
import com.f.thoth.backend.data.gdoc.metadata.SchemaValues;

/**
 * Define una familia de Builder de importacion de Documentos Simples
 */
public interface SimpleDocumentImporter
{

   public void    initImport();

   public void    setId( String id);

   public void    setDocType( DocType docType);

   public void    setMetaValues( SchemaValues metaValues);

   public void    setRecord( boolean record);

   public void    setPublicity( Publicity publicity);

   public void    setEndClassification( LocalDateTime endClassification);

   public void    setContent( Content content);

   public void    endImport();

   public boolean requiresContent();

}//SimpleDocumentImporter
