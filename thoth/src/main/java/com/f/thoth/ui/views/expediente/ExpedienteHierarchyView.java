package com.f.thoth.ui.views.expediente;

import static com.f.thoth.ui.utils.Constant.PAGE_JERARQUIA_EXPEDIENTES;
import static com.f.thoth.ui.utils.Constant.TITLE_JERARQUIA_EXPEDIENTES;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import com.f.thoth.backend.data.Role;
import com.f.thoth.backend.data.entity.util.TextUtil;
import com.f.thoth.backend.data.gdoc.classification.Classification;
import com.f.thoth.backend.data.gdoc.expediente.BaseExpediente;
import com.f.thoth.backend.data.gdoc.expediente.Nature;
import com.f.thoth.backend.data.security.ThothSession;
import com.f.thoth.backend.service.BaseExpedienteService;
import com.f.thoth.backend.service.ClassificationService;
import com.f.thoth.ui.MainView;
import com.f.thoth.ui.components.SearchBar;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
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
import com.vaadin.flow.shared.Registration;

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
class ExpedienteHierarchyView extends HorizontalLayout implements HasUrlParameter<String>, AfterNavigationObserver
{
  private BaseExpedienteService       baseExpedienteService;
  private BaseExpediente              currentExpediente;
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


  private Nature nature         = null;     // Tipo de expediente a trabajar: GRUPO/ HOJA/ EXPEDIENTE/ VOLUMEN;
  private Button add            = new Button("+ Nuevo");
  private Button save           = new Button("Guardar");
  private Button delete         = new Button("Eliminar");
  private Button close          = new Button("Cancelar");


  @Autowired
  public ExpedienteHierarchyView(BaseExpedienteService baseExpedienteService, ClassificationService classificationService)
  {
    this.baseExpedienteService = baseExpedienteService;
    this.classificationService = classificationService;
    //this.currentUser           = ThothSession.getCurrentUser();

  }//ExpedienteHierarchyView



  @Override
  public void afterNavigation(AfterNavigationEvent event)
  {
    addClassName("main-view");
    setSizeFull();

    rightSection = new VerticalLayout();
    rightSection.addClassName ("right-section");
    rightSection.setWidth("35%");
    rightSection.add(new H3("RIGHT SECTION"));
    //rightSection.add(configureForm(ExpedienteEditForm));

    content = new VerticalLayout();
    content.addClassName ("selector");
    content.setWidth("65%");
    content.add(new H2("Expedientes de la clase "+ classCode+ " - "+ className));
    content.add(new H3("Seleccione el expediente de interes"));
    content.add(configureExpedienteSelector());
    content.add(configureButtons());
    updateSelector();
    closeEditor();

    add(content, rightSection);
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


  private Component configureExpedienteSelector()
  {
    HorizontalLayout layout = new HorizontalLayout();
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
    //grid.getElement().setAttribute("colspan", "5");
    grid.setSelectionMode(Grid.SelectionMode.SINGLE);
    grid.setWidthFull();
    grid.addHierarchyColumn(BaseExpediente::getName).setHeader("Nombre del expediente").setWidth("78%");
    grid.addColumn(BaseExpediente::formatCode).setHeader("Código").setWidth("22%");
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
      {  return baseExpedienteService.hasChildren(expediente, expediente == null? null: expediente.getClassificationClass());
      }

      @Override
      protected Stream<BaseExpediente> fetchChildrenFromBackEnd(  HierarchicalQuery<BaseExpediente, Void> query)
      {
        Stream<BaseExpediente> empty = new ArrayList<BaseExpediente>().stream();
        if ( query == null)
        {  return empty;
        }
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
      {
        currentExpediente = first.get();
        // TODO: Aqui­ llamar el metodo que procesa la seleccion:
      }
    });

  }//buildSingleSelector


  private Grid<BaseExpediente> buildSearchGrid(TreeGrid<BaseExpediente> tGrid)
  {
    Grid<BaseExpediente> sGrid = new Grid<>();
    sGrid.setVisible(false);
    sGrid.setWidthFull();
    sGrid.addColumn(BaseExpediente::getName).setHeader("Nombre de Expediente").setWidth("70%");
    sGrid.addColumn(BaseExpediente::formatCode).setHeader("Código").setWidth("30%");
    sGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
    setupSingleSelectListener( sGrid, tGrid);
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



  private Registration setupSingleSelectListener( Grid<BaseExpediente> sGrid, TreeGrid<BaseExpediente> tGrid)
  {
     Registration registration = sGrid.asSingleSelect().addValueChangeListener(e ->
     {
        tGrid.deselectAll();
        BaseExpediente value = e.getValue();
        if (value != null)
        {
           backtrackParents(tGrid::expand, value);
           tGrid.select(value);
           currentExpediente = value;
           //llamar el método que procesa la selección
        }
     });
     return registration;
  }//setupSingleSelectListener (in selection grid)



  private void backtrackParents(Consumer<Collection<BaseExpediente>> fn, final BaseExpediente value)
  {
     final Set<BaseExpediente> path = new TreeSet<>();
     BaseExpediente currentItem = value;
     while (currentItem != null && currentItem.getOwnerPath() != null)
     {
        Optional<BaseExpediente> item = baseExpedienteService.findByPath(currentItem.getOwnerPath());
        if (item.isPresent())
        {  currentItem = item.get();
           path.add(currentItem);
        }else
        {  currentItem = null;
        }
     }// while currentItem...

     fn.accept(path);

  }//backtrackParents


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
    close.addClickListener (click -> closeAll());

    save.getElement().getStyle().set("margin-left", "auto");
    add .getElement().getStyle().set("margin-left", "auto");

    HorizontalLayout buttons = new HorizontalLayout();
    buttons.setWidthFull();
    buttons.setPadding(true);
    buttons.add( delete, save, close, add);
    return buttons;
  }//configureButtons


  private void addExpediente()
  {
        // Presentar ventana emergente con el tipo GRUPO/EXPEDIENTE/VOLUMEN A CREAR en la clase
        // Crear el objeto correspondiente al expediente seleccionado
        // Editar el expediente creado
  }//addExpediente


  private void saveExpediente(BaseExpediente currentExpediente)
  {
        // Guardar el expediente seleccionado, si existe
  }//saveExpediente

  private void deleteExpediente(BaseExpediente currentExpediente)
  {
        // Borrar el expediente seleccionado si:
        //   a. Existe
        //   b. No tiene hijos
        //   c. No tiene documentos
  }//deleteExpediente


  private void closeEditor()
  {
    /*
       De acuerdo con el tipo de expediente seleccionado
       classificationForm.setClassification(null);
       classificationForm.setVisible(false);
       classificationForm.removeClassName("selected-item-form");
       
         private void closeEditor()
         {
            rightSection.removeClassName ("right-section");
            content     .removeClassName ("selector");
            removeClassName              ("main-view");
         }//closeEditor
     */

  }//closeEditor



  private void closeAll()
  {  // Cancel operation
     closeEditor();
     currentExpediente = null;
     resetSelector();
  }//closeAll


  private void updateSelector()
  {
     refresh();
  }//updateSelector



  /*
     // Probablemente habrá una de estos métodos para cada tipo de Forma que se implemente según GRUPO/EXPEDIENTE/VOLUMEN
  private void saveExpediente(ClassificationForm.SaveEvent event)
  {
     Classification classification = event.getClassification();
     classificationService.save(currentUser, classification);
     updateSelector();
     closeEditor();
  }//saveExpediente
  */

}//ExpedienteHierarchyView
