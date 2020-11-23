package com.f.thoth.backend.data.gdoc.metadata.vaadin;

import java.util.Arrays;

import com.f.thoth.backend.data.entity.util.TextUtil;
import com.f.thoth.backend.data.gdoc.metadata.Field;
import com.f.thoth.backend.data.gdoc.metadata.Metadata;
import com.f.thoth.backend.data.gdoc.metadata.Type;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;

/** 
 * Builder de exportacion de un campo (metadato) a un componente Vaadin
 */
public class FieldToComponentExporter implements Field.Exporter
{
   private Component field;
   private String    name;
   private boolean   visible;
   private boolean   readOnly;
   private boolean   required;
   private String    columns;
   private Metadata  metadata;
   
   public FieldToComponentExporter()
   {  
   }//FieldToComponentExporter

   @Override public void initExport() { }

   @Override  public void exportBasic(String name, Metadata metadata)
   {
      if ( !TextUtil.isValidName(name))
         throw new IllegalArgumentException("Nombre del campo no puede ser nulo, blanco ni vacío");
         
      if (metadata == null)
         throw new IllegalArgumentException("Metadatos del campo no pueden ser nulos");
      
      this.name     = name;
      this.metadata = metadata;

   }//exportBasic
   
   public void exportFlags( boolean visible, boolean readOnly, boolean required)
   {
      this.visible  = visible;
      this.readOnly = readOnly;
      this.required = required;      
   }//exportFlags
   
   
   public void exportNumbers( Integer sortOrder, Integer columns)
   {
      this.columns   = columns.toString();
   }//exportNumbers
   

   @Override  public void endExport() 
   { 
      Type    type = metadata.getType();
      String range = metadata.getRange();
      switch (type)
      {
      case  ENUM     :  //  String.class
         ComboBox<String> combo = new ComboBox<String>();
         String[] values = range.split(";");
         for (int i=0; i < values.length; i++)
            values[i] = values[i].trim();
         
         combo.setDataProvider(new ListDataProvider<String>( Arrays.asList(values)));
         combo.setVisible(visible);
         combo.setReadOnly(readOnly);
         combo.setRequired(required);
         combo.setRequiredIndicatorVisible(required);
         combo.setLabel(this.name);
         combo.getElement().setAttribute("colspan", columns );
         field = combo;
         break;
      case  BINARY   :  //  Byte[].class
         TextArea area = new TextArea();
         area.setVisible(visible);
         area.setReadOnly(readOnly);
         area.setRequired(required);
         area.setRequiredIndicatorVisible(required);
         area.getElement().setAttribute("colspan", columns );
         area.setLabel(this.name);
         if ( range != null)
            area.setValue(range);
         
         field=area;
         break;
      case  BOOLEAN  :  //  Boolean.class
         Checkbox check = new Checkbox();
         check.setVisible(visible);
         check.setReadOnly(readOnly);
         check.setRequiredIndicatorVisible(required);
         check.getElement().setAttribute("colspan", columns );
         check.setLabel(this.name);
         field = check;
         break;
      case  DATETIME :  //  LocalDateTime.class
         DateTimePicker fecha = new DateTimePicker();
         fecha.setVisible(visible);
         fecha.setReadOnly(readOnly);
         fecha.setRequiredIndicatorVisible(required);
         fecha.getElement().setAttribute("colspan", columns );
         fecha.setLabel(this.name);
         field = fecha;
         break;
      case  DECIMAL  :  // BigDecimal.class
      case  INTEGER  :  // BigInteger.class
      case  REFERENCE:  // String.class
      case  URI      :  // URI.class
      case  ID       :  // String.class
      case  PATH     :  // String.class
      case  HTML     :  // String.class;
      case  STRING   :  // String.class
      default:
         TextField text = new TextField();
         text.setVisible(visible);
         text.setReadOnly(readOnly);
         text.setRequired(required);
         text.setRequiredIndicatorVisible(required);
         text.getElement().setAttribute("colspan", columns );
         text.setLabel(this.name);
         field= text;
      }//switch (type)
      
   }//endExport

   @Override  public Object getProduct() { return field; }

}//MetadataToComponentExporter
