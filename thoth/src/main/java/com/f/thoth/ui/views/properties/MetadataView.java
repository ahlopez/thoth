package com.f.thoth.ui.views.properties;

import static com.f.thoth.ui.utils.Constant.PAGE_METADATA;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import com.f.thoth.app.security.CurrentUser;
import com.f.thoth.backend.data.gdoc.metadata.Metadata;
import com.f.thoth.backend.service.MetadataService;
import com.f.thoth.ui.MainView;
import com.f.thoth.ui.crud.AbstractEvidentiaCrudView;
import com.f.thoth.ui.utils.Constant;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = PAGE_METADATA, layout = MainView.class)
@PageTitle(Constant.TITLE_METADATA)
@Secured(com.f.thoth.backend.data.Role.ADMIN)
public class MetadataView extends AbstractEvidentiaCrudView<Metadata>
{
   @Autowired
   public MetadataView(MetadataService service, CurrentUser currentUser) 
   {
      super(Metadata.class, service, new Grid<>(), createForm(), currentUser);
   }

   @Override
   protected void setupGrid(Grid<Metadata> grid)
   {
      grid.addColumn(Metadata::getName).setHeader("Identificador").setFlexGrow(20);
   }//setupGrid

   @Override
   protected String getBasePage() {
      return PAGE_METADATA;
   }

   private static BinderCrudEditor<Metadata> createForm() 
   {
      TextField name = new TextField("Nombre del campo");
      name.getElement().setAttribute("colspan", "3");

      FormLayout form = new FormLayout(name);

      BeanValidationBinder<Metadata> binder = new BeanValidationBinder<>(Metadata.class);

      binder.bind(name, "name");


      return new BinderCrudEditor<Metadata>(binder, form);
   }//BinderCrudEditor

}//MetadataView

