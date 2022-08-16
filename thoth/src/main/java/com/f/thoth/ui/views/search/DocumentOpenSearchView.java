package com.f.thoth.ui.views.search;


import static com.f.thoth.ui.utils.Constant.PAGE_SELECTOR_CLASE;
import static com.f.thoth.ui.utils.Constant.PAGE_CONSULTA_LIBRE;
import static com.f.thoth.ui.utils.Constant.TITLE_CONSULTA_LIBRE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import com.f.thoth.backend.data.Role;
import com.f.thoth.backend.data.gdoc.classification.Classification;
import com.f.thoth.backend.service.ClassificationService;
import com.f.thoth.ui.MainView;
import com.f.thoth.ui.components.HierarchicalSelector;
import com.f.thoth.ui.views.expediente.ExpedienteHierarchyView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * La gestiÃ³n de expedientes procede por pasos:
 * [1] Obtiene la clase a la que pertenece el expediente
 * [2] Selecciona el expediente de interÃ©s navegando la jerarquÃ­a de expedientes en la clase
 * [3] Crea, actualiza, elimina expedientes en el expediente seleccionado
 * Esta vista corresponde al paso [1]. Los pasos [2], [3] se ejeccutan en ExpedienteHierarchyView
 */
@Route(value = PAGE_CONSULTA_LIBRE, layout = MainView.class)
@PageTitle(TITLE_CONSULTA_LIBRE)
@Secured(Role.ADMIN)
public class DocumentOpenSearchView extends HorizontalLayout
{
   private ClassificationService classificationService;
   private VerticalLayout        leftSection;
   private VerticalLayout        content;
   private VerticalLayout        rightSection;

   private HierarchicalSelector<Classification, HasValue.ValueChangeEvent<Classification>> ownerClass;


   @Autowired
   public DocumentOpenSearchView(ClassificationService classificationService)
   {
      this.classificationService =  classificationService;

      addClassName("main-view");
      setSizeFull();

      leftSection  = new VerticalLayout();
      leftSection.addClassName  ("left-section");
      leftSection.add(new H3 ("Búsqueda de documentos"));

      content      = new VerticalLayout();
      content.addClassName ("selector");
      content.add( configureClassSelector());
      content.setSizeFull();

      rightSection = new VerticalLayout();
      rightSection.addClassName ("right-section");
      rightSection.add(new Label("  "));

      add(leftSection, content, rightSection);
      updateSelector();

   }//OpenDocumentSearchView

   protected String getBasePage() { return PAGE_SELECTOR_CLASE; }


   private Component configureClassSelector()
   {
      ownerClass = new HierarchicalSelector<>( classificationService,
                                               Grid.SelectionMode.SINGLE,
                                               "Seleccione la clase a la que pertenece",
                                               true,
                                               true,
                                               this::selectedOwnerClass
                                              );
      ownerClass.getElement().setAttribute("colspan", "3");

      FormLayout form = new FormLayout(ownerClass);
      form.setResponsiveSteps( new ResponsiveStep("30em", 1),
                               new ResponsiveStep("30em", 2),
                               new ResponsiveStep("30em", 3),
                               new ResponsiveStep("30em", 4)
                             );

      BeanValidationBinder<Classification> binder = new BeanValidationBinder<>(Classification.class);
      binder.forField(ownerClass)
            .bind("owner");

      return ownerClass;

   }//configureClassSelector


   private void selectedOwnerClass(Classification ownerClass)
   {
      if (ownerClass == null)
      {  Notification.show("Owner class = null");
      }else
      {
         getUI().ifPresent(ui -> ui.navigate(ExpedienteHierarchyView.class, ownerClass.getId().toString()));
         closeEditor();
      }
      closeEditor();
   }//selectedOwnerClass


   private void closeEditor()
   {
      rightSection.removeClassName ("right-section");
      leftSection .removeClassName ("left-section");
      content     .removeClassName ("selector");
      removeClassName              ("main-view");

   }//closeEditor


   private void updateSelector()
   {
      ownerClass.refresh();
   }//updateSelector

}//OpenDocumentSearchView
