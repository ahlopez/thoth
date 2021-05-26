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
import com.f.thoth.backend.data.gdoc.expediente.ExpedienteGroup;
import com.f.thoth.backend.data.gdoc.expediente.Nature;
import com.f.thoth.backend.data.security.ThothSession;
import com.f.thoth.backend.service.BaseExpedienteService;
import com.f.thoth.backend.service.ExpedienteGroupService;
import com.f.thoth.backend.service.ClassificationService;
import com.f.thoth.backend.service.ExpedienteLeafService;
import com.f.thoth.backend.service.SchemaService;
import com.f.thoth.backend.service.VolumeService;
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

  private ClassificationService       classificationService;
  private Classification              selectedClass;
  private String                      classCode = "";
  private String                      className = "";

  private ExpedienteGroupService      expedienteGroupService;
  private ExpedienteGroupEditor       expedienteGroupEditor;

  private BaseExpedienteService       baseExpedienteService;
  private BaseExpediente              selectedBase;

  private ExpedienteLeafService           expedienteService;
  private VolumeService               volumeService;
  private SchemaService               schemaService;

  private VerticalLayout              content;
  private VerticalLayout              rightSection;

  private TreeGrid<BaseExpediente>    treeGrid;
  private HierarchicalDataProvider<BaseExpediente, Void> dataProvider;

  private Grid<BaseExpediente>        searchGrid;
  private SearchBar                   searchBar;
  private final List<BaseExpediente>  emptyGrid     = new ArrayList<>();
  private final Set<BaseExpediente>   expandedNodes = new TreeSet<>();


  private HorizontalLayout            classActions  = new HorizontalLayout();
  private HorizontalLayout            groupActions  = new HorizontalLayout();


  @Autowired
  public ExpedienteHierarchyView(ClassificationService   classificationService,
                                 BaseExpedienteService   baseExpedienteService,
                                 ExpedienteGroupService  expedienteGroupService,
                                 ExpedienteLeafService       expedienteService,
                                 SchemaService           schemaService,
                                 VolumeService           volumeService
                                )
  {
    this.classificationService   = classificationService;
    this.baseExpedienteService   = baseExpedienteService;
    this.expedienteGroupService  = expedienteGroupService;
    this.expedienteService       = expedienteService;
    this.volumeService           = volumeService;
    this.schemaService           = schemaService;
    this.selectedBase            = null;

  }//ExpedienteHierarchyView


  // -----------------------  Configuration on Arrival to the Page ----------------------
  @Override
  public void afterNavigation(AfterNavigationEvent event)
  {
    addClassName("main-view");
    setSizeFull();

    rightSection = new VerticalLayout();
    rightSection.addClassName ("right-section");
    rightSection.setWidth("35%");
    rightSection.add(new H3("RIGHT SECTION"));
    rightSection.add(expedienteGroupEditor);
    rightSection.setVisible(false);

    content = new VerticalLayout();
    content.addClassName ("selector");
    content.setWidth("65%");
    content.add(new H2("Expedientes de la clase "+ classCode+ " - "+ className));
    content.add(new H3("Seleccione el expediente de interes"));
    content.add(configureExpedienteSelector());
    content.add(configureActions());

    add(content, rightSection);

  }//afterNavigation


  protected String getBasePage() { return PAGE_JERARQUIA_EXPEDIENTES;}


  @Override
  public void setParameter(BeforeEvent event, String parameter)
  {
    Optional<Classification> cls =  classificationService.findById(Long.parseLong(parameter));
    boolean clsPresent           =  cls != null && cls.isPresent();
    this.selectedClass           =  clsPresent? cls.get()                  : null;
    this.classCode               =  clsPresent? selectedClass.formatCode() : "---";
    this.className               =  clsPresent? selectedClass.getName()    : "";
    setupGroupEditor();
    setupExpedienteEditor();
    setupVolumeEditor();
  }//setParameter


  private void setupGroupEditor()
  {
    this.expedienteGroupEditor  = new ExpedienteGroupEditor(expedienteGroupService, baseExpedienteService, schemaService, selectedClass);
    this.expedienteGroupEditor.addListener(ExpedienteGroupEditor.CloseEvent.class, e->
    {  rightSection.setVisible(false);
       selectInGrid(e.getExpediente());
    });
  }//setupGroupEditor


  private void setupExpedienteEditor()
  {
  }


  private void setupVolumeEditor()
  {
  }

  // ---------------------  Base Expediente Selector ---------------------------
  private Component configureExpedienteSelector()
  {
    HorizontalLayout layout = new HorizontalLayout();
    layout.setWidthFull();
    treeGrid = buildTreeSelector();
    layout.add(treeGrid);

    this.searchGrid   = buildSearchGrid(treeGrid);
    searchGrid.setWidth("79%");
    this.searchBar    = buildSearchBar(searchGrid);
    content.add(searchBar);
    layout .add(searchGrid);
    layout .setFlexGrow(1, treeGrid);

    refresh();
    return layout;

  }//configureSelector


  private TreeGrid<BaseExpediente> buildTreeSelector()
  {
    TreeGrid<BaseExpediente> tGrid = new TreeGrid<>();
    tGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
    tGrid.setWidthFull();
    tGrid.addHierarchyColumn(BaseExpediente::getName).setHeader("Nombre del expediente").setWidth("76%");
    tGrid.addColumn(BaseExpediente::formatCode).setHeader("Código").setWidth("24%");
    this.dataProvider = getDataProvider();
    tGrid.setDataProvider(dataProvider);
    setupTreeListeners(tGrid);
    return tGrid;

  }//buildTreeSelector


  private HierarchicalDataProvider<BaseExpediente, Void> getDataProvider()
  {
    return new AbstractBackEndHierarchicalDataProvider<BaseExpediente, Void>()
    {
      @Override
      public int getChildCount(HierarchicalQuery<BaseExpediente, Void> query)
      {
        if (query == null)
        { return 0;
        }
        BaseExpediente base = query.getParent();
        return base != null
              ? baseExpedienteService.countByParent(base)
              : baseExpedienteService.countByClass(selectedClass);
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
        return  base != null? baseExpedienteService.findByParent(base).stream()
                            : baseExpedienteService.findByClass(selectedClass).stream();
      }//fetchChildrenFromBackEnd

    };// new AbstractBackEndHierarchicalDataProvider<>

  }//getDataProvider


  private void setupTreeListeners(TreeGrid<BaseExpediente> tGrid)
  {
    tGrid.addItemClickListener      ( e-> tGrid.select  (e.getItem()));
    tGrid.addItemDoubleClickListener( e->
    {  tGrid.deselect(e.getItem());
       selectedBase = null;
       updateActions();
    });
    tGrid.addExpandListener         ( e-> expandedNodes.addAll(e.getItems()));
    tGrid.addSelectionListener      ( e->
    {
      Optional<BaseExpediente> first = e.getFirstSelectedItem();
      if ( first.isPresent() )
      { selectedBase = first.get();
        selectExpediente(selectedBase);
      }
    });

  }//setupTreeListeners

  // ------------------------ Search List Expediente Selector ------------------------------
  private Grid<BaseExpediente> buildSearchGrid(TreeGrid<BaseExpediente> tGrid)
  {
    Grid<BaseExpediente> sGrid = new Grid<>();
    sGrid.setVisible(false);
    sGrid.setWidthFull();
    sGrid.addColumn(BaseExpediente::getName).setHeader("Nombre de Expediente").setWidth("70%");
    sGrid.addColumn(BaseExpediente::formatCode).setHeader("Código").setWidth("30%");

    sGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
    sGrid.addItemClickListener                   ( e-> sGrid.select (e.getItem()));
    sGrid.addItemDoubleClickListener             ( e-> sGrid.select (e.getItem()));
    sGrid.asSingleSelect().addValueChangeListener( e-> updateGrid   (e.getValue()));

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
          rightSection.setVisible(false);
        }
      }
    });
    return searchBar;

  }//buildSearchBar


  // ---------------- When an Expediente is Selected ---------------------
  private void updateGrid( BaseExpediente selectedExpediente)
  {
     treeGrid.deselectAll();
     if (selectedExpediente != null)
     {
       backtrackParents(treeGrid::expand, selectedExpediente);
       selectedBase = selectedExpediente;
       treeGrid.select(selectedExpediente);
     }
  }//updateGrid

  private void backtrackParents(Consumer<Collection<BaseExpediente>> fn, final BaseExpediente value)
  {
    final Set<BaseExpediente> path = new TreeSet<>();
    BaseExpediente currentItem = value;
    while (currentItem != null && currentItem.getOwnerId() != null)
    {
      Optional<BaseExpediente> item = baseExpedienteService.findById(currentItem.getOwnerId());
      if (item.isPresent())
      {  currentItem = item.get();
         path.add(currentItem);
      }else
      {  currentItem = null;
      }
    }// while currentItem...

    fn.accept(path);

  }//backtrackParents

  // ---------------- Full reset and search list reset --------------------
  public void resetSelector()
  {
    treeGrid.deselectAll();
    treeGrid.collapse(expandedNodes);
    selectedBase  = null;

    expandedNodes.clear();
    resetSearch();
    updateActions();
  }//resetSelector


  private void resetSearch()
  {
    searchGrid.setItems(emptyGrid);
    searchGrid.setVisible(false);
    searchBar.clear();
    rightSection.setVisible(false);
  }//resetSearch


  public void refresh( )
  {
    selectedBase = null;
    resetSearch();
    dataProvider.refreshAll();
  }//refresh


  // -------------------------   Actions ---------------------------
  private Component configureActions()
  {
    VerticalLayout actions = new VerticalLayout();
    actions.add( configureClassActions (classActions));
    actions.add( configureGroupActions (groupActions));
    return actions;
  }//configureActions


  private void updateActions()
  {
    rightSection.setVisible(selectedBase != null);
    classActions.setVisible( selectedBase == null);
    groupActions.setVisible( selectedBase != null && selectedBase.isOfType(Nature.GRUPO));
  }//updateActions


  private Component configureClassActions  (HorizontalLayout classActions)
  {
    Button   addGrupo     = new Button("+Grupo en Clase");
    Button   addExpediente= new Button("+Expediente en Clase");
    Button   addVolumen   = new Button("+Volumen en Clase");
    Button   close        = new Button("Cancelar");

    addGrupo.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    addGrupo.addClickShortcut(Key.ENTER);
    addGrupo.addClickListener(click ->
    {  rightSection.setVisible(true);
       expedienteGroupEditor.addExpedienteGroup(selectedBase);
    });
    addGrupo.getElement().getStyle().set("margin-left", "auto");

    addExpediente.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    addExpediente.addClickShortcut(Key.ENTER);
    addExpediente.addClickListener(click ->
    {  rightSection.setVisible(true);
       addLeaf();
    });
    addExpediente.getElement().getStyle().set("margin-left", "auto");

    addVolumen.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    addVolumen.addClickShortcut(Key.ENTER);
    addVolumen.addClickListener(click ->
    {  rightSection.setVisible(true);
       addVolume();
    });
    addVolumen.getElement().getStyle().set("margin-left", "auto");

    close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
    close.addClickShortcut(Key.ESCAPE);
    close.addClickListener (click -> closeAll());

    classActions.setWidthFull();
    classActions.setPadding(true);
    classActions.add(close, addVolumen, addExpediente, addGrupo);
    return classActions;
  }//configureClassActions



  private Component configureGroupActions  (HorizontalLayout groupActions)
  {
    Button   addSubgrupo   = new Button("+SubGrupo en Grupo");
    Button   addExpediente = new Button("+Expediente en Grupo");
    Button   addVolumen    = new Button("+Volumen en Grupo");
    Button   close         = new Button("Cancelar");

    addSubgrupo.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    addSubgrupo.addClickShortcut(Key.ENTER);
    addSubgrupo.addClickListener(click ->
    {  rightSection.setVisible(true);
       expedienteGroupEditor.addExpedienteGroup(selectedBase);
    });
    addSubgrupo.getElement().getStyle().set("margin-left", "auto");

    addExpediente.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    addExpediente.addClickShortcut(Key.ENTER);
    addExpediente.addClickListener(click ->
    {  rightSection.setVisible(true);
       addLeaf();
    });
    addExpediente.getElement().getStyle().set("margin-left", "auto");

    addVolumen.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    addVolumen.addClickShortcut(Key.ENTER);
    addVolumen.addClickListener(click ->
    {  rightSection.setVisible(true);
       addVolume();
    });
    addVolumen.getElement().getStyle().set("margin-left", "auto");

    close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
    close.addClickShortcut(Key.ESCAPE);
    close.addClickListener (click -> closeAll());

    groupActions.setWidthFull();
    groupActions.setPadding(true);
    groupActions.add(close, addVolumen, addExpediente, addSubgrupo);
    return groupActions;
  }//configureGroupActions


  private void addLeaf() {}
  private void addVolume() {}


  private void selectExpediente(BaseExpediente selectedBase)
  {
    updateActions();
    if ( selectedBase == null )
    {  return;
    }
    if( selectedBase.isOfType(Nature.GRUPO))
    {
      ExpedienteGroup selectedBranch = expedienteGroupService.findByCode(selectedBase.getCode());
      expedienteGroupEditor.editExpedienteGroup(selectedBranch);
    } else if( selectedBase.isOfType(Nature.EXPEDIENTE))
    {
    } else if( selectedBase.isOfType(Nature.VOLUMEN))
    {
    }

  }//selectExpediente


  private void closeAll()
  {
    expedienteGroupEditor.closeEditor();
    resetSelector();
  }//closeAll


  // ----------------  Add a new Expediente to the Selection Tree --------------
  private void selectInGrid(BaseExpediente base)
  {
    if (base != null)
    {
       dataProvider.refreshAll();
       backtrackParents(treeGrid::expand, base);
    }
    updateActions();
  }//selectInGrid

}//ExpedienteHierarchyView
