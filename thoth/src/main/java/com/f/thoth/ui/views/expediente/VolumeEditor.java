package com.f.thoth.ui.views.expediente;

import static com.f.thoth.Parm.CURRENT_USER;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import org.vaadin.gatanaso.MultiselectComboBox;

import com.f.thoth.backend.data.gdoc.classification.Classification;
import com.f.thoth.backend.data.gdoc.expediente.BaseExpediente;
import com.f.thoth.backend.data.gdoc.expediente.Nature;
import com.f.thoth.backend.data.gdoc.expediente.Volume;
import com.f.thoth.backend.data.gdoc.expediente.VolumeInstance;
import com.f.thoth.backend.data.gdoc.metadata.DocumentType;
import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.data.security.User;
import com.f.thoth.backend.service.BaseExpedienteService;
import com.f.thoth.backend.service.DocumentTypeService;
import com.f.thoth.backend.service.SchemaService;
import com.f.thoth.backend.service.VolumeInstanceService;
import com.f.thoth.backend.service.VolumeService;
import com.f.thoth.ui.components.Notifier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.shared.Registration;

public class VolumeEditor extends VerticalLayout
{
   private VolumeService               volumeService;
   private VolumeInstanceService       volumeInstanceService;
   private BaseExpedienteService       baseExpedienteService;
   private SchemaService               schemaService;
   private DocumentTypeService         documentTypeService;

   private HorizontalLayout            volumeFields;
   private MultiselectComboBox<DocumentType> docTypes;
   private TextField                   currentInstance;

   private Classification              classificationClass;
   private Volume                      currentVolume;
   private User                        currentUser;

   private BaseExpedienteEditor        baseExpedienteEditor;
   private Notifier notifier           = new Notifier();

   private Button             save;
   private Button             delete;
   private Button             close;
   private Component          buttons;


   public VolumeEditor( VolumeService           volumeService,
                        VolumeInstanceService   volumeInstanceService,
                        BaseExpedienteService   baseExpedienteService,
                        SchemaService           schemaService,
                        DocumentTypeService     documentTypeService,
                        Classification          classificationClass
                      )
   {
     this.volumeService           = volumeService;
     this.volumeInstanceService   = volumeInstanceService;
     this.baseExpedienteService   = baseExpedienteService;
     this.schemaService           = schemaService;
     this.documentTypeService     = documentTypeService;
     this.currentUser             = (User)VaadinSession.getCurrent().getAttribute(CURRENT_USER);
     this.classificationClass     = classificationClass;
     this.currentVolume           = null;

     buttons = configureActions();

     add(configureEditor());
     add(configureVolumeFields());
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
     delete.addThemeVariants  (ButtonVariant.LUMO_ERROR);
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


   private Component  configureVolumeFields()
   {
      volumeFields = new HorizontalLayout();
      volumeFields.setWidthFull();
      docTypes = new MultiselectComboBox<>("Tipos documentales admisibles");
      List<DocumentType> allTypes = documentTypeService.findAll();
      docTypes.setItems(allTypes);
      docTypes.setItemLabelGenerator(e-> e.getName());
      docTypes.setWidth("30%");
      docTypes.setRequired(false);
      docTypes.setRequiredIndicatorVisible(true);
      docTypes.getElement().setAttribute("colspan", "1");

      currentInstance= new TextField("Instancia actual");
      currentInstance.setRequired(true);
      currentInstance.setRequiredIndicatorVisible(true);
      currentInstance.setErrorMessage("Código de la instancia actual debe ser mayor o igual a 0");
      currentInstance.getElement().setAttribute("colspan", "1");
      currentInstance.getElement().getStyle().set("color", "blue");
      currentInstance.setReadOnly(true);

      volumeFields.add( docTypes, currentInstance);

      return volumeFields;

   }//configureVolumeFields


   public void addVolume(BaseExpediente parentBase)
   {
      Volume newVolume  = createVolume(parentBase, Nature.VOLUMEN);
      editVolume(newVolume);
   }//addVolume


   public void addExpediente(BaseExpediente parentBase)
   {
      Volume newExpediente = createVolume(parentBase, Nature.EXPEDIENTE);
      editVolume(newExpediente);
   }//addExpediente


   private  Volume   createVolume(BaseExpediente parentBase, Nature type)
   {
     Volume                newVolume = new Volume();
     LocalDateTime              now  = LocalDateTime.now();
     newVolume.setType                (type);
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
     newVolume.setOwnerId             ( parentBase == null? null : parentBase.getId());
     newVolume.setOpen                (true);
     newVolume.setKeywords            ("keyword1, keyword2, keyword3");
     newVolume.setMac                 ("[mac]");
     newVolume.setCurrentInstance     (0);
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
        currentInstance.setValue(volume.getCurrentInstance().toString());
        currentInstance.setVisible(volume.isOfType(Nature.VOLUMEN));
        setVisibility(true);
        BaseExpediente base = currentVolume.getExpediente();
        String   parentCode = getParentCode( base);
        baseExpedienteEditor.editExpediente(base, parentCode);
     }
   }//editVolume


   private void setVisibility( boolean visibility)
   {
      baseExpedienteEditor.setVisible(visibility);
      volumeFields        .setVisible(visibility);
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
         volume.setAdmissibleTypes(docTypes.getValue());
         volumeService.save(currentUser, volume);
         String businessCode = volume.formatCode();
         String volType = volume.getType().toString();
         notifier.accept( isNew? volType+ " creado con código "+ businessCode: volType+ " "+ businessCode+ " actualizado");
      }
      closeEditor();

   }//saveVolume


   private void deleteVolume(Volume volume)
   {
     if (volume != null  && volume.isPersisted() && (volume.isOfType(Nature.VOLUMEN) || volume.isOfType(Nature.EXPEDIENTE)))
     {
       String type = volume.getType().toString();
       if (!volumeService.hasChildren(currentVolume))
       {  volumeService.delete(currentUser, currentVolume);
          notifier.accept(type+ " "+ volume.formatCode()+ " eliminado");
       }else
       {  notifier.error(type+ " no puede ser eliminado pues contiene documentos");
       }
     }
     closeEditor();

   }//deleteVolume


   public void openNewInstance(BaseExpediente baseVolume)
   {
      Volume     volume = volumeService.findByCode(baseVolume.getCode());
      LocalDateTime now = LocalDateTime.now();
      closeCurrentInstance (volume, now);
      currentVolume = createNewInstance(volume, now);
      notifier.accept("Nueva instancia "+ volume.getCurrentInstance()+ " creada en Volumen "+ volume.formatCode());
      editVolume(currentVolume);

   }//openNewInstance


   private void closeCurrentInstance(Volume volume, LocalDateTime closingDate)
   {
      if ( volume != null)
      {  VolumeInstance instance = volumeInstanceService.findByInstanceCode(volume, volume.getCurrentInstance());
         if (instance != null)
         { instance.setDateClosed(closingDate);
           volumeInstanceService.save(currentUser, instance);
         }
      }
   }//closeCurrentInstance


   private Volume createNewInstance(Volume volume, LocalDateTime openingDate)
   {
      if ( volume != null)
      {
         Integer    currentInstance = volume.getCurrentInstance() + 1;
         VolumeInstance newInstance = new VolumeInstance(volume, currentInstance, "", openingDate, openingDate.plusYears(1L));
         volumeInstanceService.save(currentUser, newInstance);
         volume.setCurrentInstance(currentInstance);
         volumeService.save(currentUser, volume);
         volume = volumeService.findById(volume.getId()).get();
      }
      return volume;

   }//createNewInstance


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
