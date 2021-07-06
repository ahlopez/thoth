package com.f.thoth.backend.service;

import static com.f.thoth.Parm.CLASS_ROOT;
import static com.f.thoth.Parm.TENANT;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.f.thoth.Parm;
import com.f.thoth.backend.data.entity.util.TextUtil;
import com.f.thoth.backend.data.gdoc.classification.Classification;
import com.f.thoth.backend.data.gdoc.classification.Level;
import com.f.thoth.backend.data.gdoc.document.jackrabbit.NodeType;
import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.data.security.Permission;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.Tenant;
import com.f.thoth.backend.data.security.User;
import com.f.thoth.backend.jcr.Repo;
import com.f.thoth.backend.repositories.ClassificationRepository;
import com.f.thoth.backend.repositories.LevelRepository;
import com.f.thoth.backend.repositories.ObjectToProtectRepository;
import com.f.thoth.backend.repositories.PermissionRepository;
import com.vaadin.flow.server.VaadinSession;

@Service
public class ClassificationService implements FilterableCrudService<Classification>, PermissionService<Classification>
{
   private final ClassificationRepository       claseRepository;
   private final PermissionRepository           permissionRepository;
   private final ObjectToProtectRepository      objectToProtectRepository;
   private final LevelRepository                levelRepository;

   @Autowired
   public ClassificationService(ClassificationRepository     claseRepository,
                                PermissionRepository         permissionRepository,
                                LevelRepository              levelRepository,
                                ObjectToProtectRepository    objectToProtectRepository)
   {
      this.claseRepository               = claseRepository;
      this.permissionRepository          = permissionRepository;
      this.levelRepository               = levelRepository;
      this.objectToProtectRepository     = objectToProtectRepository;
   }//ClassificationService constructor


   public Classification findByCode( String classCode) {  return claseRepository.findByCode(tenant(), classCode); }


   @Override public Page<Classification> findAnyMatching(Optional<String> filter, Pageable pageable)
   {
      if (filter.isPresent())
      {  String repositoryFilter = "%" + filter.get() + "%";
         return claseRepository.findByNameLikeIgnoreCase(tenant(), repositoryFilter, pageable);
      } else 
      {  return find(pageable);
      }
   }//findAnyMatching

   @Override public long countAnyMatching(Optional<String> filter)
   {
      if (filter.isPresent()) 
      {  String repositoryFilter = "%" + filter.get() + "%";
         return claseRepository.countByNameLikeIgnoreCase(tenant(), repositoryFilter);
      } else 
      {  long n = claseRepository.countAll(tenant());
         return n;
      }
   }//countAnyMatching

   public Page<Classification> find(Pageable pageable)
   {  return claseRepository.findBy(tenant(), pageable);
   }

   @Override public JpaRepository<Classification, Long> getRepository()
   {  return claseRepository;
   }

   @Override public Classification createNew(User currentUser)
   {
      Classification clase = new Classification();
      clase.setTenant(tenant());
      return clase;
   }//createNew

   @Override public Classification save(User currentUser, Classification clazz)
   {
      try 
      {
         /*
         ObjectToProtect associatedObject = clazz.getObjectToProtect();
         if ( !associatedObject.isPersisted())
            objectToProtectRepository.saveAndFlush( associatedObject);
         */
         Level level = clazz.getLevel();
         if ( !level.isPersisted())
            levelRepository.saveAndFlush(level);

         Classification classification = claseRepository.save(clazz);
         saveJCRClassification(currentUser, classification);
         return classification;
      } catch (DataIntegrityViolationException e) 
      {  throw new UserFriendlyDataException("Ya hay una Clase con esa llave. Por favor escoja una llave única para la clase");
      }

   }//save
   
   
   private void saveJCRClassification(User currentUser, Classification classificationClass)
   {
      try
      {
         VaadinSession vSession = VaadinSession.getCurrent();
         String classRootPath   = (String)vSession.getAttribute(CLASS_ROOT);
         Classification parent  = classificationClass.getOwner();
         String parentPath      = parent ==  null? classRootPath: parent.getPath();
         String childCode       = classificationClass.getClassCode();
         String childName       = classificationClass.getName();
         Long   childLevel      = classificationClass.getLevel().getId();
         Node classificationJCR = addJCRChild( currentUser, parentPath, childCode, childName, childLevel);
         updateJCRClassification(classificationJCR, classificationClass);
      } catch(Exception e)
      {  throw new IllegalStateException("*** No pudo guardar estructura de clasificación en el repositorio. Razón\n"+ e.getLocalizedMessage());
      }
   }//saveJCRClassification


