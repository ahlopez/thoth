package com.f.thoth.app;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import javax.jcr.Session;

import com.f.thoth.backend.data.entity.User;
import com.f.thoth.backend.data.gdoc.classification.Classification;
import com.f.thoth.backend.data.gdoc.expediente.Expediente;
import com.f.thoth.backend.data.gdoc.metadata.DocumentType;
import com.f.thoth.backend.data.gdoc.metadata.SchemaValues;
import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.data.security.SingleUser;
import com.f.thoth.backend.data.security.Tenant;
import com.f.thoth.backend.data.security.ThothSession;
import com.f.thoth.backend.repositories.ClassificationRepository;
import com.f.thoth.backend.repositories.ExpedienteRepository;

import net.bytebuddy.asm.Advice.Return;

public class ExpedienteGenerator 
{
    private ClassificationRepository claseRepository;
    private ExpedienteRepository     expedienteRepository;
    private Session                  jcrSession;
    private User                     user;
    private final Random             random = new Random(1L);
    private int                      nExpedientes = 0;
    

	private static final String[] FIRST_NAME = new String[] { "Olga", "Amanda", "Octavia", "Cristina", "Marta", "Luis",
			"Eduardo", "Alvaro", "Arsenio", "German", "Cecilia", "Silvia", "Angela", "Maria", "Fernando", "Patricio",
			"David", "Lino", "Rafael" };

	private static final String[] LAST_NAME = new String[] { "Biden", "Castro", "Duque", "Lopez", "Perez", "Parias",
			"Umana", "Rueda", "Vergara", "Gonzalez", "Nunez", "Macias", "Gallegos", "Duarte", "Mejia", "Petro",
			"Gutierrez", "Vargas", "Puentes", "Holmes", "Macias", "Ospina", "Mutis", "Cortes", "Noble", "Rodriguez", "Arenas",
			"Trump", "Mogollon", "Samper", "Estrada", "Heredia", "Maldonado", "Reyes" };


    public ExpedienteGenerator( ClassificationRepository claseRepository, Session jcrSession, ExpedienteRepository expedienteRepository)
    {
        this.claseRepository      = claseRepository;
        this.expedienteRepository = expedienteRepository;
        this.jcrSession           = jcrSession;
        this.user                 = ThothSession.getUser();
    }//ExpedienteGenerator constructor

    public int registerExpedientes( Tenant tenant)
    {
        List<Classification> allClasses =  claseRepository.findAll();
        for (Classification classificationClass: allClasses)
        {
           int numExpedientes =  random.nextInt(10)+ 1;
           for ( int i= 0; i < numExpedientes; i++)
           {
               Expediente expediente = creeExpediente( user, null, classificationClass);
               int depthExpedientes = random.nextInt(4)+1;
               genereExpedientesHijos( depthExpedientes, expediente, classificationClass);
           }
        }
        return nExpedientes;

    }//registerExpedientes

    private void genereExpedientesHijos( int depth, Expediente parent, Classification classificationClass)
    {
       if( depth == 0)
           return;
    
       Expediente currentExpediente = creeExpediente( user, parent, classificationClass);
       if (depth == 1) 
       {
           currentExpediente.setCurrentVolume( (long)random.nextInt(1));
       }else
       {
          int numHijos = random.nextInt(5)+1;
          for(int j= 0; j< numHijos; j++)
              genereExpedientesHijos( depth-1, currentExpediente, classificationClass);
       }
    }//genereExpedientesHijos 

    private Expediente creeExpediente( User user,  Expediente padre, Classification classificationClass)
    {
       Expediente  currentExpediente = new Expediente();
    
       currentExpediente.setPath                (genereCode(padre, classificationClass));
       currentExpediente.setCode                (currentExpediente.getPath());
       currentExpediente.setExpedienteCode      (currentExpediente.formatCode());
       currentExpediente.setName                (generateName());
       currentExpediente.setObjectToProtect     (new ObjectToProtect());
       currentExpediente.setCreatedBy           (user);
       currentExpediente.setMetadata            (new SchemaValues());
       currentExpediente.setDateOpened          (LocalDateTime.now());
       currentExpediente.setDateClosed          (LocalDateTime.MAX);
       currentExpediente.setEntries             (new TreeSet<>());
       currentExpediente.setOpen                (true);
       currentExpediente.setKeywords            (generateKeywords());
       currentExpediente.setLocation            ("");
       currentExpediente.setMac                 (generateMac());
       
       currentExpediente.setClassificationClass (classificationClass);
       currentExpediente.setOwner               (padre);
       currentExpediente.setCurrentVolume       (generateVolume( padre));
       currentExpediente.setAdmissibleTypes     (generateAdmissibleTypes());
    
       creeJCRNodo( currentExpediente.getPath());
       nExpedientes++;
       return currentExpediente;
    
     }//creeExpediente
    
     private String genereCode(Expediente padre, Classification classificationClass)
     {   
    	 return padre == null?   generateExpedienteCode(classificationClass) : generateSubExpedienteCode(padre);
     }//genereCode
     
     private synchronized String generateExpedienteCode(Classification classificationClass)
     {
         String expedienteCode = classificationClass.nextExpedienteCode();
         claseRepository.saveAndFlush(classificationClass);
    	 return expedienteCode;
     }//generateExpedienteCode
     
     private String generateSubExpedienteCode( Expediente padre)
     {
    	 String expedienteCode= padre.nextSubCode();
    	 expedienteRepository.saveAndFlush(padre);
    	 return expedienteCode;
     }//generateSubExpedienteCode
     
     private String generateName()
     {
    	 //  Pregenerar los nombres en otro programa y guardarlos en el disco
    	 /*
    	  * Lea lista de palabras válidas.
    	  * Lea lista de nombres, apellidos
    	  * N = random(3, 10);
    	  * name = new StringBuilder();
    	  * for ( int i = 0; i < N; i++)
    	  * {
    	  *     if (i > 0)
    	  *        name.append(" ");
    	  *     name.append( randomWord());
    	  * }
    	  * if ( N <= 8 && randomBoolean()) 
    	  *    name.append( randomName().append( randomApellido());
    	  *    
    	  * return name.toString();   
    	  */
    	 return ""; //TODO:
     }//generateName
     
     private Set<String> generateKeywords()
     {
      	  Set<String> keywords = new TreeSet<>();
    	 /*
    	  * Lea keywordList
    	  * N = random(1,3);
    	  * for( int i = 0; i < N; i++)
    	  *     keywords.add( randomKeyword());
    	  *     
    	  * return keywords;
    	  */
    	 return keywords;
     }//generateKeywords
     
     private String generateMac()
     {
    	 // Crear un BlockChain
    	 // Generar el mac del expediente usando el mac de cada documento y el precedente del block
    	 // 
    	 
    	 return ""; //TODO:
     }//generateMac
     
     private Long generateVolume( Expediente padre)
     {
    	 // Verificar que el padre no sea un volumen
    	 // Verificar que el padre no tenga documentos
    	 // Decidir si el expediente es un volumen (random boolean)
    	 // incrementar el volume number
    	 // cerrar el volumen anterior
    	 // Crear el columen en el repositorio
    	 return 0L;//TODO:
     }//generateVolume
     
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

}//ExpedienteGenerator
