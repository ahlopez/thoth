package com.f.thoth.ui.crud;

import java.util.function.Consumer;

import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolationException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;

import com.f.thoth.app.HasLogger;
import com.f.thoth.app.security.CurrentUser;
import com.f.thoth.backend.data.entity.AbstractEntity;
import com.f.thoth.backend.service.CrudService;
import com.f.thoth.backend.service.UserFriendlyDataException;
import com.f.thoth.ui.utils.messages.CrudErrorMessage;
import com.f.thoth.ui.views.HasNotifications;

public class CrudEntityPresenter<E extends AbstractEntity>  implements HasLogger 
{

   private final CrudService<E> crudService;

   private final CurrentUser currentUser;

   private final HasNotifications view;

   public CrudEntityPresenter(CrudService<E> crudService, CurrentUser currentUser, HasNotifications view) 
   {
      this.crudService = crudService;
      this.currentUser = currentUser;
      this.view = view;
   }//CrudEntityPresenter

   public void delete(E entity, Consumer<E> onSuccess, Consumer<E> onFail) 
   {
      if (executeOperation(() -> crudService.delete(currentUser.getUser(), entity))) {
         onSuccess.accept(entity);
      } else {
         onFail.accept(entity);
      }
   }//delete

   public void save(E entity, Consumer<E> onSuccess, Consumer<E> onFail) 
   {
      if (executeOperation(() -> saveEntity(entity))) {
         onSuccess.accept(entity);
      } else {
         onFail.accept(entity);
      }
   }//save

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
         consumeError( e, CrudErrorMessage.OPERATION_PREVENTED_BY_REFERENCES, true);
      } catch (OptimisticLockingFailureException e) {
         consumeError(e, CrudErrorMessage.CONCURRENT_UPDATE, true);
      } catch (EntityNotFoundException e) {
         consumeError(e, CrudErrorMessage.ENTITY_NOT_FOUND, false);
      } catch (ConstraintViolationException e) {
         consumeError(e, CrudErrorMessage.REQUIRED_FIELDS_MISSING, false);
      }
      return false;
   }//executeOperation

   private void consumeError(Exception e, String message, boolean isPersistent) 
   {
      getLogger().debug(message, e);
      view.showNotification(message, isPersistent);
   }//consumeError

   private void saveEntity(E entity) { crudService.save(currentUser.getUser(), entity);}

   public boolean loadEntity(Long id, Consumer<E> onSuccess) 
   {
      return executeOperation(() -> onSuccess.accept(crudService.load(id)));
   }
}//CrudEntityPresenter
