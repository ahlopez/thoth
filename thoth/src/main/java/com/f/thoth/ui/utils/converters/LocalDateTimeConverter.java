package com.f.thoth.ui.utils.converters;

import static com.f.thoth.ui.dataproviders.DataProviderUtil.convertIfNotNull;
import static com.f.thoth.ui.utils.FormattingUtils.FULL_DATE_FORMATTER;

import java.time.LocalDateTime;

import com.vaadin.flow.templatemodel.ModelEncoder;

public class LocalDateTimeConverter implements ModelEncoder<LocalDateTime, String>
{
   private static final LocalTimeConverter TIME_FORMATTER = new LocalTimeConverter();

   @Override
   public String encode(LocalDateTime modelValue)
   {
      return convertIfNotNull(modelValue, v -> FULL_DATE_FORMATTER.format(v) + " " + TIME_FORMATTER.encode(v.toLocalTime()));
   }//encode

   @Override
   public LocalDateTime decode(String presentationValue)
   {
      throw new UnsupportedOperationException("Fecha-hora inválida");
   }//decode
}//LocalDateTimeConverter
