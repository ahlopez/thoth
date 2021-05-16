package com.f.thoth.ui.views.expediente;

import java.time.LocalDateTime;
import java.util.List;

import com.f.thoth.backend.data.gdoc.expediente.BranchExpediente;
import com.f.thoth.backend.data.gdoc.metadata.Schema;
import com.f.thoth.backend.service.SchemaService;
import com.f.thoth.ui.utils.converters.LocalDateTimeToLocalDateTime;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.shared.Registration;

public class BranchExpedienteForm extends FormLayout
{
   private static final Converter<LocalDateTime, LocalDateTime> DATE_CONVERTER   = new LocalDateTimeToLocalDateTime();
   

   private Button           save  = new Button("Guardar Grupo");
   private Button          close  = new Button("Cancelar");
   
   private ComboBox<String>   open;
   private DateTimePicker     dateOpened;
   private DateTimePicker     dateClosed;
   private ComboBox<Schema>   schema;
   
   SchemaService  schemaService;
   
   BranchExpediente  selectedBranch = null;

   Binder<BranchExpediente> binder       = new BeanValidationBinder<>(BranchExpediente.class);

   BranchExpedienteValuesForm branchExpedienteValuesForm = new BranchExpedienteValuesForm();

   public BranchExpedienteForm(SchemaService schemaService)
   {
	  this.schemaService = schemaService;
      setWidthFull();
      setResponsiveSteps(
            new ResponsiveStep("30em", 1),
            new ResponsiveStep("30em", 2),
            new ResponsiveStep("30em", 3),
            new ResponsiveStep("30em", 4));

      H3  title = new H3("Grupo de expedientes a actualizar");
      title.getElement().setAttribute("colspan", "2");

     /*
           Campos que falta considerar, si es que se necesitan
      private Type                type;                        // Expediente tipo GRUPO/ HOJA/ EXPEDIENTE/ VOLUME
      protected SchemaValues      metadata;                    // Metadata values of the associated expediente
      protected String            ownerPath;                   // Branch Expediente to which this Branch/Leaf/Volume belongs
      protected String            location;                    // Signatura topogr�fica
    */
      /*
      TextField  expedienteCode    = new TextField("Código");
      expedienteCode.setRequired(true);
      expedienteCode.setRequiredIndicatorVisible(true);
      expedienteCode.getElement().setAttribute("colspan", "1");
      expedienteCode.setEnabled(false);
      */
      TextField  name    = new TextField("Asunto");
      name.setRequired(true);
      name.setRequiredIndicatorVisible(true);
      name.getElement().setAttribute("colspan", "4");

      TextField  classCode    = new TextField("Clase");
      classCode.setRequired(true);
      classCode.setRequiredIndicatorVisible(true);
      classCode.getElement().setAttribute("colspan", "1");
      classCode.setEnabled(false);

      open = new ComboBox<>();
      open.setItems(new String[] {"ABIERTO", "CERRADO"});
      open.setWidth("20%");
      open.setRequired(true);
      open.getElement().setAttribute("colspan", "1");
      
      schema = new ComboBox<>("Metadatos");
      List<Schema> allSchemas = schemaService.findAll();
      schema.setItems(allSchemas);
      schema.addValueChangeListener(e -> 
      { 
    	selectedBranch.setMetadataSchema(e.getValue()); 
        branchExpedienteValuesForm.setBranchExpediente(selectedBranch);
      });
      schema.setItemLabelGenerator(e-> e.getName());
      schema.setWidth("30%");
      schema.setRequired(true);
      schema.setRequiredIndicatorVisible(true);
      schema.getElement().setAttribute("colspan", "1");

      TextField  createdBy    = new TextField("Creado Por");
      createdBy.setRequired(true);
      createdBy.setRequiredIndicatorVisible(true);
      createdBy.getElement().setAttribute("colspan", "1");
      createdBy.setEnabled(false);

      dateOpened = new DateTimePicker("Creado");
      dateOpened.setRequiredIndicatorVisible(true);
      dateOpened.setWidth("40%");
      dateOpened.getElement().setAttribute("colspan", "2");
      dateOpened.setEnabled(false);

      dateClosed   = new DateTimePicker("Cerrado");
      dateClosed.setRequiredIndicatorVisible(true);
      dateClosed.setWidth("40%");
      dateClosed.getElement().setAttribute("colspan", "2");
      dateClosed.setEnabled(false);

      TextField  keywords    = new TextField("Palabras clave");
      keywords.setRequired(false);
      keywords.setRequiredIndicatorVisible(true);
      keywords.getElement().setAttribute("colspan", "4");

      add(
         //   expedienteCode       ,
            name                 ,
            classCode            ,
            schema               ,
            open                 ,
            createdBy            ,
            dateOpened           ,
            dateClosed           ,
            keywords             ,
            createButtonsLayout(),
            branchExpedienteValuesForm
         );

    //  binder.forField(expedienteCode).bind("expedienteCode");

      binder.forField(name).bind("name");

      binder.forField(classCode).bind("classificationClass.classCode");


      binder.forField(dateOpened)
            .asRequired()
            .withConverter(DATE_CONVERTER)
            .withValidator( dateFrom ->
                 {
                    LocalDateTime dateTo = dateClosed.getValue();
                    return dateTo == null || (dateFrom != null && dateFrom.equals(dateTo) || dateFrom.isBefore(dateTo));
                 },         "Fecha de cierre debe posterior a la de apertura")
            .bind("dateOpened");


      binder.forField(dateClosed)
            .asRequired()
            .withConverter(DATE_CONVERTER)
            .withValidator( dateTo ->
                            {
                               LocalDateTime dateFrom = dateOpened.getValue();
                               boolean ok =  dateFrom != null && dateTo != null && ( dateTo.equals(dateFrom) || dateTo.isAfter(dateFrom));                               
                               return ok;
                            },
                            "Fecha de cierre debe posterior a la de apertura")
            .bind("dateClosed");

      binder.forField(keywords).bind("keywords");

      binder.forField(createdBy).bind("createdBy.email");

      branchExpedienteValuesForm.addListener(BranchExpedienteValuesForm.SaveEvent.class, e->validateAndSave(e.getBranchExpediente()));
      branchExpedienteValuesForm.getElement().setAttribute("colspan", "4");

   }//BranchExpedienteForm


