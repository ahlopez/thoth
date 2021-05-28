package com.f.thoth.ui.views.expediente;

import java.time.LocalDateTime;
import java.util.Optional;

import com.f.thoth.backend.data.gdoc.classification.Classification;
import com.f.thoth.backend.data.gdoc.expediente.BaseExpediente;
import com.f.thoth.backend.data.gdoc.expediente.Expediente;
import com.f.thoth.backend.data.gdoc.expediente.ExpedienteGroup;
import com.f.thoth.backend.data.gdoc.expediente.Nature;
import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.data.security.ThothSession;
import com.f.thoth.backend.data.security.User;
import com.f.thoth.backend.service.BaseExpedienteService;
import com.f.thoth.backend.service.ExpedienteGroupService;
import com.f.thoth.backend.service.ExpedienteLeafService;
import com.f.thoth.backend.service.SchemaService;
import com.f.thoth.ui.components.Notifier;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;

public class ExpedienteLeafEditor extends VerticalLayout
{
   private ExpedienteGroupService      expedienteGroupService;
   private ExpedienteLeafService       expedienteLeafService;
   private BaseExpedienteService       baseExpedienteService;
   private SchemaService               schemaService;

   private Expediente                  currentExpediente;
   private Classification              classificationClass;
   private User                        currentUser;

   private BaseExpedienteEditor        baseExpedienteEditor;
   private Notifier notifier           = new Notifier();


   public ExpedienteLeafEditor(  ExpedienteGroupService  expedienteGroupService,
                                 ExpedienteLeafService   expedienteLeafService,
                                 BaseExpedienteService   baseExpedienteService,
                                 SchemaService           schemaService,
                                 Classification          classificationClass
                              )
   {
     this.expedienteGroupService  = expedienteGroupService;
     this.expedienteLeafService   = expedienteLeafService;
     this.baseExpedienteService   = baseExpedienteService;
     this.schemaService           = schemaService;
     this.currentUser             = ThothSession.getCurrentUser();
     this.classificationClass     = classificationClass;
     this.currentExpediente            = null;

     add(configureEditor());
     setVisible(false);

   }//ExpedienteLeafEditor



   private BaseExpedienteEditor configureEditor()
   {
     baseExpedienteEditor = new BaseExpedienteEditor(schemaService);
     baseExpedienteEditor.addListener(BaseExpedienteEditor.SaveEvent.class,    this::saveExpediente );
     baseExpedienteEditor.addListener(BaseExpedienteEditor.CloseEvent.class,   e -> closeEditor());
     baseExpedienteEditor.addListener(BaseExpedienteEditor.DeleteEvent.class,  this::deleteExpediente);
     return baseExpedienteEditor;
   }//configureEditor


   public void addExpediente(BaseExpediente parentBase)
   {
      ExpedienteGroup parentGroup  = loadGroup( parentBase);
      editExpediente(createExpediente(parentGroup));
   }//addExpediente


   private ExpedienteGroup loadGroup( BaseExpediente base)
   {
     ExpedienteGroup group = base == null? null : expedienteGroupService.findByCode(base.getCode());
     return group;
   }//loadGroup


   private  Expediente   createExpediente(ExpedienteGroup parentGroup)
   {
     Expediente        newExpediente = new Expediente();
     LocalDateTime              now  = LocalDateTime.now();
     newExpediente.setExpedienteCode      (null);
     newExpediente.setPath                (null);
     newExpediente.setName                (" ");
     newExpediente.setObjectToProtect     (new ObjectToProtect());
     newExpediente.setCreatedBy           (currentUser);
     newExpediente.setClassificationClass (classificationClass);
     newExpediente.setMetadataSchema      (null);
     newExpediente.setMetadata            (null);
     newExpediente.setDateOpened          (now);
     newExpediente.setDateClosed          (now.plusYears(1000L));
     newExpediente.setOwnerId             ( parentGroup == null? null : parentGroup.getExpediente().getId());
     newExpediente.setOpen                (true);
     newExpediente.setKeywords            ("keyword4, keyword5, keyword6");
     newExpediente.setMac                 ("[mac]");
     return newExpediente;

   }//createExpediente


