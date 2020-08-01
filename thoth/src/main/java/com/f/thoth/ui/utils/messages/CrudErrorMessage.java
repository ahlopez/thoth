package com.f.thoth.ui.utils.messages;

public final class CrudErrorMessage
{
   public static final String ENTITY_NOT_FOUND                  = "No se encontró la entidad solicitada.";

   public static final String CONCURRENT_UPDATE                 = "Alguien más ha actualizado la información. Por favor refresque y vuelva a intentarlo.";

   public static final String OPERATION_PREVENTED_BY_REFERENCES = "La operación no puede ser ejecutada pues hay referencias a entidades en la base de datos.";

   public static final String REQUIRED_FIELDS_MISSING           = "Por favor provea los campos marcados como requeridos antes de continuar.";

   private CrudErrorMessage()
   {
   }
}//CrudErrorMessage
