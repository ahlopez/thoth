package com.f.thoth.backend.data.gdoc.numerator;


import com.f.thoth.backend.service.SequenceService;


/**
 * Representa la acción de guardar una secuencia
 */
public class SaveSequence implements Instruction, Comparable<SaveSequence>
{
	private Sequence sequence;                         // Sequence to save

	@SuppressWarnings("unused")
	private SaveSequence() {}   // Elimine constructor nulo

	/**
	 * Obtiene un comando para actualizar una secuencia
	 * @param sequence  La secuencia a persistir
	 */
	public SaveSequence(Sequence sequence)
	{
		if ( sequence == null)
			throw new IllegalArgumentException("Secuencia a guardar no puede ser nula");

		this.sequence = sequence;

	}//SaveSequence


	/**
	 * Actualiza la secuencia con su nuevo valor
	 */
	public void  execute()
	{
		try
		{
			SequenceService.getInstance().update(sequence);
		} catch ( Throwable t)
		{
			throw new IllegalStateException("No pudo guardar secuencia["+ sequence.getCode()+ "], valor["+ sequence.getValue()+ "]. Razon\n"+ t);
		}

	}//execute



	/**
	 * Combina instrucciones en una sola, si es posible
	 * @param  other La instrucción que será combinada con esta
	 * @return boolean true si esta instruccion se actualizó con la presentada
	 */
	public boolean merge ( Instruction other)
	{
		boolean merged = false;
		if (other instanceof CreateSequence)
		{
			CreateSequence that = (CreateSequence) other;
			merged = this.getCode().equals(that.getCode()) && this.setGreatestValue(that.getValue());
		}
		
        if (other instanceof SaveSequence)
		{
			SaveSequence that = (SaveSequence) other;
			merged = this.getCode().equals(that.getCode()) && this.setGreatestValue(that.getValue());
		}	
        return merged;
	}// merge

	public String   getCode()                     { return sequence.getCode();}
	public Long     getValue()                    { return sequence.getValue();}
	public boolean  setGreatestValue( Long value) { return sequence.setGreatestValue(value);}

	// --------------- Object methods --------------------

	@Override public boolean equals( Object other)
	{
		if (this == other)
			return true;

		if (!(other instanceof SaveSequence ))
			return false;

		SaveSequence that = (SaveSequence) other;
		return this.sequence.equals(that.sequence);

	}//equals

	@Override public int hashCode() { return sequence == null? 746027: sequence.hashCode();}

	@Override
	public String toString()
	{
		return "SaveSequence{ sequence["+ sequence.getCode()+ "]}\n";
	}//toString


	@Override  public int compareTo(SaveSequence that)
	{
		return this.equals(that)?  0 :
			that == null?       1 :
				this.getCode().compareTo(that.getCode());

	}// compareTo


}//SaveSequence
