package com.f.thoth.app;

import java.io.BufferedReader;
import java.io.File;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.util.Random;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.f.thoth.backend.data.entity.util.TextUtil;
import com.f.thoth.backend.data.gdoc.document.jackrabbit.NodeType;
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

       try
       {
          header.addMixin   ("mix:referenceable");
      //    header.addMixin   (namespace+ "Document");
          /* Pendiente:  El tipo de nodo debe estar en el primary_types.
           * El nombre del nodo, no  lo sé. Por ahora, se está usando el definido en NodeType.
           */
          header.setProperty("jcr:nodeType",            namespace+ "basic_document");
          header.setProperty("jcr:nodeTypeName",        namespace+ NodeType.DOCUMENT_INSTANCE.getCode());
          header.setProperty(namespace+ "tenant",       tenant.getId());
          header.setProperty(namespace+ "filingId",     idNumber);
          header.setProperty(namespace+ "createdBy",    user.getEmail());
          header.setProperty(namespace+ "asunto",       documentAsuntosReader.readLine());
          header.setProperty(namespace+ "creationDate", generateCreationDate());
          header.setProperty(namespace+ "reference",    generateReference());
          Repo.getInstance().save();

       } catch (Exception e)
       {  throw new IllegalStateException("No pudo crear header del documento. Razón\n"+ e.getMessage());
       }

       // 4. Para todos los documentos hijos
       for (long docInstance = 0; docInstance < nSubDocs; docInstance++)
       {
          try
          {
             // 5.     Cree la instancia del documento item
             Node item = header.addNode(idNumber+ "_"+ docInstance);
             item.setProperty("jcr:nodeType",      Nature.DOC_ITEM.toString());
             item.setProperty("jcr:nodeTypeName",  Nature.DOC_ITEM.toString());
             item.setProperty("jcr:createdBy",     user.getEmail());

             // 6.         Cree los metadatos de la instancia item
             item.addMixin   (namespace+ "DocumentInstance");
             item.setProperty(namespace+ "instanceId",  docInstance);

             // 7.         Cree  el contenido documental
             File contentFile  = generateContent();
             Repo.getInstance().setContent(item, contentFile);
          } catch( Exception e)
          {
             throw new IllegalStateException("No pudo crear contenido documental, instancia["+ docInstance+ "] "+
                                             "archivo["+ filingId+ "]. Razón\n"+ e.getMessage());
          }
          Repo.getInstance().save();
       }//for ( docInstance ...

      return nSubDocs;
   }//generateDocs


   private String generateRadicado()
   {
      filingId++;
      return TextUtil.pad( filingId, 10);
   }//generateRadicado

   private String generateReference()
   {
      return filingId == 0?  "" : TextUtil.pad(filingId-1, 10);
   }//generateReference


   private String generateCreationDate()
   {
      LocalDate   now = LocalDate.now();
      int daysElapsed = (int) (random.nextDouble() * (365.0D * 3.0));
      LocalDate createdOn =  now.minusDays(daysElapsed);
      return TextUtil.formatDate( createdOn);
   }//generateCreationDate


   private File  generateContent()
   {
      File  contentFile = null;
      String fileName = "data/file"+ filingId+ ".pdf";
      try
      {
         Resource resource = new ClassPathResource(fileName);
         contentFile       = resource.getFile();
      }catch( Exception e)
      {  throw new IllegalStateException("No pudo abrir archivo ["+ fileName+ "]. Causa\n"+ e.getMessage());
      }
      return contentFile;

   }//generateContent



}//DocumentGenerator

