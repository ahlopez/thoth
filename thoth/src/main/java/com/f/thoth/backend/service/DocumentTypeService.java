package com.f.thoth.backend.service;

import static com.f.thoth.Parm.TENANT;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.f.thoth.backend.data.gdoc.metadata.DocumentType;
import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.data.security.Permission;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.Tenant;
import com.f.thoth.backend.data.security.User;
import com.f.thoth.backend.repositories.DocumentTypeRepository;
import com.f.thoth.backend.repositories.ObjectToProtectRepository;
import com.f.thoth.backend.repositories.PermissionRepository;
import com.vaadin.flow.server.VaadinSession;

@Service
public class DocumentTypeService implements FilterableCrudService<DocumentType>, PermissionService<DocumentType>
{
   private final DocumentTypeRepository         documentTypeRepository;
   private final PermissionRepository           permissionRepository;
   private final ObjectToProtectRepository      objectToProtectRepository;

   @Autowired
   public DocumentTypeService(DocumentTypeRepository       documentTypeRepository,
                              PermissionRepository         permissionRepository,
                              ObjectToProtectRepository    objectToProtectRepository)
   {
      this.documentTypeRepository        = documentTypeRepository;
      this.permissionRepository          = permissionRepository;
      this.objectToProtectRepository     = objectToProtectRepository;
   }

   @Override public Page<DocumentType> findAnyMatching(Optional<String> filter, Pageable pageable)
   {
      if (filter.isPresent())
      {
         String repositoryFilter = "%" + filter.get() + "%";
         return documentTypeRepository.findByNameLikeIgnoreCase(tenant(), repositoryFilter, pageable);
      } else {
         return find(pageable);
      }
   }//findAnyMatching

   @Override public long countAnyMatching(Optional<String> filter)
   {
      if (filter.isPresent()) {
         String repositoryFilter = "%" + filter.get() + "%";
         return documentTypeRepository.countByNameLikeIgnoreCase(tenant(), repositoryFilter);
      } else {
         long n = documentTypeRepository.countAll(tenant());
         return n;
      }
   }//countAnyMatching

   public Page<DocumentType> find(Pageable pageable)
   {
      return documentTypeRepository.findBy(tenant(), pageable);
   }

   @Override public JpaRepository<DocumentType, Long> getRepository()
   {
      return documentTypeRepository;
   }

   @Override public DocumentType createNew(User currentUser)
   {
      DocumentType documentType = new DocumentType();
      documentType.setTenant(tenant());
      return documentType;
   }//createNew

   @Override public DocumentType save(User currentUser, DocumentType documentType)
   {
      try {
         return FilterableCrudService.super.save(currentUser, documentType);
      } catch (DataIntegrityViolationException e) {
         throw new UserFriendlyDataException("Ya hay un tipo documental con esa llave. Por favor escoja una llave única para el tipo documental");
      }

   }//save


   //  ----- implements HierarchicalService ------
   @Override public List<DocumentType> findAll() { return documentTypeRepository.findAll(tenant()); }

   @Override public Optional<DocumentType> findById(Long id)  { return documentTypeRepository.findById( id);}

   @Override public List<DocumentType>  findByParent  ( DocumentType owner) { return documentTypeRepository.findByParent(owner); }
   @Override public int         countByParent ( DocumentType owner) { return documentTypeRepository.countByParent (owner); }
   @Override public boolean     hasChildren   ( DocumentType documentType){ return documentTypeRepository.countByChildren(documentType) > 0; }

   @Override public List<DocumentType> findByNameLikeIgnoreCase (Tenant tenant, String name)
       { return documentTypeRepository.findByNameLikeIgnoreCase (tenant, name); }
   @Override public long  countByNameLikeIgnoreCase(Tenant tenant, String name)
       { return documentTypeRepository.countByNameLikeIgnoreCase(tenant, name); }

   //  --------  Permission handling ---------------------

   @Override public List<Permission> findGrants( Role role)
   {
      List<DocumentType> clases = documentTypeRepository.findDocumentTypesGranted(role);
      List<ObjectToProtect>    objects = new ArrayList<>();
      clases.forEach( clas-> objects.add(clas.getObjectToProtect()));
      return  permissionRepository.findByObjects(objects);
   }//findGrants

   @Override public List<DocumentType> findObjectsGranted( Role role)
   {
      return documentTypeRepository.findDocumentTypesGranted(role);
   }

   public void grantRevoke( User currentUser, Role role, Set<Permission> newGrants, Set<Permission> newRevokes)
   {
      grant ( currentUser, role, newGrants);
      revoke( currentUser, role, newRevokes);

   }//grantRevoke

   public void grant( User currentUser, Role role, Set<Permission> newGrants)
   {
      newGrants.forEach( newGrant->
      {
         ObjectToProtect objectOfDocumentType= newGrant.getObjectToProtect();
         if ( !newGrant.isPersisted())
            permissionRepository.saveAndFlush(newGrant);

         objectOfDocumentType.grant(newGrant);
         objectToProtectRepository.saveAndFlush(objectOfDocumentType);
      });
   }//grant


   public void revoke( User currentUser, Role role, Set<Permission> newRevokes)
   {
      newRevokes.forEach( newRevoke->
      {
         ObjectToProtect objectOfDocumentType= newRevoke.getObjectToProtect();
         Permission toRevoke = permissionRepository.findByRoleObject(newRevoke.getRole(),objectOfDocumentType);
         if ( toRevoke != null)
         {
            objectOfDocumentType.revoke(toRevoke);
            objectToProtectRepository.saveAndFlush(objectOfDocumentType);
            permissionRepository.delete(toRevoke);
         }
      });

   }//revoke

   private Tenant  tenant() { return (Tenant)VaadinSession.getCurrent().getAttribute(TENANT); }

}//ClaseService
