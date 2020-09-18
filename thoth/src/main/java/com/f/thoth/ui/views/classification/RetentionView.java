package com.f.thoth.ui.views.classification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import com.f.thoth.app.security.CurrentUser;
import com.f.thoth.backend.data.gdoc.classification.Disposicion;
import com.f.thoth.backend.data.gdoc.classification.Retention;
import com.f.thoth.backend.data.gdoc.classification.TradicionDocumental;
import com.f.thoth.backend.service.RetentionService;
import com.f.thoth.ui.MainView;
import com.f.thoth.ui.crud.AbstractEvidentiaCrudView;
import com.f.thoth.ui.utils.Constant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;


/**
 * View para mantenimiento del calendario de retención
 */
@Route(value = Constant.PAGE_RETENCION, layout = MainView.class)
@PageTitle(Constant.TITLE_RETENCION)
@Secured(com.f.thoth.backend.data.Role.ADMIN)
public class RetentionView extends AbstractEvidentiaCrudView<Retention>
{
   private static final Converter<String, Integer>    YEAR_PERIOD_CONVERTER =
                  new StringToIntegerConverter( 0, "Período de retención inválido");

   @Autowired
   public RetentionView(RetentionService service, CurrentUser currentUser) 
   {
      super(Retention.class, service, new Grid<>(), createForm(), currentUser);    
      
   }//RetentionView

   @Override
   protected void setupGrid(Grid<Retention> grid)
   {
      grid.addColumn(ret -> ret.getName().toLowerCase()).setHeader("Nombre").setFlexGrow(40);
      grid.addColumn(ret -> ret.getGestion())   .setHeader("Dura. Gestión").setFlexGrow(20);
      grid.addColumn(ret -> ret.getCentral())   .setHeader("Dura. Central").setFlexGrow(20);
      grid.addColumn(ret -> ret.getIntermedio()).setHeader("Dura. Intermedio").setFlexGrow(20);
      grid.addColumn(ret -> ret.getDisposicion().name().toLowerCase()).setHeader("Disposición").setFlexGrow(40);
      grid.addColumn(ret -> ret.getTradicion()  .name().toLowerCase()).setHeader("Tradición").setFlexGrow(40);

   }//setupGrid

   @Override
   protected String getBasePage() { return Constant.PAGE_RETENCION; }

   private static BinderCrudEditor<Retention> createForm() 
   {
      TextField name = new TextField("Nombre del calendario");
      name.getElement().setAttribute("colspan", "4");
      name.setRequired(true);
      name.setRequiredIndicatorVisible(true);
      
      TextField gestion = new TextField("Duración Gestión");
      gestion.getElement().setAttribute("colspan", "1");
      gestion.setRequired(true);
      gestion.setRequiredIndicatorVisible(true);
      
      TextField central = new TextField("Duración Central");
      central.getElement().setAttribute("colspan", "1");
      central.setRequired(true);
      central.setRequiredIndicatorVisible(true);
      
      TextField intermedio = new TextField("Duración Intermedio");
      intermedio.getElement().setAttribute("colspan", "1");
      intermedio.setRequired(true);
      intermedio.setRequiredIndicatorVisible(true);

      ComboBox<Disposicion> disposicion = new ComboBox<>("Disposición");
      disposicion.setItems(Disposicion.values());
      disposicion.setItemLabelGenerator( disp -> disp.getDisplayName());
      disposicion.setRequired(true);
      disposicion.setRequiredIndicatorVisible(true);
      disposicion.getElement().setAttribute("colspan", "2");

      ComboBox<TradicionDocumental> tradicion = new ComboBox<>("Tradición Documental");
      tradicion.setItems(TradicionDocumental.values());
      tradicion.setItemLabelGenerator( trad -> trad.getDisplayName());
      tradicion.setRequired(true);
      tradicion.setRequiredIndicatorVisible(true);
      tradicion.getElement().setAttribute("colspan", "2");

      FormLayout form = new FormLayout(name, gestion, central, intermedio, disposicion, tradicion);
      form.setResponsiveSteps(
            new ResponsiveStep("30em", 1),
            new ResponsiveStep("30em", 2),
            new ResponsiveStep("30em", 3),
            new ResponsiveStep("30em", 4)
            );

      BeanValidationBinder<Retention> binder = new BeanValidationBinder<>(Retention.class);

      binder.bind(name, "name");
      binder.forField(gestion)
            .withValidator(text -> text.length() > 0, "Duración es un número positivo") //Validación del texto
            .withConverter(YEAR_PERIOD_CONVERTER)
            .withValidator(g -> g > 0, "Duración es un número positivo")             // Validación del número
            .bind("gestion");
      
      binder.forField(central)
            .withValidator(text -> text.length() > 0, "Duración es un número positivo") //Validación del texto
            .withConverter(YEAR_PERIOD_CONVERTER)
            .withValidator(c -> c > 0, "Duración es un número positivo")             // Validación del número
            .bind("central");
      
      binder.forField(intermedio)
            .withValidator(text -> text.length() > 0, "Duración es un número positivo") //Validación del texto
            .withConverter(YEAR_PERIOD_CONVERTER)
            .withValidator(i -> i > 0, "Duración es un número positivo")             // Validación del número
            .bind("intermedio");
      
      binder.bind(disposicion,"disposicion");
      binder.bind(tradicion,"tradicion");

      return new BinderCrudEditor<Retention>(binder, form);
   }//BinderCrudEditor

}//RetentionView
