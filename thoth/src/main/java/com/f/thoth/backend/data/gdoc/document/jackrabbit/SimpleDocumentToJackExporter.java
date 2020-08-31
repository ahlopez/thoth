package com.f.thoth.backend.data.gdoc.document.jackrabbit;

import java.time.LocalDateTime;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.version.VersionManager;

import com.f.thoth.backend.data.entity.util.TextUtil;
import com.f.thoth.backend.data.gdoc.document.Content;
import com.f.thoth.backend.data.gdoc.document.Publicity;
import com.f.thoth.backend.data.gdoc.document.SimpleDocument;
import com.f.thoth.backend.data.gdoc.metadata.DocType;
import com.f.thoth.backend.data.gdoc.metadata.SchemaValues;
import com.f.thoth.backend.data.security.ThothSession;
import com.f.thoth.ui.utils.FormattingUtils;


/**
 * Builder de exportación de un documento simple a
 * un nodo del repositorio JackRabbit
 */
public class SimpleDocumentToJackExporter implements SimpleDocument.Exporter
{
   private String  docPath;
   private Session jackSession;
   private Node    node;

   public SimpleDocumentToJackExporter( String docPath, Session jackSession)
   {
      if (docPath == null)
         throw new IllegalArgumentException("Identificador del documento a crear no puede ser nulo");

      if (jackSession == null)
         throw new IllegalArgumentException("Sesión del repositorio no puede ser nula");

      this.docPath       = docPath;
      this.jackSession   = jackSession;

   }//SimpleDocumentToJackExporter

   @Override public void   initExport()
   {
      try {
         if (jackSession.itemExists(docPath))
            throw new IllegalStateException("Documento["+ docPath+ "] a crear ya existe en el repositorio");

         String[] nodeNames = (null != docPath) ? docPath.split("/") : null;
         this.node = createNodes( nodeNames);
      }catch(Exception e)
      {
         e.printStackTrace();
         throw new IllegalStateException("No pudo cread los nodos del documento. Razón\n"+ e.getMessage());
      }

   }//initExport

   @Override public void   exportBasic (String id, DocType docType, boolean isRecord, Publicity  publicity, LocalDateTime endClassification)
   {
      try
      {
         node.setProperty("evid:radicado", id);
         node.setProperty("evid:docType", docType.getCode());
         node.setProperty("evid:isSimple", "TRUE");
         node.setProperty("evid:publicity", publicity.toString());
         node.setProperty("evid:isrecord", isRecord? "TRUE": "FALSE");
         if (endClassification != null)
            node.setProperty("evid:endclassification", endClassification.format(FormattingUtils.FULL_DATE_FORMATTER));
      } catch( Exception e)
      {
         e.printStackTrace();
         throw new IllegalStateException("No pudo guardar la informacion basica del documento simple. Razon\n"+ e.getMessage());
      }

   }//exportBasic

   @Override public void   exportMeta ( SchemaValues values)
   {
      /*
      try {
         while( metaIter.hasNext())
         {
            Map.Entry<String,Value<?>> value = metaIter.next();
            node.setProperty(value.getKey(), value.getValue().toString());
         }
      }catch( Exception e)
      {
         e.printStackTrace();
         throw new IllegalStateException("No pudo guardar las propiedades del documento simple. Razon\n"+ e.getMessage());
      }
      */

   }//exportMeta

   @Override public void   exportContent( Content content)
   {
      try {
         Node fileHolder = node.addNode(content.getName());
         fileHolder.addMixin("mix:versionable");
         fileHolder.setProperty("jcr:createdBy", ThothSession.getCurrentUser().getCode());
         fileHolder.setProperty("jcr:nodeType", "file_node");
         fileHolder.setProperty("size", content.getSize());

         Node file1 = fileHolder.addNode("theFile", "nt:file"); // create node of type file.
         Node contentStream = file1.addNode("jcr:content", "nt:resource");
         contentStream.setProperty("jcr:mimeType", content.getMimeType().getBaseType());
         contentStream.setProperty("jcr:data", content.getContent());
         contentStream.setProperty("jcr:lastModified", LocalDateTime.now().format(FormattingUtils.FULL_DATE_FORMATTER));
         VersionManager vm = jackSession.getWorkspace().getVersionManager();
         vm.checkin(fileHolder.getPath());
      }catch(Exception e)
      {
         e.printStackTrace();
         throw new IllegalStateException("No pudo guardar el contenido documental del documento simple");
      }
   }//exportContent

   @Override public void   endExport()
   {
      try
      {
         jackSession.save();
      }catch(Exception e)
      {
         e.printStackTrace();
         throw new IllegalStateException("No pudo guardar el nodo JackRabbit. Razon\n"+ e.getMessage());
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


}//SimpleDocumentToJackExporter
