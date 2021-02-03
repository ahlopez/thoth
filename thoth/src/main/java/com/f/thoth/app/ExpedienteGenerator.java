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

public class ExpedienteGenerator 
{
    private ClassificationRepository claseRepository;
    private ExpedienteRepository     expedienteRepository;
    private Session                  jcrSession;
    private User                     user;
    private final Random             random = new Random(1L);
    private int                      nExpedientes = 0;

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
    	 return ""; //TODO:
     }//genereCode
     
     private String generateName()
     {
    	 return ""; //TODO:
     }//generateName
     
     private Set<String> generateKeywords()
     {
    	 return new TreeSet<>(); //TODO:
     }//generateKeywords
     
     private String generateMac()
     {
    	 return ""; //TODO:
     }//generateMac
     
     private Long generateVolume( Expediente padre)
     {
    	 return 0L;//TODO:
     }//generateVolume
     
     private Set<DocumentType> generateAdmissibleTypes()
     {
    	 return new TreeSet<>(); //TODO:
     }//generateAdmissibleTypes
     
     private void creeJCRNodo( String path)
     {
    	 
     }//creeJCRNodo

}//ExpedienteGenerator
