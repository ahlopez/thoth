package com.f.thoth.ui.views.expediente;

import java.time.LocalDateTime;

import com.f.thoth.backend.data.gdoc.expediente.BranchExpediente;
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

   private Button save   = new Button("Guardar Grupo");
   private Button close  = new Button("Cancelar");

   BranchExpediente  selectedBranch = null;

   Binder<BranchExpediente> binder       = new BeanValidationBinder<>(BranchExpediente.class);

   BranchExpedienteValuesForm BranchExpedienteValuesForm = new BranchExpedienteValuesForm();

   public BranchExpedienteForm(BranchExpediente selectedBranch)
   {
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

      TextField  expedienteCode    = new TextField("Código");
      expedienteCode.setRequired(true);
      expedienteCode.setRequiredIndicatorVisible(true);
      expedienteCode.getElement().setAttribute("colspan", "1");
      expedienteCode.setEnabled(false);

      TextField  name    = new TextField("Asunto");
      name.setRequired(true);
      name.setRequiredIndicatorVisible(true);
      name.getElement().setAttribute("colspan", "3");

      TextField  classCode    = new TextField("Clase");
      classCode.setRequired(true);
      classCode.setRequiredIndicatorVisible(true);
      classCode.getElement().setAttribute("colspan", "1");
      classCode.setEnabled(false);

      LocalDateTime now       = LocalDateTime.now();
      LocalDateTime endOfTimes= now.plusYears(200L);

      ComboBox<String> open = new ComboBox<>("Abierto");
      open.setItems(new String[] {"SI", "NO"});
      open.setWidth("20%");
      open.setRequired(true);
      open.getElement().setAttribute("colspan", "2");

      DateTimePicker dateOpened = new DateTimePicker("Creado");
      dateOpened.setValue(now);
      dateOpened.setRequiredIndicatorVisible(true);
      dateOpened.setWidth("40%");
      dateOpened.getElement().setAttribute("colspan", "1");

      DateTimePicker dateClosed   = new DateTimePicker("Cerrado");
      dateClosed.setValue(endOfTimes);
      dateClosed.setRequiredIndicatorVisible(true);
      dateClosed.setWidth("40%");
      dateClosed.getElement().setAttribute("colspan", "1");

      TextField  keywords    = new TextField("Palabras clave");
      keywords.setRequired(false);
      keywords.setRequiredIndicatorVisible(true);
      keywords.getElement().setAttribute("colspan", "3");

      TextField  createdBy    = new TextField("Creado Por");
      createdBy.setRequired(true);
      createdBy.setRequiredIndicatorVisible(true);
      createdBy.getElement().setAttribute("colspan", "2");
      createdBy.setEnabled(false);


      add(
            expedienteCode       ,
            name                 ,
            classCode            ,
            dateOpened           ,
            dateClosed           ,
            keywords             ,
            createdBy            ,
            createButtonsLayout(),
            BranchExpedienteValuesForm
         );

      binder.forField(expedienteCode).bind("expedienteCode");

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

      BranchExpedienteValuesForm.addListener(BranchExpedienteValuesForm.SaveEvent.class, e->validateAndSave(e.getBranchExpediente()));
      BranchExpedienteValuesForm.getElement().setAttribute("colspan", "4");

   }//BranchExpedienteForm


   public void setExpediente(BranchExpediente expediente)
   {
      binder.setBean(expediente);
      this.selectedBranch = expediente;
      BranchExpedienteValuesForm.setVisible(true);
      BranchExpedienteValuesForm.setBranchExpediente(selectedBranch);
   }//setExpediente

   private Component createButtonsLayout()
   {
      save.addThemeVariants  (ButtonVariant.LUMO_PRIMARY);
      close.addThemeVariants (ButtonVariant.LUMO_TERTIARY);

      save.addClickShortcut (Key.ENTER);
      close.addClickShortcut(Key.ESCAPE);

      save.  setWidth("20%");
      save.getElement().getStyle().set("margin-left", "auto");
      close. setWidth("20%");
      // TODO: Botones para   + Grupo/ + Expediente/ + Volumen

      save.addClickListener  (click -> validateAndSave(binder.getBean()));
      close.addClickListener (click -> { close(); fireEvent(new CloseEvent(this));});

      binder.addStatusChangeListener(evt -> save.setEnabled(binder.isValid()));
      HorizontalLayout buttons = new HorizontalLayout(close, save);
      buttons.getElement().setAttribute("colspan", "3");

      return buttons;
   }//createButtonsLayout

   private void validateAndSave(BranchExpediente expediente)
   {
      if (binder.isValid())
      {
         close();
         fireEvent(new SaveEvent(this, expediente));
      }
   }//validateAndSave

   private void close() {  BranchExpedienteValuesForm.setVisible(false); }

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
