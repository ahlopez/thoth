package com.f.thoth.ui.utils.converters;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

public class StringToString implements Converter<String, String>
{
	String DEFAULT_STRING = "";
	
	public StringToString( String defaultString)
	{
		DEFAULT_STRING = defaultString;
	}//StringToString

	@Override
	public Result<String> convertToModel( String fieldValue, ValueContext context) 
	{
		return fieldValue == null? Result.ok(DEFAULT_STRING): Result.ok(fieldValue);
	}//convertToModel

	@Override
	public String convertToPresentation( String text, ValueContext context) 
	{
		return text == null? DEFAULT_STRING: text;
	}

}//StringToStringConverter
