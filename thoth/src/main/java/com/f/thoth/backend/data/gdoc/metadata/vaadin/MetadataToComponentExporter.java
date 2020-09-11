package com.f.thoth.backend.data.gdoc.metadata.vaadin;

import java.util.Arrays;

import com.f.thoth.backend.data.gdoc.metadata.Metadata.Exporter;
import com.f.thoth.backend.data.gdoc.metadata.Type;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;

public class MetadataToComponentExporter implements Exporter
{
   private Component field;
   
   public MetadataToComponentExporter()
   {     
   }

   @Override public void initExport() { }

   @Override  public void export(String name, Type type, String range)
   {
      switch (type)
      {
      case  STRING   :  // String.class
         field = new TextField();
         break;
      case  ENUM     :  //  String.class
         ComboBox<String> combo = new ComboBox<String>();
         String[] values = range.split(";");
         combo.setDataProvider(new ListDataProvider<String>( Arrays.asList(values)));
         field = combo;
         break;
      case  BINARY   :  //  Byte[].class
         TextArea area = new TextArea();
         if ( range != null)
            area.setValue(range);
         
         break;
      case  BOOLEAN  :  //  Boolean.class
         field = new Checkbox();
         break;
      case  DECIMAL  :  //  BigDecimal.class
         field = new TextField();
         break;
      case  INTEGER  :  //  BigInteger.class
         field = new TextField();
         break;
      case  DATETIME :  //  LocalDateTime.class
         field = new DateTimePicker();
         break;
      case  REFERENCE:  // String.class
         field = new TextField();
         break;
      case  URI      :  // URI.class
         field = new TextField();
         break;
      case  ID       :  // String.class
         field = new TextField();
         break;
      case  PATH     :  // String.class
         field = new TextField();
         break;
      case  HTML     :  // String.class;
         field = new TextField();
      default:
         field = new TextField();
      }//switch (type)

   }//export

   @Override  public void endExport() { }

   @Override  public Object getProduct() { return field; }

}//MetadataToComponentExporter
