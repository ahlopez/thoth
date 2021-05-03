package com.f.thoth.backend.service;

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

import com.f.thoth.backend.data.gdoc.expediente.BaseExpediente;
import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.data.security.Permission;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.Tenant;
import com.f.thoth.backend.data.security.ThothSession;
import com.f.thoth.backend.data.security.User;
import com.f.thoth.backend.repositories.BaseExpedienteRepository;
import com.f.thoth.backend.repositories.ObjectToProtectRepository;
import com.f.thoth.backend.repositories.PermissionRepository;

@Service
public class BaseExpedienteService implements FilterableCrudService<BaseExpediente>, PermissionService<BaseExpediente>
{
	private final BaseExpedienteRepository     baseExpedienteRepository;
	private final PermissionRepository         permissionRepository;
	private final ObjectToProtectRepository    objectToProtectRepository;

	@Autowired
	public BaseExpedienteService(  BaseExpedienteRepository   baseExpedienteRepository,
			PermissionRepository       permissionRepository,
			ObjectToProtectRepository  objectToProtectRepository)
	{
		this.baseExpedienteRepository    = baseExpedienteRepository;
		this.permissionRepository        = permissionRepository;
		this.objectToProtectRepository   = objectToProtectRepository;
	}//BaseExpedienteService constructor


	@Override public Page<BaseExpediente> findAnyMatching(Optional<String> filter, Pageable pageable)
	{
		if (filter.isPresent())
		{
			String repositoryFilter = "%" + filter.get() + "%";
			return baseExpedienteRepository.findByNameLikeIgnoreCase(ThothSession.getCurrentTenant(), repositoryFilter, pageable);
		}
		else
		{
			return find(pageable);
		}
	}//findAnyMatching


	@Override public long countAnyMatching(Optional<String> filter)
	{
		if (filter.isPresent())
		{
			String repositoryFilter = "%" + filter.get() + "%";
			return baseExpedienteRepository.countByNameLikeIgnoreCase(ThothSession.getCurrentTenant(), repositoryFilter);
		}
		else
		{
			long n = baseExpedienteRepository.countAll(ThothSession.getCurrentTenant());
			return n;
		}
	}//countAnyMatching


	public Page<BaseExpediente> find(Pageable pageable)
	{ return baseExpedienteRepository.findBy(ThothSession.getCurrentTenant(), pageable);}

	@Override public JpaRepository<BaseExpediente, Long> getRepository()
	{ return baseExpedienteRepository;}

	@Override public BaseExpediente createNew(User currentUser)
	{
		BaseExpediente   baseExpediente   = new BaseExpediente();
		baseExpediente.setTenant(ThothSession.getCurrentTenant());
		baseExpediente.setCreatedBy(null /*TODO: currentUser*/);
		return baseExpediente;

	}//createNew

	@Override public BaseExpediente save(User currentUser, BaseExpediente expediente)
	{
		try
		{
			ObjectToProtect associatedObject = expediente.getObjectToProtect();
			if ( !associatedObject.isPersisted())
				objectToProtectRepository.saveAndFlush( associatedObject);

			return FilterableCrudService.super.save(currentUser, expediente);
		}
		catch (DataIntegrityViolationException e)
		{
			throw new UserFriendlyDataException("Ya hay un expediente con esa identificación. Por favor escoja un identificador único para el expediente");
		}

	}//save


	//  ----- implements HierarchicalService ------
	@Override public List<BaseExpediente>     findAll()                            {return baseExpedienteRepository.findAll(ThothSession.getCurrentTenant());}
	@Override public Optional<BaseExpediente> findById(Long id)                    {return baseExpedienteRepository.findById( id);}
	@Override public List<BaseExpediente>     findByParent( BaseExpediente owner)  {return baseExpedienteRepository.findByParent(owner == null? "[ROOT]" :owner.getOwnerPath());}
	@Override public int        countByParent ( BaseExpediente owner)              {return baseExpedienteRepository.countByParent (owner == null? "[ROOT]" :owner.getOwnerPath());}
	@Override public boolean    hasChildren   ( BaseExpediente expediente)         {return baseExpedienteRepository.countByChildren(expediente.getPath())> 0;}

	@Override public List<BaseExpediente> findByNameLikeIgnoreCase (Tenant tenant, String name)
	{ return baseExpedienteRepository.findByNameLikeIgnoreCase (tenant, name);}

	@Override public long  countByNameLikeIgnoreCase(Tenant tenant, String name)
	{ return baseExpedienteRepository.countByNameLikeIgnoreCase(tenant, name);}

	//  --------  Permission handling ---------------------

	@Override public List<Permission> findGrants( Role role)
	{
		List<BaseExpediente>  expedientes = baseExpedienteRepository.findExpedientesGranted(role);
		List<ObjectToProtect>     objects = new ArrayList<>();
		expedientes.forEach( expediente-> objects.add(expediente.getObjectToProtect()));
		return  permissionRepository.findByObjects(objects);
	}//findGrants

	@Override public List<BaseExpediente> findObjectsGranted( Role role)
	{  return baseExpedienteRepository.findExpedientesGranted(role);}

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

}//BaseExpedienteService
