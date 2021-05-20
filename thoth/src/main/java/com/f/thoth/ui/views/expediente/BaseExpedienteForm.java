package com.f.thoth.ui.views.expediente;

import java.time.LocalDateTime;
import java.util.List;

import com.f.thoth.backend.data.gdoc.expediente.BaseExpediente;
import com.f.thoth.backend.data.gdoc.metadata.Schema;
import com.f.thoth.backend.data.gdoc.metadata.SchemaValues;
import com.f.thoth.backend.service.SchemaService;
import com.f.thoth.ui.components.MetadataEditor;
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
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ReadOnlyHasValue;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.shared.Registration;

/*
  Editor de la informacion basica de todo expediente/volumen
*/
public class BaseExpedienteForm extends FormLayout
{
  private static final Converter<LocalDateTime, LocalDateTime> DATE_CONVERTER   = new LocalDateTimeToLocalDateTime();

  private Button             save;
  private Button             close;
  private Component          buttons;

  private ReadOnlyHasValue<String> theTitle;
  private H3                 title;
  private TextField          classCode;
  private ComboBox<String>   open;
  private DateTimePicker     dateOpened;
  private DateTimePicker     dateClosed;
  private ComboBox<Schema>   schema;

  private MetadataEditor     metadataEditor;

  BaseExpediente             selectedExpediente = null;
  Binder<BaseExpediente>     binder             = new BeanValidationBinder<>(BaseExpediente.class);

