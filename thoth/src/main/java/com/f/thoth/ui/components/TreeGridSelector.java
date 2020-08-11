package com.f.thoth.ui.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import com.f.thoth.backend.data.entity.HierarchicalEntity;
import com.f.thoth.backend.data.security.Tenant;
import com.f.thoth.backend.service.HierarchicalService;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.hierarchy.AbstractBackEndHierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.shared.Registration;

public class TreeGridSelector<T extends HierarchicalEntity<T>, E extends HasValue.ValueChangeEvent<T>> extends  VerticalLayout
implements HasValue<E, T>
{
   private HierarchicalDataProvider<T, Void> dataProvider;
   private HierarchicalService<T> service;
   private Collection<T>          result;
   private TreeGrid<T>            treeGrid;
   private Grid<T>                searchGrid;

   public TreeGridSelector ( Tenant tenant, HierarchicalService<T> service)
   {
      this.service = service;
      treeGrid     = buildTreeGrid(tenant, service);
      searchGrid   = buildSearchGrid(tenant, service, treeGrid);
      

      /* -----------------------
      treeGrid = new TreeGrid<>();
      treeGrid.addHierarchyColumn(T::getCode).setFlexGrow(70).setHeader("Nombre");
      treeGrid.addColumn(T::getName).setFlexGrow(30).setHeader("ID");
      treeGrid.setDataProvider(dataProvider);
      // --------------------------

      searchGrid = new Grid<>();
      searchGrid.addColumn(T::getCode).setHeader("ID").setFlexGrow(30);
      searchGrid.addColumn(T::getName).setHeader("Nombre").setFlexGrow(70);

      // Display search results independent of their hierarchy-level

      searchGrid.setItems(service.findByNameLikeIgnoreCase( tenant, "a"));  //TODO:  La bÃºsqueda debe ser abierta
      add(treeGrid, searchGrid);
      searchGrid.asSingleSelect().addValueChangeListener(e ->
      {
         treeGrid.deselectAll();
         treeGrid.select(e.getValue());
         backtrackParents(treeGrid::expand, e.getValue());
      } );
      ------------------------------*/

   }//TreeGridSelector constructor

   public void refresh( )
   {
      dataProvider = getDataProvider();
      treeGrid.setDataProvider(dataProvider);
   }


   private void backtrackParents(Consumer<Collection<T>> fn, final T value)
   {
      final List<T> path = new ArrayList<>();
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


   private TreeGrid<T> buildTreeGrid(Tenant tenant, HierarchicalService<T> service)
   {
      TreeGrid<T> tGrid = new TreeGrid<>();
      tGrid.addColumn(T::getName).setFlexGrow(30).setHeader("ID");
      tGrid.addHierarchyColumn(T::getCode).setFlexGrow(70).setHeader("Nombre");
      dataProvider = getDataProvider();
      tGrid.setDataProvider(dataProvider);
      List<T> all = service.findAll();
      all.forEach(p -> tGrid.getTreeData().addItem(p.getOwner(), p));
      return tGrid;
   }//buildGrid
   
   
   private Grid<T> buildSearchGrid(Tenant tenant, HierarchicalService<T> service, TreeGrid<T> tGrid)
   {
      Grid<T> sGrid = new Grid<>();
      sGrid.addColumn(T::getCode).setHeader("ID").setFlexGrow(30);
      sGrid.addColumn(T::getName).setHeader("Nombre").setFlexGrow(70);
      sGrid.setItems(service.findByNameLikeIgnoreCase( tenant, "a"));  //TODO:  La búsqueda debe ser abierta
      sGrid.asSingleSelect().addValueChangeListener(e ->
      {
         tGrid.deselectAll();
         tGrid.select(e.getValue());
         backtrackParents(tGrid::expand, e.getValue());
      } );

      return sGrid;
      
   }//buildSearchGrid



   private  HierarchicalDataProvider<T, Void>  getDataProvider()
   {
      final HierarchicalDataProvider<T, Void> dataProvider = new AbstractBackEndHierarchicalDataProvider<T, Void>()
      {
         @Override
         public int getChildCount(final HierarchicalQuery<T, Void> hierarchicalQuery)
         {
            final Long parentId = hierarchicalQuery.getParentOptional()
                  .map(T::getId)
                  .orElse(null);

            return service.countByParent(parentId);

         }//getChildCount


         @Override
         public boolean hasChildren(final T item)
         {
            final Long parentId = Optional.ofNullable(item)
                  .map(T::getId)
                  .orElse(null);

            return service.existsByParent(parentId);

         }//hasChildren


         @Override
         protected Stream<T> fetchChildrenFromBackEnd(final HierarchicalQuery<T, Void> hierarchicalQuery)
         {
            final Long parentId = hierarchicalQuery.getParentOptional()
                  .map(T::getId)
                  .orElse(null);

            return service.findByParent(parentId).stream();

         }//fetchChildrenFromBackEnd


      }; //new AbstractBackEndHierarchicalDataProvider<>()

      return dataProvider;

   }//getDataProvider

   // ---------- implements

   //Resets the value to the empty one.
   //default void clear()

   //Returns the value that represents an empty value.
   //default V getEmptyValue()

   //Returns the current value of this object, wrapped in an Optional.
   //default Optional<V>   getOptionalValue()

   //Returns whether this HasValue is considered to be empty.
   //default boolean isEmpty()

   //Adds a value change listener.
   @Override public Registration addValueChangeListener( HasValue.ValueChangeListener <? super E> listener) {return null;}

   //Returns the current value of this object.
   @Override public T  getValue() { return null;}

   //Returns whether this HasValue is in read-only mode or not.
   @Override public boolean  isReadOnly() { return true;}

   //Checks whether the required indicator is visible.
   @Override public boolean  isRequiredIndicatorVisible() { return false;}

   //Sets the read-only mode of this HasValue to given mode.
   @Override public void  setReadOnly(boolean readOnly) {}

   //Sets the required indicator visible or not.
   @Override public void  setRequiredIndicatorVisible(boolean requiredIndicatorVisible) {}

   //Sets the value of this object.
   @Override public void  setValue(T value) { result.clear(); result.add(value);}

   /*
    TreeGrid<Account> grid = new TreeGrid<>();
grid.addHierarchyColumn(Account::toString).setHeader("Account Title");
grid.addColumn(Account::getCode).setHeader("Code");

HierarchicalDataProvider dataProvider =
        new AbstractBackEndHierarchicalDataProvider<Account, Void>() {

    @Override
    public int getChildCount(HierarchicalQuery<Account, Void> query) {
        return (int) accountService.getChildCount(query.getParent());
    }

    @Override
    public boolean hasChildren(Account item) {
        return accountService.hasChildren(item);
    }

    @Override
    protected Stream<Account> fetchChildrenFromBackEnd(
            HierarchicalQuery<Account, Void> query) {
        return accountService.fetchChildren(query.getParent()).stream();
    }
};

grid.setDataProvider(dataProvider);
add(grid);
    */

}//TreeGridSelector
