package com.f.thoth.ui.views.admin.selector;

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
import com.vaadin.flow.shared.Registration;

public class TreeGridSelector<E extends HierarchicalEntity> extends  VerticalLayout 
                                implements HasValue
{
   private HierarchicalDataProvider<E, Void> dataProvider;
   private HierarchicalService<E> service;
   private Collection<E>          result;
   private TreeGrid<E>            lazyTree;
   private Grid<E>                searchGrid;

   public TreeGridSelector ( Tenant tenant, HierarchicalService<E> service)
   {
      this.service = service;
      dataProvider = getDataProvider();

      lazyTree = new TreeGrid<>();
      lazyTree.addHierarchyColumn(E::getName).setFlexGrow(70).setHeader("Nombre");
      lazyTree.addColumn(E::getCode).setFlexGrow(30).setHeader("ID");
      lazyTree.setDataProvider(dataProvider);

      searchGrid = new Grid<>();
      searchGrid.addColumn(E::getCode).setHeader("ID").setFlexGrow(30);
      searchGrid.addColumn(E::getName).setHeader("Nombre").setFlexGrow(70);

      // Display search results independent of their hierarchy-level

      searchGrid.setItems(service.findByNameLikeIgnoreCase( tenant, "a"));  //TODO:  La búsqueda debe ser abierta
      searchGrid.asSingleSelect().addValueChangeListener(e ->
                 {
                    lazyTree.deselectAll();
                    lazyTree.select(e.getValue());
                 });

      add(lazyTree, searchGrid);
      searchGrid.asSingleSelect().addValueChangeListener(e ->
                 {
                   lazyTree.deselectAll();
                   lazyTree.select(e.getValue());
                    backtrackParents(lazyTree::expand, e.getValue());
                 } );

   }//TreeGridSelector constructor
   
   public void refresh( )
   {
	   dataProvider = getDataProvider();
	   lazyTree.setDataProvider(dataProvider);
   }


   private void backtrackParents(Consumer<Collection<E>> fn, final E value)
   {
      final List<E> path = new ArrayList<>();
      E currentItem = value;
      while (currentItem != null && currentItem.getParent() != null)
      {
       Optional<E> item = service.findById(currentItem.getParent());
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


   private  HierarchicalDataProvider<E, Void>  getDataProvider()
   {
      final HierarchicalDataProvider<E, Void> dataProvider =
         new AbstractBackEndHierarchicalDataProvider<E, Void>()
      {
         @Override
         public int getChildCount(final HierarchicalQuery<E, Void> hierarchicalQuery)
         {
            final Long parentId = hierarchicalQuery.getParentOptional()
                                  .map(E::getId)
                                  .orElse(null);

            return service.countByParent(parentId);

         }//getChildCount


         @Override
         public boolean hasChildren(final E item)
         {
            final Long parentId = Optional.ofNullable(item)
                                  .map(E::getId)
                                  .orElse(null);

            return service.existsByParent(parentId);

         }//hasChildren


         @Override
         protected Stream<E> fetchChildrenFromBackEnd(final HierarchicalQuery<E, Void> hierarchicalQuery)
         {
            final Long parentId = hierarchicalQuery.getParentOptional()
                                    .map(E::getId)
                                    .orElse(null);

            return service.findByParent(parentId).stream();

         }//fetchChildrenFromBackEnd


      }; //new AbstractBackEndHierarchicalDataProvider<>()

      return dataProvider;

   }//getDataProvider

   //Adds a value change listener.
   public Registration   addValueChangeListener( HasValue.ValueChangeListener listener) {return null; }

   //Resets the value to the empty one.
   //default void clear()

   //Returns the value that represents an empty value.
   //default V getEmptyValue()

   //Returns the current value of this object, wrapped in an Optional.
   //default Optional<V>   getOptionalValue()

   //Returns the current value of this object.
   @Override public E  getValue() { return null;}

   //Returns whether this HasValue is considered to be empty.
   //default boolean isEmpty()

   //Returns whether this HasValue is in read-only mode or not.
   @Override public boolean  isReadOnly() { return true;}

   //Checks whether the required indicator is visible.
   @Override public boolean  isRequiredIndicatorVisible() { return false;}

   //Sets the read-only mode of this HasValue to given mode.
   @Override public void  setReadOnly(boolean readOnly) { }

   //Sets the required indicator visible or not.
   @Override public void  setRequiredIndicatorVisible(boolean requiredIndicatorVisible) { }

   //Sets the value of this object.
   void  setValue(E value) { result.clear(); result.add(value);}
   @Override  public void setValue(Object arg0) { }

}//TreeGridSelector
