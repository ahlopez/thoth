package com.f.thoth.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import javax.jcr.Session;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.f.thoth.backend.data.gdoc.classification.Classification;
import com.f.thoth.backend.data.gdoc.expediente.BaseExpediente;
import com.f.thoth.backend.data.gdoc.expediente.Expediente;
import com.f.thoth.backend.data.gdoc.expediente.ExpedienteGroup;
import com.f.thoth.backend.data.gdoc.expediente.ExpedienteIndex;
import com.f.thoth.backend.data.gdoc.expediente.Volume;
import com.f.thoth.backend.data.gdoc.expediente.VolumeInstance;
import com.f.thoth.backend.data.gdoc.metadata.DocumentType;
import com.f.thoth.backend.data.gdoc.metadata.Schema;
import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.data.security.Tenant;
import com.f.thoth.backend.data.security.ThothSession;
import com.f.thoth.backend.data.security.User;
import com.f.thoth.backend.repositories.ClassificationRepository;
import com.f.thoth.backend.repositories.DocumentTypeRepository;
import com.f.thoth.backend.repositories.ExpedienteGroupRepository;
import com.f.thoth.backend.repositories.ExpedienteIndexRepository;
import com.f.thoth.backend.repositories.ExpedienteLeafRepository;
import com.f.thoth.backend.repositories.SchemaRepository;
import com.f.thoth.backend.repositories.VolumeInstanceRepository;
import com.f.thoth.backend.repositories.VolumeRepository;

