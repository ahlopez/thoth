package com.f.thoth.ui.views.expediente;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import org.vaadin.gatanaso.MultiselectComboBox;

import com.f.thoth.backend.data.gdoc.classification.Classification;
import com.f.thoth.backend.data.gdoc.expediente.BaseExpediente;
import com.f.thoth.backend.data.gdoc.expediente.Expediente;
import com.f.thoth.backend.data.gdoc.expediente.Nature;
import com.f.thoth.backend.data.gdoc.metadata.DocumentType;
import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.data.security.ThothSession;
import com.f.thoth.backend.data.security.User;
import com.f.thoth.backend.service.BaseExpedienteService;
import com.f.thoth.backend.service.DocumentTypeService;
import com.f.thoth.backend.service.ExpedienteLeafService;
import com.f.thoth.backend.service.SchemaService;
import com.f.thoth.ui.components.Notifier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;

public class ExpedienteLeafEditor extends VerticalLayout
{
   private ExpedienteLeafService       expedienteLeafService;
   private BaseExpedienteService       baseExpedienteService;
   private SchemaService               schemaService;
   private DocumentTypeService         documentTypeService;

   private MultiselectComboBox<DocumentType> docTypes;

   private Expediente                  currentExpediente;
   private Classification              classificationClass;
   private User                        currentUser;

   private BaseExpedienteEditor        baseExpedienteEditor;
   private Notifier notifier           = new Notifier();

   private Button                      save;
   private Button                      delete;
   private Button                      close;
   private Component                   buttons;


   public ExpedienteLeafEditor(  ExpedienteLeafService   expedienteLeafService,
                                 BaseExpedienteService   baseExpedienteService,
                                 SchemaService           schemaService,
                                 DocumentTypeService     documentTypeService,
                                 Classification          classificationClass
                              )
   {
     this.expedienteLeafService   = expedienteLeafService;
     this.baseExpedienteService   = baseExpedienteService;
     this.schemaService           = schemaService;
     this.documentTypeService     = documentTypeService;
     this.currentUser             = ThothSession.getCurrentUser();
     this.classificationClass     = classificationClass;
     this.currentExpediente       = null;

     baseExpedienteEditor         = configureEditor();
     docTypes                     = configureDocTypes();
     buttons                      = configureActions();
     add(baseExpedienteEditor, docTypes, buttons);
     
     addClassName("field-form");
     setVisible(false);

   }//ExpedienteLeafEditor



   private BaseExpedienteEditor configureEditor()
   {
     BaseExpedienteEditor editor = new BaseExpedienteEditor(schemaService);
     editor.addListener(BaseExpedienteEditor.ValidationEvent.class, e -> save.setEnabled(e.getValidationResult()));
     return editor;
   }//configureEditor


   private MultiselectComboBox<DocumentType>  configureDocTypes()
   {
      MultiselectComboBox<DocumentType> docTypes = new MultiselectComboBox<>("Tipos documentales admisibles");
      List<DocumentType> allTypes = documentTypeService.findAll();
      docTypes.setItems(allTypes);
     // docTypes.addValueChangeListener(e -> currentExpediente.setAdmissibleTypes(e.getValue()));
      docTypes.setItemLabelGenerator(e-> e.getName());
      docTypes.setWidth("30%");
      docTypes.setRequired(false);
      docTypes.setRequiredIndicatorVisible(true);
      docTypes.getElement().setAttribute("colspan", "1");
      return docTypes;

   }//configureDocTypes


   private Component configureActions()
   {
     save = new Button("Guardar");
     save.addClickShortcut (Key.ENTER);
     save.addThemeVariants  (ButtonVariant.LUMO_PRIMARY);
     save.getElement().getStyle().set("margin-left", "auto");
     save.setWidth("20%");
     save.addClickListener  (click -> saveExpediente(currentExpediente));

     delete = new Button("Eliminar");
     delete.addClickShortcut (Key.DELETE);
     delete.addThemeVariants  (ButtonVariant.LUMO_CONTRAST);
     delete.getElement().getStyle().set("margin-left", "auto");
     delete.setWidth("20%");
     delete.addClickListener  (click -> deleteExpediente(currentExpediente));

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


   public void addExpediente(BaseExpediente parentBase)
   {
      Expediente newExpediente  = createExpediente(parentBase);
      editExpediente(newExpediente);
   }//addExpediente


   private  Expediente   createExpediente(BaseExpediente parentBase)
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
     newExpediente.setOwnerId             ( parentBase == null? null : parentBase.getId());
     newExpediente.setOpen                (true);
     newExpediente.setKeywords            ("keyword4, keyword5, keyword6");
     newExpediente.setMac                 ("[mac]");

     return newExpediente;

   }//createExpediente 
   
   
   private void setVisibility( boolean visibility)
   {
      baseExpedienteEditor.setVisible(visibility);
      docTypes            .setVisible(visibility);
      buttons             .setVisible(visibility);
      setVisible(visibility);
   }//setVisibility


   public void editExpediente(Expediente expediente)
   {
     if (expediente == null)
     {  closeEditor();
     } else
     {
        currentExpediente = expediente;
        docTypes.deselectAll();
        Set<DocumentType> admissibleTypes = currentExpediente.getAdmissibleTypes();
        docTypes.setValue(admissibleTypes == null? new TreeSet<>() : admissibleTypes);
        setVisibility(true);
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


   private void saveExpediente(Expediente expediente)
   {
      if ( expediente != null && baseExpedienteEditor.saveBaseExpediente())
      {
         boolean isNew = !expediente.isPersisted();
         expediente.setAdmissibleTypes(docTypes.getValue());
         expedienteLeafService.save(currentUser, expediente);
         String businessCode = expediente.formatCode();
         notifier.accept( isNew? "Expediente creado con código "+ businessCode: "Expediente "+ businessCode+ " actualizado");
      }
      closeEditor();

   }//saveExpediente
   
   
   


   private void deleteExpediente(Expediente expediente)
   {
    if (expediente != null && expediente.isOfType(Nature.EXPEDIENTE) && expediente.isPersisted())
     {
       if (!expedienteLeafService.hasChildren(currentExpediente))
       {  expedienteLeafService.delete(currentUser, currentExpediente);
          notifier.accept("Expediente "+ expediente.formatCode()+ " eliminado");
       }else
       {  notifier.error("Expediente no puede ser eliminado pues contiene documentos");
       }
     }
     closeEditor();

   }//deleteExpediente


   public void closeEditor()
   {
     setVisibility(false);
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
