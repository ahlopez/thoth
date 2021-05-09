package com.f.thoth.ui.views.expediente;

import static com.f.thoth.ui.utils.Constant.PAGE_JERARQUIA_EXPEDIENTES;
import static com.f.thoth.ui.utils.Constant.PAGE_SELECTOR_CLASE;
import static com.f.thoth.ui.utils.Constant.TITLE_JERARQUIA_EXPEDIENTES;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import com.f.thoth.backend.data.Role;
import com.f.thoth.backend.data.gdoc.classification.Classification;
import com.f.thoth.backend.data.gdoc.expediente.BaseExpediente;
import com.f.thoth.backend.data.security.ThothSession;
import com.f.thoth.backend.data.security.User;
import com.f.thoth.backend.service.BaseExpedienteService;
import com.f.thoth.backend.service.ClassificationService;
import com.f.thoth.ui.MainView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.hierarchy.AbstractBackEndHierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * La gestión de expedientes procede por pasos:
 * [1] Obtiene la clase a la que pertenece el expediente
 * [2] Selecciona el expediente de interés navegando la jerarquía de expedientes en la clase
 * [3] Crea, actualiza, elimina expedientes en el expediente seleccionado
 * Esta vista corresponde a los pasos [2], [3]. El paso [1] se ejeccuta en ExpedienteClassSelectorView
 */
@Route(value = PAGE_JERARQUIA_EXPEDIENTES, layout = MainView.class)
@PageTitle(TITLE_JERARQUIA_EXPEDIENTES)
@Secured(Role.ADMIN)
class ExpedienteHierarchyView extends HorizontalLayout implements HasUrlParameter<String>, AfterNavigationObserver
{

  private BaseExpedienteService baseExpedienteService;
  private User                  currentUser;
  private BaseExpediente        currentExpediente;

  private ClassificationService classificationService;
  private String                classCode;
  private Classification        classificationClass;

  private VerticalLayout        leftSection;
  private VerticalLayout        content;
  private VerticalLayout        rightSection;

  private Button add      = new Button("+ Nuevo Expediente");
  private Button save     = new Button("Guardar expediente");
  private Button delete   = new Button("Eliminar expediente");
  private Button close    = new Button("Cancelar");


  @Autowired
  public ExpedienteHierarchyView(BaseExpedienteService baseExpedienteService, ClassificationService classificationService)
  {
    this.baseExpedienteService = baseExpedienteService;
    this.currentUser           = ThothSession.getCurrentUser();
    this.classificationService = classificationService;

    addClassName("main-view");
    setSizeFull();

    leftSection  = new VerticalLayout();
    leftSection.addClassName  ("left-section");

    content      = new VerticalLayout();
    content.addClassName ("selector");
    content.setSizeFull();
    content.add( configureExpedienteSelector());
    content.add( configureButtons());

    rightSection = new VerticalLayout();
    rightSection.addClassName ("right-section");
    rightSection.add(new Label(" "));

    add(leftSection, content, rightSection);
    updateSelector();

  }//ExpedienteHierarchyView

  private Component configureExpedienteSelector()
  {
    TreeGrid<BaseExpediente> grid = new TreeGrid<>();
    grid.addHierarchyColumn(BaseExpediente::getName).setHeader("Nombre expediente");
    grid.addColumn(BaseExpediente::getCode).setHeader("Código");

    HierarchicalDataProvider<BaseExpediente, Void> dataProvider = new AbstractBackEndHierarchicalDataProvider<BaseExpediente, Void>()
    {

      @Override
      public int getChildCount(HierarchicalQuery<BaseExpediente, Void> query)
      {
        if (query == null)
          return 0;

        BaseExpediente base = query.getParent();
        return base != null? baseExpedienteService.countByParent(base):
        baseExpedienteService.countByClass(classificationClass);
      }//getChildCount

      @Override
      public boolean hasChildren(BaseExpediente item)
      {
        return baseExpedienteService.hasChildren(item, item == null? null: item.getClassificationClass());
      }

      @Override
      protected Stream<BaseExpediente> fetchChildrenFromBackEnd(  HierarchicalQuery<BaseExpediente, Void> query)
      {
        Stream<BaseExpediente> empty = new ArrayList<BaseExpediente>().stream();
        if ( query == null)
          return empty;

        BaseExpediente base = query.getParent();
        return  base != null? baseExpedienteService.findByParent(base).stream():
                              baseExpedienteService.findByClass(classificationClass).stream();
      }//fetchChildrenFromBackEnd
    };

    grid.setDataProvider(dataProvider);
    return grid;
  }//configureSelector


