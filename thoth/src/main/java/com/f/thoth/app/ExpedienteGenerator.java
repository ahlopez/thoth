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
import com.f.thoth.backend.data.gdoc.expediente.BranchExpediente;
import com.f.thoth.backend.data.gdoc.expediente.BaseExpediente;
import com.f.thoth.backend.data.gdoc.metadata.DocumentType;
import com.f.thoth.backend.data.gdoc.metadata.SchemaValues;
import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.data.security.Tenant;
import com.f.thoth.backend.data.security.ThothSession;
import com.f.thoth.backend.repositories.ClassificationRepository;
import com.f.thoth.backend.repositories.ExpedienteRepository;

public class ExpedienteGenerator
{
    private ClassificationRepository claseRepository;
    private ExpedienteRepository     expedienteRepository;
    private Session                  jcrSession;
    private User                     user;
    private final Random             random = new Random(1L);
    private int                      nExpedientes = 0;
    private BufferedReader           namesFile;


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


    public ExpedienteGenerator( ClassificationRepository claseRepository, Session jcrSession, ExpedienteRepository expedienteRepository)
    {
        this.claseRepository      = claseRepository;
        this.expedienteRepository = expedienteRepository;
        this.jcrSession           = jcrSession;
        this.user                 = ThothSession.getUser();
        try
        {
           this.namesFile            = new BufferedReader(new FileReader("word/CREA_total.TXT"));
        } catch (Exception e)
        {
           throw new IllegalStateException("No pudo abrir archivo de nombres de expedientes");
        }
    }//ExpedienteGenerator constructor

    public int registerExpedientes( Tenant tenant)
    {
        List<Classification> allClasses =  claseRepository.findAll();
        for (Classification classificationClass: allClasses)
        {
           int numExpedientes =  random.nextInt(10)+ 1;
           for ( int i= 0; i < numExpedientes; i++)
           {
               BaseExpediente expediente = creeExpediente( user, null, classificationClass);
               int depthExpedientes = random.nextInt(4)+1;
               genereExpedientesHijos( depthExpedientes, expediente, classificationClass);
           }
        }
        return nExpedientes;

    }//registerExpedientes

    private void genereExpedientesHijos( int depth, BaseExpediente parent, Classification classificationClass)
    {
       if( depth == 0)
           return;

       BaseExpediente currentExpediente = creeExpediente( user, parent, classificationClass);
       if (depth == 1)
       {
           currentExpediente.setCurrentVolume( random.nextInt(1));
       }else
       {
          int numHijos = random.nextInt(5)+1;
          for(int j= 0; j< numHijos; j++)
              genereExpedientesHijos( depth-1, currentExpediente, classificationClass);
       }
    }//genereExpedientesHijos

    private BaseExpediente creeExpediente( User user,  BranchExpediente padre, Classification classificationClass)
    {
       BaseExpediente  currentExpediente = new BaseExpediente();

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
     //  currentExpediente.setLocation            ("");
       currentExpediente.setMac                 (generateMac());

       currentExpediente.setClassificationClass (classificationClass);
       currentExpediente.setOwner               (padre);
    //   currentExpediente.setAdmissibleTypes     (generateAdmissibleTypes());

       creeJCRNodo( currentExpediente.getPath());
       nExpedientes++;
       return currentExpediente;

     }//creeExpediente

     private String genereCode(BaseExpediente padre, Classification classificationClass)
     {
         return padre == null?   generateExpedienteCode(classificationClass) : generateSubExpedienteCode(padre);
     }//genereCode

     private synchronized String generateExpedienteCode(Classification classificationClass)
     {
         String expedienteCode = classificationClass.nextExpedienteCode();
         claseRepository.saveAndFlush(classificationClass);
         return expedienteCode;
     }//generateExpedienteCode

     private String generateSubExpedienteCode( BaseExpediente padre)
     {
         String expedienteCode= padre.nextSubCode();
         expedienteRepository.saveAndFlush(padre);
         return expedienteCode;
     }//generateSubExpedienteCode

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

     private Integer generateVolume( BranchExpediente padre)
     {
        /*  Verificar que el padre no sea un volumen
            Verificar que el padre no tenga documentos
            Decidir si el expediente es un volumen (random boolean)
            incrementar el volume number
            cerrar el volumen anterior
            Crear el volumen en el repositorio
         */
         return 0;//TODO:
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

    private <T> T getRandom(T[] array)
    {
        return array[random.nextInt(array.length)];
    }


}//ExpedienteGenerator
