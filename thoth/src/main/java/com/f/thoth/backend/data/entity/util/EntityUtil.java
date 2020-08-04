package com.f.thoth.backend.data.entity.util;

import java.util.Map;
import java.util.TreeMap;

import com.f.thoth.backend.data.entity.AbstractEntity;

public final class EntityUtil
{
   private static Map<String,String> className = new TreeMap<>();
   static
   {
	   className.put( "DocType",         "Tipo Documental");
	   className.put( "ObjectToProtect", "Objeto");
	   className.put( "Permission",      "Permiso");
	   className.put( "Role",            "Rol");
	   className.put( "Schema",          "Esquema");
	   className.put( "SingleUser",      "Usuario");
	   className.put( "Tenant",          "Cliente");
	   className.put( "UserGroup",       "Grupo de Usuarios");
   }
  
   public static final String getName(Class<? extends AbstractEntity> type)
   {
      String typeName = type.getSimpleName();
      String label    = className.get(typeName);
      return label == null? typeName : label;
    }//getName

}//EntityUtil
