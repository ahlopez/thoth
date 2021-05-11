package com.f.thoth.ui.views.expediente;

import static com.f.thoth.ui.utils.Constant.PAGE_JERARQUIA_EXPEDIENTES;
import static com.f.thoth.ui.utils.Constant.TITLE_JERARQUIA_EXPEDIENTES;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import com.f.thoth.backend.data.Role;
import com.f.thoth.backend.data.entity.util.TextUtil;
import com.f.thoth.backend.data.gdoc.classification.Classification;
import com.f.thoth.backend.data.gdoc.expediente.BaseExpediente;
import com.f.thoth.backend.data.security.ThothSession;
import com.f.thoth.backend.service.BaseExpedienteService;
import com.f.thoth.backend.service.ClassificationService;
import com.f.thoth.ui.MainView;
import com.f.thoth.ui.components.SearchBar;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Label;
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
 * La gestion de expedientes procede por pasos:
 * [1] Obtiene la clase a la que pertenece el expediente
 * [2] Selecciona el expediente de interes navegando la jerarquia de expedientes en la clase
 * [3] Crea, actualiza, elimina expedientes en el expediente seleccionado
 * Esta vista corresponde a los pasos [2], [3]. El paso [1] se ejeccuta en ExpedienteClassSelectorView
 */
@Route(value = PAGE_JERARQUIA_EXPEDIENTES, layout = MainView.class)
@PageTitle(TITLE_JERARQUIA_EXPEDIENTES)
@Secured(Role.ADMIN)
class ExpedienteHierarchyView extends VerticalLayout implements HasUrlParameter<String>, AfterNavigationObserver
{
  private BaseExpedienteService       baseExpedienteService;
  //private BaseExpediente              currentExpediente;
  //private User                        currentUser;

  private ClassificationService       classificationService;
  private Classification              selectedClass;
  private String                      classCode = "";
  private String                      className = "";

  private VerticalLayout              content;
  private VerticalLayout              rightSection;


  private TreeGrid<BaseExpediente>    treeGrid;
  private HierarchicalDataProvider<BaseExpediente, Void> dataProvider;

  private Grid<BaseExpediente>        searchGrid;
  private SearchBar                   searchBar;
  private final List<BaseExpediente>  emptyGrid     = new ArrayList<>();
  private final Set<BaseExpediente>   expandedNodes = new TreeSet<>();



  @Autowired
  public ExpedienteHierarchyView(BaseExpedienteService baseExpedienteService, ClassificationService classificationService)
  {
    this.baseExpedienteService = baseExpedienteService;
    this.classificationService = classificationService;
    //this.currentUser           = ThothSession.getCurrentUser();

  }//ExpedienteHierarchyView

  private Component configureExpedienteSelector()
  {
    HorizontalLayout layout = new HorizontalLayout();
    layout.getElement().setAttribute("colspan", "3");
    layout.setWidthFull();
    treeGrid = buildSelector();
    layout.add(treeGrid);

    this.searchGrid   = buildSearchGrid(treeGrid);
    searchGrid.setWidth("79%");
    this.searchBar    = buildSearchBar(searchGrid);
    content.add(searchBar);
    layout.add(searchGrid);
    layout.setFlexGrow(1, treeGrid);

    refresh();
    return layout;

  }//configureSelector


  private TreeGrid<BaseExpediente> buildSelector()
  {
    TreeGrid<BaseExpediente> grid = new TreeGrid<>();
    grid.setSelectionMode(Grid.SelectionMode.SINGLE);
    grid.setWidthFull();
    grid.addHierarchyColumn(BaseExpediente::getName).setHeader("Nombre del expediente");
    grid.addColumn(BaseExpediente::formatCode).setHeader("Código");
    this.dataProvider = getDataProvider();
    grid.setDataProvider(dataProvider);
    buildSingleSelector(grid);
    return grid;

  }//buildSelector


  private HierarchicalDataProvider<BaseExpediente, Void> getDataProvider()
  {
    return new AbstractBackEndHierarchicalDataProvider<BaseExpediente, Void>()
    {

      @Override
      public int getChildCount(HierarchicalQuery<BaseExpediente, Void> query)
      {
        if (query == null)
          return 0;

        BaseExpediente base = query.getParent();
        return base != null? baseExpedienteService.countByParent(base):
        baseExpedienteService.countByClass(selectedClass);
      }//getChildCount

      @Override
      public boolean hasChildren(BaseExpediente expediente)
      {
        return baseExpedienteService.hasChildren(expediente, expediente == null? null: expediente.getClassificationClass());
      }//hasChildren

      @Override
      protected Stream<BaseExpediente> fetchChildrenFromBackEnd(  HierarchicalQuery<BaseExpediente, Void> query)
      {
        Stream<BaseExpediente> empty = new ArrayList<BaseExpediente>().stream();
        if ( query == null)
          return empty;

        BaseExpediente base = query.getParent();
        return  base != null? baseExpedienteService.findByParent(base).stream():
        baseExpedienteService.findByClass(selectedClass).stream();
      }//fetchChildrenFromBackEnd

    };// new AbstractBackEndHierarchicalDataProvider<>
  }//getDataProvider


