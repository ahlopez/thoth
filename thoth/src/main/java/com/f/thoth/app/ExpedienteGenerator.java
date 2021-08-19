package com.f.thoth.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.f.thoth.Parm;
import com.f.thoth.backend.data.entity.util.TextUtil;
import com.f.thoth.backend.data.gdoc.classification.Classification;
import com.f.thoth.backend.data.gdoc.document.jackrabbit.NodeType;
import com.f.thoth.backend.data.gdoc.expediente.BaseExpediente;
import com.f.thoth.backend.data.gdoc.expediente.ExpedienteGroup;
import com.f.thoth.backend.data.gdoc.expediente.ExpedienteIndex;
import com.f.thoth.backend.data.gdoc.expediente.Nature;
import com.f.thoth.backend.data.gdoc.expediente.Volume;
import com.f.thoth.backend.data.gdoc.expediente.VolumeInstance;
import com.f.thoth.backend.data.gdoc.metadata.DocumentType;
import com.f.thoth.backend.data.gdoc.metadata.Schema;
import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.data.security.Tenant;
import com.f.thoth.backend.data.security.User;
import com.f.thoth.backend.jcr.Repo;
import com.f.thoth.backend.repositories.ClassificationRepository;
import com.f.thoth.backend.repositories.DocumentTypeRepository;
import com.f.thoth.backend.repositories.ExpedienteGroupRepository;
import com.f.thoth.backend.repositories.ExpedienteIndexRepository;
import com.f.thoth.backend.repositories.SchemaRepository;
import com.f.thoth.backend.repositories.VolumeInstanceRepository;
import com.f.thoth.backend.repositories.VolumeRepository;

public class ExpedienteGenerator implements HasLogger
{
   private ClassificationRepository   claseRepository;
   private ExpedienteIndexRepository  expedienteIndexRepository;
   private ExpedienteGroupRepository  expedienteGroupRepository;
   private VolumeRepository           volumeRepository;
   private VolumeInstanceRepository   volumeInstanceRepository;
   private Tenant                     tenant;
   private User                       user;
   private final Random               random = new Random(1L);
   private BufferedReader             expedienteNamesReader;
   private int                        nNodes;
   private int                        nExpedientes;
   private int                        nBranches;
   private int                        nLeaves;
   private int                        nLeavesFinal;
   private int                        nVolumes;
   private int                        nInstances;
   private List<Schema>               availableSchemas;
   private List<DocumentType>         availableTypes;

   private static String KEYWORD_NAMES[] = {
         "belleza",      "escepticismo", "nostalgia",    "justicia",     "esperanza",    "tentación",   "nación",       "espiritualidad",
         "infinito",     "pobreza",      "hambre",       "arrogancia",   "gula",         "honradez",    "compañerismo", "terror",
         "imaginación",  "fe",           "rencor",       "obsesión",     "dulzura",      "cariño",      "pasión",       "amargura",
         "verdad",       "paz",          "guerra",       "ansiedad",     "pereza",       "rabia",       "creatividad",  "pobreza",
         "sonido",       "esperanza",    "pureza",       "afición",      "vitalidad",    "respeto",     "lujuria",      "religión",
         "salud",        "riqueza",      "pasión",       "soledad",      "dureza",       "astucia",     "piedad",       "rudeza",
         "dicha",        "maldad",       "verano",       "fealdad",      "miedo",        "otoño",       "virtud",       "justicia",
         "invierno",     "honradez",     "injusticia",   "primavera",    "inteligencia", "ingenio",     "abundancia",   "pensamiento",
         "ira",          "escasez",      "razonamiento", "poder",        "abuso",        "suerte",      "salud",        "diversidad",
         "afecto",       "solidaridad",  "biodiversidad","alegría",      "rencor",       "movimiento",  "ambición",     "templanza",
         "aceptación",   "amor",         "temor",        "actuación",    "amistad",      "terror",      "ansiedad",     "odio",
         "nobleza",      "dolor",        "drama",        "sabiduría",    "cariño",       "verdad",      "serenidad",    "certeza",
         "venganza",     "carisma",      "virtud",       "ternura",      "contento",     "valentía",     "felicidad",   "contradicción",
         "idiotez",      "nación",       "creencia",     "niñez",        "patria",       "deseo",       "mentira",      "ceremonia",
         "dogma",        "ciencia",      "ritual",       "avaricia",     "alma",         "verdor",      "empatía",      "calidad",
         "gordura",      "ego",          "codicia",      "altura",       "añoranza",     "admiración",  "estima",       "tiempo",
         "responsabilidad"};




