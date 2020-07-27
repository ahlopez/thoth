package com.f.thoth.backend.data.gdoc.document;

import java.time.LocalDateTime;

import com.f.thoth.backend.data.gdoc.metadata.DocType;


/**
 * Define una familia de Builder de importacion de Documentos Compuestos
 */
public interface CompositeDocumentImporter
{

   public void    initImport();

   public void    setId( String id);

   public void    setDocType( DocType docType);

   public void    setMetaValues( MetaValues metaValues);

   public void    setRecord( boolean record);

   public void    setPublicity( Publicity publicity);

   public void    setEndClassification( LocalDateTime endClassification);

   public void    addDocument( Document document);

   public void    endImport();

}//CompositeDocumentImporter
