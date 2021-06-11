package com.f.thoth.ui.utils;

import com.f.thoth.Parm;

public class TemplateUtil
{
   public static String generateLocation(String basePage, String entityId)
   {
      return basePage + (entityId == null || entityId.isEmpty() ? "" : Parm.PATH_SEPARATOR + entityId);
   }
}//TemplateUtil