   public ExpedienteGenerator(  Tenant tenant, User currentUser,
                                ClassificationRepository claseRepository, ExpedienteIndexRepository expedienteIndexRepository,
                                ExpedienteGroupRepository expedienteGroupRepository, DocumentTypeRepository documentTypeRepository,
                                VolumeRepository volumeRepository, VolumeInstanceRepository volumeInstanceRepository,
                                SchemaRepository schemaRepository
                              )
   {
      this.tenant                     = tenant;
      this.user                       = currentUser;
      this.claseRepository            = claseRepository;
      this.expedienteIndexRepository  = expedienteIndexRepository;
      this.expedienteGroupRepository  = expedienteGroupRepository;
      this.volumeRepository           = volumeRepository;
      this.volumeInstanceRepository   = volumeInstanceRepository;
      this.availableSchemas           = schemaRepository.findAll(this.tenant);
      this.availableTypes             = documentTypeRepository.findAll();

      nNodes       = 0;
      nExpedientes = 0;
      nBranches    = 0;
      nLeaves      = 0;
      nLeavesFinal = 0;
      nVolumes     = 0;
      nInstances   = 0;

      expedienteNamesReader           = openNamesFile("data/theNames.txt");

   }//ExpedienteGenerator constructor


   private BufferedReader  openNamesFile(String names)
   {
      /*
       * Paths may be used with the Files class to operate on files, directories, and other types of files.
       * For example, suppose we want a BufferedReader to read text from a file "access.log".
       * The file is located in a directory "logs" relative to the current working directory and is UTF-8 encoded.
       *
       * Path path = FileSystems.getDefault().getPath("logs", "access.log");
       * BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
       */
      BufferedReader namesReader = null;
      try
      {
         Resource resource = new ClassPathResource(names);
         File namesFile    = resource.getFile();
         namesReader       = new BufferedReader( new FileReader(namesFile));
         getLogger().info("    >>> Opened ["+ names+ "]");
      }catch( Exception e)
      {  throw new IllegalStateException("No pudo abrir archivo de nombres de expediente["+ names+ "]. Causa\n"+ e.getMessage());
      }
      return namesReader;
   }//openNamesFile


   public int  registerExpedientes( Tenant tenant) throws RepositoryException, UnknownHostException
   {
      String workspace = tenant.getWorkspace();
      String expedienteRootPath =  workspace+ Parm.PATH_SEPARATOR+ NodeType.EXPEDIENTE.getCode();
      Repo.getInstance().addNode( expedienteRootPath, "Expediente_Root de Tenant["+ tenant.getName()+ "]", user.getEmail());
      nNodes++;

      List<Classification> leafClasses =  claseRepository.findLeaves(tenant);
      for (Classification classificationClass: leafClasses)
      {
         createClassRoot( expedienteRootPath, classificationClass);
         int nExpedientesInClass =  random.nextInt(10)+ 1;
         for ( int i= 0; i < nExpedientesInClass; i++)
         {   creeExpediente( tenant, user, classificationClass, null);
         }
     }
      getLogger().info("    >>> Expedientes created["+ nExpedientes+ "]");
      getLogger().info("    >>> Branch Expedientes ["+ nBranches+    "]");
      getLogger().info("    >>> Leaf   Expedientes ["+ nLeaves+      "]");
      getLogger().info("    >>> Expedientes        ["+ nLeavesFinal+ "]");
      getLogger().info("    >>> Volumes            ["+ nVolumes+     "]");
      getLogger().info("    >>> Volume instances   ["+ nInstances+   "]");
      getLogger().info("    >>> Repository nodes   ["+ nNodes+       "]");
      return nExpedientes;
   }//registerExpedientes


