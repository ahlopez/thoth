package com.f.thoth.ui.utils.converters;

import java.time.LocalDateTime;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

public class LocalDateTimeToLocalDateTime implements Converter<LocalDateTime, LocalDateTime> 
{
	   @Override
	    public Result<LocalDateTime> convertToModel( LocalDateTime fieldValue, ValueContext context) 
	   {
		   return fieldValue == null? Result.ok(LocalDateTime.now()): Result.ok(fieldValue);
	    }//convertToModel

	    @Override
	    public LocalDateTime convertToPresentation( LocalDateTime date, ValueContext context) 
	    {
	    	return date == null? LocalDateTime.now(): date;
	    }


}//LocalDateTimeToLocalDateTimeConverter
