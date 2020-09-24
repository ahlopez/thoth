package com.f.thoth.ui.views.classification;

import java.time.LocalDate;
import java.util.List;

import com.f.thoth.backend.data.gdoc.classification.Classification;
import com.f.thoth.backend.data.gdoc.classification.Retention;
import com.f.thoth.ui.utils.converters.LocalDateToLocalDate;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.shared.Registration;

public class ClassificationForm extends FormLayout
{
   private static final Converter<LocalDate, LocalDate> DATE_CONVERTER   = new LocalDateToLocalDate();

   private Button save   = new Button("Guardar Clase");
   private Button close  = new Button("Cancelar");

   Binder<Classification> binder       = new BeanValidationBinder<>(Classification.class);
   
   ClassificationValuesForm valuesForm = new ClassificationValuesForm();

   public ClassificationForm(List<Retention> availabeSchedules) 
   {  
      setWidthFull();
      setResponsiveSteps(
            new ResponsiveStep("30em", 1),
            new ResponsiveStep("30em", 2),
            new ResponsiveStep("30em", 3));

      H3  title = new H3("Clase a actualizar");
      title.getElement().setAttribute("colspan", "2");

      TextField  clase    = new TextField("Nombre");
      clase.setRequired(true);
      clase.setRequiredIndicatorVisible(true);
      clase.getElement().setAttribute("colspan", "2");

      LocalDate now = LocalDate.now();
      LocalDate yearStart =now.minusDays(now.getDayOfYear());
      DatePicker fromDate = new DatePicker("Válida Desde");
      fromDate.setRequired(true);
      fromDate.setValue(now);
      fromDate.setRequiredIndicatorVisible(true);
      fromDate.setWidth("40%");
      fromDate.getElement().setAttribute("colspan", "1");

      DatePicker toDate   = new DatePicker("Válida Hasta");
      toDate.setRequired(true);
      toDate.setValue(yearStart.plusYears(1));
      toDate.setRequiredIndicatorVisible(true);
      toDate.setWidth("40%");
      toDate.getElement().setAttribute("colspan", "1");
      
      ComboBox<Retention> schedule = new ComboBox<>("Programa de Retención");
      schedule.setItems(availabeSchedules);
      schedule.setItemLabelGenerator(Retention::getName);
      schedule.setRequired(true);
      schedule.getElement().setAttribute("colspan", "2");
   
      add(
            title,
            clase,
            new Label(" "),
            fromDate,
            toDate,
            schedule,
            createButtonsLayout(),
            valuesForm
            );

      binder.forField(clase).bind("name");

      
      binder.forField(fromDate)
            .asRequired()
            .withConverter(DATE_CONVERTER)
            .withValidator( dateFrom ->
                 {
                       LocalDate dateTo = toDate.getValue();
                       return dateTo != null && dateFrom.equals(dateTo) || dateFrom.isBefore(dateTo);
                 }, "Fecha de cierre debe posterior a la de apertura")
            .bind("dateOpened");

      binder.forField(toDate)
            .asRequired()
            .withConverter(DATE_CONVERTER)
            .withValidator( dateTo -> dateTo.equals(fromDate.getValue()) || dateTo.isAfter(fromDate.getValue()), 
                            "Fecha de cierre debe posterior a la de apertura")
            .bind("dateClosed");
      
      binder.forField(schedule).bind("retentionSchedule");
      
      valuesForm.addListener(ClassificationValuesForm.SaveEvent.class, e->validateAndSave(e.getClassification()));
      valuesForm.getElement().setAttribute("colspan", "3");

   }//ClassificationForm
   

   public void setClassification(Classification clase) 
   {
      binder.setBean(clase);
      valuesForm.setVisible(true);
      valuesForm.setClassification(clase);
   }//setClassification

   private Component createButtonsLayout() 
   {
      save.addThemeVariants  (ButtonVariant.LUMO_PRIMARY);
      close.addThemeVariants (ButtonVariant.LUMO_TERTIARY);

      save.addClickShortcut (Key.ENTER);
      close.addClickShortcut(Key.ESCAPE);

      save.  setWidth("20%");
      save.getElement().getStyle().set("margin-left", "auto");
      close. setWidth("20%");

      save.addClickListener  (click -> validateAndSave(binder.getBean()));
      close.addClickListener (click -> { close(); fireEvent(new CloseEvent(this));});

      binder.addStatusChangeListener(evt -> save.setEnabled(binder.isValid()));
      HorizontalLayout buttons = new HorizontalLayout(close, save);
      buttons.getElement().setAttribute("colspan", "3");

      return buttons; 
   }//createButtonsLayout

   private void validateAndSave(Classification clase) 
   {
      if (binder.isValid()) 
      {
         close();
         fireEvent(new SaveEvent(this, clase));
      }
   }//validateAndSave
   
   private void close() {  valuesForm.setVisible(false); }

   // --------------------- Events -----------------------
   public static abstract class ClassificationFormEvent extends ComponentEvent<ClassificationForm> 
   {
      private Classification clase;

      protected ClassificationFormEvent(ClassificationForm source, Classification clase) 
      {
         super(source, false);
         this.clase = clase;
      }//ClassificationFormEvent

      public Classification getClassification() 
      {
         return clase;
      }
   }//ClassificationFormEvent

   public static class SaveEvent extends ClassificationFormEvent 
   {
      SaveEvent(ClassificationForm source, Classification clase) 
      {
         super(source, clase);
      }
   }//SaveEvent

   public static class DeleteEvent extends ClassificationFormEvent 
   {
      DeleteEvent(ClassificationForm source, Classification clase) 
      {
         super(source, clase);
      }
   }//DeleteEvent

   public static class CloseEvent extends ClassificationFormEvent 
   {
      CloseEvent(ClassificationForm source) 
      {
         super(source, null);
      }
   }//CloseEvent

   public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) 
   {
      return getEventBus().addListener(eventType, listener);
   }//addListener

}//ClassificationForm
