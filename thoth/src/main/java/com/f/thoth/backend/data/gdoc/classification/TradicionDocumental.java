package com.f.thoth.backend.data.gdoc.classification;

import java.util.Locale;

import com.vaadin.flow.shared.util.SharedUtil;

/**
 * Enumeracion con las tradiciones documentales
 * usadas al momento de disposicion final
 * de documentos
 */
public enum TradicionDocumental
{
   ORIGINAL,
   COPIA_FISICA,
   COPIA_DIGITAL;

   public String getDisplayName() { return SharedUtil.capitalize(name().toUpperCase(Locale.ENGLISH));}

   
}//TradicionDocumental