package com.f.thoth.backend.data.gdoc.document;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.f.thoth.backend.data.entity.util.TextUtil;
import com.f.thoth.backend.data.gdoc.metadata.DocType;
import com.f.thoth.backend.data.gdoc.metadata.Metadata;
import com.f.thoth.backend.data.gdoc.metadata.Value;
import com.f.thoth.ui.utils.FormattingUtils;

/**
 * Representa un documento compuesto
 */
public class CompositeDocument implements Document, CompositeDocumentImporter
{
   @NotBlank(message = "{evidentia.name.required}")
   @NotNull (message = "{evidentia.name.required}")
   private String        id;

   @NotNull (message = "{evidentia.type.required}")
   private DocType       docType;

   @NotNull (message = "{evidentia.metadata.required}")
   private MetaValues    metaValues;

   private boolean       record;
   private Publicity     publicity;
   private LocalDateTime endClassification;

   @NotNull (message = "{evidentia.documents.required}")
   private Set<Document> documents;

   // ------------- Constructors ------------------

   public CompositeDocument()
   {
      documents = new TreeSet<>();
   }


   public CompositeDocument( String id, DocType docType, Publicity publicity, boolean record, LocalDateTime endClassification, Set<Document> documents)
   {
      if (id == null)
         throw new IllegalArgumentException("Identificador del documento no puede ser nulo");

      if ( docType == null)
         throw new IllegalArgumentException( "Tipo documental no puede ser nulo");

      if ( (publicity == Publicity.RESERVED || publicity == Publicity.CLASSIFIED) && endClassification == null )
         throw new IllegalArgumentException("Documentos reservados o clasificados no pueden tener fecha de fin de clasificación nula");

      if(  documents == null )
         throw new IllegalArgumentException( "Documentos de un documento compuesto no pueden ser nulos");

      this.id                = id;
      this.docType           = docType;
      this.publicity         = publicity;
      this.record            = record;
      this.endClassification = endClassification;
      this.documents         = documents;


      // TODO: Persistir el documento en el repositorio. Ojo con el id

   }//CompositeDocument


   public CompositeDocument ( CompositeDocument.ImporterDirector importerDirector)
   {
      importerDirector.dirija( this);
      setPublicity(this.publicity);

      String ok = isValid();
      if ( ok != null)
         throw new IllegalStateException("Composite Document es inválido. Razón"+ ok);

   }//CompositeDocument


   // -------------- Getters & Setters ----------------

   public String          getId() { return id;}
   @Override public void  setId( String id) { this.id = id;}

   public DocType         getDocType() { return docType;}
   @Override public void  setDocType( DocType docType) { this.docType = docType;}

   public MetaValues     getMetaValues() { return metaValues;}
   @Override public void  setMetaValues( MetaValues metaValues) { this.metaValues = metaValues;}

   public boolean         getRecord() { return record;}
   @Override public void  setRecord( boolean record) { this.record = record;}

   public Publicity       getPublicity() { return publicity; }
   @Override public void  setPublicity( Publicity publicity)  { this.publicity = publicity;}

   public LocalDateTime   getEndClassification() { return endClassification;}
   @Override public void  setEndClassification( LocalDateTime endClassification) { this.endClassification = endClassification;}

   public Set<Document>   getDocuments() { return documents;}
   public void            setDocuments( Set<Document> documents) { this.documents = documents;}

   // --------------- Object methods ---------------------

   @Override
   public boolean equals(Object o)
   {
      if (this == o)
         return true;

      if (o == null || getClass() != o.getClass())
         return false;

      if (!super.equals(o))
         return false;

      CompositeDocument that = (CompositeDocument) o;

      return  this.id.equals(that.id);


   }// equals

   @Override
   public int hashCode()
   {
      return id.hashCode();
   }

   @Override
   public String toString()
   {
      StringBuilder s = new StringBuilder();
      s.append( " CompositeDocument{").
      append( " id["+ id+ "]").
      append( " docType["+ docType.getName()+ "]").
      append( " metadatos["+ metaValues.toString()+ "]").
      append( " isRecord["+ record+ "]").
      append( " publicity["+ publicity+ "]").
      append( " endClassification["+ endClassification == null? "" : endClassification.format( FormattingUtils.FULL_DATE_FORMATTER)+ "]").
      append( " n documents["+ documents.size()+ "]}");

      return s.toString();
   }//toString

   public int compareTo(Document other)
   {
      if ( ! (other instanceof CompositeDocument))
         return 1;

      return this.id.compareTo(((CompositeDocument)other).id);
   }//compareTo


   // --------------- Import / Export ------------------------------
   public interface Exporter
   {
      public void   initExport();

      public void   exportBasic (String id, DocType docType, boolean isRecord, Publicity publicity, LocalDateTime endClassification);

      public void   exportMeta ( Iterator<Map.Entry<String,Value<?>>> propertyIter);

      public void   exportDocuments( Iterator<Document> docs);

      public void   endExport();

      public Object getProduct();

   }//Exporter


   public Object export( CompositeDocument.Exporter exporter)
   {
      exporter.initExport();
      exporter.exportBasic( id, docType, record, publicity, endClassification);
      exporter.exportMeta( metaValues.iterator());
      exporter.exportDocuments( docIterator());
      exporter.endExport();

      return exporter.getProduct();

   }//export


   public interface ImporterDirector
   {
      public void dirija( CompositeDocumentImporter compositeDocumentImportBuilder);
   }//ImporterDirector

   @Override public void initImport() { }

   @Override public void endImport(){}

   // ------------------- Consistency -------------------------

   public String isValid()
   {
      StringBuilder s = new StringBuilder();

      if (TextUtil.isEmpty(id))
         s.append("Identificador del documento no puede ser nulo ni vacío");

      if( docType == null)
         s.append("Tipo documental del documento no puede ser nulo");

      if( metaValues == null)
         s.append("Metadatos del documento no pueden ser nulos");

      if( publicity == null)
         s.append("Publicidad del documento no puede ser nula");

      if ( isClassified() && endClassification ==  null)
         s.append("Documentos clasificados deben tener una fecha de fin de clasificación no nula");

      if ( documents.size() == 0)
         s.append("Un documento compuesto debe contener documentos hijos");

      return s.toString();

   }//isValid


   // --------------- Logic ------------------------------

   @Override public boolean isSimple(){ return false;}

   @Override public boolean isComposite(){ return ! isSimple();}

   @Override public boolean isPublic(){ return ! isReserved() && ! isClassified();}

   @Override public boolean isReserved()
   {
      LocalDateTime now = LocalDateTime.now();
      return publicity == Publicity.RESERVED && endClassification != null && now.isBefore(endClassification);
   }//isReserved

   @Override public boolean isClassified()
   {
      LocalDateTime now = LocalDateTime.now();
      return publicity == Publicity.CLASSIFIED && endClassification != null && now.isBefore(endClassification);
   }//isClassified

   @Override public boolean isRecord()    { return record;}

   @Override public Iterator<Metadata> metaIterator(){ return docType.iterator(); }

   @Override public void addDocument( Document document) { this.documents.add( document);}

   public Iterator<Document> docIterator() { return documents.iterator();}

}//CompositeDocument