   private void createClassRoot(String expedienteRootPath, Classification rootClass)
       throws RepositoryException, UnknownHostException
   {
      String code      = ""+ rootClass.getId();
      String clazzPath =  expedienteRootPath+ Parm.PATH_SEPARATOR+ code;
      Repo.getInstance().addNode( clazzPath, code, user.getEmail());
      nNodes++;
   }//createClassRoot


   private void creeExpediente( Tenant tenant, User user, Classification classificationClass, Long ownerId)
         throws RepositoryException, UnknownHostException
   {
      int branchProbability =  random.nextInt(100);
      if ( branchProbability < 20)
      {  creeExpedienteGroup(tenant, user, classificationClass, ownerId);
      }else
      {  creeExpedienteLeaf  (tenant, user, classificationClass, ownerId);
      }
   }//creeExpediente


   private ExpedienteGroup creeExpedienteGroup(Tenant tenant, User user, Classification classificationClass, Long ownerId)
         throws RepositoryException, UnknownHostException
   {
      BaseExpediente   base  = createBase( classificationClass, user, ownerId);
      ExpedienteGroup branch = new ExpedienteGroup(base);
      expedienteGroupRepository.saveAndFlush(branch);
      createIndex(base);
      
      @SuppressWarnings("unused")
      Node jcrGroup = createJCRGroup( base);
      int nChildren = random.nextInt(4)+1;
      for( int i=0; i< nChildren; i++)
      {  creeExpediente( tenant, user, classificationClass, base.getId());
      }
      nBranches++;
      nExpedientes++;
      return branch;

   }//creeExpedienteGroup
   

   
   private Node createJCRGroup( BaseExpediente base)
         throws RepositoryException, UnknownHostException
   {
      Node jcrGroup = createJCRExpediente(base, null);
      jcrGroup.setProperty("jcr:nodeTypeName", Nature.GRUPO.toString());
      return jcrGroup;
   }//createJCRGroup



   private void  creeExpedienteLeaf(Tenant tenant, User user, Classification classificationClass, Long ownerId)
         throws RepositoryException, UnknownHostException
   {
      BaseExpediente   base   = createBase( classificationClass, user, ownerId);
      Set<DocumentType> admissibleTypes = generateAdmissibleTypes();
      Node jcrLeaf = createJCRExpediente(base, admissibleTypes);

      int  volProbability = random.nextInt(100);
      if ( volProbability < 15)
      {  createVolume(base, jcrLeaf, admissibleTypes);
      } else
      {  createExpediente(base, jcrLeaf, admissibleTypes);
      }
      base.createIndex();
      nLeaves++;
      nExpedientes++;

   }//creeExpedienteLeaf



   private BaseExpediente createBase(Classification classificationClass, User user, Long parentId)
         throws RepositoryException, UnknownHostException
   {
      LocalDateTime  now      =   LocalDateTime.now();
      BaseExpediente base     =   new BaseExpediente();
      base.setTenant              (tenant);
      base.setName                (generateName());
      base.setObjectToProtect     (new ObjectToProtect());
      base.setCreatedBy           (user);
      base.setClassificationClass (classificationClass);
      base.setMetadataSchema      (availableSchemas.get(random.nextInt(availableSchemas.size()))  );
      base.setDateOpened          (now);
      base.setDateClosed          (now.plusYears(200L));
      base.setOwnerId             (parentId);
      base.setOpen                (true);
      base.setKeywords            (generateKeywords());
      base.setMac                 (generateMac());
 //   base.setMetadata            (SchemaValues.EMPTY);
      base.buildCode();


      return base;

   }//createBase

   private void createIndex(BaseExpediente base)
   {
      ExpedienteIndex idx = base.createIndex();
      expedienteIndexRepository.saveAndFlush(idx);
   }//createIndex


