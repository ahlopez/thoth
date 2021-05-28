package com.f.thoth.ui.views.expediente;

import java.time.LocalDateTime;
import java.util.List;

import com.f.thoth.backend.data.gdoc.expediente.BaseExpediente;
import com.f.thoth.backend.data.gdoc.expediente.Nature;
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
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ReadOnlyHasValue;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.shared.Registration;

/*
  Editor de la informacion basica de todo grupo/expediente/volumen
*/
public class BaseExpedienteEditor extends FormLayout
{
  private static final Converter<LocalDateTime, LocalDateTime> DATE_CONVERTER   = new LocalDateTimeToLocalDateTime();

  private Button             save;
  private Button             close;
  private Component          buttons;

  private ReadOnlyHasValue<String> theTitle;
  private H3                 title;
  private TextField          expedienteCode;
  private TextField          classCode;
  private ComboBox<String>   open;
  private DateTimePicker     dateOpened;
  private DateTimePicker     dateClosed;
  private ComboBox<Schema>   schema;

  private MetadataEditor     metadataEditor;
  private String             parentCode;

  BaseExpediente             selectedExpediente = null;
  Binder<BaseExpediente>     binder             = new BeanValidationBinder<>(BaseExpediente.class);

  public BaseExpedienteEditor(SchemaService schemaService)
  {
    setWidthFull();
    setResponsiveSteps(
                      new ResponsiveStep("30em", 1),
                      new ResponsiveStep("30em", 2),
                      new ResponsiveStep("30em", 3),
                      new ResponsiveStep("30em", 4)
                      );

    title = new H3("(((( TITULO ))))");
    title.getElement().setAttribute("colspan", "4");
    title.getElement().getStyle().set("background",  "ivory");
    title.getElement().getStyle().set("color",       "blue");
    title.getElement().getStyle().set("font-weight", "bold");

    TextField  name    = new TextField("Asunto");
    name.setRequired(true);
    name.setRequiredIndicatorVisible(true);
    name.setErrorMessage("El Asunto es obligatorio y no puede estar en blanco");
    name.getElement().setAttribute("colspan", "4");

    expedienteCode    = new TextField("Código");
    expedienteCode.setRequired(false);
    expedienteCode.setRequiredIndicatorVisible(false);
    expedienteCode.getElement().setAttribute("colspan", "1");
    expedienteCode.getElement().getStyle().set("color", "blue");
    expedienteCode.setReadOnly(true);

    classCode= new TextField("Clase");
    classCode.setRequired(true);
    classCode.setRequiredIndicatorVisible(true);
    classCode.setErrorMessage("Código de la clase a que pertenece es obligatorio");
    classCode.getElement().setAttribute("colspan", "1");
    classCode.getElement().getStyle().set("color", "blue");
    classCode.setReadOnly(true);

    TextField  location    = new TextField("Localización");
    location.setRequired(false);
    location.setRequiredIndicatorVisible(false);
    location.getElement().setAttribute("colspan", "1");
    location.setEnabled(false);

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
      boolean metaSelected = (selectedSchema != null);
      metadataEditor.setVisible(metaSelected);
      if (metaSelected)
      {
        selectedExpediente.setMetadataSchema(selectedSchema);
        metadataEditor.editMetadata(selectedSchema, selectedExpediente.getMetadata());
      }
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

    buttons = configureActions();

    add(
        title                ,
        name                 ,
        expedienteCode       ,
        classCode            ,
        location             ,
        createdBy            ,
        dateOpened           ,
        dateClosed           ,
        keywords             ,
        schema               ,
        open
       );

    binder.forField(name)          .bind("name");
    binder.forField(location)      .bind("location");
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
    add(metadataEditor, buttons);

  }//BaseExpedienteEditor


  public void editExpediente(BaseExpediente expediente, String parentCode)
  {
    if ( expediente != null)
    {
      this.selectedExpediente = expediente;
      this.parentCode         = parentCode;
      binder.setBean(selectedExpediente);
      initValues( selectedExpediente);
      metadataEditor.editMetadata( selectedExpediente.getMetadataSchema(), selectedExpediente.getMetadata());
      metadataEditor.setVisible(selectedExpediente == null || selectedExpediente.getMetadataSchema() != null);
      addClassName  ("field-form");
    }

  }//editExpediente


