package com.f.thoth.ui.views.expediente;

import static com.f.thoth.ui.utils.Constant.PAGE_JERARQUIA_EXPEDIENTES;
import static com.f.thoth.ui.utils.Constant.TITLE_JERARQUIA_EXPEDIENTES;

import java.time.LocalDateTime;
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
import com.f.thoth.backend.data.gdoc.expediente.BranchExpediente;
import com.f.thoth.backend.data.gdoc.metadata.SchemaValues;
import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.data.security.ThothSession;
import com.f.thoth.backend.data.security.User;
import com.f.thoth.backend.service.BaseExpedienteService;
import com.f.thoth.backend.service.BranchExpedienteService;
import com.f.thoth.backend.service.ClassificationService;
import com.f.thoth.backend.service.ExpedienteService;
import com.f.thoth.backend.service.SchemaService;
import com.f.thoth.backend.service.SchemaValuesService;
import com.f.thoth.backend.service.VolumeService;
import com.f.thoth.ui.MainView;
import com.f.thoth.ui.components.Notifier;
import com.f.thoth.ui.components.SearchBar;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
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
  private BaseExpedienteForm          baseExpedienteForm;
  private BranchExpedienteService     branchExpedienteService;
  private BranchExpediente            currentBranch;              // Branch that is presented on right panel
  //  private BranchExpediente            selectedBranch;             // Branch that is selected on content panel

  private BaseExpedienteService       baseExpedienteService;
  private ExpedienteService           expedienteService;
  private VolumeService               volumeService;
  private SchemaService               schemaService;
  private SchemaValuesService         schemaValuesService;
  private BaseExpediente              currentExpediente;
  private User                        currentUser;

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

  private Button   add          = new Button("+ Nuevo");
  private Button   save         = new Button("Guardar");
  private Button   delete       = new Button("Eliminar");
  private Button   close        = new Button("Cancelar");
  private Notifier notifier     = new Notifier();


  @Autowired
  public ExpedienteHierarchyView(ClassificationService   classificationService,
                                 BaseExpedienteService   baseExpedienteService,
                                 BranchExpedienteService branchExpedienteService,
                                 ExpedienteService       expedienteService,
                                 SchemaService           schemaService,
                                 SchemaValuesService     schemaValuesService,
                                 VolumeService           volumeService
                                )
  {
    this.classificationService   = classificationService;
    this.baseExpedienteService   = baseExpedienteService;
    this.branchExpedienteService = branchExpedienteService;
    this.expedienteService       = expedienteService;
    this.volumeService           = volumeService;
    this.schemaService           = schemaService;
    this.schemaValuesService     = schemaValuesService;
    this.currentBranch           = null;
    this.currentExpediente       = null;
    this.currentUser             = ThothSession.getCurrentUser();

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
    rightSection.add(configureForm(currentExpediente));

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
    grid.setSelectionMode(Grid.SelectionMode.SINGLE);
    grid.setWidthFull();
    grid.addHierarchyColumn(BaseExpediente::getName).setHeader("Nombre del expediente").setWidth("76%");
    grid.addColumn(BaseExpediente::formatCode).setHeader("Código").setWidth("24%");
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
        //  selectedBranch    = branchExpedienteService.findByCode(currentExpediente == null? null: currentExpediente.getCode());

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


  private BaseExpedienteForm configureForm(BaseExpediente selectedExpediente)
  {
    baseExpedienteForm = new BaseExpedienteForm(schemaService);
    baseExpedienteForm.addListener(BaseExpedienteForm.SaveEvent.class,   this::saveExpediente );
    baseExpedienteForm.addListener(BaseExpedienteForm.CloseEvent.class,  e -> closeEditor());
    return baseExpedienteForm;

  }//configureForm



  private Component configureButtons()
  {
    add.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    add.addClickListener(click -> addExpediente());
    add.getElement().getStyle().set("margin-left", "auto");

    save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    save.addClickShortcut(Key.ENTER);
    save.addClickListener(click -> saveExpediente(currentExpediente));
    save.getElement().getStyle().set("margin-left", "auto");

    delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
    delete.addClickListener(click -> deleteExpediente(currentExpediente));

    close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
    close.addClickShortcut(Key.ESCAPE);
    close.addClickListener (click -> closeAll());


    HorizontalLayout buttons = new HorizontalLayout();
    buttons.setWidthFull();
    buttons.setPadding(true);
    buttons.add( delete, save, close, add);
    return buttons;
  }//configureButtons


  private void addBranchExpediente()
  {
    currentBranch = newBranch();
    currentBranch.setClassificationClass(selectedClass);
    String ownerPath = currentExpediente == null? null : currentExpediente.getPath();
    currentBranch.setOwnerPath(ownerPath);
    editBranch(currentBranch);
  }//addBranchExpediente


  public void editBranchExpediente( BranchExpediente branch)
  {
    currentBranch = branchExpedienteService.load(branch.getId());
    if(  currentBranch != null )
    {   editBranch(currentBranch);
    }
  }//editBranchExpediente



  private  BranchExpediente   newBranch()
  {
    BranchExpediente currentBranch = new BranchExpediente();
    LocalDateTime  now  = LocalDateTime.now();
    currentBranch.setExpedienteCode      (null);
    currentBranch.setPath                (null);
    currentBranch.setName                (" ");
    currentBranch.setObjectToProtect     (new ObjectToProtect());
    currentBranch.setCreatedBy           (currentUser);
    currentBranch.setClassificationClass (selectedClass);
    currentBranch.setMetadataSchema      (null);
    currentBranch.setMetadata            (SchemaValues.EMPTY);
    currentBranch.setDateOpened          (now);
    currentBranch.setDateClosed          (now.plusYears(200L));
    currentBranch.setOwnerPath           (null);
    currentBranch.setOpen                (true);
    currentBranch.setKeywords            ("keyword1, keyword2, keyword3");
    currentBranch.setMac                 ("[mac]");
    return currentBranch;

  }//newBranch


  private void saveBranchExpediente( BranchExpediente branch)
  {
    if (branch == null)
      return;

    branchExpedienteService.save(currentUser, branch);
    updateSelector();
    closeEditor();
    currentBranch     = null;
    currentExpediente = null;

  }//saveBranchExpediente


  private void saveExpediente(BaseExpedienteForm.SaveEvent event)
  {
    BaseExpediente expediente = event.getBaseExpediente();
    boolean isNew = expediente.getId() == null;
    schemaValuesService.save(currentUser, expediente.getMetadata());
    switch(expediente.getType())
    {
    case GRUPO:
      saveBranchExpediente(currentBranch);
      break;
    case EXPEDIENTE:
      break;
    case VOLUMEN:
      break;
    case HOJA:
    }

    if (isNew)
    {
      notifier.show("Expediente creado con código "+ expediente.formatCode(),  "notifier-accept",  6000,  Notification.Position.BOTTOM_CENTER);
      updateSelector();
    }else
    {   notifier.show("Expediente "+ expediente.formatCode()+ " actualizado",    "notifier-accept",  3000,  Notification.Position.BOTTOM_CENTER);
    }

  }//saveBaseExpediente


  private void deleteBranchExpediente(BranchExpediente branch)
  {
    if (branch == null)
      return;

    branchExpedienteService.delete(currentUser, branch);
    updateSelector();
    closeEditor();
    currentBranch     = null;
    currentExpediente = null;
  }//deleteBranchExpediente


  private void addExpediente()
  {
    addBranchExpediente();
    // Presentar ventana emergente con el tipo GRUPO/EXPEDIENTE/VOLUMEN A CREAR en la clase
    // Crear el objeto correspondiente al expediente seleccionado
    // Editar el expediente creado
  }//addExpediente


  private void saveExpediente(BaseExpediente currentExpediente)
  {
    saveBranchExpediente(currentBranch);
  }//saveExpediente

  private void deleteExpediente(BaseExpediente currentExpediente)
  {
    deleteBranchExpediente(currentBranch);
    // Borrar el expediente seleccionado si:
    //   a. Existe
    //   b. No tiene hijos
    //   c. No tiene documentos
  }//deleteExpediente


  private void editBranch(BranchExpediente branch)
  {
    if (branch == null)
    {
      closeEditor();
    } else
    {
      if( branch.isPersisted())
        branch = branchExpedienteService.load(branch.getId());

      baseExpedienteForm.setVisible(true);
      baseExpedienteForm.addClassName("selected-item-form");
      baseExpedienteForm.setExpediente(branch.getExpediente());
      rightSection.setVisible(true);
    }
  }//editBranch


  private void closeEditor()
  {
    baseExpedienteForm.setExpediente(null);
    baseExpedienteForm.setVisible(false);
    baseExpedienteForm.removeClassName("selected-item-form");
  }//closeEditor


  private void closeAll()
  {  // Cancel operation
    closeEditor();
    currentBranch     = null;
    currentExpediente = null;
    resetSelector();
  }//closeAll


  private void updateSelector()
  {
    refresh();
  }//updateSelector

}//ExpedienteHierarchyView
