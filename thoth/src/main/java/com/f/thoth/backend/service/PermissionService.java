package com.f.thoth.backend.service;

import java.util.List;
import java.util.Set;

import com.f.thoth.backend.data.entity.User;
import com.f.thoth.backend.data.security.Permission;
import com.f.thoth.backend.data.security.Role;

public interface PermissionService<T> extends HierarchicalService<T>
{
   public List<Permission>  findGrants( Role role);
   
   public List<T>           findObjectsGranted( Role role);
   
   public void grantRevoke( User currentUser, Role role, Set<Permission>newGrants, Set<Permission>newRevokes);
   
   public void grant      ( User currentUser, Role role, Set<Permission> grants);
   
   public void revoke     ( User currentUser, Role role, Set<Permission> revokes);
   
}//PermissionService
