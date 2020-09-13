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
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;
import com.vaadin.flow.data.selection.MultiSelect;
import com.vaadin.flow.shared.Registration;

public class AbstractHierarchicalSelector<T extends HierarchicalEntity<T>, E extends HasValue.ValueChangeEvent<T>>
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


   public AbstractHierarchicalSelector ( HierarchicalService<T> service, Grid.SelectionMode selectionMode, String name)
   {
      this.tenant         = ThothSession.getCurrentTenant();
      this.result         = new TreeSet<>();
      this.selectionMode  = selectionMode;
      this.service        = service;
      setup(name);

   }//AbstractHierarchicalSelector constructor


   private void setup( String name)
   {
      add( new Label(name));
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

   }//setup


   private TreeGrid<T> buildSelector( )
   {
      TreeGrid<T>tGrid = new TreeGrid<>();
      tGrid.addClassName("selector-tree");
      tGrid.setWidthFull();
      tGrid.addHierarchyColumn(T::getName).setHeader("Nombre");
      tGrid.setSelectionMode(selectionMode);

      dataProvider = getDataProvider(service);
      tGrid.setDataProvider(dataProvider);
      tGrid.addExpandListener            ( e-> expandedNodes.addAll(e.getItems()));

      switch( selectionMode)
      {
      case SINGLE:
         tGrid.addItemClickListener      ( e-> tGrid.select(e.getItem()));
         tGrid.addItemDoubleClickListener( e-> tGrid.deselect(e.getItem()));
         tGrid.addSelectionListener      ( e-> setValue( e.getFirstSelectedItem().get()));
         break;
      case MULTI:
         multiSelect = tGrid.asMultiSelect();
         multiSelect.addValueChangeListener(event -> 
         {
            Set<T> values = event.getValue();
            if ( values != null)
               values.forEach( value-> result.add(value));
         });
         break;
      default:

      }//switch

      return tGrid;

      /*
         Alternatively you can use a grid-specific selection API. To get
         the selected value or values in any selection model, you can
         use a SelectionListener, with the provided generic
         SelectionEvent, to get the selected value or values.
         Example: Using addSelectionListener to get all selected
         items.
         Grid<Person> grid = new Grid<>();
         // switch to multiselect mode
         grid.setSelectionMode(SelectionMode.MULTI);
         grid.addSelectionListener(event ->
         {
             Set<Person> selected = event.getAllSelectedItems();
             message.setText(selected.size() + " items selected");
         });
       */

   }//buildTreeGrid


   private Grid<T> buildSearchGrid(TreeGrid<T> tGrid)
   {
      Grid<T> sGrid = new Grid<>();
      sGrid.addClassName("selector-list");
      sGrid.setVisible(false);
      sGrid.setWidthFull();
      sGrid.addColumn(T::getName).setHeader("Nombre").setFlexGrow(50);
      //sGrid.addColumn(T::getCode).setHeader("ID").setFlexGrow(50);
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
      switch (selectionMode)
      {
      case SINGLE:
      {
         registration = sGrid.asSingleSelect().addValueChangeListener(e ->
         {
            tGrid.deselectAll();
            T value = e.getValue();
            if (value != null)
            {
               setValue(value);
               tGrid.select(value);
               backtrackParents(tGrid::expand, value);
            }
         });
         break;
      }
      case MULTI:
      {
         registration = sGrid.asMultiSelect().addValueChangeListener(e ->
         {
            Set<T> vals= (Set<T>)e.getValue();
            multiSelect.setValue(vals);
            for (T value: vals)
            {
               backtrackParents(tGrid::expand, value);
            }
         });
         break;
      }
      default:
         tGrid.deselectAll();
      }

      return registration;

   }//addValueChangeListener


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

   //   --------------------------     init, reset -------------------------------
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
      searchBar.clear();
      treeGrid.deselectAll();
      treeGrid.collapse(expandedNodes);
      expandedNodes.clear();
      searchGrid.setItems(emptyGrid);
      result.clear();

   }//resetSelector



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

   public Set<T> getValues(){ return result;}

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