  private Component configureButtons()
  {
    add.     addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    save.    addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    delete.  addThemeVariants(ButtonVariant.LUMO_ERROR);
    close.   addThemeVariants(ButtonVariant.LUMO_TERTIARY);

    save.addClickShortcut (Key.ENTER);
    close.addClickShortcut(Key.ESCAPE);

    add .addClickListener  (click -> addExpediente());
    save.addClickListener  (click -> saveExpediente(currentExpediente));
    delete.addClickListener(click -> deleteExpediente(currentExpediente));
    close.addClickListener (click -> closeExpediente());

    save.getElement().getStyle().set("margin-left", "auto");
    add .getElement().getStyle().set("margin-left", "auto");

    HorizontalLayout buttons = new HorizontalLayout();
    buttons.setWidthFull();
    buttons.setPadding(true);
    buttons.add( delete, save, close, add);
    return buttons;
  }//configureButtons

  protected String getBasePage() { return PAGE_SELECTOR_CLASE;}

  /*
  private Component configureBaseExpedienteSelector()
  {
    ownerExpediente = new HierarchicalSelector<>( classificationService,
        Grid.SelectionMode.SINGLE,
        "Seleccione el Expediente de interÃƒÂ©s",
        true,
        true,
        this::selectedOwnerClass
        );
    ownerExpediente.getElement().setAttribute("colspan", "3");

    FormLayout form = new FormLayout(ownerExpediente);
    form.setResponsiveSteps( new ResponsiveStep("30em", 1),
        new ResponsiveStep("30em", 2),
        new ResponsiveStep("30em", 3),
        new ResponsiveStep("30em", 4)
        );

    BeanValidationBinder<Classification> binder = new BeanValidationBinder<>(Classification.class);
    binder.forField(ownerExpediente)
    .bind("owner");

    return ownerExpediente;

  }//configureExpedienteSelector

  private void selectedOwnerClass(Classification ownerExpediente)
  {
    if (ownerExpediente == null)
    {  Notification.show("Expediente seleccionado = null");
    }else
    {
      getUI().ifPresent(ui -> ui.navigate(ExpedienteHierarchyView.class, ownerExpediente.formatCode()));
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
   */
  private void addExpediente()
  {

  }//addExpediente


  private void saveExpediente(BaseExpediente currentExpediente)
  {

  }

  private void deleteExpediente(BaseExpediente currentExpediente)
  {

  }//saveExpediente


  private void closeExpediente()
  {

  }//closeExpediente



  private void updateSelector()
  {
    //ownerExpediente.refresh();
  }//updateSelector


  @Override
  public void afterNavigation(AfterNavigationEvent event)
  {
    leftSection.add(new H2 ("Expedientes de la clase   "+ classCode));
  }//afterNavigation

  @Override
  public void setParameter(BeforeEvent event, String parameter)
  {
    Notification.show("Voy a navegar con parÃƒÂ¡metro["+ parameter+ "]");
    Optional<Classification> cls =  classificationService.findById(Long.parseLong(parameter));
    if ( cls.isPresent())
    {
      this.classificationClass =  cls.get();
      this.classCode = classificationClass.formatCode();
    }
    else
    {
      this.classificationClass = null;
      this.classCode = "---";
    }
  }//setParameter


}//ExpedienteHierarchyView
