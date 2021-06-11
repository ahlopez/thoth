package com.f.thoth.ui.views.security.permission;

import static com.f.thoth.Parm.TENANT;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;

import com.f.thoth.app.HasLogger;
import com.f.thoth.app.security.CurrentUser;
import com.f.thoth.backend.data.entity.HierarchicalEntity;
import com.f.thoth.backend.data.security.User;
import com.f.thoth.backend.data.security.Permission;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.Tenant;
import com.f.thoth.backend.service.PermissionService;
import com.f.thoth.ui.components.Period;
import com.f.thoth.ui.views.HasNotifications;
import com.vaadin.flow.server.VaadinSession;

public class PermissionPresenter<E extends HierarchicalEntity<E>>  implements HasLogger
{
   private final PermissionService<E>     service;
   private final Tenant                   tenant;
   private final User                     currentUser;

   @Autowired
   public PermissionPresenter(PermissionService<E> service, CurrentUser currentUser, HasNotifications view)
   {
      this.service           = service;
      this.currentUser       = currentUser.getUser();
      VaadinSession vSession = VaadinSession.getCurrent();
      this.tenant = vSession == null? null: (Tenant)vSession.getAttribute(TENANT);

   }//PermissionPresenter


   public List<E> loadGrants( Role role )
   {
      return service.findObjectsGranted(role);
   }//loadGrants


   public void grantRevoke( Collection<E> objectsGranted, Role role, Period period )
   {
      List<Permission> oldGrants  = service.findGrants(role);

      Set<Permission> newGrants  = getNewGrants ( objectsGranted, oldGrants, role, period);
      Set<Permission> newRevokes = getNewRevokes( objectsGranted, oldGrants, role, period);
      service.grantRevoke(currentUser, role, newGrants, newRevokes);

   }//grantRevoke


   private Set<Permission> getNewGrants( Collection<E> objectsGranted, List<Permission> oldGrants, Role role, Period period)
   {
      Set<Permission> newGrants  = new TreeSet<>();
      objectsGranted.forEach (obj ->
      {
         boolean nuevo= true;
         for (Permission oGrant: oldGrants)
         {
            if ( oGrant.objectToProtect.equals( obj.getObjectToProtect()))
            {
               nuevo = false;
               break;
            }
         }
         if (nuevo)
            newGrants.add(  new Permission(tenant, role, obj.getObjectToProtect(), period.getFromDate(), period.getToDate()));
      });

      return newGrants;

   }//getNewGrants


   private Set<Permission> getNewRevokes( Collection<E> objectsGranted, List<Permission> oldGrants, Role role, Period period)
   {
      Set<Permission> newRevokes = new TreeSet<>();
      oldGrants.forEach (oldPermit ->
      {
         boolean still= false;
         for (E  obj: objectsGranted)
         {
            if ( oldPermit.getObjectToProtect().equals(obj.getObjectToProtect()))
            {
               still = true;
               break;
            }
         }
         if (!still)
            newRevokes.add(  new Permission(tenant, role, oldPermit.getObjectToProtect(), period.getFromDate(), period.getToDate()));
      });

      return newRevokes;

   }//getNewRevokes


   /*
   private boolean executeOperation(Runnable operation)
   {
      try {
         operation.run();
         return true;
      } catch (UserFriendlyDataException e) {
         // Commit failed because of application-level data constraints
         consumeError(e, e.getMessage(), true);
      } catch (DataIntegrityViolationException e) {
         // Commit failed because of validation errors
         consumeError( e, Message.DATA_INTEGRITY.text(), true);
      } catch (OptimisticLockingFailureException e) {
         // Concurrent update
         consumeError(e, Message.CONCURRENCY.text(), true);
      } catch (EntityNotFoundException e) {
         // Could not find entity
         consumeError(e, Message.NOT_FOUND.text(), false);
      } catch (ConstraintViolationException e) {
         // Required fields missing
         consumeError(e, Message.REQUIRED_FIELDS.text(), false);
      }
      return false;
   }//executeOperation



   private void consumeError(Exception e, String message, boolean isPersistent)
   {
      getLogger().debug(message, e);
      view.showNotification(message, isPersistent);
   }//consumeError
    */

}//PermitPresenter