   private Node createJCRExpediente(BaseExpediente base, Set<DocumentType> admissibleTypes)
         throws RepositoryException, UnknownHostException
   {
      Tenant tenant    = base.getTenant();
      String namespace = tenant.getName()+ ":";
      Node node = Repo.getInstance().addNode( base.getPath(), base.getName(), base.getCreatedBy().getEmail());
      node.addMixin   ( "mix:referenceable");
      node.setProperty(namespace+ "tenant",         tenant.getId());
      node.setProperty(namespace+ "expedienteCode", base.formatCode());
      Node clase = Repo.getInstance().findNode( base.getClassificationClass().getPath());
      node.setProperty(namespace+ "classification", clase.getIdentifier());
      if (admissibleTypes != null)
      {  for( DocumentType admissibleType: admissibleTypes)
         {  node.setProperty(namespace+ "admissibleTypes", admissibleType.getCode());
         }
      }
      node.setProperty(namespace+ "open",           base.isOpen());
      if ( base.isOpen())
      {  node.setProperty(namespace+ "dateOpened",  TextUtil.formatDateTime(base.getDateOpened()));
      }  else
      {  node.setProperty(namespace+ "dateClosed",  TextUtil.formatDateTime(base.getDateClosed()));
      }
      if (base.getLocation() != null)
      {  node.setProperty(namespace+ "location",    base.getLocation());
      }
      if (TextUtil.isNotEmpty( base.getKeywords()))
      {  String[] keywords = base.getKeywords().split(Parm.VALUE_SEPARATOR);
         for( String keyword: keywords)
         {  node.setProperty(namespace+ "keywords", keyword);
         }
      }
      if ( base.getMac() != null )
      {  node.setProperty(namespace+ "mac", base.getMac());
      }
      Schema schema = base.getMetadataSchema();
      if ( schema != null)
      {  Repo.getInstance().updateMixin( node, namespace, schema, base.getMetadata());
      }
      Repo.getInstance().save();
      nNodes++;
      return node;

   }//createJCRExpediente



   private void createExpediente(BaseExpediente base, Node jcrExpediente, Set<DocumentType> admissibleTypes)
        throws RepositoryException, UnknownHostException
   {
      base.setType(Nature.EXPEDIENTE);
      Volume volume = new Volume( base, Nature.EXPEDIENTE, 1, admissibleTypes);
      volumeRepository.save(volume);

      createJCRExpediente( jcrExpediente, admissibleTypes);

      VolumeInstance expedienteInstance = createVolumeInstance( volume, jcrExpediente, 1, true, base.getDateOpened(), LocalDateTime.MAX);
      expedienteInstance.setOpen(true);
      volumeInstanceRepository.saveAndFlush(expedienteInstance);
      createJCRInstance(expedienteInstance, jcrExpediente);

      nLeavesFinal++;

   }//createExpediente
   
   
   private void createJCRExpediente( Node jcrExpediente, Set<DocumentType> admissibleTypes)
         throws RepositoryException, UnknownHostException
   {
      jcrExpediente.addMixin("FCN:Volume");
      String namespace = tenant.getName()+ ":";
      jcrExpediente.setProperty("jcr:nodeTypeName", Nature.EXPEDIENTE.toString());
      jcrExpediente.setProperty(namespace+ "expedienteType",  Nature.EXPEDIENTE.toString());
      jcrExpediente.setProperty(namespace+ "currentInstance", 0L);
      if (admissibleTypes != null)
      {  for( DocumentType admissibleType: admissibleTypes)
         {  jcrExpediente.setProperty(namespace+ "admissibleTypes", admissibleType.getCode());
         }
      }
      
   }//createJCRExpediente



   private void createVolume(BaseExpediente base, Node jcrVol, Set<DocumentType> admissibleTypes)
       throws RepositoryException, UnknownHostException
   {
      base.setType(Nature.VOLUMEN);
      Volume volume = createVolumeHeader(base, admissibleTypes);
      VolumeInstance currentInstance = null;
      int nInstances = random.nextInt(3)+1;
      volume.setCurrentInstance(nInstances);
      volumeRepository.save(volume);
      
      createJCRVolume(jcrVol, volume);
      LocalDateTime dateOpened =  LocalDateTime.now().minusDays(365L*4);
      LocalDateTime dateClosed =  dateOpened.plusDays(365L);
      for (int instance=0; instance < nInstances; instance++)
      {
         boolean isOpen  = (instance == nInstances-1);
         currentInstance = createVolumeInstance(volume, jcrVol, instance, isOpen, dateOpened, isOpen? LocalDateTime.MAX : dateClosed);
         dateOpened      = dateClosed.plusDays(1L);
         dateClosed      = dateOpened.plusDays(365L);
      }
      currentInstance.setOpen(true);
      currentInstance.setDateClosed(LocalDateTime.MAX);
      volumeInstanceRepository.saveAndFlush(currentInstance);
      nVolumes++;

   }//createVolume
   
   
   private void  createJCRVolume(Node jcrVol, Volume volume)
         throws RepositoryException, UnknownHostException
   {
      String  namespace = tenant.getName()+ ":";
      String  volNature = Nature.VOLUMEN.toString();
      jcrVol.setProperty("jcr:nodeTypeName", volNature);
      jcrVol.setProperty(namespace+ "currentInstance", ""+ volume.getCurrentInstance());
   }//createJCRVolume


