package com.f.thoth.ui.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.stream.Stream;

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
import com.vaadin.flow.data.provider.hierarchy.AbstractBackEndHierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;
import com.vaadin.flow.shared.Registration;

public class TreeGridSelector<T extends HierarchicalEntity<T>, E extends HasValue.ValueChangeEvent<T>>
extends  VerticalLayout
implements HasValue<E, T>
{
   private enum Mode { NONE, SINGLE, MULTI}

   private HierarchicalDataProvider<T, Void> dataProvider;
   private final Mode                   mode;
   private final Tenant                 tenant;         
   private final ArrayList<T>           result;
   private SearchBar                    searchBar;
   private TreeGrid<T>                  treeGrid;
   private Grid<T>                      searchGrid;
   private final Grid.SelectionMode     selectionMode;
   private final HierarchicalService<T> service;
   private final List<T>                emptyGrid = new ArrayList<>();


   public TreeGridSelector ( HierarchicalService<T> service, Grid.SelectionMode selectionMode, String name)
   {
      this.mode           = selectionMode == Grid.SelectionMode.SINGLE? Mode.SINGLE :
         selectionMode == Grid.SelectionMode.MULTI?  Mode.MULTI:  
            Mode.NONE;
      this.tenant         = ThothSession.getCurrentTenant();
      this.result         = new ArrayList<>();
      this.selectionMode  = selectionMode;
      this.service        = service;
      setup(tenant, name);

   }//TreeGridSelector constructor

   private void setup(Tenant tenant, String name)
   {
      add( new Label(name));
      this.treeGrid = buildTreeGrid();
      HorizontalLayout  layout = new HorizontalLayout();
      layout.setWidthFull();
      layout.add(treeGrid);

      if ( mode != Mode.NONE)
      {
         this.searchGrid   = buildSearchGrid(treeGrid);
         searchGrid.setVisible(false);
         this.searchBar    = buildSearchBar(searchGrid);
         add(searchBar);
         layout.add(searchGrid);
      }
      add( layout);

   }//buildLayout


   private TreeGrid<T> buildTreeGrid()
   {
      TreeGrid<T> tGrid = new TreeGrid<>();
      tGrid.setWidthFull();
      tGrid.addColumn(T::getName).setFlexGrow(30).setHeader("Nombre");
      tGrid.addHierarchyColumn(T::getCode).setFlexGrow(70).setHeader("Id");
      this.dataProvider = getDataProvider(service);
      tGrid.setDataProvider(dataProvider);

      return tGrid;

   }//buildTreeGrid


   private Grid<T> buildSearchGrid(TreeGrid<T> tGrid)
   {
      Grid<T> sGrid = new Grid<>();
      sGrid.setVisible(false);
      sGrid.setWidthFull();
      sGrid.addColumn(T::getCode).setHeader("ID").setFlexGrow(30);
      sGrid.addColumn(T::getName).setHeader("Nombre").setFlexGrow(70);
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
            Collection<T> filteredItems = service.findByNameLikeIgnoreCase( tenant,filter);
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
      switch (mode)
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
            for (T value: e.getValue())
            {
               setValue(value);
               tGrid.select(value);
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

   private  HierarchicalDataProvider<T, Void>  getDataProvider(HierarchicalService<T> service )
   {
      final HierarchicalDataProvider<T, Void> dataProvider = new AbstractBackEndHierarchicalDataProvider<T, Void>()
      {
         @Override
         public int getChildCount(final HierarchicalQuery<T, Void> hierarchicalQuery)
         {
            final T owner = hierarchicalQuery.getParent();
            return  service.countByParent(owner);
         }//getChildCount


         @Override
         public boolean hasChildren(final T node)
         {
            return service.hasChildren(node);
         }//hasChildren


         @Override
         protected Stream<T> fetchChildrenFromBackEnd(final HierarchicalQuery<T, Void> hierarchicalQuery)
         {
            final T owner = hierarchicalQuery.getParent();
            return service.findByParent(owner).stream();

         }//fetchChildrenFromBackEnd


      }; //new AbstractBackEndHierarchicalDataProvider<>()

      return dataProvider;

   }//getDataProvider


   public void refresh( )
   {
      //treeGrid.setDataProvider(treeGrid.getDataProvider());
      result.clear();
      dataProvider.refreshAll();
      treeGrid.deselectAll();
      searchGrid.setItems(emptyGrid);
      searchBar.setPlaceHolder("");
   }//refreshrefresh


   // ---------- implements HasValue<E,T> --------------------

   //Resets the value to the empty one.
   @Override public void clear() { result.clear();}

   //Returns the value that represents an empty value.
   @Override public T getEmptyValue() { return null;}

   //Returns the current value of this object, wrapped in an Optional.
   @Override public Optional<T>   getOptionalValue() { return Optional.ofNullable(getValue()); }

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
      return result.isEmpty()? null: result.get(0) ;
   }

   public Collection<T> getValues(){ return result;}

   //Returns whether this HasValue is in read-only mode or not.
   @Override public boolean  isReadOnly() { return mode == Mode.NONE;}

   //Checks whether the required indicator is visible.
   @Override public boolean  isRequiredIndicatorVisible() { return true;}

   //Sets the read-only mode of this HasValue to given mode.
   @Override public void  setReadOnly(boolean readOnly)
   {
      if ( mode == Mode.SINGLE)
         searchGrid.asSingleSelect().setReadOnly(readOnly);
      else
         searchGrid.asMultiSelect().setReadOnly(readOnly);
   }//setReadOnly

   //Sets the required indicator visible or not.
   @Override public void  setRequiredIndicatorVisible(boolean requiredIndicatorVisible) { }

   //Sets the value of this object.
   @Override public void  setValue(T value)
   {
      switch (mode)
      {
      case MULTI:    
      {
         result.add(value);
         break;
      }
      case SINGLE:
      {
         result.clear();
         result.add(value);
         break;
      }
      default:
         result.clear();

      }

   }//setValue

}//TreeGridSelector