   private Node addJCRChild(User currentUser, String parentPath, String childCode, String childName, Long childLevel)
         throws RepositoryException, UnknownHostException
   {
      String ns= nameSpace();                    
      String childPath = parentPath+ Parm.PATH_SEPARATOR+ childCode;
      Node   child     = Repo.getInstance().addNode(childPath, childName, currentUser.getEmail());
      child.setProperty( "jcr:nodeTypeName", NodeType.CLASSIFICATION.name());
      child.setProperty( ns+ "id",    UUID.randomUUID().toString());    
      child.setProperty( ns+ "code",  childCode);     // Subclass code inside the parent class  vg 01, 02, etc
      child.setProperty( ns+ "level", childLevel);
      return child;
   }//addJCRChild
   
   
   private void updateJCRClassification(Node classificationJCR, Classification classificationClass)
   {
      try
      {
         String ns= nameSpace();
         classificationJCR.setProperty( ns+ "tenant",    classificationClass.getTenant().getId());
         classificationJCR.setProperty( ns+ "classCode", classificationClass.formatCode()); // Complete class code vg 01-01-01, 01-01-02, etc
         classificationJCR.setProperty( ns+ "opened",    TextUtil.formatDate(classificationClass.getDateOpened()));
         classificationJCR.setProperty( ns+ "closed",    TextUtil.formatDate(classificationClass.getDateClosed()));
         classificationJCR.setProperty( ns+ "open",      ""+ classificationClass.isOpen());
         classificationJCR.setProperty( ns+ "retention", classificationClass.getRetentionSchedule().getId());
         // protected SchemaValues metadata;                        //TODO:  Metadata values of the associated classification.level
      } catch(Exception e)
      {   throw new IllegalStateException("No pudo actualizar clase["+ classificationClass.formatCode()+ "]. Razón\n"+ e.getMessage());
      }
     
   }//updateJCRClassification

   //TODO: Falta implementar el delete en el repositorio JCR

   //  ----- implements HierarchicalService ------
   @Override public List<Classification> findAll() { return claseRepository.findAll(tenant()); }

   @Override public Optional<Classification> findById(Long id)  { return claseRepository.findById( id);}

   @Override public List<Classification>  findByParent  ( Classification owner) { return claseRepository.findByParent(owner); }
   @Override public int                   countByParent ( Classification owner) { return claseRepository.countByParent (owner); }
   @Override public boolean               hasChildren   ( Classification clase) { return claseRepository.countByChildren(clase) > 0; }

   @Override public List<Classification> findByNameLikeIgnoreCase (Tenant tenant, String name)
       { return claseRepository.findByNameLikeIgnoreCase (tenant, name); }
   @Override public long  countByNameLikeIgnoreCase(Tenant tenant, String name)
       { return claseRepository.countByNameLikeIgnoreCase(tenant, name); }

   //  --------  Permission handling ---------------------

   @Override public List<Permission> findGrants( Role role)
   {
      List<Classification> clases = claseRepository.findClasesGranted(role);
      List<ObjectToProtect>    objects = new ArrayList<>();
      clases.forEach( clas-> objects.add(clas.getObjectToProtect()));
      return  permissionRepository.findByObjects(objects);
   }//findGrants

   @Override public List<Classification> findObjectsGranted( Role role)
   {  return claseRepository.findClasesGranted(role);
   }

   public void grantRevoke( User currentUser, Role role, Set<Permission> newGrants, Set<Permission> newRevokes)
   {  grant ( currentUser, role, newGrants);
      revoke( currentUser, role, newRevokes);
   }//grantRevoke

   public void grant( User currentUser, Role role, Set<Permission> newGrants)
   {
      newGrants.forEach( newGrant->
      {
         ObjectToProtect objectOfClass= newGrant.getObjectToProtect();
         if ( !newGrant.isPersisted())
            permissionRepository.saveAndFlush(newGrant);

         objectOfClass.grant(newGrant);
         objectToProtectRepository.saveAndFlush(objectOfClass);
      });
   }//grant


   public void revoke( User currentUser, Role role, Set<Permission> newRevokes)
   {
      newRevokes.forEach( newRevoke->
      {
         ObjectToProtect objectOfClass= newRevoke.getObjectToProtect();
         Permission toRevoke = permissionRepository.findByRoleObject(newRevoke.getRole(),objectOfClass);
         if ( toRevoke != null)
         {
            objectOfClass.revoke(toRevoke);
            objectToProtectRepository.saveAndFlush(objectOfClass);
            permissionRepository.delete(toRevoke);
         }
      });

   }//revoke

   private Tenant  tenant()     { return (Tenant)VaadinSession.getCurrent().getAttribute(TENANT); }   
   
   private String nameSpace()   { return tenant().getName()+ ":";}


}//ClassificcationService
