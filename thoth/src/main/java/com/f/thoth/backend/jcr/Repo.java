package com.f.thoth.backend.jcr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.jackrabbit.commons.cnd.CndImporter;
import org.apache.jackrabbit.oak.Oak;
import org.apache.jackrabbit.oak.jcr.Jcr;
import org.apache.jackrabbit.oak.plugins.document.DocumentNodeStore;
import org.apache.jackrabbit.oak.plugins.document.mongo.MongoDocumentNodeStoreBuilder;
import org.slf4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.f.thoth.Parm;
import com.f.thoth.app.HasLogger;
import com.f.thoth.backend.data.entity.util.TextUtil;
import com.f.thoth.backend.data.gdoc.metadata.Field;
import com.f.thoth.backend.data.gdoc.metadata.Property;
import com.f.thoth.backend.data.gdoc.metadata.Schema;
import com.f.thoth.backend.data.gdoc.metadata.SchemaValues;
import com.f.thoth.backend.data.gdoc.metadata.jcr.SchemaValuesToPropertiesExporter;

public class Repo implements HasLogger
{
   private static Repo        INSTANCE = null;
   private static Session   jcrSession;
   private static Logger        logger;

   /**
    * Obtiene una instancia del Administrador del Repositorio
    * @return Repo La instancia solicitada del Administrador del Repositorio
    */
   public static synchronized Repo getInstance()
         throws RepositoryException, UnknownHostException
   {
      try
      {
         if ( INSTANCE == null)
         {  INSTANCE = Parm.IN_MEMORY_JCR_REPO
                     ? new Repo()
                     : new Repo(Parm.DEFAULT_REPO_HOST, Parm.DEFAULT_REPO_PORT, Parm.DEFAULT_REPO_NAME);
         }
      } catch(Exception e)
      { throw new IllegalStateException("No pudo inicializar Repositorio Documental. Razón\n"+ e.getMessage());
      }
      return INSTANCE;

   }//getInstance


   private Repo() throws RepositoryException
   {
      if (jcrSession != null)
      {  throw new IllegalStateException("Repositorio JCR ya ha sido inicializado");
      }
      logger          = getLogger();
      Repository repo = initJCRRepo();
      jcrSession      = loginToRepo(repo, Parm.DEFAULT_ADMIN_LOGIN, Parm.DEFAULT_ADMIN_PASSWORD);

   }//Repo

   private Repo(String host, final int port, String dbName)
         throws RepositoryException, UnknownHostException
   {
      synchronized( jcrSession)
      {
         if (jcrSession != null)
         {   throw new IllegalStateException("Repositorio JCR ya ha sido inicializado");
         }
         logger          = getLogger();
         Repository repo = initJCRRepo (host, port, dbName);
         jcrSession      = loginToRepo(repo, Parm.DEFAULT_ADMIN_LOGIN, Parm.DEFAULT_ADMIN_PASSWORD);
      }
   }//Repo


   private Repository initJCRRepo()
   {
      Repository repo = new Jcr(new Oak()).createRepository();
      logger.info("... Got an in-memory repo");
      return repo;

      //   Ver ejemplo completo en   C:\ahl\estudio\dzone\ecm\oak-mongodb-demo-master

   }//initJCRRepo


   private Repository initJCRRepo (String host, final int port, String dbName) throws UnknownHostException
   {
      String uri = "mongodb://" + host + ":" + port;
      DocumentNodeStore store = new MongoDocumentNodeStoreBuilder().setMongoDB(uri, dbName, 0).build();
      Repository repo = new Jcr(new Oak(store)).createRepository();
      logger.info("... Got repo at "+ uri+ Parm.PATH_SEPARATOR+ dbName);
      return repo;

   }//initRepo


   private Session loginToRepo(Repository jcrRepo, String userCode, String passwordHash) throws RepositoryException
   {
      if (jcrRepo == null)
      {  throw new NullPointerException("Repositorio no inicializado");
      }
      Session session = jcrRepo.login(new SimpleCredentials(userCode, passwordHash.toCharArray()));
      logger.info("... acquired session to jcr(JackRabbit) repo, user["+ userCode+ "], pwd["+ passwordHash+ "]");
      return session;

      //   jcr spec:    return  Repository.login(Credentials credentials, workspaceName);
   }//loginToRepo


   public void initWorkspace(String workspacePath, String name) throws RepositoryException
   {
      if ( !jcrSession.nodeExists(workspacePath))
      {
         Node node = jcrSession.getRootNode();
         Node jcrWorkspace = node.addNode(workspacePath.substring(1));
         jcrWorkspace.setProperty("jcr:name", name);
         jcrWorkspace.setProperty("jcr:created", TextUtil.formatDateTime(LocalDateTime.now()));
         if ( !workspacePath.equals( jcrWorkspace.getPath()))
         { logger.info("Workspace path["+ workspacePath+ "] diferente del path en repositorio["+ jcrWorkspace.getPath()+ "]");
           System.exit(-1);
         }
         logger.info("    >>> Tenant["+ name+   "], "+
                     "workspace["+ name+              "], "+
                     "path["+ jcrWorkspace.getPath()+ "], "+
                     "created["+ jcrWorkspace.getProperty("jcr:created").getValue().toString()+ "]");
         loadTypes(workspacePath.substring(1));
      }
   }//initWorkspace


   /**
    * Para propósitos de debugging.
    * Verifica la existencia de los archivos .cnd que definen los primary and mixin types of JCR
    * @param place   Localización en el código fuente de este chequeo
    * @param code    Código del namespace del .cnd en su definición
    */
   public void checkCnds(int place, String code)
   {
      try {
           List<Path> cndList =  getCndFiles(code);
           getLogger().info(">>> place="+ place+ " cnds=" + cndList.size());
      }catch(Exception e)
      {
         getLogger().info("*** No pudo obtener el numero de cnd files. Razon\n"+ e.getMessage());
      }
   }//checkCnds



