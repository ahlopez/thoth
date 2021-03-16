package com.f.thoth.app;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import javax.jcr.Session;

import com.f.thoth.backend.data.gdoc.classification.Classification;
import com.f.thoth.backend.data.gdoc.expediente.BaseExpediente;
import com.f.thoth.backend.data.gdoc.expediente.BranchExpediente;
import com.f.thoth.backend.data.gdoc.expediente.Expediente;
import com.f.thoth.backend.data.gdoc.expediente.LeafExpediente;
import com.f.thoth.backend.data.gdoc.expediente.Volume;
import com.f.thoth.backend.data.gdoc.expediente.VolumeInstance;
import com.f.thoth.backend.data.gdoc.metadata.DocumentType;
import com.f.thoth.backend.data.gdoc.metadata.SchemaValues;
import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.data.security.Tenant;
import com.f.thoth.backend.data.security.ThothSession;
import com.f.thoth.backend.data.security.User;
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
		int nExpedientes = 0;
		List<Classification> leafClasses =  claseRepository.findLeaves(tenant);
		for (Classification classificationClass: leafClasses)
		{
			int nExpedientesInClass =  random.nextInt(10)+ 1;
			for ( int i= 0; i < nExpedientesInClass; i++)
			{   creeExpediente( tenant, user, classificationClass, null);
			}
			nExpedientes+= nExpedientesInClass;         
		}
		return nExpedientes;
	}//registerExpedientes


	private BaseExpediente creeExpediente( Tenant tenant, User user, Classification classificationClass, BranchExpediente owner)
	{
		int branchProbability =  random.nextInt(100);
		if ( branchProbability < 20)
		{
			BranchExpediente branch = creeBranchExpediente(tenant, user, classificationClass, owner);
			return branch.getExpediente();
		}
		LeafExpediente leaf = creeLeafExpediente  (tenant, user, classificationClass, owner);
		return leaf.getExpediente();

	}//creeRootExpediente


	private BranchExpediente creeBranchExpediente(Tenant tenant, User user, Classification classificationClass, BranchExpediente owner)
	{
		BaseExpediente   base   = createBase( classificationClass,  owner);
		BranchExpediente branch = new BranchExpediente();
		branch.setExpediente(base);
		int nChildren = random.nextInt(10)+1;
		for( int i=0; i< nChildren; i++)
		{
		   BaseExpediente child = creeExpediente( tenant, user, classificationClass, branch);
		   branch.addChild(child);
		}
		branchExpedienteRepository.saveAndFlush(branch);
		
		return branch;

	}//creeBranchExpediente
	
	
	
	private LeafExpediente	creeLeafExpediente(Tenant tenant, User user, Classification classificationClass, BranchExpediente owner)
	{
		BaseExpediente   base   = createBase( classificationClass,  owner);
		LeafExpediente   leaf   = new LeafExpediente();
		leaf.setExpediente(base);
		Set<DocumentType> admissibleTypes = generateAdmissibleTypes();
		leaf.setAdmissibleTypes(admissibleTypes);
		int  volProbability = random.nextInt(100);
		if ( volProbability < 15)
			createVolume(leaf);
		else
			createExpediente(leaf);
		
		leafExpedienteRepository.saveAndFlush(leaf);
		return leaf;
		
	}//creeLeafExpediente

	
	private BaseExpediente createBase(Classification classificationClass, BranchExpediente parent)
	{
		BaseExpediente base     =   new BaseExpediente();
		base.setPath                (genereCode(base, classificationClass));
		base.setCode                (base.getPath());
		base.setExpedienteCode      (base.formatCode());
		base.setName                (generateName());
		base.setObjectToProtect     (new ObjectToProtect());
		base.setCreatedBy           (user);
		base.setClassificationClass (classificationClass);
		base.setMetadata            (new SchemaValues());
		base.setDateOpened          (LocalDateTime.now());
		base.setDateClosed          (LocalDateTime.MAX);
		base.setOwner               (parent);
		base.setEntries             (new TreeSet<>());
		base.setOpen                (true);
		base.setKeywords            (generateKeywords());
		base.setMac                 (generateMac());
		baseExpedienteRepository.saveAndFlush(base);

		creeJCRNodo( base.getPath());
		nExpedientes++;
		return base;
		
	}//createBase




	private void createExpediente(LeafExpediente leaf)
	{
		Expediente expediente = new Expediente();
		expediente.setExpediente( leaf);
		expediente.setPath(leaf.getExpediente().getPath());
		expediente.setLocation( "[LOCATION]");
		expedienteRepository.saveAndFlush(expediente);

	}//createExpediente



	private void createVolume(LeafExpediente leaf)
	{
		Volume volume = createVolumeHeader(leaf);
		VolumeInstance currentInstance = null;
		int nInstances = random.nextInt(3)+1;
		volume.setCurrentInstance(nInstances);
		volumeRepository.saveAndFlush(volume);
		for (int instance=1; instance <= nInstances; instance++)
			currentInstance = createVolumeInstance(volume, instance);

		volumeRepository.saveAndFlush(volume);
		currentInstance.setOpen(true);
		currentInstance.setDateOpened(LocalDateTime.now().minusDays((long)random.nextInt(1000)));
		volumeInstanceRepository.saveAndFlush(currentInstance);
	}//createVolume



	private Volume createVolumeHeader(LeafExpediente leaf)
	{
		Volume volume = new Volume();
		volume.setExpediente(leaf);
		volume.setCurrentInstance(0);
		return volume;
	}//createVolumeHeader



	private VolumeInstance createVolumeInstance( Volume vol, int i)
	{
		VolumeInstance instance = new VolumeInstance();
		instance.setVolume(vol);
		instance.setInstance(i);
		instance.setPath(vol.getExpediente().getExpediente().getPath());
		instance.setDateOpened(null);
		instance.setDateClosed(null);
		instance.setOpen(false);
		vol.addInstance(instance);
		volumeInstanceRepository.saveAndFlush(instance);
		return instance;
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



	private String generateKeywords()
	{
		StringBuilder keywords = new StringBuilder();
		int nKeywords = getRandom(1,3);
		for( int i = 0; i < nKeywords; i++)
		{
			if (i > 0)
				keywords.append(",");
			
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
