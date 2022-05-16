package com.f.thoth.backend.data.gdoc.document.jackrabbit;

/**
 * Representa un tipo de nodo del repositorio
 */
public enum NodeType
{
    CLASSIFICATION    ("CLS") ,
    EXPEDIENTE        ("EXP"), 
    VOLUMEN           ("VOL"), 
    VOLUME_INSTANCE   ("INS"), 
    DOCUMENT          ("DHD"), 
    DOCUMENT_INSTANCE ("DIN"), 
    EXPEDIENTE_INDEX  ("IDX") ;
	
	private String code;
	
	private NodeType ( String code)
	{
		this.code = code;
	}
	
	public NodeType typeOf( String code)
	{
		if ( code ==  null)
			return null;
		
		for ( NodeType type: values())
		{
			if ( type.code.equals(code.trim().toUpperCase()))
				return type;
		}
		return null;
	}//typeOf
	
	public String getCode() { return code;}
	
}//NodeType
