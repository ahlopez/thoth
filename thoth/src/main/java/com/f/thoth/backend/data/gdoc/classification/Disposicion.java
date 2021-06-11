package com.f.thoth.backend.data.gdoc.classification;

import java.util.Locale;

import com.vaadin.flow.shared.util.SharedUtil;

/**
 * Enumeracion con las acciones de disposicion
 * usadas al momento de disposicion final
 * de documentos
 */
public enum Disposicion
{
   CONSERVACION_TOTAL,
   MUESTREO,
   SELECCION,
   ELIMINACION;

   public String getDisplayName() { return SharedUtil.capitalize(name().toUpperCase(Locale.ENGLISH));}

}//Disposicion