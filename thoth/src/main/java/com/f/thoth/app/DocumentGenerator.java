package com.f.thoth.app;

import java.io.BufferedReader;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.util.Random;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import com.f.thoth.backend.data.entity.util.TextUtil;
import com.f.thoth.backend.data.gdoc.expediente.Nature;
import com.f.thoth.backend.data.security.Tenant;
import com.f.thoth.backend.data.security.User;
import com.f.thoth.backend.jcr.Repo;

public class DocumentGenerator
{
   private Tenant       tenant;
   private User         user;
   private Random       random;
   private static long  filingId = 0L;

   // C:\ahl\des\wk1\docgen\data

   public DocumentGenerator( Tenant tenant, User user)
   {
      this.tenant  = tenant;
      this.user    = user;
      this.random  = new Random(1L);

   }//DocumentGenerator



   public int generateDocs(Node parent, BufferedReader documentAsuntosReader)
         throws RepositoryException, UnknownHostException
   {
       // 1. Obtenga todos los documentos hijos que componen el documento
       int nSubDocs = random.nextInt(2)+ 1;

       // 2. Asigne el radicado que identifica el documento
       String idNumber = generateRadicado();

       // 3. Cree el nodo padre del documento compuesto
       Node   header    = parent.addNode(idNumber);
       String namespace = tenant.getName()+ ":";
       filingId++;
       try
       {
          header.addMixin   ("mix:referenceable");
          header.addMixin   (namespace+ "Document");
          header.setProperty("jcr:nodeTypeName", Nature.DOC_HEADER.toString());
          header.setProperty(namespace+ "tenant",       tenant.getId());
          header.setProperty(namespace+ "filingId",     idNumber);
          header.setProperty(namespace+ "createdBy",    user.getEmail());
          header.setProperty(namespace+ "asunto",       documentAsuntosReader.readLine());
          header.setProperty(namespace+ "creationDate", generateCreationDate());
          header.setProperty(namespace+ "reference",    TextUtil.pad( filingId, 10));
          Repo.getInstance().save();

       } catch (Exception e)
       {  throw new IllegalStateException("No pudo crear header del documento. Raz�n\n"+ e.getMessage());
       }

       // 4. Para todos los documentos hijos
       for (long docInstance = 0; docInstance < nSubDocs; docInstance++)
       {
          // 5.     Cree la instancia del documento item
          Node item = header.addNode(idNumber+ "_"+ docInstance);
          item.setProperty("jcr:nodeTypeName",  Nature.DOC_ITEM.toString());

          // 6.         Cree los metadatos de la instancia item
          item.addMixin   (namespace+ "DocumentInstance");
          item.setProperty(namespace+ "instanceId",  docInstance);

          // 7.         Cree  el contenido documental
          /*
          public static void addFileNode(Session session, String absPath, FileDetail fileDetail)
                throws RepositoryException, IOException
        {
            // FIXME add null check for all incoming parameters
            // FIXME refactor this method to reduce duplicate codes
            Node node = createNodes(session, absPath);
            if (node.hasNode(fileDetail.getFileName()))
            {   System.out.println("File already added.");
                return;
            }

            Node fileHolder = node.addNode(fileDetail.getFileName()); // Created a node with that of file Name
            fileHolder.addMixin("mix:versionable");
            fileHolder.setProperty("jcr:createdBy", fileDetail.getCreatedBy());
            fileHolder.setProperty("jcr:nodeType", NodeType.FILE.getValue());
            fileHolder.setProperty("size", fileDetail.getSize());

            Node file1 = fileHolder.addNode("theFile", "nt:file"); // create node of type file.

            Date now = new Date();
            now.toInstant().toString();

            Node content = file1.addNode("jcr:content", "nt:resource");
            content.setProperty("jcr:mimeType", fileDetail.getContentType());

            Binary binary = session.getValueFactory().createBinary(fileDetail.getFileData());

            content.setProperty("jcr:data", binary);
            content.setProperty("jcr:lastModified", now.toInstant().toString());
            session.save();
            VersionManager vm = session.getWorkspace().getVersionManager();
            vm.checkin(fileHolder.getPath());
            System.out.println("File Saved...");
        }
            */
          
          Repo.getInstance().save();

       }

      return nSubDocs;
   }//generateDocs


   private String generateRadicado()
   {
      filingId++;
      return TextUtil.pad( filingId, 10);
   }//generateRadicado


   private String generateCreationDate()
   {
      LocalDate   now = LocalDate.now();
      int daysElapsed = (int) (random.nextDouble() * (365.0D * 3.0));
      LocalDate createdOn =  now.minusDays(daysElapsed);
      return TextUtil.formatDate( createdOn);
   }//generateCreationDate



}//DocumentGenerator

