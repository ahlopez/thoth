package com.f.thoth.ui.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;

import com.f.thoth.backend.data.entity.HierarchicalEntity;
import com.f.thoth.backend.data.entity.util.TextUtil;
import com.f.thoth.backend.data.security.Tenant;
import com.f.thoth.backend.data.security.ThothSession;
import com.f.thoth.backend.service.HierarchicalService;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;
import com.vaadin.flow.data.selection.MultiSelect;
import com.vaadin.flow.shared.Registration;

@CssImport(value="./styles/grid-tree-toggle-adjust.css", themeFor="vaadin-grid-tree-toggle")
public class HierarchicalSelector<T extends HierarchicalEntity<T>, E extends HasValue.ValueChangeEvent<T>>
       extends  VerticalLayout
       implements HasValue<E, T>
{
   private TreeDataProvider<T>          dataProvider;
   private final Tenant                 tenant;
   private final Set<T>                 result;
   private SearchBar                    searchBar;
   private TreeGrid<T>                  treeGrid;
   private MultiSelect<Grid<T>, T>      multiSelect;
   private List<T>                      gridNodes;
   private Grid<T>                      searchGrid;
   private final Grid.SelectionMode     selectionMode;
   private final HierarchicalService<T> service;
   private final List<T>                emptyGrid     = new ArrayList<>();
   private final Set<T>                 expandedNodes = new TreeSet<>();
   private Consumer<T>                  actionOnSelect;


   public HierarchicalSelector ( HierarchicalService<T> service, Grid.SelectionMode selectionMode, String name, Consumer<T> action)
   {
      this.tenant         = ThothSession.getCurrentTenant();
      this.result         = new TreeSet<>();
      this.selectionMode  = selectionMode;
      this.service        = service;
      this.actionOnSelect = action;
      setup(name);

   }//AbstractHierarchicalSelector constructor


   private void setup( String name)
   {
      add( new H3(name));
      HorizontalLayout  layout = new HorizontalLayout();
      layout.setWidthFull();
      treeGrid = buildSelector();
      layout.add(treeGrid);

      if ( selectionMode != Grid.SelectionMode.NONE)
      {
         this.searchGrid   = buildSearchGrid(treeGrid);
         this.searchBar    = buildSearchBar(searchGrid);
         add(searchBar);
         layout.add(searchGrid);
      }
      add( layout);
      refresh();

   }//setup


   private TreeGrid<T> buildSelector( )
   {
      TreeGrid<T>tGrid = new TreeGrid<>();
      tGrid.setWidthFull();
      
      tGrid.addHierarchyColumn(T::getName).setHeader("Nombre");
      tGrid.setSelectionMode(selectionMode);

      dataProvider = getDataProvider(service);
      tGrid.setDataProvider(dataProvider);

      if( selectionMode == Grid.SelectionMode.SINGLE)
         buildSingleSelector(tGrid);
      else if( selectionMode == Grid.SelectionMode.MULTI)
         buildMultiSelector(tGrid);

      return tGrid;

   }//buildSelector

   private void buildSingleSelector(TreeGrid<T> tGrid)
   {
      
      tGrid.addItemDoubleClickListener( e-> tGrid.deselect(e.getItem()));

      tGrid.addExpandListener ( e-> expandedNodes.addAll(e.getItems()));

      tGrid.addSelectionListener      ( e->
            {
               Optional<T> first = e.getFirstSelectedItem();
               if ( first.isPresent())
                  setValue( first.get());
            });
      
      if (actionOnSelect != null)
          tGrid.asSingleSelect().addValueChangeListener(e-> actionOnSelect.accept(e.getValue()));
      
   }//buildSingleSelector

   private void buildMultiSelector(TreeGrid<T> tGrid)
   {
      multiSelect = tGrid.asMultiSelect();
      
      tGrid.addItemClickListener(event->
      {
         T  valueClicked = event.getItem();
         if ( multiSelect.isSelected(valueClicked))
            tGrid.deselect(valueClicked);
      });

      tGrid.addExpandListener ( e-> expandedNodes.addAll(e.getItems()));

      multiSelect.addValueChangeListener(event ->
      {
         Set<T> values = event.getValue();
         if ( values != null)
            values.forEach( value-> setValue(value));
      });
      
      if (actionOnSelect != null)
      {
          multiSelect.addValueChangeListener(e-> 
              {
                 Set<T> values = e.getValue();
                 values.forEach(val-> actionOnSelect.accept(val));                 
              });
     }

   }//buildMultiSelector


   private Grid<T> buildSearchGrid(TreeGrid<T> tGrid)
   {
      Grid<T> sGrid = new Grid<>();
      sGrid.setVisible(false);
      sGrid.setWidthFull();
      sGrid.addColumn(T::getName).setHeader("Nombre");
      sGrid.setSelectionMode(selectionMode);
      addValueChangeListener(sGrid, tGrid);

      return sGrid;

   }//buildSearchGrid


   private SearchBar buildSearchBar(Grid<T> searchGrid)
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
            Collection<T> filteredItems = service.findByNameLikeIgnoreCase(tenant, filter);
            if ( filteredItems.size() > 0)
            {
               searchGrid.setVisible(true);
               searchGrid.setItems(filteredItems);
            }
         }
      });

      return searchBar;

   }//buildSearchBar


   private Registration addValueChangeListener( Grid<T> sGrid, TreeGrid<T> tGrid)
   {
      Registration registration = null;

      if ( selectionMode == Grid.SelectionMode.SINGLE)
         registration = setupSingleselectListener(sGrid, tGrid);
      else if( selectionMode == Grid.SelectionMode.MULTI)
         registration = setupMultiselectListener(sGrid, tGrid);
      else
         tGrid.deselectAll();

      return registration;

   }//addValueChangeListener


   private Registration setupSingleselectListener( Grid<T> sGrid, TreeGrid<T> tGrid)
   {
      Registration registration = sGrid.asSingleSelect().addValueChangeListener(e ->
      {
         tGrid.deselectAll();
         T value = e.getValue();
         if (value != null)
         {
            setValue(value);
            backtrackParents(tGrid::expand, value);
            tGrid.select(value);
         }
      });
      return registration;
   }//setupSingleselectListener

   private Registration setupMultiselectListener( Grid<T> sGrid, TreeGrid<T> tGrid)
   {
      Registration registration = sGrid.asMultiSelect().addValueChangeListener(e ->
      {
         Set<T> vals= (Set<T>)e.getValue();
         multiSelect.setValue(vals);
         for (T value: vals)
         {
            backtrackParents(tGrid::expand, value);
            tGrid.select(value);
         }
      });
      return registration;
   }//setupMultiselectListener


   private void backtrackParents(Consumer<Collection<T>> fn, final T value)
   {
      final Set<T> path = new TreeSet<>();
      T currentItem = value;
      while (currentItem != null && currentItem.getOwner() != null)
      {
         Optional<T> item = service.findById(currentItem.getOwner().getId());
         if (item.isPresent())
         {
            currentItem = item.get();
            path.add(currentItem);
         }
         else
         {
            currentItem = null;
         }
      }// while currentItem...

      fn.accept(path);

   }//backtrackParents


   private  TreeDataProvider<T>  getDataProvider(HierarchicalService<T> service )
   {
      gridNodes = service.findAll();
      TreeData<T> treeData = new TreeData<T>();
      addChildrenOf(null, treeData);
      TreeDataProvider<T> dataProvider = new TreeDataProvider<>(treeData);
      return dataProvider;

   }//getDataProvider


   private void addChildrenOf(T parent, TreeData<T> treeData)
   {
      List<T> children = getChildrenOf( parent);
      children.forEach( child->
      {
         treeData.addItem(parent, child);
         addChildrenOf(child, treeData);
         gridNodes.remove(child);
      });
   }//addChildrenOf


   private List<T>getChildrenOf( T owner)
   {
      List<T> children = new ArrayList<>();
      gridNodes.forEach(item->
      {
         T parent =  item.getOwner();
         if (parent == null)
         {
            if (owner == null)
               children.add(item);
         }
         else if (parent.equals(owner))
            children.add(item);
      });
      return children;
   }//getChildrenOf

   //   --------------------------     init, reset, refresh -------------------------------
   public void init( Collection<T> initialSelection)
   {
      resetSelector();
      if (initialSelection != null)
      {
         result.clear();
         initialSelection.forEach(value->
         {
            setValue(value);
            backtrackParents(treeGrid::expand, value);
         });
      }
   }//init


   public void resetSelector()
   {
      treeGrid.deselectAll();
      treeGrid.collapse(expandedNodes);
      expandedNodes.clear();
      resetSearch();
      result.clear();

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
      dataProvider = getDataProvider(service);
      treeGrid.setDataProvider(dataProvider);
      dataProvider.refreshAll();
   }//refresh



   // ---------- implements HasValue<E,T> --------------------

   //Resets the value to the empty one.
   @Override public void clear() { result.clear();}

   //Returns the value that represents an empty value.
   @Override public T getEmptyValue() { return null;}

   //Returns the current value of this object, wrapped in an Optional.
   @Override public Optional<T>   getOptionalValue() { return Optional.ofNullable(getValue());}

   //Returns whether this HasValue is considered to be empty.
   @Override public boolean isEmpty() { return result.isEmpty();}

   //Adds a value change listener.
   @Override public Registration addValueChangeListener( HasValue.ValueChangeListener <? super E> listener)
   {
      return addValueChangeListener(searchGrid, treeGrid);
   }//addValueChangeListener

   //Returns the current value of this object.
   @Override public T  getValue()
   {
      return result.isEmpty()? null: result.iterator().next() ;
   }

   public Set<T> getValues()
   {
      Set<T> values = new TreeSet<>();
      values.addAll(result);
      return values;
   }//getValues

   //Returns whether this HasValue is in read-only mode or not.
   @Override public boolean  isReadOnly() { return selectionMode == Grid.SelectionMode.NONE;}

   //Checks whether the required indicator is visible.
   @Override public boolean  isRequiredIndicatorVisible() { return true;}

   //Sets the read-only mode of this HasValue to given mode.
   @Override public void  setReadOnly(boolean readOnly)
   {
      if ( selectionMode == Grid.SelectionMode.SINGLE)
         searchGrid.asSingleSelect().setReadOnly(readOnly);
      else
         searchGrid.asMultiSelect().setReadOnly(readOnly);
   }//setReadOnly

   //Sets the required indicator visible or not.
   @Override public void  setRequiredIndicatorVisible(boolean requiredIndicatorVisible) {}

   //Sets the value of this object.
   @Override public void  setValue(T value)
   {
      if (value == null)
         return;

      if ( selectionMode != Grid.SelectionMode.MULTI)
         result.clear();

      if ( selectionMode != Grid.SelectionMode.NONE)
      {
         treeGrid.select(value);
         result.add(value);
      }

   }//setValue

}//AbstractHierarchicalSelector
