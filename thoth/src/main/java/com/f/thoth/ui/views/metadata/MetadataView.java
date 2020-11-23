package com.f.thoth.ui.views.metadata;

import static com.f.thoth.ui.dataproviders.DataProviderUtil.createItemLabelGenerator;
import static com.f.thoth.ui.utils.Constant.PAGE_METADATA;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import com.f.thoth.app.security.CurrentUser;
import com.f.thoth.backend.data.gdoc.metadata.Metadata;
import com.f.thoth.backend.data.gdoc.metadata.Type;
import com.f.thoth.backend.service.MetadataService;
import com.f.thoth.ui.MainView;
import com.f.thoth.ui.crud.AbstractEvidentiaCrudView;
import com.f.thoth.ui.utils.Constant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.provider.DataProvider;
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
      grid.addColumn(Metadata::getName) .setHeader("Nombre").setFlexGrow(20);
      grid.addColumn(Metadata::getType) .setHeader("Tipo")  .setFlexGrow(10);
      grid.addColumn(Metadata::getRange).setHeader("Rango") .setFlexGrow(30);
   }//setupGrid

   @Override
   protected String getBasePage() { return PAGE_METADATA; }

   private static BinderCrudEditor<Metadata> createForm() 
   {
      TextField name  = new TextField("Nombre del campo");
      name.getElement().setAttribute("colspan", "2");
      name.setRequired(true);
      name.setRequiredIndicatorVisible(true);
      
      ComboBox<Type>  type  = new ComboBox<>("Tipo");
      type.getElement().setAttribute("colspan", "1");
      type.setItemLabelGenerator(createItemLabelGenerator(Type::getDisplayName));
      type.setDataProvider(DataProvider.ofItems(Type.values()));
      type.setRequired(true);
      type.setRequiredIndicatorVisible(true);

       
      TextField range = new TextField("Rango");
      range.getElement().setAttribute("colspan", "3");
      range.setRequired(false);
      range.setRequiredIndicatorVisible(false);

      FormLayout form = new FormLayout(name, type, range);

      BeanValidationBinder<Metadata> binder = new BeanValidationBinder<>(Metadata.class);

      binder.bind(name,  "name");
      binder.bind(type,  "type");
      binder.bind(range, "range");

      return new BinderCrudEditor<Metadata>(binder, form);
   }//BinderCrudEditor

}//MetadataView

