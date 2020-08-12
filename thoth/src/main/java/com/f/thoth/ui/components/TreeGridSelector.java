package com.f.thoth.ui.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.stream.Stream;

import com.f.thoth.backend.data.entity.HierarchicalEntity;
import com.f.thoth.backend.data.security.Tenant;
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

public class TreeGridSelector<T extends HierarchicalEntity<T>, E extends HasValue.ValueChangeEvent<T>> extends  VerticalLayout
implements HasValue<E, T>
{
   private HierarchicalDataProvider<T, Void> dataProvider;
   private HierarchicalService<T> service;
   private ArrayList<T>           result;
   private TreeGrid<T>            treeGrid;
   private Grid<T>                searchGrid;
   private Grid.SelectionMode     selectionMode ;

   public TreeGridSelector ( Tenant tenant, HierarchicalService<T> service, Grid.SelectionMode selectionMode, String name)
   {
      this.result         = new ArrayList<>();
      this.selectionMode  = selectionMode;
      this.service        = service;
      getElement().setAttribute("colspan", "4");
      add( new Label(name));
      this.treeGrid       = buildTreeGrid  (tenant, service);

      if ( selectionMode != Grid.SelectionMode.NONE)
      {
         this.searchGrid     = buildSearchGrid(tenant, service, treeGrid);
         SearchBar searchBar = buildSearchBar(tenant, searchGrid);
         add(searchBar);
      }
      
      HorizontalLayout  layout = new HorizontalLayout();
      layout.getElement().setAttribute("colspan",  "4");
      layout.add(treeGrid, searchGrid);
      add( layout);

   }//TreeGridSelector constructor
   

   private TreeGrid<T> buildTreeGrid(Tenant tenant, HierarchicalService<T> service)
   {     
      dataProvider = getDataProvider();
      TreeGrid<T> tGrid = new TreeGrid<>();
      tGrid.setVisible(true);
      tGrid.getElement().setAttribute("colspan", "1");
      tGrid.setWidth("50");
      tGrid.addHierarchyColumn(T::getCode).setFlexGrow(70).setHeader("Nombre");      
      tGrid.addColumn(T::getName).setFlexGrow(30).setHeader("ID");
      tGrid.setDataProvider(dataProvider);
      
      return tGrid;
      
   }//buildGrid


   private Grid<T> buildSearchGrid(Tenant tenant, HierarchicalService<T> service, TreeGrid<T> tGrid)
   {
      Grid<T> sGrid = new Grid<>();
      sGrid.getElement().setAttribute("colspan", "1");
      sGrid.setVisible(selectionMode != Grid.SelectionMode.NONE);
      sGrid.setWidth("50");
      sGrid.addColumn(T::getCode).setHeader("ID").setFlexGrow(30);
      sGrid.addColumn(T::getName).setHeader("Nombre").setFlexGrow(70);
      sGrid.setSelectionMode(selectionMode);
      addValueChangeListener(sGrid, tGrid);

      return sGrid;

   }//buildSearchGrid
   
   
   private SearchBar buildSearchBar(Tenant tenant, Grid<T> searchGrid)
   {
      SearchBar searchBar = new SearchBar();
      searchBar.setActionText("Buscar ");
      searchBar.getActionButton().getElement().setAttribute("new-button", false);
      searchBar.addFilterChangeListener(e -> searchGrid.setItems(service.findByNameLikeIgnoreCase( tenant, searchBar.getFilter())));
      
      return searchBar;  
      
   }//buildSearchBar


   private Registration addValueChangeListener( Grid<T> sGrid, TreeGrid<T> tGrid)
   {
      Registration registration = null;
      if ( selectionMode == Grid.SelectionMode.SINGLE)
      {
         registration = sGrid.asSingleSelect().addValueChangeListener(e ->
         {
            tGrid.deselectAll();
            tGrid.select(e.getValue());
            backtrackParents(tGrid::expand, e.getValue());
         }); 
      }else if (selectionMode == Grid.SelectionMode.MULTI)
      { 

         registration = sGrid.asMultiSelect().addValueChangeListener(e ->
         {
            for (T g: e.getValue())
            {
               tGrid.select(g);
               backtrackParents(tGrid::expand, g);
            }
         });
      }else 
      {
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

   private  HierarchicalDataProvider<T, Void>  getDataProvider()
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
      dataProvider.refreshAll();
   }


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
   @Override public T  getValue() { return result.isEmpty()? null: result.get(0) ; }

   public Collection<T> getValues(){ return result;}

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
   @Override public void  setRequiredIndicatorVisible(boolean requiredIndicatorVisible) { }

   //Sets the value of this object.
   @Override public void  setValue(T value) 
   { 
      if ( selectionMode == Grid.SelectionMode.MULTI)
      {
         result.add(value); 
      }else if ( selectionMode == Grid.SelectionMode.SINGLE)
      {
         result.clear();
         result.add(value);
      }else 
      {
         result.clear();
      }

   }//setValue

}//TreeGridSelector
