package com.f.thoth.backend.data.gdoc.expediente;

import java.util.Set;
import java.util.TreeSet;

import com.f.thoth.backend.data.entity.util.TextUtil;

/**
 * Representa un nodo del repositorio documental
 */

public class RepoNode implements Comparable<RepoNode>
{
          protected Long              tenant;         // DB id del tenant dueño del repositorio
          protected String            type;           // Node type: {EXPEDIENTE/VOLUME}
          protected Long              id;             // DB id of the expediente
          protected String            code;           // Expediente code (unique visible id)
          protected String            ownerPath;      // Expediente to which this expediente belongs
          protected String            name;           // Expediente name (asunto)
          protected Set<String>       keywords;       // Search keywords

          // --------------- Constructors --------------------
          public RepoNode()
          {
          }

          public RepoNode( Long tenant, String code, String name, String type, String ownerPath, Long id, Set<String>keywords)
          {
            if( tenant == null)
              throw new IllegalArgumentException("Tenant del expediente no puede ser nulo ni vacío");

            if( TextUtil.isEmpty(code))
              throw new IllegalArgumentException("Código del expediente no puede ser nulo ni vacío");

            if( TextUtil.isEmpty(name))
              throw new IllegalArgumentException("Nombre del expediente no puede ser nulo ni vacío");

            if( TextUtil.isEmpty(type))
              throw new IllegalArgumentException("Tipo del nodo del repositorio no puede ser nulo ni vacío");

            if( TextUtil.isEmpty(ownerPath))
              throw new IllegalArgumentException("Path del padre del expediente no puede ser nulo ni vacío");

            if( id == null)
              throw new IllegalArgumentException("Id del expediente no puede ser nula");

            if( keywords == null)
                keywords =  new TreeSet<String>();

            this.tenant     = tenant;
            this.type       = type;
            this.id         = id;
            this.code       = code;
            this.ownerPath  = ownerPath;
            this.name       = name;
            this.keywords   = keywords;

          }//RepoNode constructor

          // ---------------- Getters & Setters --------------

          public Long         getTenant()   { return tenant;}
          public void         setTenant(Long tenant){ this.tenant = tenant; }

          public String       getType()   { return type;}
          public void         setType(String type){ this.type = type; }

          public Long         getId()   { return id;}
          public void         setId(Long id){ this.id = id; }

          public String       getCode()   { return code;}
          public void         setCode(String code){ this.code = code; }

          public String       getOwnerPath()  { return ownerPath;}
          public void         setOwnerPath(String ownerPath) { this.ownerPath = ownerPath; }

          public String       getName()   { return name;}
          public void         setName(String name){ this.name = name; }

          public Set<String>  getKeywords() { return keywords;}
          public void         setKeywords( Set<String> keywords){ this.keywords = keywords;}


          // ---------------------- Object -----------------------

          @Override public boolean equals( Object o)
          {
            if (this == o)
              return true;

            if (!(o instanceof RepoNode ))
              return false;

            RepoNode that = (RepoNode) o;
            return this.id != null && this.id.equals(that.id);

          }//equals

          @Override public int hashCode() { return id == null? 832739: id.hashCode();}

          @Override public String toString()
          {
            StringBuilder s = new StringBuilder();
            s.append("RepoNode{tenant["+ tenant+ "]")
             .append(" type["+ type+ "]")
             .append(" id["+ id+ "]")
             .append(" code["+ code+ "]")
             .append(" name["+ name+ "]")
             .append(" ownerPath["+ ownerPath+ "]")
             .append(" keywords[");

            for(  String keyword: keywords )
              s.append( " "+ keyword);

            s.append("]}\n");
            return s.toString();
          }//toString

          @Override  public int compareTo(RepoNode that)
          {
            return this.equals(that)?  0 :
                   that == null?       1 :
                   this.code.compareTo(that.code);

          }// compareTo

}//RepoNode
