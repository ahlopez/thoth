package com.f.thoth.backend.service;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.f.thoth.backend.data.entity.User;
import com.f.thoth.backend.data.security.Role;

public interface PermissionService<T> extends HierarchicalService<T>
{
   public List<T>  findGrants( Role role);
   
   public void grantRevoke( User currentUser, Role role, Set<T>newGrants, Set<T>newRevokes);
   
   public void grant ( User currentUser, Role role, Collection<T> grants);
   
   public void revoke( User currentUser, Role role, Collection<T> revokes);
   
}//PermissionService