public class ExpedienteGenerator implements HasLogger
{
   private ClassificationRepository   claseRepository;
   private ExpedienteIndexRepository  expedienteIndexRepository;
   private ExpedienteGroupRepository  expedienteGroupRepository;
   private ExpedienteLeafRepository   expedienteRepository;
   private VolumeRepository           volumeRepository;
   private VolumeInstanceRepository   volumeInstanceRepository;
   private Session                    jcrSession;
   private User                       user;
   private final Random               random = new Random(1L);
   private BufferedReader             expedienteNamesReader;
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
         ClassificationRepository claseRepository, Session jcrSession, ExpedienteIndexRepository expedienteIndexRepository,
         ExpedienteGroupRepository expedienteGroupRepository, ExpedienteLeafRepository expedienteRepository, DocumentTypeRepository documentTypeRepository,
         VolumeRepository volumeRepository, VolumeInstanceRepository volumeInstanceRepository, SchemaRepository schemaRepository
         )
   {
      this.claseRepository            = claseRepository;
      this.expedienteIndexRepository  = expedienteIndexRepository;
      this.expedienteGroupRepository  = expedienteGroupRepository;
      this.expedienteRepository       = expedienteRepository;
      this.volumeRepository           = volumeRepository;
      this.volumeInstanceRepository   = volumeInstanceRepository;
      this.availableSchemas           = schemaRepository.findAll(ThothSession.getCurrentTenant());
      this.availableTypes             = documentTypeRepository.findAll();

      this.jcrSession                 = jcrSession;
      this.user                       = ThothSession.getUser();

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


   public int  registerExpedientes( Tenant tenant)
   {
      List<Classification> leafClasses =  claseRepository.findLeaves(tenant);
      for (Classification classificationClass: leafClasses)
      {
         int nExpedientesInClass =  random.nextInt(10)+ 1;
         for ( int i= 0; i < nExpedientesInClass; i++)
         {   creeExpediente( tenant, user, classificationClass, null);
         }
      }
      getLogger().info("    >>> Expedientes generados["+ nExpedientes+ "]");
      getLogger().info("    >>> Expedientes rama["+ nBranches+ "]");
      getLogger().info("    >>> Expedientes hoja["+ nLeaves+ "]");
      getLogger().info("    >>> Expedientes hoja-final["+ nLeavesFinal+ "]");
      getLogger().info("    >>> Volumenes["+ nVolumes+ "]");
      getLogger().info("    >>> Instancias de volumen["+ nInstances+ "]");
      return nExpedientes;
   }//registerExpedientes


   private void creeExpediente( Tenant tenant, User user, Classification classificationClass, Long ownerId)
   {
      int branchProbability =  random.nextInt(100);
      if ( branchProbability < 20)
      {  creeExpedienteGroup(tenant, user, classificationClass, ownerId);
      }else
         creeExpedienteLeaf  (tenant, user, classificationClass, ownerId);

   }//creeExpediente


   private ExpedienteGroup creeExpedienteGroup(Tenant tenant, User user, Classification classificationClass, Long ownerId)
   {
      BaseExpediente   base   = createBase( classificationClass, user, ownerId);      base.buildCode();
      ExpedienteGroup branch = new ExpedienteGroup();
      branch.setExpediente(base);
      expedienteGroupRepository.saveAndFlush(branch);
      int nChildren = random.nextInt(4)+1;
      for( int i=0; i< nChildren; i++)
      {  creeExpediente( tenant, user, classificationClass, base.getId());
      }
      nBranches++;
      nExpedientes++;
      return branch;

   }//creeExpedienteGroup



   private void  creeExpedienteLeaf(Tenant tenant, User user, Classification classificationClass, Long ownerId)
   {
      BaseExpediente   base   = createBase( classificationClass, user, ownerId);
      Set<DocumentType> admissibleTypes = generateAdmissibleTypes();

      int  volProbability = random.nextInt(100);
      if ( volProbability < 15)
      {  createVolume(base, admissibleTypes);
      } else
      {  createExpediente(base, admissibleTypes);
      }
      nLeaves++;
      nExpedientes++;

   }//creeExpedienteLeaf



   private BaseExpediente createBase(Classification classificationClass, User user, Long parentId)
   {
      LocalDateTime  now      =   LocalDateTime.now();
      BaseExpediente base     =   new BaseExpediente();
      base.setName                (generateName());
      base.setObjectToProtect     (new ObjectToProtect());
      base.setCreatedBy           (user);
      base.setClassificationClass (classificationClass);
      base.setMetadataSchema      (availableSchemas.get(random.nextInt(availableSchemas.size()))  );
   //   base.setMetadata            (SchemaValues.EMPTY);
      base.setDateOpened          (now);
      base.setDateClosed          (now.plusYears(200L));
      base.setOwnerId             (parentId);
      base.setOpen                (true);
      base.setKeywords            (generateKeywords());
      base.setMac                 (generateMac());
      base.buildCode();

      ExpedienteIndex idx = base.createIndex();
      expedienteIndexRepository.saveAndFlush(idx);

      creeJCRNodo( base.getPath());
      return base;

   }//createBase




   private void createExpediente(BaseExpediente base, Set<DocumentType> admissibleTypes)
   {
      Expediente expediente = new Expediente();
      expediente.setExpediente( base);
      expediente.setAdmissibleTypes(admissibleTypes);
      expediente.setPath(base.getPath());
      expediente.setLocation( "[LOCATION]");
      expedienteRepository.saveAndFlush(expediente);
      nLeavesFinal++;

   }//createExpediente



   private void createVolume(BaseExpediente base, Set<DocumentType> admissibleTypes)
   {
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
         dateOpened = dateClosed.plusDays(1L);
         dateClosed = dateOpened.plusDays(365L);
      }
      currentInstance.setOpen(true);
      currentInstance.setDateClosed(LocalDateTime.MAX);
      volumeInstanceRepository.saveAndFlush(currentInstance);
      nVolumes++;

   }//createVolume



   private Volume createVolumeHeader(BaseExpediente base, Set<DocumentType> admissibleTypes)
   {
      Volume volume = new Volume();
      volume.setExpediente(base);
      volume.setType();
      volume.setAdmissibleTypes(admissibleTypes);
      volume.setCurrentInstance(0);
      return volume;
   }//createVolumeHeader





   private VolumeInstance createVolumeInstance( Volume vol, int instanceNumber, LocalDateTime dateOpened, LocalDateTime dateClosed)
   {
      VolumeInstance instance = new VolumeInstance( vol, instanceNumber, "[Loc]", dateOpened , dateClosed);
      instance.setOpen(false);
      volumeInstanceRepository.saveAndFlush(instance);
      nInstances++;
      return instance;
   }//createVolumeInstance


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



   private void creeJCRNodo( String path)
   {

   }//creeJCRNodo



   private <T> T getRandom(T[] array)
   {  return array[random.nextInt(array.length)];
   }


}//ExpedienteGenerator