  private void initValues (BaseExpediente expediente)
  {
    LocalDateTime now        = LocalDateTime.now();
    LocalDateTime endOfTimes = now.plusYears(1000L);
    boolean isNew  = expediente == null || expediente.getId() == null;
    boolean isOpen = isNew || expediente.getOpen();
    classCode.setValue(expediente.getClassificationClass().formatCode());
    open.setValue    (isOpen? "ABIERTO" : "CERRADO");
    open.setEnabled  (isOpen);
    schema.setValue  (expediente.getMetadataSchema());
    schema.setEnabled(isNew);

    theTitle = new ReadOnlyHasValue<>( text ->title.setText(text));
    binder.forField(theTitle).bind(e->getTitle(), null);

    if (isNew)
    {
      dateOpened.setValue(now);
      dateClosed.setValue(endOfTimes);
    }else
    { expedienteCode.setValue(expediente.getExpedienteCode());
    }

  }//initValues


  private String  getTitle()
  {
     if( selectedExpediente != null)
     {
        boolean   isNew = !selectedExpediente.isPersisted();
        String oldOrNew = isNew? "NUEVO " : "";
        String   prefix = selectedExpediente.isOfType(Nature.GRUPO)
                        ? selectedExpediente.getOwnerId() == null? "" : "SUB"
                        :   "";

        String title =  selectedExpediente.getType()+ (isNew? "" : (" "+ selectedExpediente.formatCode()+ " - "+ selectedExpediente.getName()));

        String classOrGroup = selectedExpediente == null
                            ?  ""
                            : selectedExpediente.getOwnerId() != null
                            ? ", DE GRUPO "+ parentCode
                            : ", EN CLASE "+ parentCode;

        return oldOrNew + prefix+ title+ classOrGroup;
     }else
     {  return  "";
     }
  }//getTitle


  private Component configureActions()
  {
    save = new Button("Guardar");
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
  }//configureActions


  private void validateAndSave(SchemaValues metadataValues)
  {
    if (selectedExpediente != null)
    {   selectedExpediente.setMetadata(metadataValues);
    }
    saveBaseExpediente(selectedExpediente);
  }//validateAndSave


  private void saveBaseExpediente(BaseExpediente expediente)
  {
     if ( expediente != null && binder.isValid())
     {
        whenExpedienteCloses(expediente);
        SchemaValues metaValues = metadataEditor.validateAndSave();
        if (metaValues != null)
        {   expediente.setMetadata(metaValues);
        }
        close();
        fireEvent(new SaveEvent(this, expediente));
     }
  }//saveBaseExpediente


  private void whenExpedienteCloses( BaseExpediente expediente)
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

  private void close()
  {  metadataEditor.setVisible(false);
     removeClassName("field-form");
  }//close

  // --------------------- Events -----------------------
  public static abstract class BaseExpedienteFormEvent extends ComponentEvent<BaseExpedienteEditor>
  {
    private BaseExpediente baseExpediente;

    protected BaseExpedienteFormEvent(BaseExpedienteEditor source, BaseExpediente baseExpediente)
    {  super(source, false);
       this.baseExpediente = baseExpediente;
    }//BaseExpedienteFormEvent

    public BaseExpediente getBaseExpediente()
    { return baseExpediente;
    }
  }//BaseExpedienteFormEvent

  public static class SaveEvent extends BaseExpedienteFormEvent
  {
    SaveEvent(BaseExpedienteEditor source, BaseExpediente baseExpediente)
    { super(source, baseExpediente);
    }
  }//SaveEvent

  public static class DeleteEvent extends BaseExpedienteFormEvent
  {
    DeleteEvent(BaseExpedienteEditor source, BaseExpediente baseExpediente)
    { super(source, baseExpediente);
    }
  }//DeleteEvent

  public static class CloseEvent extends BaseExpedienteFormEvent
  {
    CloseEvent(BaseExpedienteEditor source)
    { super(source, null);
    }
  }//CloseEvent

  public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener)
  {  return getEventBus().addListener(eventType, listener);
  }//addListener

}//BaseExpedienteEditor
