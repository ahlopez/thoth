package com.f.thoth.ui.views.expediente;

import java.time.LocalDate;

import com.f.thoth.backend.data.gdoc.classification.Classification;
import com.f.thoth.backend.data.gdoc.classification.Retention;
import com.f.thoth.backend.data.gdoc.expediente.BaseExpediente;
import com.f.thoth.backend.data.gdoc.expediente.BranchExpediente;
import com.f.thoth.ui.utils.converters.LocalDateToLocalDate;
import com.f.thoth.ui.views.classification.ClassificationValuesForm;
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

public class BranchExpedienteForm extends FormLayout
{
   private static final Converter<LocalDate, LocalDate> DATE_CONVERTER   = new LocalDateToLocalDate();

   private Button save   = new Button("Guardar Grupo");
   private Button close  = new Button("Cancelar");

   Binder<BranchExpediente> binder       = new BeanValidationBinder<>(BranchExpediente.class);

   BranchExpedienteValuesForm BranchExpedienteValuesForm = new BranchExpedienteValuesForm();

   public BranchExpedienteForm(BaseExpediente selectedExpediente)
   {
      setWidthFull();
      setResponsiveSteps(
            new ResponsiveStep("30em", 1),
            new ResponsiveStep("30em", 2),
            new ResponsiveStep("30em", 3));

      H3  title = new H3("Grupo de expedientes a actualizar");
      title.getElement().setAttribute("colspan", "2");

      TextField  asunto    = new TextField("Asunto");
      clase.setRequired(true);
      clase.setRequiredIndicatorVisible(true);
      clase.getElement().setAttribute("colspan", "3");

      LocalDate now = LocalDate.now();
      LocalDate yearStart =now.minusDays(now.getDayOfYear());

      DatePicker dateCreated = new DatePicker("Válida Desde");
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
/*
  protected String            expedienteCode;              // Business id unique inside the owner (class or expediente), vg 001,002, etc
  private Type                type;                        // Expediente tipo GRUPO/ HOJA/ EXPEDIENTE/ VOLUME
  protected String            path;                        // Node path in document repository
  protected String            name;                        // Expediente name
  protected ObjectToProtect   objectToProtect;             // Associated security object
  protected User              createdBy;                   // User that created this expediente
  protected Classification    classificationClass;         // Classification class to which this expediente belongs (Subserie si TRD)
  protected SchemaValues      metadata;                    // Metadata values of the associated expediente
  protected LocalDateTime     dateOpened;                  // Date expediente was opened
  protected LocalDateTime     dateClosed;                  // Date expediente was closed
  protected String            ownerPath;                   // Branch Expediente to which this Branch/Leaf/Volume belongs
  protected Boolean           open;                        // Is the expediente currently open?
  protected String            location;                    // Signatura topogr�fica
  protected String            keywords;                    // Search keywords
  protected String            mac;                         // Message authentication code
*/
            title,
            clase,
            new Label(" "),
            fromDate,
            toDate,
            schedule,
            createButtonsLayout(),
            BranchExpedienteValuesForm
         );

      binder.forField(clase).bind("name");


      binder.forField(fromDate)
            .asRequired()
            .withConverter(DATE_CONVERTER)
            .withValidator( dateFrom ->
                 {
                    LocalDate dateTo = toDate.getValue();
                    return dateTo == null || (dateFrom != null && dateFrom.equals(dateTo) || dateFrom.isBefore(dateTo));
                 },         "Fecha de cierre debe posterior a la de apertura")
            .bind("dateOpened");


      binder.forField(toDate)
            .asRequired()
            .withConverter(DATE_CONVERTER)
            .withValidator( dateTo ->
                            {
                               LocalDate dateFrom = fromDate.getValue();
                               boolean ok =  dateFrom != null && dateTo != null && ( dateTo.equals(dateFrom) || dateTo.isAfter(dateFrom));
                               return ok;
                            },
                            "Fecha de cierre debe posterior a la de apertura")
            .bind("dateClosed");

      binder.forField(schedule).bind("retentionSchedule");

      BranchExpedienteValuesForm.addListener(ClassificationValuesForm.SaveEvent.class, e->validateAndSave(e.getClassification()));
      BranchExpedienteValuesForm.getElement().setAttribute("colspan", "3");

   }//BranchExpedienteForm


   public void setExpediente(BranchExpediente branch)
   {
      binder.setBean(branch);
      BranchExpedienteValuesForm.setVisible(true);
      BranchExpedienteValuesForm.setBranchExpediente(branch);
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

      save.addClickListener  (click -> validateAndSave(binder.getBean()));
      close.addClickListener (click -> { close(); fireEvent(new CloseEvent(this));});

      binder.addStatusChangeListener(evt -> save.setEnabled(binder.isValid()));
      HorizontalLayout buttons = new HorizontalLayout(close, save);
      buttons.getElement().setAttribute("colspan", "3");

      return buttons;
   }//createButtonsLayout

   private void validateAndSave(BranchExpediente branch)
   {
      if (binder.isValid())
      {
         close();
         fireEvent(new SaveEvent(this, branch));
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
   }//ClassificationFormEvent

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
