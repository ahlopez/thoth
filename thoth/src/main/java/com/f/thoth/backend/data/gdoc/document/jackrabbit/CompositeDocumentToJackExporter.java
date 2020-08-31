package com.f.thoth.backend.data.gdoc.document.jackrabbit;

import java.time.LocalDateTime;
import java.util.Iterator;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.f.thoth.backend.data.entity.util.TextUtil;
import com.f.thoth.backend.data.gdoc.document.CompositeDocument;
import com.f.thoth.backend.data.gdoc.document.Document;
import com.f.thoth.backend.data.gdoc.document.Publicity;
import com.f.thoth.backend.data.gdoc.document.SimpleDocument;
import com.f.thoth.backend.data.gdoc.metadata.DocType;
import com.f.thoth.backend.data.gdoc.metadata.SchemaValues;
import com.f.thoth.ui.utils.FormattingUtils;


/**
 * Builder de exportación de un documento compuesto a
 * un nodo del repositorio JackRabbit
 */
public class CompositeDocumentToJackExporter implements CompositeDocument.Exporter
{
   private String  docPath;
   private Session jackSession;
   private Node    node;

   public CompositeDocumentToJackExporter( String docPath, Session jackSession)
   {
      if (docPath == null)
         throw new IllegalArgumentException("Identificador del documento a crear no puede ser nulo");

      if (jackSession == null)
         throw new IllegalArgumentException("Sesión del repositorio no puede ser nula");

      this.docPath       = docPath;
      this.jackSession   = jackSession;

   }//CompositeDocumentToJackExporter

   @Override public void   initExport()
   {
      try {
         if (jackSession.itemExists(docPath))
            throw new IllegalStateException("Documento["+ docPath+ "] a crear ya existe en el repositorio");

         String[] nodeNames = (null != docPath) ? docPath.split("/") : null;
         this.node = createNodes( nodeNames);
      }catch( Exception e)
      {
         e.printStackTrace();
         throw new IllegalStateException("No pudo verificar la existencia del nodo. Razon\n"+ e.getMessage());
      }

   }//initExport

   @Override public void   exportBasic (String id, DocType docType, boolean isRecord, Publicity  publicity, LocalDateTime endClassification)
   {
      try {
         node.setProperty("evid:radicado",  id);
         node.setProperty("evid:docType",   docType.getCode());
         node.setProperty("evid.isSimple",  "FALSE");
         node.setProperty("evid:publicity", publicity.toString());
         node.setProperty("evid:isrecord",  isRecord? "TRUE": "FALSE");
         if (endClassification != null)
            node.setProperty("evid:endclassification", endClassification.format(FormattingUtils.FULL_DATE_FORMATTER));
      }catch( Exception e)
      {
         e.printStackTrace();
         throw new IllegalStateException("No pudo guardar las propiedades del documento compuesto. Razon\n"+ e.getMessage());
      }

   }//exportBasic

   @Override public void   exportMeta ( SchemaValues metaValues)
   {
      /*
      try {
         while( valueIter.hasNext())
         {
            Map.Entry<String,Value<?>> val = valueIter.next();
            node.setProperty(val.getKey(), val.getValue().toString());
         }
         jackSession.save();
      }catch(Exception e)
      {
         e.printStackTrace();
         throw new IllegalStateException("No pudo guardar las propiedades del documento compuesto. Razon\n"+ e.getMessage());
      }
      */

   }//exportMeta

   @Override public void   exportDocuments( Iterator<Document> docIterator)
   {
      try {
         while ( docIterator.hasNext() )
         {
            Document doc = docIterator.next();
            if ( doc.isSimple())
            {
               SimpleDocument simpleDoc = (SimpleDocument)doc;
               SimpleDocument.Exporter simpleExporter = new SimpleDocumentToJackExporter( docPath + "/"+ simpleDoc.getId(), jackSession);
               simpleDoc.export( simpleExporter);
            }else
            {
               CompositeDocument compositeDoc = (CompositeDocument)doc;
               CompositeDocument.Exporter compositeExporter = new CompositeDocumentToJackExporter( docPath + "/"+ compositeDoc.getId(), jackSession);
               compositeDoc.export( compositeExporter);
            }
            jackSession.save();
         }
      }catch(Exception e)
      {
         e.printStackTrace();
         throw new IllegalStateException("No pudo guardar los documentos del documento compuesto. Razon\n"+ e.getMessage());
      }
   }//exportDocuments

   @Override public void   endExport()
   {
      try {
         jackSession.save();
      }catch( Exception e)
      {
         e.printStackTrace();
         throw new IllegalStateException("No pudo guardar los documentos del documento compuesto. Razon\n"+ e.getMessage());
      }
   }

   @Override public Object getProduct()
   {
      return docPath;
   }


   private Node createNodes(String[] nodeNames) throws RepositoryException
   {
      Node parentNode = jackSession.getRootNode();
      for (String childNode : nodeNames)
      {
         if ( TextUtil.isNotEmpty(childNode))
         {
            if ( !parentNode.hasNode(childNode))
               parentNode.addNode(childNode);

            parentNode = parentNode.getNode(childNode);
            parentNode.setProperty("jcr:nodeType", "folder_node");
         }
      }
      return parentNode;

   }//createNodes


}//CompositeDocumentToJackExporter
