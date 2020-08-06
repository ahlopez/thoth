package com.f.thoth.ui.utils.converters;

import java.time.LocalDate;

import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

public class DatePickerToLocalDateConverter implements Converter<DatePicker, LocalDate>
{
	   @Override
	    public Result<LocalDate> convertToModel( DatePicker fieldValue, ValueContext context) 
	   {
	        try {
	            return Result.ok(fieldValue.getValue());
	        } catch (NumberFormatException e) 
	        {
	            return Result.error("Ingrese una fecha válida");
	        }
	    }//convertToModel

	    @Override
	    public DatePicker convertToPresentation( LocalDate date, ValueContext context) 
	    {
	    	return new DatePicker(date);
	    }
}//DatePickerToLocalDateConverter