  private void buildSingleSelector(TreeGrid<BaseExpediente> tGrid)
  {
    tGrid.addItemDoubleClickListener( e-> tGrid.deselect(e.getItem()));
    tGrid.addExpandListener         ( e-> expandedNodes.addAll(e.getItems()));
    tGrid.addSelectionListener      ( e->
     {
       Optional<BaseExpediente> first = e.getFirstSelectedItem();
       if ( first.isPresent() )
       {  // Aqui­ llamar el metodo que procesa la seleccion:
          //  setValue( first.get() );
       }
     });
  }//buildSingleSelector


  private Grid<BaseExpediente> buildSearchGrid(TreeGrid<BaseExpediente> tGrid)
  {
    Grid<BaseExpediente> sGrid = new Grid<>();
    sGrid.setVisible(false);
    sGrid.setWidthFull();
    sGrid.addColumn(BaseExpediente::getName).setHeader("Nombre de Expediente").setFlexGrow(80);
    sGrid.addColumn(BaseExpediente::formatCode).setHeader("Código").setFlexGrow(20);
    sGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
    return sGrid;
  }//buildSearchGrid


  private SearchBar buildSearchBar(Grid<BaseExpediente> searchGrid)
  {
    SearchBar searchBar = new SearchBar();
    searchBar.setActionText("Buscar ");
    searchBar.getActionButton().getElement().setAttribute("new-button", false);
    searchBar.addFilterChangeListener(e ->
     {
       String filter = searchBar.getFilter();
       searchGrid.setVisible(false);
       if ( TextUtil.isNotEmpty(filter))
       {
         Collection<BaseExpediente> items = baseExpedienteService.findByNameLikeIgnoreCase(ThothSession.getCurrentTenant(), filter, selectedClass);
         if ( items.size() > 0)
         {
           searchGrid.setVisible(true);
           searchGrid.setItems(items);
         }
       }
     });

    return searchBar;

  }//buildSearchBar



  public void resetSelector()
  {
    treeGrid.deselectAll();
    treeGrid.collapse(expandedNodes);
    expandedNodes.clear();
    resetSearch();

  }//resetSelector


  private void resetSearch()
  {
    searchGrid.setItems(emptyGrid);
    searchGrid.setVisible(false);
    searchBar.clear();
  }//resetSearch


  public void refresh( )
  {
    resetSearch();
    dataProvider.refreshAll();
  }//refresh


  protected String getBasePage() { return PAGE_JERARQUIA_EXPEDIENTES;}

/*
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


  private Component configureBaseExpedienteSelector()
  {
    ownerExpediente = new HierarchicalSelector<>( classificationService,
        Grid.SelectionMode.SINGLE,
        "Seleccione el Expediente de interes",
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



  private void addExpediente()
  {

  }//addExpediente


  private void saveExpediente(BaseExpediente currentExpediente)
  {

  }

  private void deleteExpediente(BaseExpediente currentExpediente)
  {

  }//saveExpediente
  */

  private void closeEditor()
  {
    rightSection.removeClassName ("right-section");
    content     .removeClassName ("selector");
    removeClassName              ("main-view");

  }//closeEditor

  /*
  private void closeEditor()
  {
     classificationForm.setClassification(null);
     classificationForm.setVisible(false);
     classificationForm.removeClassName("selected-item-form");

  }//closeEditor



 private void closeExpediente()
 {

 }//closeExpediente
  */


  private void updateSelector()
  {
    //ownerExpediente.refresh();
  }//updateSelector


  @Override
  public void afterNavigation(AfterNavigationEvent event)
  {
    addClassName("main-view");
    setSizeFull();

    rightSection = new VerticalLayout();
    rightSection.addClassName ("right-section");
    rightSection.add(new Label("  "));
    //rightSection.add(configureForm(ExpedienteEditForm));

    content = new VerticalLayout();
    content.addClassName ("selector");
    content.add(new H2("Expedientes de la clase "+ classCode+ " - "+ className));
    content.add(new H3("Seleccione el expediente de interes"));
    content.add(configureExpedienteSelector());
    updateSelector();
    closeEditor();

    HorizontalLayout panel=  new HorizontalLayout(content, rightSection);
    panel.setSizeFull();
    add( panel);
  }//afterNavigation


  @Override
  public void setParameter(BeforeEvent event, String parameter)
  {
    Optional<Classification> cls =  classificationService.findById(Long.parseLong(parameter));
    if ( cls.isPresent())
    {
      this.selectedClass = cls.get();
      this.classCode     = selectedClass.formatCode();
      this.className     = selectedClass.getName();
    }
    else
    {
      this.selectedClass = null;
      this.classCode = "---";
      this.className = "";
    }
  }//setParameter

  /*
  private Button add      = new Button("+ Nuevo Expediente");
  private Button save     = new Button("Guardar expediente");
  private Button delete   = new Button("Eliminar expediente");
  private Button close    = new Button("Cancelar");
  */


}//ExpedienteHierarchyView
