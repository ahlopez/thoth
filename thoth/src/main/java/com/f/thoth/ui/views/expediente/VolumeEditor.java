package com.f.thoth.ui.views.expediente;

import java.time.LocalDateTime;
import java.util.Optional;

import com.f.thoth.backend.data.gdoc.classification.Classification;
import com.f.thoth.backend.data.gdoc.expediente.BaseExpediente;
import com.f.thoth.backend.data.gdoc.expediente.ExpedienteGroup;
import com.f.thoth.backend.data.gdoc.expediente.Nature;
import com.f.thoth.backend.data.gdoc.expediente.Volume;
import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.data.security.ThothSession;
import com.f.thoth.backend.data.security.User;
import com.f.thoth.backend.service.BaseExpedienteService;
import com.f.thoth.backend.service.ExpedienteGroupService;
import com.f.thoth.backend.service.SchemaService;
import com.f.thoth.backend.service.VolumeService;
import com.f.thoth.ui.components.Notifier;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;

public class VolumeEditor extends VerticalLayout
{
   private ExpedienteGroupService      expedienteGroupService;
   private VolumeService               volumeService;
   private BaseExpedienteService       baseExpedienteService;
   private SchemaService               schemaService;

   private Volume                      currentVolume;
   private Classification              classificationClass;
   private User                        currentUser;

   private BaseExpedienteEditor        baseExpedienteEditor;
   private Notifier notifier           = new Notifier();


   public VolumeEditor( ExpedienteGroupService  expedienteGroupService,
                        VolumeService           volumeService,
                        BaseExpedienteService   baseExpedienteService,
                        SchemaService           schemaService,
                        Classification          classificationClass
                      )
   {
     this.expedienteGroupService  = expedienteGroupService;
     this.volumeService           = volumeService;
     this.baseExpedienteService   = baseExpedienteService;
     this.schemaService           = schemaService;
     this.currentUser             = ThothSession.getCurrentUser();
     this.classificationClass     = classificationClass;
     this.currentVolume           = null;

     add(configureEditor());
     setVisible(false);

   }//VolumeEditor



   private BaseExpedienteEditor configureEditor()
   {
     baseExpedienteEditor = new BaseExpedienteEditor(schemaService);
     baseExpedienteEditor.addListener(BaseExpedienteEditor.SaveEvent.class,    this::saveVolume );
     baseExpedienteEditor.addListener(BaseExpedienteEditor.CloseEvent.class,   e -> closeEditor());
     baseExpedienteEditor.addListener(BaseExpedienteEditor.DeleteEvent.class,  this::deleteVolume);
     return baseExpedienteEditor;
   }//configureEditor


   public void addVolume(BaseExpediente parentBase)
   {
      ExpedienteGroup parentGroup  = loadGroup( parentBase);
      editVolume(createVolume(parentGroup));
   }//addVolume


   private ExpedienteGroup loadGroup( BaseExpediente base)
   {
     ExpedienteGroup group = base == null? null : expedienteGroupService.findByCode(base.getCode());
     return group;
   }//loadGroup


   private  Volume   createVolume(ExpedienteGroup parentGroup)
   {
     Volume                newVolume = new Volume();
     LocalDateTime              now  = LocalDateTime.now();
     newVolume.setExpedienteCode      (null);
     newVolume.setPath                (null);
     newVolume.setName                (" ");
     newVolume.setObjectToProtect     (new ObjectToProtect());
     newVolume.setCreatedBy           (currentUser);
     newVolume.setClassificationClass (classificationClass);
     newVolume.setMetadataSchema      (null);
     newVolume.setMetadata            (null);
     newVolume.setDateOpened          (now);
     newVolume.setDateClosed          (now.plusYears(1000L));
     newVolume.setOwnerId             ( parentGroup == null? null : parentGroup.getExpediente().getId());
     newVolume.setOpen                (true);
     newVolume.setKeywords            ("keyword1, keyword2, keyword3");
     newVolume.setMac                 ("[mac]");
     return newVolume;

   }//createVolume


   public void editVolume(Volume volume)
   {
     if (volume == null)
     {  closeEditor();
     } else
     {
        currentVolume = volume;
        setVisible(true);
        baseExpedienteEditor.setVisible(true);
        BaseExpediente base = currentVolume.getExpediente();
        String   parentCode = getParentCode( base);
        baseExpedienteEditor.editExpediente(base, parentCode);
     }
   }//editVolume


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


   private void saveVolume(BaseExpedienteEditor.SaveEvent event)
   {
      BaseExpediente expediente = event.getBaseExpediente();
      if ( expediente != null)
      {
         boolean isNew = !expediente.isPersisted();
         int  duration = isNew? 6000 : 3000;
         if (currentVolume != null)
         {  currentVolume.setExpediente(expediente);
            volumeService.save(currentUser, currentVolume);
            String businessCode = expediente.formatCode();
            String msg          = isNew? "Volumen creado con código "+ businessCode: "Volumen "+ businessCode+ " actualizado";
            notifier.show(msg, "notifier-accept", duration, Notification.Position.BOTTOM_CENTER);
         }
         closeEditor();
      }
   }//saveVolume


   private void deleteVolume(BaseExpedienteEditor.DeleteEvent event)
   {
     BaseExpediente volumen = event.getBaseExpediente();
     if (volumen != null && volumen.isOfType(Nature.VOLUMEN) && volumen.isPersisted())
     {
       if (!volumeService.hasChildren(currentVolume))
       {  volumeService.delete(currentUser, currentVolume);
          notifier.show("Volumen "+ volumen.formatCode()+ " eliminado",    "notifier-accept",  3000,  Notification.Position.BOTTOM_CENTER);
       }else
       {  notifier.error("Volumen no puede ser eliminado pues contiene documentos");
       }
     }
     closeEditor();
   }//deleteVolume


   public void closeEditor()
   {
     baseExpedienteEditor.setVisible(false);
     fireEvent(new CloseEvent(this, currentVolume));
     currentVolume = null;
   }//closeEditor


   // --------------------- Events -----------------------
   public static abstract class VolumeEditorEvent extends ComponentEvent<VolumeEditor>
   {
     private Volume volume;

     protected VolumeEditorEvent(VolumeEditor source, Volume volume)
     {  super(source, false);
        this.volume = volume;
     }//VolumeEditorEvent

     public Volume           getVolume()      { return volume;  }
     public BaseExpediente   getExpediente()  { return volume == null? null: volume.getExpediente();}

   }//VolumeEditorEvent

   public static class SaveEvent extends VolumeEditorEvent
   {
     SaveEvent(VolumeEditor source, Volume volume)
     {  super(source, volume);
     }
   }//SaveEvent

   public static class DeleteEvent extends VolumeEditorEvent
   {
     DeleteEvent(VolumeEditor source, Volume volume)
     {  super(source, volume);
     }
   }//DeleteEvent

   public static class CloseEvent extends VolumeEditorEvent
   {
     CloseEvent(VolumeEditor source, Volume volume)
     {  super(source, volume);
     }
   }//CloseEvent

   public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener)
   {
     return getEventBus().addListener(eventType, listener);
   }//addListener


}//VolumeEditor