   public void setExpediente(BranchExpediente expediente)
   {
      binder.setBean(expediente);
      this.selectedBranch = expediente;
      setStatus( expediente);
      branchExpedienteValuesForm.setVisible(true);
      branchExpedienteValuesForm.setBranchExpediente(selectedBranch);
   }//setExpediente
   
   
   private void setStatus (BranchExpediente expediente)
   {
	   LocalDateTime now       = LocalDateTime.now();
	   LocalDateTime endOfTimes= now.plusYears(200L);
	   boolean isNew  = expediente == null || expediente.getId() == null;
	   boolean isOpen = isNew || expediente.getOpen();
	   open.setValue( isOpen? "ABIERTO" : "CERRADO");
	   open.setEnabled(isOpen);
	   schema.setEnabled(isNew);
	   if (isNew)
	   {
		  dateOpened.setValue(now);
		  dateClosed.setValue(endOfTimes);
	   }
		   
   }//setStatus

   private Component createButtonsLayout()
   {
      save.addThemeVariants  (ButtonVariant.LUMO_PRIMARY);
      close.addThemeVariants (ButtonVariant.LUMO_TERTIARY);

      save.addClickShortcut (Key.ENTER);
      close.addClickShortcut(Key.ESCAPE);

      close. setWidth("20%");
      save.  setWidth("20%");
      // TODO: Botones para   + Grupo/ + Expediente/ + Volumen

      save.addClickListener  (click -> validateAndSave(binder.getBean()));
      close.addClickListener (click -> { close(); fireEvent(new CloseEvent(this));});
      save.getElement().getStyle().set("margin-left", "auto");

      binder.addStatusChangeListener(evt -> save.setEnabled(binder.isValid()));
      HorizontalLayout buttons = new HorizontalLayout(close, save);
      buttons.getElement().setAttribute("colspan", "4");
      buttons.setWidthFull();

      return buttons;
   }//createButtonsLayout

   private void validateAndSave(BranchExpediente expediente)
   {
      if (binder.isValid())
      {
    	 verifyClosing(expediente);
         close();
         fireEvent(new SaveEvent(this, expediente));
      }
   }//validateAndSave
   
   private void verifyClosing( BranchExpediente expediente)
   {
  	 boolean wasOpen = expediente.getOpen();
  	 boolean isClosed= "CERRADO".equals(open.getValue());
  	 if( wasOpen && isClosed)
  	 {
  	     expediente.setOpen( false);
  	     LocalDateTime now = LocalDateTime.now();
  	     dateClosed.setValue(now);
  	     expediente.setDateClosed(now);
  	 }
   }//whenExpedienteCloses

   private void close() {  branchExpedienteValuesForm.setVisible(false); }

   // --------------------- Events -----------------------
   public static abstract class BranchExpedienteFormEvent extends ComponentEvent<BranchExpedienteForm>
   {
      private BranchExpediente branch;

      protected BranchExpedienteFormEvent(BranchExpedienteForm source, BranchExpediente branch)
      {
         super(source, false);
         this.branch = branch;
      }//BranchExpedienteFormEvent

      public BranchExpediente getBranchExpediente()
      {
         return branch;
      }
   }//BranchExpedienteFormEvent

   public static class SaveEvent extends BranchExpedienteFormEvent
   {
      SaveEvent(BranchExpedienteForm source, BranchExpediente branch)
      {
         super(source, branch);
      }
   }//SaveEvent

   public static class DeleteEvent extends BranchExpedienteFormEvent
   {
      DeleteEvent(BranchExpedienteForm source, BranchExpediente branch)
      {
         super(source, branch);
      }
   }//DeleteEvent

   public static class CloseEvent extends BranchExpedienteFormEvent
   {
      CloseEvent(BranchExpedienteForm source)
      {
         super(source, null);
      }
   }//CloseEvent

   public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener)
   {
      return getEventBus().addListener(eventType, listener);
   }//addListener

}//BranchExpedienteForm
