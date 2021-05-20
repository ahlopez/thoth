package com.f.thoth.ui.components;

import java.util.ArrayList;
import java.util.List;

import com.f.thoth.backend.data.gdoc.metadata.Schema;
import com.f.thoth.backend.data.gdoc.metadata.SchemaValues;
import com.f.thoth.backend.data.gdoc.metadata.vaadin.SchemaToVaadinExporter;
import com.f.thoth.backend.data.gdoc.metadata.vaadin.SchemaValuesToVaadinExporter;
import com.f.thoth.ui.utils.Constant;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;

public class MetadataEditor extends VerticalLayout
{
  private Schema                schema         = null;
  private Component             schemaFields   = null;
  private Schema.Exporter       schemaExporter = new SchemaToVaadinExporter();
  private SchemaValues.Exporter valuesExporter = new SchemaValuesToVaadinExporter();

  private Button                save ;
  private Button                close;

  public MetadataEditor()
  {
    schemaExporter = new SchemaToVaadinExporter();
    valuesExporter = new SchemaValuesToVaadinExporter();
    setWidthFull();
  }//MetadataEditor


  public void setSchema( Schema schema, SchemaValues values)
  {
    this.schema       = schema;
    this.schemaFields =  schema == null?  new FormLayout() : (Component)schema.export(schemaExporter); 
    removeAll();
    add(getEditor( schema, values));
    add(createButtonsLayout());
  }//setSchema
  
 

  public Component getEditor( Schema schema, SchemaValues values)
  {
    Component editor =  values == null || SchemaValues.EMPTY.equals(values)?
                        schema == null?  new FormLayout()
                                      : (Component)schema.export(schemaExporter)
                                      : (Component)values.export(valuesExporter);

    return editor;
  }//getEditor


  private void endEditing()
  {
    setVisible(false);
    removeClassName("field-form");
  }//endEditing


  private void validateAndSave()
  {
    String values = getValuesFromFields(schemaFields);
    SchemaValues vals = new SchemaValues(schema, values);
    endEditing();
    fireEvent(new SaveEvent(this, vals));
  }//validateAndSave


  private String getValuesFromFields( Component schemaFields)
  {
	if ( schemaFields == null)
		return null;
	
    List<Component> fields = new ArrayList<>();
    schemaFields.getChildren().forEach( c ->
    {
      if (c instanceof HasValue<?,?>)
        fields.add(c);
    });

    int i= 0;
    StringBuilder values = null;
    for (Component field: fields)
    {
      Object val = ((HasValue<?,?>)field).getValue();
      if (i++ == 0)
      {  values = new StringBuilder();
      }else
      {  values.append(Constant.VALUE_SEPARATOR);
      }
      values.append(val == null? Constant.NULL_VALUE: val.toString());
    }

    return values == null? null: values.toString();

  }//getValuesFromFields



  private Component createButtonsLayout()
  {
    save =  new Button("Guardar");
    save.addThemeVariants  (ButtonVariant.LUMO_PRIMARY);
    save.addClickShortcut (Key.ENTER);
    save.  setWidth("20%");
    save .addClickListener (click -> validateAndSave());
    save.getElement().getStyle().set("margin-left", "auto");

    close=  new Button("Cancelar");
    close.addThemeVariants (ButtonVariant.LUMO_TERTIARY);
    close.addClickShortcut(Key.ESCAPE);
    close. setWidth("20%");
    close.addClickListener (click -> close());

    HorizontalLayout buttons = new HorizontalLayout( close, save);
    buttons.setWidthFull();

    return buttons;
  }//createButtonsLayout


  private void close()
  {
    endEditing();
    fireEvent(new CloseEvent(this));
  }//close


  // --------------------- Events -----------------------
  public static abstract class MetadataEditorEvent extends ComponentEvent<MetadataEditor>
  {
    private SchemaValues values;

    protected MetadataEditorEvent(MetadataEditor source, SchemaValues values)
    {
      super(source, false);
      this.values = values;
    }//MetadataEditorEvent

    public SchemaValues getValues()
    {  return values;
    }
  }//MetadataEditorEvent

  public static class SaveEvent extends MetadataEditorEvent
  {
    SaveEvent(MetadataEditor source, SchemaValues values)
    {  super(source, values);
    }
  }//SaveEvent

  public static class CloseEvent extends MetadataEditorEvent
  {
    CloseEvent(MetadataEditor source)
    {  super(source, null);
    }
  }//CloseEvent

  public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener)
  {
    return getEventBus().addListener(eventType, listener);
  }//addListener


}//MetadataEditor

