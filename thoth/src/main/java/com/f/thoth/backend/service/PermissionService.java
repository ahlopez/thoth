package com.f.thoth.backend.service;

import java.util.List;
import java.util.Set;

import com.f.thoth.app.security.CurrentUser;
import com.f.thoth.backend.data.security.Role;

public interface PermissionService<T> extends HierarchicalService<T>
{
   public List<T>  findGrants( Role role);
   
   public void grant ( CurrentUser currentUser, Role role, Set<T> grants);
   
   public void revoke( CurrentUser currentUser, Role role, Set<T> revokes);
   
}//PermissionService
