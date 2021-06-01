package com.f.thoth.ui.views.expediente;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import org.vaadin.gatanaso.MultiselectComboBox;

import com.f.thoth.backend.data.gdoc.classification.Classification;
import com.f.thoth.backend.data.gdoc.expediente.BaseExpediente;
import com.f.thoth.backend.data.gdoc.expediente.ExpedienteGroup;
import com.f.thoth.backend.data.gdoc.expediente.Nature;
import com.f.thoth.backend.data.gdoc.expediente.Volume;
import com.f.thoth.backend.data.gdoc.metadata.DocumentType;
import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.data.security.ThothSession;
import com.f.thoth.backend.data.security.User;
import com.f.thoth.backend.service.BaseExpedienteService;
import com.f.thoth.backend.service.DocumentTypeService;
import com.f.thoth.backend.service.ExpedienteGroupService;
import com.f.thoth.backend.service.SchemaService;
import com.f.thoth.backend.service.VolumeService;
import com.f.thoth.ui.components.Notifier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;

public class VolumeEditor extends VerticalLayout
{
   private ExpedienteGroupService      expedienteGroupService;
   private VolumeService               volumeService;
   private BaseExpedienteService       baseExpedienteService;
   private SchemaService               schemaService;
   private DocumentTypeService         documentTypeService;

   private MultiselectComboBox<DocumentType> docTypes;

   private Volume                      currentVolume;
   private Classification              classificationClass;
   private User                        currentUser;

   private BaseExpedienteEditor        baseExpedienteEditor;
   private Notifier notifier           = new Notifier();

   private Button             save;
   private Button             delete;
   private Button             close;
   private Component          buttons;


   public VolumeEditor( ExpedienteGroupService  expedienteGroupService,
                        VolumeService           volumeService,
                        BaseExpedienteService   baseExpedienteService,
                        SchemaService           schemaService,
                        DocumentTypeService     documentTypeService,
                        Classification          classificationClass
                      )
   {
     this.expedienteGroupService  = expedienteGroupService;
     this.volumeService           = volumeService;
     this.baseExpedienteService   = baseExpedienteService;
     this.schemaService           = schemaService;
     this.documentTypeService     = documentTypeService;
     this.currentUser             = ThothSession.getCurrentUser();
     this.classificationClass     = classificationClass;
     this.currentVolume           = null;

     buttons = configureActions();

     add(configureEditor());
     add(configureDocTypes());
     add(buttons);

     addClassName("field-form");
     setVisible(false);

   }//VolumeEditor



   private BaseExpedienteEditor configureEditor()
   {
     baseExpedienteEditor = new BaseExpedienteEditor(schemaService);
     baseExpedienteEditor.addListener(BaseExpedienteEditor.ValidationEvent.class, e -> save.setEnabled(e.getValidationResult()));
     return baseExpedienteEditor;
   }//configureEditor


   private Component configureActions()
   {
     save = new Button("Guardar");
     save.addClickShortcut (Key.ENTER);
     save.addThemeVariants  (ButtonVariant.LUMO_PRIMARY);
     save.getElement().getStyle().set("margin-left", "auto");
     save.setWidth("20%");
     save.addClickListener  (click -> saveVolume(currentVolume));

     delete = new Button("Eliminar");
     delete.addClickShortcut (Key.DELETE);
     delete.addThemeVariants  (ButtonVariant.LUMO_CONTRAST);
     delete.getElement().getStyle().set("margin-left", "auto");
     delete.setWidth("20%");
     delete.addClickListener  (click -> deleteVolume(currentVolume));


     close= new Button("Cancelar");
     close.addThemeVariants (ButtonVariant.LUMO_TERTIARY);
     close.addClickShortcut(Key.ESCAPE);
     close. setWidth("20%");
     close.addClickListener (click ->
     { baseExpedienteEditor.close();
       closeEditor();
     });

     HorizontalLayout buttons = new HorizontalLayout(close, delete, save);
     buttons.getElement().setAttribute("colspan", "4");
     buttons.setWidthFull();
     return buttons;
   }//configureActions


   private Component  configureDocTypes()
   {
      docTypes = new MultiselectComboBox<>("Tipos documentales");
      List<DocumentType> allTypes = documentTypeService.findAll();   // Recibirlo como parámetro
      docTypes.setItems(allTypes);
   //   docTypes.addValueChangeListener(e -> currentVolume.setAdmissibleTypes(e.getValue()));
      docTypes.setItemLabelGenerator(e-> e.getName());
      docTypes.setWidth("30%");
      docTypes.setRequired(false);
      docTypes.setRequiredIndicatorVisible(true);
      docTypes.getElement().setAttribute("colspan", "1");
      return docTypes;

   }//configureDocTypes


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
     newVolume.setOwnerId             ( parentGroup == null? null : parentGroup.getOwnerId());
     newVolume.setOpen                (true);
     newVolume.setKeywords            ("keyword1, keyword2, keyword3");
     newVolume.setMac                 ("[mac]");
     newVolume.setCurrentInstance     (0);
     newVolume.setInstances           (new TreeSet<>());
     return newVolume;

   }//createVolume


   public void editVolume(Volume volume)
   {
     if (volume == null)
     {  closeEditor();
     } else
     {
        currentVolume = volume;
        docTypes.deselectAll();
        Set<DocumentType> admissibleTypes = currentVolume.getAdmissibleTypes();
        docTypes.setValue(admissibleTypes == null? new TreeSet<>() : admissibleTypes);
        setVisibility(true);
        BaseExpediente base = currentVolume.getExpediente();
        String   parentCode = getParentCode( base);
        baseExpedienteEditor.editExpediente(base, parentCode);
     }
   }//editVolume
   
   
   private void setVisibility( boolean visibility)
   {
      baseExpedienteEditor.setVisible(visibility);
      docTypes            .setVisible(visibility);
      buttons             .setVisible(visibility);
      setVisible(visibility);
   }//setVisibility


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


   private void saveVolume(Volume volume)
   {
      if ( volume != null && baseExpedienteEditor.saveBaseExpediente())
      {
         boolean isNew = !volume.isPersisted();
         int  duration = isNew? 6000 : 3000;
         volume.setAdmissibleTypes(docTypes.getValue());
         volumeService.save(currentUser, volume);
         String businessCode = volume.formatCode();
         String msg          = isNew? "Volumen creado con código "+ businessCode: "Volumen "+ businessCode+ " actualizado";
         notifier.show(msg, "notifier-accept", duration, Notification.Position.BOTTOM_CENTER);
      }
      closeEditor();

   }//saveVolume


   private void deleteVolume(Volume volume)
   {
     if (volume != null && volume.isOfType(Nature.VOLUMEN) && volume.isPersisted())
     {
       if (!volumeService.hasChildren(currentVolume))
       {  volumeService.delete(currentUser, currentVolume);
          notifier.show("Volumen "+ volume.formatCode()+ " eliminado",    "notifier-accept",  3000,  Notification.Position.BOTTOM_CENTER);
       }else
       {  notifier.error("Volumen no puede ser eliminado pues contiene documentos");
       }
     }
     closeEditor();

   }//deleteVolume


   public void closeEditor()
   {
     setVisibility(false);
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
