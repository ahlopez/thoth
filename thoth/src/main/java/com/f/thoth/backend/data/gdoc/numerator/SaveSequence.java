package com.f.thoth.backend.data.gdoc.numerator;


import com.f.thoth.backend.repositories.SequenceRepository;


/**
 * Representa la acción de guardaar una secuencia
 */
public class SaveSequence implements Instruction, Comparable<SaveSequence>
{
	private static SequenceRepository sequenceRepository;    // JPA repository
	private Sequence sequence;                               // Secuencia a persistir

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
	public void execute()
	{
		try
		{
			sequenceRepository.saveAndFlush(sequence);
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
		SaveSequence that = (SaveSequence)other;
		return this.equals(other) && setGreatestValue(that.getValue());
	}// merge

	private String   getCode()                     { return sequence.getCode();}
	private Long     getValue()                    { return sequence.getValue();}
	private boolean  setGreatestValue( Long value) { return sequence.setGreatestValue(value);}

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
