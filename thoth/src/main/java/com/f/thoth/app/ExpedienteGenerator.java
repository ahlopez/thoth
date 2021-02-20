package com.f.thoth.app;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import javax.jcr.Session;

import com.f.thoth.backend.data.entity.User;
import com.f.thoth.backend.data.gdoc.classification.Classification;
import com.f.thoth.backend.data.gdoc.expediente.BaseExpediente;
import com.f.thoth.backend.data.gdoc.expediente.BranchExpediente;
import com.f.thoth.backend.data.gdoc.expediente.LeafExpediente;
import com.f.thoth.backend.data.gdoc.expediente.Volume;
import com.f.thoth.backend.data.gdoc.metadata.DocumentType;
import com.f.thoth.backend.data.gdoc.metadata.SchemaValues;
import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.data.security.Tenant;
import com.f.thoth.backend.data.security.ThothSession;
import com.f.thoth.backend.repositories.BaseExpedienteRepository;
import com.f.thoth.backend.repositories.BranchExpedienteRepository;
import com.f.thoth.backend.repositories.ClassificationRepository;
import com.f.thoth.backend.repositories.ExpedienteRepository;
import com.f.thoth.backend.repositories.LeafExpedienteRepository;
import com.f.thoth.backend.repositories.VolumeInstanceRepository;
import com.f.thoth.backend.repositories.VolumeRepository;

