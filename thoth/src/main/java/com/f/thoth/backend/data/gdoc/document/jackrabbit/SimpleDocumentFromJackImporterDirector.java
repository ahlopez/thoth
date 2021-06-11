package com.f.thoth.backend.data.gdoc.document.jackrabbit;

import javax.jcr.Node;
import javax.jcr.Session;

import com.f.thoth.backend.data.gdoc.document.Content;
import com.f.thoth.backend.data.gdoc.document.SimpleDocument;
import com.f.thoth.backend.data.gdoc.document.SimpleDocumentImporter;

/**
 * Director del builder de importacion de un documento simple
 * desde un repositorio JackRabbit
 */
public class SimpleDocumentFromJackImporterDirector implements SimpleDocument.ImporterDirector
{
   private String  docPath;
   private Session jackSession;
   private SimpleDocumentImporter simpleDocumentImportBuilder;


   public SimpleDocumentFromJackImporterDirector ( String docPath, Session jackSession)
   {
      try {
         if (docPath == null)
            throw new IllegalArgumentException("Identificador del documento a cargar no puede ser nulo");

         if (jackSession == null)
            throw new IllegalArgumentException("Sesión del repositorio no puede ser nula");

         if ( ! jackSession.nodeExists(docPath))
            throw new IllegalStateException("Documento["+ docPath+ "] no existe en el repositorio");

         this.docPath       = docPath;
         this.jackSession = jackSession;
      }catch( Exception e)
      {
         e.printStackTrace();
         throw new IllegalStateException("No pudo instanciar el director de importación de documentos simples. Razon\n"+ e.getMessage());
      }
   }//SimpleDocumentFromJackImporterDirector


   public void dirija( SimpleDocumentImporter simpleDocumentImportBuilder)
   {
      if ( simpleDocumentImportBuilder == null)
         throw new IllegalArgumentException("Builder de importacion del documento simple no puede ser nulo");

      this.simpleDocumentImportBuilder = simpleDocumentImportBuilder;
      Node  node = null;
      try {
         node = jackSession.getNode(docPath);
      }catch(Exception e)
      {
         e.printStackTrace();
         throw new IllegalArgumentException("No pudo encontrar nodo del documento simple. Razon\n"+ e.getMessage());
      }
      loadMetadata( node);
      loadContents( node);

   }//dirija

   private void loadMetadata(Node node)
   {
      /*
      try {
         Tenant tenant = (Tenant)VaadinSession.getCurrent().getAttribute("TENANT");
         MetaValues vals = new MetaValues();
         for (PropertyIterator propertyIter = node.getProperties(); propertyIter.hasNext(); )
         {
            Property p = propertyIter.nextProperty();
            String          propertyName  = p.getName();
            javax.jcr.Value propertyValue = p.getValue();
            Value<?>        value         = null;

            if ("evid:publicity".equals(propertyName)) {
               Publicity publicity = Publicity.valueOf(propertyValue.getString());
               simpleDocumentImportBuilder.setPublicity( publicity);
               value = new ImmutableValue<Publicity>(publicity, Type.ENUM);
            }else if ("evid:radicado".equals(propertyName)) {
               String id = propertyValue.getString();
               simpleDocumentImportBuilder.setId( id);
               value = new ImmutableValue<String>(id,Type.ID);
            }else if ("evid:isrecord".equals(propertyName)) {
               Boolean record = propertyValue.getBoolean();
               simpleDocumentImportBuilder.setRecord( record);
               value = new ImmutableValue<Boolean>(record,Type.BOOLEAN);
            }else if ("evid:endclassification".equals(propertyName)){
               LocalDateTime endClassification = LocalDateTime.parse( propertyValue.getString());
               simpleDocumentImportBuilder.setEndClassification( endClassification);
               value = new ImmutableValue<LocalDateTime>(endClassification, Type.DATETIME);
            }else if ("evid:docType".equals(propertyName)) {
               String typeId = propertyValue.getString();
               DocType docType = tenant.getTypeById(typeId);
               simpleDocumentImportBuilder.setDocType( docType);
               value = new ImmutableValue<String>(typeId, Type.STRING);
            }else {
               value = new ImmutableValue<String>( propertyValue.getString(), Type.STRING);
            }

            vals.addValue( propertyName, value);

         }// for propertyIter...

         simpleDocumentImportBuilder.setMetaValues( vals);

      }catch( Exception e)
      {
         e.printStackTrace();
         throw new IllegalStateException("No pudo cargar los metadatos del documento simple. Razon\n");
      }
      */
   }//loadMetadata


   private void loadContents(Node node)
   {
      try {
         if (simpleDocumentImportBuilder.requiresContent())
         {
            Content content = new Content(node.getNode("jcr:content"));
            simpleDocumentImportBuilder.setContent( content);
         }
      }catch(Exception e)
      {
         e.printStackTrace();
         throw new IllegalStateException("No pudo cargar contenido documental del documento simple");
      }

   }//loadContents



}//SimpleDocumentFromJackImporterDirector
