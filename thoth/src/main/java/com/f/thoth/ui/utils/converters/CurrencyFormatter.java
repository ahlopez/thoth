package com.f.thoth.ui.utils.converters;

import com.f.thoth.ui.dataproviders.DataProviderUtil;
import com.f.thoth.ui.utils.FormattingUtils;
import com.vaadin.flow.templatemodel.ModelEncoder;

public class CurrencyFormatter implements ModelEncoder<Integer, String>
{

   @Override
   public String encode(Integer modelValue)
   {
      return DataProviderUtil.convertIfNotNull(modelValue, FormattingUtils::formatAsCurrency);
   }

   @Override
   public Integer decode(String presentationValue)
   {
      throw new UnsupportedOperationException();
   }
}//CurrencyFormatter
