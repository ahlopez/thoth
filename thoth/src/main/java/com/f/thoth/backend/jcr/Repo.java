package com.f.thoth.backend.jcr;

import java.net.UnknownHostException;
import java.time.LocalDateTime;

import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.jackrabbit.oak.Oak;
import org.apache.jackrabbit.oak.jcr.Jcr;
import org.apache.jackrabbit.oak.plugins.document.DocumentNodeStore;
import org.apache.jackrabbit.oak.plugins.document.mongo.MongoDocumentNodeStoreBuilder;
import org.slf4j.Logger;

import com.f.thoth.Parm;
import com.f.thoth.app.HasLogger;
import com.f.thoth.backend.data.entity.util.TextUtil;

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
      
      /*
         Ver ejemplo completo en   C:\ahl\estudio\dzone\ecm\oak-mongodb-demo-master

         2021/06/04 : From the JackRabbit Oak Repository Construction page at https://jackrabbit.apache.org/oak/docs/construct.html
         DB db = new MongoClient("127.0.0.1", 27017).getDB("test2");
         DocumentNodeStore ns = new DocumentMK.Builder().
         setMongoDB(db).getNodeStore();
         Repository repo = new Jcr(new Oak(ns)).createRepository();

         2021/06/04: Adaptation of the following article and Oak documentation
         Repository repo = initRepo("mongodb://localhost", 27017, "oak");

         2021/06/04: From the Dzone article "Creating a Content Repository Using Jackrabbit Oak and MongoDB, Bishnu Mishra  Apr. 07, 18"
         String uri = "mongodb://" + host + ":" + port;
         DocumentNodeStore ns = new DocumentMK.Builder().setMongoDB(uri, "oak_demo", 16).getNodeStore();
         Repository repo = new Jcr(new Oak(ns)).createRepository();

         2021/06/04: From the Dzone article ibid,   Creating File Nodes
         Node fileNodeParent = session.getNode("pathToParentNode"); // /node1/node2/
         Node fileNode = fileNodeParent.addNode("theFile", "nt:file");
         Node content = fileNode.addNode("jcr:content", "nt:resource");
         InputStream is = getFileInputStream();//Get the file data as stream.
         Binary binary = session.getValueFactory().createBinary(is);
         content.setProperty("jcr:data", binary);
         session.save();
         // To enable versioning use VersionManager
         VersionManager vm = session.getWorkspace().getVersionManager();
         vm.checkin(fileNode.getPath());

         2021/06/04: From the Dzone article ibid,   Retrieving File From Repository
         Node fileNodeParent = session.getNode("pathToParentNode"); // /node1/node2/
         Node fileContent = fileNodeParent.getNode("theFile").getNode("jcr:content");
         Binary bin = fileContent.getProperty("jcr:data").getBinary();
         InputStream stream = bin.getStream();
         byte[] bytes = IOUtils.toByteArray(stream);
         bin.dispose();
         stream.close();

         2021/06/04: From the Dzone article ibid,   Retrieving Version of a Content
         VersionManager vm = session.getWorkspace().getVersionManager();
         javax.jcr.version.VersionHistory versionHistory = vm.getVersionHistory("filePath");
         Version currentVersion = vm.getBaseVersion(filePath);// This is the current version of the file
         VersionIterator itr = versionHistory.getAllVersions();// gets all the versions of that content

         We can iterate over the VersionIterator to get specific versions and its properties.
         Similarly, we can restore a specific version of a content.

         //Restoring a specific version
         VersionManager vm = session.getWorkspace().getVersionManager();
         Version version = (Version) session.getNodeByIdentifier("versionId");
         vm.restore(version, false);// boolean flag governs what happens in case of an identifier collision.

         2021/06/04:  To access the repository (example) - See https://jackrabbit.apache.org/oak/docs/construct.html
         Session session = repo.login( new SimpleCredentials("admin", "admin".toCharArray()));
         Node root = session.getRootNode();
         if (root.hasNode("hello"))
         {
             Node hello = root.getNode("hello");
             long count = hello.getProperty("count").getLong();
             hello.setProperty("count", count + 1);
             System.out.println("found the hello node, count = " + count);
         } else
         {
             System.out.println("creating the hello node");
             root.addNode("hello").setProperty("count", 1);
         }
         session.save();

         2021/06/04:  To logout and close the store - See https://jackrabbit.apache.org/oak/docs/construct.html
         session.logout();
         // depending on NodeStore implementation either:
         // close FileStore
         fs.close();
         // or close DocumentNodeStore
         ns.dispose();

      // Gets an in-memory repo
      repo = new Jcr(new Oak()).createRepository();
      getLogger().info("... Got an in-memory repo");
      return repo;
       */
      
   }//initJCRRepo


   private Repository initJCRRepo (String host, final int port, String dbName) throws UnknownHostException
   {
      String uri = "mongodb://" + host + ":" + port;
      DocumentNodeStore store = new MongoDocumentNodeStoreBuilder().setMongoDB(uri, dbName, 0).build();
      Repository repo = new Jcr(new Oak(store)).createRepository();
      logger.info("... Got repo at "+ uri+ "/"+ dbName);
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


   public void initWorkspace(String workspacePath, String name, String tenantName) throws RepositoryException
   {
      if ( !jcrSession.nodeExists(workspacePath))
      {
         Node node = jcrSession.getRootNode();
         Node jcrWorkspace = node.addNode(workspacePath.substring(1));
         jcrWorkspace.setProperty("name", name);
         logger.info("    >>> Tenant["+ tenantName+ "] workspace["+ name+ "], path["+ jcrWorkspace.getPath()+ "]");
         if ( !workspacePath.equals( jcrWorkspace.getPath()))
         { throw new RepositoryException("Workspace path["+ workspacePath+ "] diferente del path en repositorio["+ jcrWorkspace.getPath()+ "]");
         }
      }
   }//initWorkspace


   public Node addNode( String path, String name, String user) 
         throws RepositoryException
   {
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
      child.setProperty("jcr:createdBy", user);
      child.setProperty("jcr:creationDate", TextUtil.formatDateTime(LocalDateTime.now()));
      return child;

   }//addNode

}//Repo
