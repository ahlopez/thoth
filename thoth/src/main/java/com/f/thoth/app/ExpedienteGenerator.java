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
import com.f.thoth.backend.data.security.ThothSession;
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




   public ExpedienteGenerator(
         ClassificationRepository claseRepository, ExpedienteIndexRepository expedienteIndexRepository,
         ExpedienteGroupRepository expedienteGroupRepository, DocumentTypeRepository documentTypeRepository,
         VolumeRepository volumeRepository, VolumeInstanceRepository volumeInstanceRepository, SchemaRepository schemaRepository
         )
   {
      this.claseRepository            = claseRepository;
      this.expedienteIndexRepository  = expedienteIndexRepository;
      this.expedienteGroupRepository  = expedienteGroupRepository;
      this.volumeRepository           = volumeRepository;
      this.volumeInstanceRepository   = volumeInstanceRepository;
      this.availableSchemas           = schemaRepository.findAll(ThothSession.getCurrentTenant());
      this.availableTypes             = documentTypeRepository.findAll();

      this.user                       = ThothSession.getUser();

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
      getLogger().info("    >>> Branch Expedientes ["+ nBranches+ "]");
      getLogger().info("    >>> Leaf   Expedientes ["+ nLeaves+ "]");
      getLogger().info("    >>> Expedientes        ["+ nLeavesFinal+ "]");
      getLogger().info("    >>> Volumes            ["+ nVolumes+ "]");
      getLogger().info("    >>> Volume instances   ["+ nInstances+ "]");
      getLogger().info("    >>> Repository nodes   ["+ nNodes+ "]");
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
      BaseExpediente   base   = createBase( classificationClass, user, ownerId);
      ExpedienteGroup branch = new ExpedienteGroup(base);
      expedienteGroupRepository.saveAndFlush(branch);
      
      Node jcrGroup = createJCRExpediente(base);
      jcrGroup.setProperty("ev:type", Nature.GRUPO.toString());
      
      int nChildren = random.nextInt(4)+1;
      for( int i=0; i< nChildren; i++)
      {  creeExpediente( tenant, user, classificationClass, base.getId());
      }
      nBranches++;
      nExpedientes++;
      return branch;

   }//creeExpedienteGroup



   private void  creeExpedienteLeaf(Tenant tenant, User user, Classification classificationClass, Long ownerId)
         throws RepositoryException, UnknownHostException
   {
      BaseExpediente   base   = createBase( classificationClass, user, ownerId);
      Set<DocumentType> admissibleTypes = generateAdmissibleTypes();
      Node jcrLeaf = createJCRExpediente(base);
      setAdmissibleTypes(jcrLeaf, admissibleTypes);

      int  volProbability = random.nextInt(100);
      if ( volProbability < 15)
      {  jcrLeaf.setProperty("ev:type", Nature.VOLUMEN.toString());
         createVolume(base, admissibleTypes);
      } else
      {  jcrLeaf.setProperty("ev:type", Nature.EXPEDIENTE.toString());
         createExpediente(base, admissibleTypes);
      }
      nLeaves++;
      nExpedientes++;

   }//creeExpedienteLeaf



   private BaseExpediente createBase(Classification classificationClass, User user, Long parentId)
         throws RepositoryException, UnknownHostException
   {
      LocalDateTime  now      =   LocalDateTime.now();
      BaseExpediente base     =   new BaseExpediente();
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
      
      ExpedienteIndex idx = base.createIndex();
      expedienteIndexRepository.saveAndFlush(idx);

      return base;

   }//createBase
   
   
   private Node createJCRExpediente(BaseExpediente base)
         throws RepositoryException, UnknownHostException
   {
      Schema schema = base.getMetadataSchema();
      Long  ownerId = base.getOwnerId();
      Node node = Repo.getInstance().addNode( base.getPath(), base.getName(), base.getCreatedBy().getEmail());
      node.setProperty("evd:id",             ""+ base.getId());
      node.setProperty("evd:tenant",         base.getTenant().getCode());
      node.setProperty("evd:code",           base.formatCode());
      node.setProperty("evd:type",           base.getType().toString());
      node.setProperty("evd:classification", base.getClassificationClass().formatCode());
      node.setProperty("evd:schema",         schema == null? "" : schema.getName());
      node.setProperty("evd:dateOpened",     TextUtil.formatDateTime(base.getDateOpened()));
      node.setProperty("evd:dateClosed",     TextUtil.formatDateTime(base.getDateClosed()));
      node.setProperty("evd:ownerId",        ""+ (ownerId == null? "" : ownerId));
      node.setProperty("evd:open", ""+       base.getOpen());
      node.setProperty("evd:location",       base.getLocation());
      node.setProperty("evd:keywords",       base.getKeywords()); 
      nNodes++;
      return node;

      /*
      protected ObjectToProtect   objectToProtect;             // Associated security object
      protected SchemaValues      metadata;                    // Metadata values of the associated expediente
      protected String            mac;                         // Message authentication code
      protected Integer           currentInstance;
      */
      
   }//createJCRExpediente



   private void createExpediente(BaseExpediente base, Set<DocumentType> admissibleTypes)
        throws RepositoryException, UnknownHostException
   {
      base.setType(Nature.EXPEDIENTE);
      Volume volume = new Volume( base, Nature.EXPEDIENTE, 1, admissibleTypes);
      volumeRepository.save(volume);

      Node jcrExpediente = Repo.getInstance().findNode(base.getPath());
      jcrExpediente.setProperty("ev:type", Nature.EXPEDIENTE.toString());
      jcrExpediente.setProperty("ev:currentInstance", "1");
      
      LocalDateTime dateOpened = LocalDateTime.now().minusDays(365L*4);
      LocalDateTime dateClosed = dateOpened.plusYears(1000L);
      VolumeInstance expedienteInstance = createVolumeInstance( volume, 1, dateOpened, dateClosed);
      expedienteInstance.setOpen(true);
      volumeInstanceRepository.saveAndFlush(expedienteInstance);
      createJCRInstance(expedienteInstance);
      
      nLeavesFinal++;

   }//createExpediente



   private void createVolume(BaseExpediente base, Set<DocumentType> admissibleTypes)
       throws RepositoryException, UnknownHostException
   {
      base.setType(Nature.VOLUMEN);
      Volume volume = createVolumeHeader(base, admissibleTypes);      
      VolumeInstance currentInstance = null;
      int nInstances = random.nextInt(3)+1;
      volume.setCurrentInstance(nInstances);
      volumeRepository.save(volume);
      LocalDateTime dateOpened =  LocalDateTime.now().minusDays(365L*4);
      LocalDateTime dateClosed =  dateOpened.plusDays(365L);
      for (int instance=1; instance <= nInstances; instance++)
      {
         currentInstance = createVolumeInstance(volume, instance, dateOpened, dateClosed);
         dateOpened      = dateClosed.plusDays(1L);
         dateClosed      = dateOpened.plusDays(365L);
         createJCRInstance(currentInstance);
      }
      currentInstance.setOpen(true);
      currentInstance.setDateClosed(LocalDateTime.MAX);
      volumeInstanceRepository.saveAndFlush(currentInstance);
      Node jcrInstance = Repo.getInstance().findNode(volume.getPath()+ Parm.PATH_SEPARATOR+ nInstances);
      jcrInstance.setProperty("ev:open", "true");     

      Node jcrVolume = Repo.getInstance().findNode(base.getPath());
      jcrVolume.setProperty("ev:type", Nature.VOLUMEN.toString());
      jcrVolume.setProperty("ev:currentInstance", ""+ volume.getCurrentInstance());
      nVolumes++;

   }//createVolume


   private Volume createVolumeHeader(BaseExpediente base, Set<DocumentType> admissibleTypes)
   {
      Volume volume = new Volume();
      volume.setExpediente(base);
      volume.setType(base.getType());
      volume.setAdmissibleTypes(admissibleTypes);
      volume.setCurrentInstance(0);
      return volume;
   }//createVolumeHeader


   private VolumeInstance createVolumeInstance( Volume vol, int instanceNumber, LocalDateTime dateOpened, LocalDateTime dateClosed)
       throws RepositoryException, UnknownHostException
   {
      VolumeInstance instance = new VolumeInstance( vol, instanceNumber, "[Loc]", dateOpened , dateClosed);
      instance.setOpen(false);
      volumeInstanceRepository.saveAndFlush(instance);
      nInstances++;
      return instance;
   }//createVolumeInstance
   
   
   private void createJCRInstance(VolumeInstance instance)
      throws RepositoryException, UnknownHostException
   {
      Volume          volume = instance.getVolume();
      Integer instanceNumber = instance.getInstance();
      String            path = volume.getPath()+ Parm.PATH_SEPARATOR+ instanceNumber;
      Node       jcrInstance = Repo.getInstance().addNode(path, volume.getName()+ " instance "+ instanceNumber, user.getEmail());
      jcrInstance.setProperty("ev:instance", ""+ instanceNumber);
      jcrInstance.setProperty("ev:opened",   TextUtil.formatDateTime(instance.getDateOpened()));
      jcrInstance.setProperty("ev:closed",   TextUtil.formatDateTime(instance.getDateClosed()));
      jcrInstance.setProperty("ev:open",     ""+ instance.getOpen());     
      nNodes++;
   }//createJCRInstance
   
   
   private void setAdmissibleTypes(Node jcrNode, Set<DocumentType> admissibleTypes)
       throws RepositoryException
   {
      StringBuilder admissible = new StringBuilder();
      try
      {
         for ( DocumentType docType: admissibleTypes)
         {   admissible.append(docType.getName()).append(";");
         }
         if (admissible.length() > 0)
         {   jcrNode.setProperty("ev:admissibleTypes", admissible.toString());
         }
      }catch( Exception e)
      { throw new IllegalStateException("No pudo guardar tipos documentales admisibles["+ admissible.toString()+ "] en nodo["+ jcrNode.getPath()+ "]");
      }
   }//setAdmissibleTypes
   


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
      // Crear un BlockChain
      // Generar el mac del expediente usando el mac de cada documento y el precedente del block
      //

      return ""; //TODO:
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