   private void loadTypes(String workspaceName )
   {
         List<Path> cndFiles = getCndFiles(workspaceName);
         getLogger().info("    >>> Loading "+ cndFiles.size()+ " files of type definitions");
         cndFiles.forEach( cndPath->
         {
            try
            {
               long cndSize = Files.size(cndPath);
               getLogger().info(cndPath.getFileName()+ " size=["+cndSize+"]");
               BufferedReader cndReader = Files.newBufferedReader(cndPath, StandardCharsets.UTF_8);
               CndImporter.registerNodeTypes(cndReader, jcrSession);
               getLogger().info("    >>> ["+ cndPath.getFileName()+ "]... loaded");
            }catch (Exception e)
            {  getLogger().info("No pudo cargar definiciones de Tipos de Nodo para workspace["+ workspaceName+ "]. Razón\n"+ e.getMessage());
               System.exit(-1);
            }

         });

   }//loadTypes


   private List<Path>  getCndFiles(String workspaceName)
   {
      List<Path> cndFiles = new ArrayList<>();
      try
      {
         Resource resource = new ClassPathResource("defs");
         Path     dirPath  = Paths.get(resource.getFile().getPath());
         cndFiles =  Files.list(dirPath)
                          .filter(path -> (path.getFileName()).toString().toLowerCase().startsWith(workspaceName.toLowerCase()))
                          .collect(Collectors.toList());
      }catch(Exception e)
      {  getLogger().info("No pudo obtener lista de archivos con las definiciones de tipos de nodo, para el workspace["+ workspaceName+ "]");
         System.exit(-1);
      }
      return cndFiles;

   }//getCndFiles


   public Node addNode( String path, String name, String user)
         throws RepositoryException
   {
      if(jcrSession.nodeExists(path))
      {  return jcrSession.getNode(path);
      }
      int i = path.lastIndexOf(Parm.PATH_SEPARATOR);
      if (i < 0)
      {   throw new IllegalArgumentException("Ruta del nuevo nodo es inválida["+ path+ "]");
      }
      String parentPath = path.substring(0,i);
      if (!jcrSession.nodeExists(parentPath))
      {   throw new IllegalArgumentException("No existe el nodo padre["+ parentPath+ "]");
      }
      Node parent = i == 0? jcrSession.getRootNode(): jcrSession.getNode(parentPath);
      String childCode= path.substring(i+1);
      Node child = parent.hasNode(childCode)? parent.getNode(childCode) : parent.addNode(childCode);
      child.setProperty("jcr:name", name);
      child.setProperty("jcr:created", TextUtil.formatDateTime(LocalDateTime.now()));
      child.setProperty("jcr:createdBy", user);
      return child;

   }//addNode


   public Node findNode( String path)
   {
     Node node = null;
     try
     {
        if (jcrSession.itemExists(path))
        {  node = jcrSession.getNode(path);
        }
     }catch( PathNotFoundException pnf)
     {
     }catch( Exception e)
     { throw new IllegalStateException("Error. No pudo encontrar el nodo ["+ path+ "]. Razón\n"+ e.getMessage());
     }
     return node;
   }//findNode


   public void save()
   {
      try
      {  jcrSession.save();
      } catch( Exception e)
      {  throw new IllegalStateException("No pudo guardar nodo del repositorio. Razón\n"+ e.getMessage());
      }
   }//save


   public String updateMixin( Node node, String namespace, Schema schema, SchemaValues metadata) throws RepositoryException
   {
      if( schema == null || schema.getName().equals("EMPTY"))
         return null;

      String mixinName = namespace+ schema.getName();
      node.addMixin(mixinName);
      if (metadata == null)
      {   return null;
      }
      SchemaValues.Exporter metaExporter = new SchemaValuesToPropertiesExporter();
      @SuppressWarnings("unchecked")
      List<Property> properties          = (List<Property>)metadata.export( metaExporter);
      String msg  = checkRequired( metadata, properties);
      if ( msg == null)
      {  for ( Property p: properties)
         {  node.setProperty( namespace+ p.getName(), p.getValue());
         }
      }
      return msg;
   }//updateMixin



   public Node setContent(Node parent, File contentFile, String namespace) throws Exception
   {
      Path path          = contentFile.toPath();
      Long size          = (Long)Files.getAttribute( path, "size");
      String contentType = Files.probeContentType(path);
      FileInputStream is = new FileInputStream(contentFile);
      Binary      binary = jcrSession.getValueFactory().createBinary(is);
      String         now = TextUtil.formatDateTime(LocalDateTime.now());

      Node content       = parent.addNode("jcr:content", "nt:resource");
      content.addMixin   ("mix:DocumentContent");
      content.setProperty("jcr:mimeType",      contentType);
      content.setProperty("jcr:data",          binary);
      content.setProperty("jcr:lastModified",  now);
      content.setProperty("size", size);

      return content;
   }//setContent


   private String checkRequired( SchemaValues metadata, List<Property> properties)
   {
      Set<Field>     fields              = metadata.getSchema().getFields();
      StringBuilder msg = new StringBuilder();
      for ( Field field : fields)
      {  if (field.isRequired() && !propertyExists(field, properties))
         {   msg.append("Campo requerido[" + field.getName()+ "] no existe\n");
         }
      }
      return  msg.length() == 0? null : msg.toString();
   }//checkRequired


   private boolean propertyExists(Field field, List<Property> properties)
   {
      boolean ok = false;
      for (Property p: properties)
      {  if (p.hasName(field.getName()))
         {  ok = true;
            break;
         }
      }
      return ok;
   }//propertyExists





}//Repo
