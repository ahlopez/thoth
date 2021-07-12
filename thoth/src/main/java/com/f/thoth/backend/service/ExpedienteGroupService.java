package com.f.thoth.backend.service;

import static com.f.thoth.Parm.EXPEDIENTE_ROOT;
import static com.f.thoth.Parm.TENANT;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
import com.f.thoth.backend.data.gdoc.document.jackrabbit.NodeType;
import com.f.thoth.backend.data.gdoc.expediente.BaseExpediente;
import com.f.thoth.backend.data.gdoc.expediente.ExpedienteGroup;
import com.f.thoth.backend.data.gdoc.expediente.Nature;
import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.data.security.Permission;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.Tenant;
import com.f.thoth.backend.data.security.User;
import com.f.thoth.backend.jcr.Repo;
import com.f.thoth.backend.repositories.ExpedienteGroupRepository;
import com.f.thoth.backend.repositories.ObjectToProtectRepository;
import com.f.thoth.backend.repositories.PermissionRepository;
import com.vaadin.flow.server.VaadinSession;

@Service
public class ExpedienteGroupService implements FilterableCrudService<ExpedienteGroup>, PermissionService<ExpedienteGroup>
{
   private final ExpedienteGroupRepository   expedienteGroupRepository;
   private final PermissionRepository         permissionRepository;
   private final ObjectToProtectRepository    objectToProtectRepository;

   @Autowired
   public ExpedienteGroupService(ExpedienteGroupRepository   expedienteGroupRepository,
                                  PermissionRepository         permissionRepository,
                                  ObjectToProtectRepository    objectToProtectRepository)
   {
      this.expedienteGroupRepository  = expedienteGroupRepository;
      this.permissionRepository        = permissionRepository;
      this.objectToProtectRepository   = objectToProtectRepository;
   }//ExpedienteGroupService constructor


   @Override public Page<ExpedienteGroup> findAnyMatching(Optional<String> filter, Pageable pageable)
   {
      if (filter.isPresent())
      {
         String repositoryFilter = "%" + filter.get() + "%";
         return expedienteGroupRepository.findByNameLikeIgnoreCase(tenant(), repositoryFilter, pageable);
      } else {
         return find(pageable);
      }
   }//findAnyMatching


   @Override public long countAnyMatching(Optional<String> filter)
   {
      if (filter.isPresent()) {
         String repositoryFilter = "%" + filter.get() + "%";
         return expedienteGroupRepository.countByNameLikeIgnoreCase(tenant(), repositoryFilter);
      } else {
         long n = expedienteGroupRepository.countAll(tenant());
         return n;
      }
   }//countAnyMatching


   public Page<ExpedienteGroup> find(Pageable pageable)
       { return expedienteGroupRepository.findBy(tenant(), pageable);}

   public ExpedienteGroup  findByCode(String code) { return expedienteGroupRepository.findByCode(code);}
/*
   public Optional<ExpedienteGroup>  findParent(String parentPath) { return expedienteGroupRepository.findParent(tenant(),parentPath);}
*/
   @Override public JpaRepository<ExpedienteGroup, Long> getRepository()
       { return expedienteGroupRepository; }

   @Override public ExpedienteGroup createNew(User currentUser)
   {
      BaseExpediente   baseExpediente   = new BaseExpediente();
      baseExpediente.setTenant(tenant());
      baseExpediente.setCreatedBy(null /*TODO: currentUser*/);

      ExpedienteGroup expedienteGroup = new ExpedienteGroup();
      expedienteGroup.setExpediente(baseExpediente);
      return expedienteGroup;

   }//createNew

   @Override public ExpedienteGroup save(User currentUser, ExpedienteGroup expediente)
   {
      try
      {  ExpedienteGroup group = FilterableCrudService.super.save(currentUser, expediente);
         saveJCRExpedienteGroup(currentUser, group);
         return group;
      } catch (DataIntegrityViolationException e) {
         throw new UserFriendlyDataException("Ya hay un expediente con esa identificación. Por favor escoja un identificador único para el expediente");
      }

   }//save


   private void saveJCRExpedienteGroup(User currentUser, ExpedienteGroup group)
   {
      try
      {
         VaadinSession vSession   = VaadinSession.getCurrent();
         String      parentPath   = (String)vSession.getAttribute(EXPEDIENTE_ROOT);
         Long          parentId   = group.getOwnerId();
         if (parentId != null)
         {   Optional<ExpedienteGroup> parent = expedienteGroupRepository.findById(parentId);
             if (parent.isPresent())
             {  parentPath =  parent.get().getPath();
             }
         }
         Node groupJCR = addJCRChild( currentUser, parentPath, group);
         updateJCRExpedienteGroup(groupJCR, group);
      } catch(Exception e)
      {
         throw new IllegalStateException("*** No pudo guardar estructura de clasificación en el repositorio. Razón\n"+ e.getLocalizedMessage());
      }
   }//saveJCRExpedienteGroup