public class ExpedienteGenerator
{
    private ClassificationRepository   claseRepository;
	private BaseExpedienteRepository   baseExpedienteRepository;
	private BranchExpedienteRepository branchExpedienteRepository;
	private LeafExpedienteRepository   leafExpedienteRepository;
	private ExpedienteRepository       expedienteRepository;
	private VolumeRepository           volumeRepository;
	private VolumeInstanceRepository   volumeInstanceRepository;
    private Session                    jcrSession;
    private User                       user;
    private final Random               random = new Random(1L);
    private int                        nExpedientes = 0;
    private BufferedReader             namesFile;


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
    		ClassificationRepository claseRepository, Session jcrSession, BaseExpedienteRepository baseExpedienteRepository, 
    		BranchExpedienteRepository branchExpedienteRepository, LeafExpedienteRepository leafExpedienteRepository,
			ExpedienteRepository expedienteRepository, VolumeRepository volumeRepository, VolumeInstanceRepository volumeInstanceRepository 
    		)
    {
        this.claseRepository            = claseRepository;
        this.baseExpedienteRepository   = baseExpedienteRepository;
        this.branchExpedienteRepository = branchExpedienteRepository;
        this.leafExpedienteRepository   = leafExpedienteRepository;
        this.expedienteRepository       = expedienteRepository;
        this.volumeRepository           = volumeRepository;
        this.volumeInstanceRepository   = volumeInstanceRepository;
        this.jcrSession                 = jcrSession;
        this.user                       = ThothSession.getUser();
        String namesFile                = "data/theNames.txt";
        try
        {
           this.namesFile         = new BufferedReader(new FileReader(namesFile));
        } catch (Exception e)
        {
           throw new IllegalStateException("No pudo abrir archivo de nombres de expedientes["+ namesFile+ "]");
        }
    }//ExpedienteGenerator constructor
    

    public int  registerExpedientes( Tenant tenant)
    {
    	int nRegistered = registerBaseExpedientes(tenant);    	
    	return nRegistered;
    }//registerExpedientes

    
    private int registerBaseExpedientes(Tenant tenant)
    {
        List<Classification> leafClasses =  claseRepository.findLeaves(tenant);
        int nExpedientes = 0;
        for (Classification classificationClass: leafClasses)
        {
           int numBaseExpedientes =  random.nextInt(10)+ 1;
           nExpedientes+= numBaseExpedientes;
           for ( int i= 0; i < numBaseExpedientes; i++) 
           {   creeBaseExpediente( tenant, user, null, classificationClass);
           }
         }
        return nExpedientes;
    }//registerBaseExpedientes
    

    private BaseExpediente creeBaseExpediente( Tenant tenant,  User user,  BranchExpediente padre, Classification classificationClass)
    {
       BaseExpediente  currentExpediente = new BaseExpediente();

       currentExpediente.setPath                (genereCode(tenant, padre, classificationClass));
       currentExpediente.setCode                (currentExpediente.getPath());
       currentExpediente.setExpedienteCode      (currentExpediente.formatCode());
       currentExpediente.setName                (generateName());
       currentExpediente.setObjectToProtect     (new ObjectToProtect());
       //currentExpediente.setCreatedBy           (user);   TODO:
       currentExpediente.setMetadata            (new SchemaValues());
       currentExpediente.setDateOpened          (LocalDateTime.now());
       currentExpediente.setDateClosed          (LocalDateTime.MAX);
       currentExpediente.setEntries             (new TreeSet<>());
       currentExpediente.setOpen                (true);
       currentExpediente.setKeywords            (generateKeywords());
     //  currentExpediente.setLocation            ("");
       currentExpediente.setMac                 (generateMac());

       currentExpediente.setClassificationClass (classificationClass);
       currentExpediente.setOwner               (padre);
    //   currentExpediente.setAdmissibleTypes    (generateAdmissibleTypes());

       creeJCRNodo( currentExpediente.getPath());
       nExpedientes++;
       
       // Cree 80% leaf expedientes, 20% branch expedientes
		int b = random.nextInt(100)+1;
		if (b > 80 )
			createBranchExpediente(tenant, currentExpediente);
		else
			createLeafExpediente(tenant, currentExpediente);
       
       
       return currentExpediente;

     }//creeExpediente

	
	
	private void createBranchExpediente(Tenant tenant, BaseExpediente base)
	{
		
	}//createBranchExpediente
	
	
	private void createLeafExpediente(Tenant tenant, BaseExpediente base)
	{
		LeafExpediente leaf = createLeaf(tenant, base);
		int b = random.nextInt(100)+1;
		if (b > 80 )
			createExpediente(tenant, leaf);
		else
			createVolume(tenant, leaf);
		
	}//createLeafExpediente
	
	
	
	private LeafExpediente createLeaf( Tenant tenant, BaseExpediente base)
	{
		return null;
	}//createLeaf
	
	 
	
	private void createExpediente( Tenant tenant, LeafExpediente leaf)
	{
		
	}//createExpediente
	
	
	
	private void createVolume(Tenant tenant, LeafExpediente leaf)
	{
	       /*  Verificar que el padre no sea un volumen
        Verificar que el padre no tenga documentos
        Decidir si el expediente es un volumen (random boolean)
        incrementar el volume number
        cerrar el volumen anterior
        Crear el volumen en el repositorio
     */
		Volume vol = createVolumeHeader(tenant, leaf);
		int v = random.nextInt(3)+1;
		for (int i=0; i < v; i++)
			createVolumeInstance(tenant, vol, i);
		
	}//createVolume
	
	
	
	private Volume createVolumeHeader(Tenant tenant, LeafExpediente leaf)
	{
		return null;
	}//createVolumeHeader
	
	
	
	private void createVolumeInstance( Tenant tenant, Volume vol, int i)
	{
		
	}//createVolumeInstance    
    
    

     private String genereCode(BaseExpediente padre, Classification classificationClass)
     {
    	 return null; //TODO:
     }//genereCode
     
          

     private String generateName()
     {
        String name = "";
        try
        {  // Los nombres han sido pre-generados
           name = namesFile.readLine();
        } catch (Exception e)
        {
            throw new IllegalStateException("No pudo abrir archivo de nombres de expediente");
        }
        return name;
     }//generateName
     
     

     private Set<String> generateKeywords()
     {
          Set<String> keywords = new TreeSet<>();
          int nKeywords = getRandom(1,3);
          for( int i = 0; i < nKeywords; i++)
              keywords.add( getRandom(KEYWORD_NAMES));

          return keywords;
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
         // Cargar la lista de tipos documentales
         // N = ramdom(1, 10)
         // for (int i = 0; i < N; i++)
         // {
         //    admissibleTypes.add(  select a doc type at random);
         // }
         //
         return admissibleTypes;
     }//generateAdmissibleTypes
     
     

     private void creeJCRNodo( String path)
     {

     }//creeJCRNodo
     
     

    private <T> T getRandom(T[] array)
    {
        return array[random.nextInt(array.length)];
    }


}//ExpedienteGenerator