   public void editExpediente(Expediente expediente)
   {
     if (expediente == null)
     {  closeEditor();
     } else
     {
        currentExpediente = expediente;
        setVisible(true);
        baseExpedienteEditor.setVisible(true);
        BaseExpediente base = currentExpediente.getExpediente();
        String   parentCode = getParentCode( base);
        baseExpedienteEditor.editExpediente(base, parentCode);
     }
   }//editExpediente


   private String getParentCode( BaseExpediente base)
   {
      if (base != null)
      {
         Long parentId = base.getOwnerId();
         if (parentId != null)
         {
            Optional<BaseExpediente> parent = baseExpedienteService.findById( parentId);
            if(parent.isPresent())
            { return parent.get().formatCode();
            }
        }
        return base.getClassificationClass().formatCode();
      }
      return null;
    }//getParentCode


   private void saveExpediente(BaseExpedienteEditor.SaveEvent event)
   {
      BaseExpediente expediente = event.getBaseExpediente();
      if ( expediente != null)
      {
         boolean isNew = !expediente.isPersisted();
         int  duration = isNew? 6000 : 3000;
         if (currentExpediente != null)
         {  currentExpediente.setExpediente(expediente);
            expedienteLeafService.save(currentUser, currentExpediente);
            String businessCode = expediente.formatCode();
            String msg          = isNew? "Expediente creado con código "+ businessCode: "Expediente "+ businessCode+ " actualizado";
            notifier.show(msg, "notifier-accept", duration, Notification.Position.BOTTOM_CENTER);
         }
         closeEditor();
      }
   }//saveExpediente


   private void deleteExpediente(BaseExpedienteEditor.DeleteEvent event)
   {
     BaseExpediente expediente = event.getBaseExpediente();
     if (expediente != null && expediente.isOfType(Nature.EXPEDIENTE) && expediente.isPersisted())
     {
       if (!expedienteLeafService.hasChildren(currentExpediente))
       {  expedienteLeafService.delete(currentUser, currentExpediente);
          notifier.show("Expediente "+ expediente.formatCode()+ " eliminado",    "notifier-accept",  3000,  Notification.Position.BOTTOM_CENTER);
       }else
       {  notifier.error("Expediente no puede ser eliminado pues contiene documentos");
       }
     }
     closeEditor();
   }//deleteExpediente


   public void closeEditor()
   {
     baseExpedienteEditor.setVisible(false);
     fireEvent(new CloseEvent(this, currentExpediente));
     currentExpediente = null;
   }//closeEditor


   // --------------------- Events -----------------------
   public static abstract class ExpedienteLeafEditorEvent extends ComponentEvent<ExpedienteLeafEditor>
   {
     private Expediente expediente;

     protected ExpedienteLeafEditorEvent(ExpedienteLeafEditor source, Expediente expediente)
     {  super(source, false);
        this.expediente = expediente;
     }//ExpedienteLeafEditorEvent

     public Expediente       getExpedienteLeaf()  { return expediente;  }
     public BaseExpediente   getExpediente()      { return expediente == null? null: expediente.getExpediente();}

   }//ExpedienteLeafEditorEvent

   public static class SaveEvent extends ExpedienteLeafEditorEvent
   {
     SaveEvent(ExpedienteLeafEditor source, Expediente expediente)
     {  super(source, expediente);
     }
   }//SaveEvent

   public static class DeleteEvent extends ExpedienteLeafEditorEvent
   {
     DeleteEvent(ExpedienteLeafEditor source, Expediente expediente)
     {  super(source, expediente);
     }
   }//DeleteEvent

   public static class CloseEvent extends ExpedienteLeafEditorEvent
   {
     CloseEvent(ExpedienteLeafEditor source, Expediente expediente)
     {  super(source, expediente);
     }
   }//CloseEvent

   public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener)
   {
     return getEventBus().addListener(eventType, listener);
   }//addListener


}//ExpedienteLeafEditor