  public BaseExpedienteForm(SchemaService schemaService)
  {
    setWidthFull();
    setResponsiveSteps(
                      new ResponsiveStep("30em", 1),
                      new ResponsiveStep("30em", 2),
                      new ResponsiveStep("30em", 3),
                      new ResponsiveStep("30em", 4));

    title = new H3("(((( TITULO ))))");
    title.getElement().setAttribute("colspan", "4");
    title.getElement().setAttribute("background-color", "snow"); 
    title.getElement().setAttribute("color",            "blue");
    title.getElement().setAttribute("font-weight",      "bold");

    /*
          Campos que falta considerar, si es que se necesitan
     protected String            ownerPath;                   // Branch Expediente to which this Branch/Leaf/Volume belongs
     protected String            location;                    // Signatura topográfica
   */

    TextField  name    = new TextField("Asunto");
    name.setRequired(true);
    name.setRequiredIndicatorVisible(true);
    name.setErrorMessage("El Asunto es obligatorio y no puede estar en blanco");
    name.getElement().setAttribute("colspan", "4");

    TextField  expedienteCode    = new TextField("Código");
    expedienteCode.setRequired(false);
    expedienteCode.setRequiredIndicatorVisible(false);
    expedienteCode.getElement().setAttribute("colspan", "1");
    expedienteCode.setEnabled(false);

    classCode= new TextField("Clase");
    classCode.setRequired(true);
    classCode.setRequiredIndicatorVisible(true);
    classCode.setErrorMessage("Código de la clase a que pertenece es obligatorio");
    classCode.getElement().setAttribute("colspan", "1");
    classCode.setEnabled(false);

    open = new ComboBox<>();
    open.setItems(new String[] {"ABIERTO", "CERRADO"});
    open.setWidth("20%");
    open.setRequired(true);
    open.setErrorMessage("Debe indicar si el expediente está ABIERTO ó CERRADO");
    open.getElement().setAttribute("colspan", "1");

    schema = new ComboBox<>("Metadatos");
    List<Schema> allSchemas = schemaService.findAll();
    schema.setItems(allSchemas);
    schema.addValueChangeListener(e ->
    {
      Schema selectedSchema = e.getValue();
      if (selectedSchema != null)
      {
        selectedExpediente.setMetadataSchema(selectedSchema);
        selectedExpediente.setMetadata(null);
        metadataEditor.setSchema(selectedSchema, null);
        buttons.setVisible(false);
      }else
      { buttons.setVisible(true);
      }
      buttons.setVisible(true);
      metadataEditor.setVisible(selectedSchema != null);
    });
    schema.setItemLabelGenerator(e-> e.getName());
    schema.setWidth("30%");
    schema.setRequired(true);
    schema.setRequiredIndicatorVisible(true);
    schema.setErrorMessage("Debe seleccionar si el expediente tiene asociado un esquema de metadatos");
    schema.getElement().setAttribute("colspan", "1");

    TextField  createdBy    = new TextField("Creado Por");
    createdBy.setRequired(true);
    createdBy.setRequiredIndicatorVisible(true);
    createdBy.setErrorMessage("La identificación del creador del expediente es obligatoria");
    createdBy.getElement().setAttribute("colspan", "1");
    createdBy.setEnabled(false);

    dateOpened = new DateTimePicker("Creado en");
    dateOpened.setRequiredIndicatorVisible(true);
    dateOpened.setWidth("40%");
    dateOpened.getElement().setAttribute("colspan", "2");
    dateOpened.setEnabled(false);

    dateClosed   = new DateTimePicker("Cerrado en");
    dateClosed.setRequiredIndicatorVisible(true);
    dateClosed.setWidth("40%");
    dateClosed.getElement().setAttribute("colspan", "2");
    dateClosed.setEnabled(false);

    TextField  keywords    = new TextField("Palabras clave");
    keywords.setRequired(false);
    keywords.setRequiredIndicatorVisible(true);
    keywords.getElement().setAttribute("colspan", "4");

    buttons = createButtonsLayout();

    add(
        title                ,
        name                 ,
        expedienteCode       ,
        schema               ,
        open                 ,
        createdBy            ,
        dateOpened           ,
        dateClosed           ,
        classCode            ,
        keywords             ,
        buttons
       );

    binder.forField(name)          .bind("name");
  //  binder.forField(expedienteCode).bind("expedienteCode");
  //  binder.forField(classCode)     .bind("classificationClass.classCode");

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
           },       "Fecha de cierre debe posterior a la de apertura")
          .bind("dateClosed");

    binder.forField(keywords) .bind("keywords");
    binder.forField(createdBy).bind("createdBy.email");

    metadataEditor = new MetadataEditor();
    metadataEditor.addListener(MetadataEditor.SaveEvent.class, e->validateAndSave(e.getValues()));
    metadataEditor.getElement().setAttribute("colspan", "4");
    add(metadataEditor);

  }//BaseExpedienteForm


  public void setExpediente(BaseExpediente expediente)
  {
    if ( expediente != null)
    {
      this.selectedExpediente = expediente;
      binder.setBean(selectedExpediente);
      setStatus( selectedExpediente);
      metadataEditor.setSchema( selectedExpediente.getMetadataSchema(), selectedExpediente.getMetadata());
      metadataEditor.setVisible(selectedExpediente == null || selectedExpediente.getMetadataSchema() != null);
    }

  }//setExpediente


  private void setStatus (BaseExpediente expediente)
  {
    LocalDateTime now       = LocalDateTime.now();
    LocalDateTime endOfTimes= now.plusYears(1000L);
    boolean isNew  = expediente == null || expediente.getId() == null;
    boolean isOpen = isNew || expediente.getOpen();
    classCode.setValue(expediente.getClassificationClass().formatCode());
    open.setValue( isOpen? "ABIERTO" : "CERRADO");
    open.setEnabled(isOpen);
    schema.setValue(expediente.getMetadataSchema());
    schema.setEnabled(isNew);

    theTitle = new ReadOnlyHasValue<>( text ->title.setText(text));
    binder.forField(theTitle).bind(e->getTitle(), null);

    if (isNew)
    {
      dateOpened.setValue(now);
      dateClosed.setValue(endOfTimes);
    }

  }//setStatus
  

  private String  getTitle()
  {
	  String oldOrNew = selectedExpediente == null?        ""
			  : !selectedExpediente.isPersisted()? "NUEVO "+ selectedExpediente.getType()
			  :                                    selectedExpediente.getType()+ " "+ selectedExpediente.formatCode()+ " - "+ selectedExpediente.getName();

	  String classOrGroup = selectedExpediente == null?                          ""
			  : selectedExpediente.getOwnerPath() != null?           ", EN GRUPO "+ selectedExpediente.getOwnerPath()
			  : selectedExpediente.getClassificationClass() != null? ", EN CLASE "+ selectedExpediente.getClassificationClass().formatCode() : "";

	  return oldOrNew + classOrGroup;

  }//getTitle


  private Component createButtonsLayout()
  {
    save = new Button("Guardar Grupo");
    save.addClickShortcut (Key.ENTER);
    save.addThemeVariants  (ButtonVariant.LUMO_PRIMARY);
    save.getElement().getStyle().set("margin-left", "auto");
    save.setWidth("20%");
    save.addClickListener  (click -> saveBaseExpediente(binder.getBean()));
    binder.addStatusChangeListener(evt -> save.setEnabled(binder.isValid()));

    close= new Button("Cancelar");
    close.addThemeVariants (ButtonVariant.LUMO_TERTIARY);
    close.addClickShortcut(Key.ESCAPE);
    close. setWidth("20%");
    close.addClickListener (click -> { close(); fireEvent(new CloseEvent(this));});

    HorizontalLayout buttons = new HorizontalLayout(close, save);
    buttons.getElement().setAttribute("colspan", "4");
    buttons.setWidthFull();

    return buttons;
  }//createButtonsLayout


  private void validateAndSave(SchemaValues metadataValues)
  {
    if (selectedExpediente != null)
    {   selectedExpediente.setMetadata(metadataValues);
    }
    saveBaseExpediente(selectedExpediente);
  }//validateAndSave


  private void saveBaseExpediente(BaseExpediente expediente)
  {
    if ( expediente != null)
    {
      boolean valid =binder.isValid();
      if (valid)
      {
        verifyClosing(expediente);
        close();
        fireEvent(new SaveEvent(this, expediente));
      }
    }
  }//saveBaseExpediente


  private void verifyClosing( BaseExpediente expediente)
  {
    boolean wasOpen = expediente.getOpen();
    boolean isClosed= "CERRADO".equals(open.getValue());
    if ( wasOpen && isClosed)
    {
      expediente.setOpen( false);
      LocalDateTime now = LocalDateTime.now();
      dateClosed.setValue(now);
      expediente.setDateClosed(now);
    }
  }//whenExpedienteCloses

  private void close() {  metadataEditor.setVisible(false);}

  // --------------------- Events -----------------------
  public static abstract class BaseExpedienteFormEvent extends ComponentEvent<BaseExpedienteForm>
  {
    private BaseExpediente baseExpediente;

    protected BaseExpedienteFormEvent(BaseExpedienteForm source, BaseExpediente baseExpediente)
    {
      super(source, false);
      this.baseExpediente = baseExpediente;
    }//BaseExpedienteFormEvent

    public BaseExpediente getBaseExpediente()
    {
      return baseExpediente;
    }
  }//BaseExpedienteFormEvent

  public static class SaveEvent extends BaseExpedienteFormEvent
  {
    SaveEvent(BaseExpedienteForm source, BaseExpediente baseExpediente)
    {
      super(source, baseExpediente);
    }
  }//SaveEvent

  public static class DeleteEvent extends BaseExpedienteFormEvent
  {
    DeleteEvent(BaseExpedienteForm source, BaseExpediente baseExpediente)
    {
      super(source, baseExpediente);
    }
  }//DeleteEvent

  public static class CloseEvent extends BaseExpedienteFormEvent
  {
    CloseEvent(BaseExpedienteForm source)
    {
      super(source, null);
    }
  }//CloseEvent

  public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener)
  {
    return getEventBus().addListener(eventType, listener);
  }//addListener

}//BaseExpedienteForm
