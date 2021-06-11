package com.f.thoth.backend.data.gdoc.document;

import javax.activation.MimeType;
import javax.jcr.Binary;
import javax.jcr.Node;

/**
 * Content stream de un documento
 */
public class Content
{
   private MimeType  mimeType;
   private long      size;
   private Binary    content;
   private String    name;


   // ------------- Constructors ------------------
   public Content()
   {
   }

   public Content( Node fileContent)
   {
      if (fileContent == null)
         throw new IllegalArgumentException("Contenido documental no puede ser nulo");

      try
      {
         this.mimeType = new MimeType(fileContent.getProperty("jcr:mimeType").getString());
         this.content  = fileContent.getProperty("jcr:data").getBinary();
         this.size     = fileContent.getProperty("jcr:data").getLength();
      } catch (Exception e)
      {
         e.printStackTrace();
         throw new IllegalStateException("No pudo construir el contenido con base en la información del nodo. Razon\n"+ e.getMessage());
      }

   }//Content

   // -------------- Getters & Setters ----------------

   public  MimeType     getMimeType(){ return mimeType; }
   public  void         setMimetype( MimeType mimeType) { this.mimeType = mimeType;}

   public  String       getName()    { return name;     }
   public  void         setName( String name){ this.name = name;}

   public  long         getSize()    { return size;     }
   public  void         setSize( long size){ this.size = size;}

   public  Binary       getContent() { return content;  }
   public  void         setContent(Binary content) { this.content = content;}


}//Content
