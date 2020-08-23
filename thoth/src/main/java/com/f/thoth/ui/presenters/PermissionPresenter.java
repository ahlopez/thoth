package com.f.thoth.ui.presenters;

import java.util.function.Consumer;

import javax.management.relation.Role;
import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolationException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;

import com.f.thoth.app.HasLogger;
import com.f.thoth.app.security.CurrentUser;
import com.f.thoth.backend.data.entity.HierarchicalEntity;
import com.f.thoth.backend.service.HierarchicalService;
import com.f.thoth.backend.service.UserFriendlyDataException;
import com.f.thoth.ui.views.HasNotifications;

public class PermissionPresenter<E extends HierarchicalEntity<E>>  implements HasLogger
{
   public enum Message
   {
       DATA_INTEGRITY  ("La operación no puede ser ejecutada pues dañaría las referencias a otras entidades en la base de datos."),
       CONCURRENCY     ("Alguien más ha actualizado la información. Por favor refresque y vuelva a intentarlo."),
       NOT_FOUND       ("No se encontró la entidad solicitada."),
       REQUIRED_FIELDS ("Por favor provea los campos marcados como requeridos antes de continuar.");

       private String msg = "";
       private Message( String msg) { this.msg = msg;}
       private String text() { return msg;}

   }//Message

   private final HierarchicalService<E> service;
   private final CurrentUser            currentUser;
   private final HasNotifications       view;

   public PermissionPresenter(HierarchicalService<E> service, CurrentUser currentUser, HasNotifications view)
   {
      this.service     = service;
      this.currentUser = currentUser;
      this.view        = view;

   }//PermitPresenter

   public Role loadRole( Long id) { return null; }

   public void grant(E entity, Role role, Consumer<E> onSuccess, Consumer<E> onFail)
   {
      if (executeOperation(() -> service.grant(currentUser.getUser(), entity, role)))
         onSuccess.accept(entity);
      else
         onFail.accept(entity);

   }//grant



   public void revoke(E entity, Role role, Consumer<E> onSuccess, Consumer<E> onFail)
   {
      if (executeOperation(() -> service.revoke(currentUser.getUser(), entity, role)))
         onSuccess.accept(entity);
      else
         onFail.accept(entity);

   }//revoke

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


   private void saveEntity(E entity) { service.save(currentUser.getUser(), entity); }


   public boolean loadEntity(Long id, Consumer<E> onSuccess) { return executeOperation(() -> onSuccess.accept(service.load(id))); }

}//PermitPresenter
