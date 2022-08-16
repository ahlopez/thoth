package com.f.thoth.ui.views.metadata;

import static com.f.thoth.Parm.CURRENT_USER;
import static com.f.thoth.ui.utils.Constant.PAGE_TIPOS_DOCUMENTALES;
import static com.f.thoth.ui.utils.Constant.TITLE_TIPOS_DOCUMENTALES;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import com.f.thoth.backend.data.Role;
import com.f.thoth.backend.data.gdoc.metadata.DocumentType;
import com.f.thoth.backend.data.gdoc.metadata.Schema;
import com.f.thoth.backend.data.security.User;
import com.f.thoth.backend.service.DocumentTypeService;
import com.f.thoth.backend.service.SchemaService;
import com.f.thoth.ui.MainView;
import com.f.thoth.ui.components.HierarchicalSelector;
import com.f.thoth.ui.components.Notifier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

@Route(value = PAGE_TIPOS_DOCUMENTALES, layout = MainView.class)
@PageTitle(TITLE_TIPOS_DOCUMENTALES)
@Secured(Role.ADMIN)
public class DocumentTypeView extends VerticalLayout
{
   private DocumentTypeForm      documentTypeForm;
   private DocumentTypeService   documentTypeService;
   private User                  currentUser;

   private VerticalLayout        leftSection;
   private VerticalLayout        content;
   private VerticalLayout        rightSection;

   private HierarchicalSelector<DocumentType, HasValue.ValueChangeEvent<DocumentType>> ownerDocType;
   private DocumentType          currentDocType= null;

   private Button   add      = new Button("+ Nuevo Tipo");
   private Button   delete   = new Button("Eliminar Tipo");
   private Button   close    = new Button("Cancelar");
   private Notifier notifier = new Notifier();

   private List<Schema>  availableSchemas;

   @Autowired
   public DocumentTypeView(DocumentTypeService documentTypeService, SchemaService schemaService)
   {
      this.documentTypeService = documentTypeService;
      this.currentUser         = (User)VaadinSession.getCurrent().getAttribute(CURRENT_USER);

      availableSchemas = schemaService.findAll();

      addClassName("main-view");
      setSizeFull();

      leftSection  = new VerticalLayout();
      leftSection.addClassName  ("left-section");
      leftSection.add(new Label (" "));

      rightSection = new VerticalLayout();
      rightSection.addClassName ("right-section");

      content      = new VerticalLayout();
      content.addClassName      ("content");
      content.setSizeFull();
      content.add(new H3("Tipos registrados"));

      content.add( configureGrid(), configureButtons());
      rightSection.add(configureForm(availableSchemas));
      updateSelector();
      closeEditor();

      HorizontalLayout panel=  new HorizontalLayout(leftSection, content, rightSection);
      panel.setSizeFull();
      add( panel);

   }//DocumentTypeView

   protected String getBasePage() { return PAGE_TIPOS_DOCUMENTALES; }


   private Component configureGrid()
   {
      ownerDocType = new HierarchicalSelector<>(
                           documentTypeService,
                           Grid.SelectionMode.SINGLE,
                           "Seleccione el tipo padre",
                           true,
                           false,
                           this::editOwner
                           );
      ownerDocType.getElement().setAttribute("colspan", "4");

      FormLayout form = new FormLayout(ownerDocType);
      form.setResponsiveSteps(
            new ResponsiveStep("30em", 1),
            new ResponsiveStep("30em", 2),
            new ResponsiveStep("30em", 3),
            new ResponsiveStep("30em", 4));

      BeanValidationBinder<DocumentType> binder = new BeanValidationBinder<>(DocumentType.class);
      binder.forField(ownerDocType)
            .bind("owner");

      return ownerDocType;

   }//configureGrid


   private Component configureButtons()
   {
      add.     addThemeVariants(ButtonVariant.LUMO_PRIMARY);
      delete.  addThemeVariants(ButtonVariant.LUMO_ERROR);
      close.   addThemeVariants(ButtonVariant.LUMO_TERTIARY);

      close.addClickShortcut(Key.ESCAPE);

      add .addClickListener  (click -> addDocumentType());
      delete.addClickListener(click -> deleteDocumentType(currentDocType));
      close.addClickListener (click -> closeAll());

      add   .getElement().getStyle().set("margin-left", "auto");

      HorizontalLayout buttons = new HorizontalLayout();
      buttons.setWidthFull();
      buttons.setPadding(true);
      buttons.add( delete, close, add);
      return buttons;
   }//configureButtons


   private DocumentTypeForm configureForm(List<Schema>availableSchemas)
   {
      documentTypeForm = new DocumentTypeForm(availableSchemas);
      documentTypeForm.addListener(DocumentTypeForm.SaveEvent.class,   this::saveDocumentType);
      documentTypeForm.addListener(DocumentTypeForm.CloseEvent.class,  e -> closeEditor());
      return documentTypeForm;

   }//configureForm


   private void editOwner(DocumentType owner)
   {
      this.currentDocType = owner;
      editDocumentType(currentDocType);
   }//editOwner


   private void addDocumentType()
   {
      currentDocType = new DocumentType();
      DocumentType owner = ownerDocType.getValue();
      currentDocType.setOwner(owner);
      editDocumentType(currentDocType);

   }//addDocumentType


   private void deleteDocumentType(DocumentType documentType)
   {
      try
      {
         if( documentType != null && documentType.isPersisted())
             documentTypeService.delete(currentUser, documentType);
      } catch (Exception e)
      {
         notifier.error("Tipo Documental["+ documentType.getName()+ "] tiene referencias. No puede ser borrado");
      }
      updateSelector();
      closeEditor();
   }//deleteDocumentType


   private void editDocumentType(DocumentType documentType)
   {
      if (documentType == null)
      {
         closeEditor();
      } else
      {
         if( documentType.isPersisted())
            documentType = documentTypeService.load(documentType.getId());

         documentTypeForm.setVisible(true);
         documentTypeForm.setDocumentType(documentType);
         documentTypeForm.addClassName("selected-item-form");
         rightSection.setVisible(true);
      }
   }//editDocumentType


   private void closeEditor()
   {
      documentTypeForm.setDocumentType(null);
      documentTypeForm.setVisible(false);
      documentTypeForm.removeClassName("selected-item-form");
   }//closeEditor


   private void closeAll()
   {
      closeEditor();
      currentDocType = null;
      ownerDocType.resetSelector();
   }//closeAll


   private void updateSelector()
   {
     ownerDocType.refresh();
   }//updateSelector


   private void saveDocumentType(DocumentTypeForm.SaveEvent event)
   {
      DocumentType documentType = event.getDocumentType();
      documentTypeService.save(currentUser, documentType);
      updateSelector();
      closeEditor();
   }//saveDocumentType

}//DocumentTypeView