   private Volume createVolumeHeader(BaseExpediente base, Set<DocumentType> admissibleTypes)
   {
      Volume volume = new Volume();
      volume.setExpediente(base);
      volume.setType(base.getType());
      volume.setAdmissibleTypes(admissibleTypes);
      volume.setCurrentInstance(0);
      return volume;
   }//createVolumeHeader


   private VolumeInstance createVolumeInstance( Volume vol, Node jcrVol, int instanceNumber, boolean open, LocalDateTime dateOpened, LocalDateTime dateClosed)
       throws RepositoryException, UnknownHostException
   {
      VolumeInstance instance = new VolumeInstance( vol, instanceNumber, "[Loc]", dateOpened , dateClosed);
      instance.setOpen(open);
      volumeInstanceRepository.saveAndFlush(instance);
      createJCRInstance(instance, jcrVol);
      nInstances++;
      return instance;
   }//createVolumeInstance


   private void createJCRInstance(VolumeInstance instance, Node jcrVol)
      throws RepositoryException, UnknownHostException
   {
      String       namespace = tenant.getName()+ ":";
      Volume          volume = instance.getVolume();
      Integer instanceNumber = instance.getInstance();
      String            path = volume.getPath()+ Parm.PATH_SEPARATOR+ instanceNumber;
      Node       jcrInstance = Repo.getInstance().addNode(path, volume.getName()+ " instance "+ instanceNumber, user.getEmail());
      jcrInstance.setProperty(namespace+ "instance", instanceNumber);
      jcrInstance.setProperty(namespace+ "open",     instance.getOpen());
      jcrInstance.setProperty(namespace+ "opened",   TextUtil.formatDateTime(instance.getDateOpened()));
      jcrInstance.setProperty(namespace+ "closed",   TextUtil.formatDateTime(instance.getDateClosed()));
      //TODO:   Simular  - FCN:location            ( STRING    )                     // Physical archive location  (topographic signature)
      nNodes++;
   }//createJCRInstance


   private String generateName()
   {
      String name = "";
      try
      {  // Los nombres han sido pre-generados
         name =  expedienteNamesReader.readLine();
      } catch (Exception e)
      { throw new IllegalStateException("No pudo leer archivo de nombres de expediente");
      }
      return name;
   }//generateName



   private String generateKeywords()
   {
      StringBuilder keywords = new StringBuilder();
      int nKeywords = getRandom(1,3);
      for( int i = 0; i < nKeywords; i++)
      {
         if (i > 0)
         {   keywords.append(",");
         }
         keywords.append( getRandom(KEYWORD_NAMES));
      }

      return keywords.toString();
   }//generateKeywords



   private int getRandom( int low, int high)
   {
      int range = high-low+1;
      return low+ random.nextInt(range);
   }//getRandom



   private String generateMac()
   {
      //TODO: Crear un BlockChain
      // Generar el mac del expediente usando el mac de cada documento y el precedente del block
      //

      return "";
   }//generateMac



   private Set<DocumentType> generateAdmissibleTypes()
   {
      Set<DocumentType> admissibleTypes = new TreeSet<>();
      int sizeAvailable = availableTypes.size();
      int N =random.nextInt(4);
      for (int i = 0; i < N; i++)
      {  admissibleTypes.add( availableTypes.get(random.nextInt(sizeAvailable)));
      }
      return admissibleTypes;
   }//generateAdmissibleTypes



   private <T> T getRandom(T[] array)
   {  return array[random.nextInt(array.length)];
   }


 }//ExpedienteGenerator
