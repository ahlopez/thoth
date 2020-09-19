package com.f.thoth.backend.data.entity.util;

import java.util.Map;
import java.util.TreeMap;

import com.f.thoth.backend.data.entity.AbstractEntity;

public final class EntityUtil
{
   private static Map<String,String> className = new TreeMap<>();
   static
   {
      className.put( "Tenant",              "Cliente");
      className.put( "ObjectToProtect",     "Objeto");
      className.put( "Operation",           "Operación");
      className.put( "Permission",          "Permiso");
      className.put( "Role",                "Rol");
      className.put( "SingleUser",          "Usuario");
      className.put( "UserGroup",           "Grupo de Usuarios");
      className.put( "Metadata",            "Metadato");
      className.put( "Field",               "Campo");
      className.put( "Schema",              "Esquema");
      className.put( "DocType",             "Tipo Documental");
      className.put( "Level",               "Nivel");
      className.put( "BranchClass",         "Rama");
      className.put( "LeafClass",           "Clase");
      className.put( "Retention",           "Calendario conservación");
      className.put( "Classification",      "Clase");

   }//className

   public static final String getName(Class<? extends AbstractEntity> type)
   {
      String typeName = type.getSimpleName();
      String label    = className.get(typeName);
      return label == null? typeName : label;
    }//getName

}//EntityUtil
