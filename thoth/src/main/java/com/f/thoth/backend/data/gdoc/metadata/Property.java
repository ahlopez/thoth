package com.f.thoth.backend.data.gdoc.metadata;

/*
 * Represents a field that has name and value
 */
public class Property
{
   Field  field;
   String value;
   
   public Property(Field field, String value)
   {
      this.field = field;
      this.value = value;
   }//Property

   public String     getName()     { return field.getName();}
   public Metadata   getMetadata() { return field.getMetadata();}
   public boolean    isVisible()   { return field.isVisible();}
   public boolean    isReadOnly()  { return field.isReadOnly();}
   public boolean    isRequired()  { return field.isRequired();}
   public Integer    getSortOrder(){ return field.getSortOrder();}
   public Integer    getColumns()  { return field.getColumns();}
   public String     getValue()    { return value;}
   
   public boolean    hasName(String name)     { return field.getName().equalsIgnoreCase(name);}
   
   public String     toString()
   {  return  "{Property "+ field.toString()+ "=["+ value+ "]}\n";
   }

}//Property
