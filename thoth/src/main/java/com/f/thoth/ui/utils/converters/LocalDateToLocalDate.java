package com.f.thoth.ui.utils.converters;

import java.time.LocalDate;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

public class LocalDateToLocalDate implements Converter<LocalDate, LocalDate>
{
	   @Override
	    public Result<LocalDate> convertToModel( LocalDate fieldValue, ValueContext context) 
	   {
		   return fieldValue == null? Result.ok(LocalDate.now()): Result.ok(fieldValue);
	    }//convertToModel

	    @Override
	    public LocalDate convertToPresentation( LocalDate date, ValueContext context) 
	    {
	    	return date;
	    }

}//LocalDateToLocalDate
