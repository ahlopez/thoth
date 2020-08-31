package com.f.thoth.backend.data.gdoc.document.jackrabbit;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;

import com.f.thoth.backend.data.gdoc.document.CompositeDocument;
import com.f.thoth.backend.data.gdoc.document.CompositeDocumentImporter;
import com.f.thoth.backend.data.gdoc.document.Document;
import com.f.thoth.backend.data.gdoc.document.SimpleDocument;

/**
 * Director del builder de importacion de un documento compuesto
 * desde un repositorio JackRabbit
 */
public class CompositeDocumentFromJackImporterDirector implements CompositeDocument.ImporterDirector
{
   private String  docPath;
   private Session jackSession;
   private CompositeDocumentImporter  compositeDocumentImportBuilder;

   public CompositeDocumentFromJackImporterDirector ( String docPath, Session jackSession)
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
      }catch(Exception e)
      {
         e.printStackTrace();
         throw new IllegalStateException("No pudo verificar la existencia del documento a exportar. Razon\n"+ e.getMessage());
      }
   }//CompositeDocumentFromJackImporterDirector


   public void dirija( CompositeDocumentImporter compositeDocumentImportBuilder)
   {
      if (compositeDocumentImportBuilder == null)
         throw new IllegalArgumentException("Builder de importacion del documento compuesto no puede ser nulo");

      try {
         this.compositeDocumentImportBuilder = compositeDocumentImportBuilder;
         Node node =  jackSession.getNode(docPath);
         loadMetadata ( node);
         loadDocuments( node);
      }catch( Exception e)
      {
         e.printStackTrace();
         throw new IllegalStateException("No pudo importar un documento compuesto. Razon\n"+ e.getMessage());
      }

   }//dirija

   private void loadMetadata( Node node)
   {
      /*
      try {
         MetaValues vals = new MetaValues();
         for (PropertyIterator propertyIter = node.getProperties(); propertyIter.hasNext(); )
         {
            Property p = propertyIter.nextProperty();
            String          propertyName  = p.getName();
            javax.jcr.Value propertyValue = p.getValue();
            Value<?>        value         = null;

            if ("evid:publicity".equals(propertyName)) {
               Publicity publicity = Publicity.valueOf(propertyValue.getString());
               compositeDocumentImportBuilder.setPublicity( publicity);
               value = new ImmutableValue<Publicity>(publicity, Type.ENUM);
            }else if ("evid:radicado".equals(propertyName)) {
               String id = propertyValue.getString();
               compositeDocumentImportBuilder.setId( id);
               value = new ImmutableValue<String>(id,Type.ID);
            }else if ("evid:isrecord".equals(propertyName)) {
               Boolean record = propertyValue.getBoolean();
               compositeDocumentImportBuilder.setRecord( record);
               value = new ImmutableValue<Boolean>(record,Type.BOOLEAN);
            }else if ("evid:endclassification".equals(propertyName)){
               LocalDateTime endClassification = LocalDateTime.parse( propertyValue.getString());
               compositeDocumentImportBuilder.setEndClassification( endClassification);
               value = new ImmutableValue<LocalDateTime>(endClassification, Type.DATETIME);
            }else if ("evid:docType".equals(propertyName)) {
               String typeId = propertyValue.getString();
               DocType docType = ThothSession.getCurrentTenant().getTypeById(typeId);
               compositeDocumentImportBuilder.setDocType( docType);
               value = new ImmutableValue<String>(typeId, Type.STRING);
            }else {
               value = new ImmutableValue<String>( propertyValue.getString(), Type.STRING);
            }


            vals.addValue( propertyName, value);

         }// for propertyIter...

         compositeDocumentImportBuilder.setMetaValues( vals);

      }catch( Exception e)
      {
         e.printStackTrace();
         throw new IllegalStateException("No pudo cargar los metadatos del documento compuesto. Razon\n"+ e.getMessage());
      }
      */

   }//loadMetadata


   private void loadDocuments( Node baseNode)
   {
      try {
         Document     doc      = null;
         NodeIterator nodeIter = baseNode.getNodes();
         while( nodeIter.hasNext())
         {
            Node node = nodeIter.nextNode();
            String  docId    = node.getProperty("evid:docId").getString();
            boolean isSimple = "TRUE".equals(node.getProperty("evid:isSimple").toString());
            if (isSimple)
            {
               SimpleDocument.ImporterDirector simpleDirector = new SimpleDocumentFromJackImporterDirector( docId, jackSession);
               doc = new SimpleDocument( simpleDirector);
            }else
            {
               CompositeDocument.ImporterDirector compositeDirector = new CompositeDocumentFromJackImporterDirector( docId, jackSession);
               doc = new CompositeDocument( compositeDirector);
            }

            compositeDocumentImportBuilder.addDocument( doc);

         }// while nodeIter ...
      }catch( Exception e)
      {
         e.printStackTrace();
         throw new IllegalStateException("No pudo cargar los documentos del documento compuesto. Razon\n"+ e.getMessage());
      }

   }//loadDocuments



}//CompositeDocumentFromJackImporterDirector