   private Node addJCRChild(User currentUser, String parentPath,ExpedienteGroup group)
         throws RepositoryException, UnknownHostException
   {
      String namespace      = currentUser.getTenant().getName()+ ":";
      String expedienteCode = group.getExpedienteCode();
      String      childPath = parentPath+ Parm.PATH_SEPARATOR+ expedienteCode;
      Node            child = Repo.getInstance().addNode(childPath, group.getName(), currentUser.getEmail());
      child.setProperty("jcr:nodeTypeName", NodeType.EXPEDIENTE.name());
      child.setProperty(namespace+ "code",  expedienteCode);
      return child;
   }//addJCRChild


   private void updateJCRExpedienteGroup(Node groupJCR, ExpedienteGroup group)
   {
      try
      {
         String namespace      = group.getTenant().getName()+ ":";
         groupJCR.setProperty(namespace+ "type",      Nature.GRUPO.toString());
         groupJCR.setProperty(namespace+ "class",     group.getClassificationClass().formatCode());
         groupJCR.setProperty(namespace+ "schema",    group.getMetadataSchema().getCode());
         groupJCR.setProperty(namespace+ "open",      group.isOpen());
         groupJCR.setProperty(namespace+ "opened",    TextUtil.formatDateTime(group.getDateOpened()));
         groupJCR.setProperty(namespace+ "closed",    TextUtil.formatDateTime(group.getDateClosed()));
         groupJCR.setProperty(namespace+ "location",  group.getLocation());
         String keywords = group.getKeywords();
         if (keywords != null)
         {  String[] keys = keywords.split(Parm.VALUE_SEPARATOR);
            for( String k: keys)
            {  groupJCR.setProperty(namespace+ "keywords", k);
            }
         }

         // TODO: Revisar como incorporar los campos objectToProtect, metadata, expedienteIndex, mac en el repositorio
         // protected ObjectToProtect   objectToProtect;            // Associated security object
         // protected SchemaValues      metadata;                   // Metadata values of the associated expediente
         // protected ExpedienteIndex   expedienteIndex;            // Expediente index entries
         // protected String            mac;                        // Message authentication code

      } catch(Exception e)
      {   throw new IllegalStateException("No pudo actualizar grupo de expedientes["+ group.formatCode()+ "] en el repositorio. Razón\n"+ e.getMessage());
      }

   }//updateJCRExpedienteGroup


   //TODO: Falta implementar el delete en el repositorio JCR


   //  ----- implements HierarchicalService ------
   @Override public List<ExpedienteGroup>     findAll()                            {return expedienteGroupRepository.findAll(tenant()); }
   @Override public Optional<ExpedienteGroup> findById(Long id)                    {return expedienteGroupRepository.findById( id);}
   @Override public List<ExpedienteGroup>     findByParent( ExpedienteGroup owner){return expedienteGroupRepository.findByParent(owner.getOwnerId()); }
   @Override public int        countByParent ( ExpedienteGroup owner)              {return expedienteGroupRepository.countByParent (owner.getOwnerId()); }
   @Override public boolean    hasChildren   ( ExpedienteGroup expediente)         {return expedienteGroupRepository.countByChildren(expediente.getId())> 0;}

   @Override public List<ExpedienteGroup> findByNameLikeIgnoreCase (Tenant tenant, String name)
                          { return expedienteGroupRepository.findByNameLikeIgnoreCase (tenant, name); }

   @Override public long  countByNameLikeIgnoreCase(Tenant tenant, String name)
                          { return expedienteGroupRepository.countByNameLikeIgnoreCase(tenant, name); }

   //  --------  Permission handling ---------------------

   @Override public List<Permission> findGrants( Role role)
   {
      List<ExpedienteGroup> expedientes = expedienteGroupRepository.findExpedientesGranted(role);
      List<ObjectToProtect>     objects = new ArrayList<>();
      expedientes.forEach( expediente-> objects.add(expediente.getObjectToProtect()));
      return  permissionRepository.findByObjects(objects);
   }//findGrants

   @Override public List<ExpedienteGroup> findObjectsGranted( Role role)
             {  return expedienteGroupRepository.findExpedientesGranted(role); }

   public void grantRevoke( User currentUser, Role role, Set<Permission> newGrants, Set<Permission> newRevokes)
   {
      grant ( currentUser, role, newGrants);
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

   private Tenant  tenant() { return (Tenant)VaadinSession.getCurrent().getAttribute(TENANT); }

}//ExpedienteGroupService
