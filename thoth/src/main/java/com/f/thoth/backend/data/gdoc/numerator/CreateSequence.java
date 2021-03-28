package com.f.thoth.backend.data.gdoc.numerator;


import com.f.thoth.backend.service.SequenceService;

/**
 * Representa la acción de crear una secuencia
 */
public class CreateSequence implements Instruction, Comparable<CreateSequence>
{
	private Sequence                sequence;
	private static SequenceService  sequenceService;

	@SuppressWarnings("unused")
	private CreateSequence() {}   // No permita uso del constructor nulo


	/**
	 * Obtiene un comando para crear una secuencia persistente
	 * @param sequence La secuencia a persistir
	 */
	public CreateSequence(Sequence sequence)
	{
		this.sequence        = sequence;
	}//CreateSequence


	public static void setService( SequenceService service)
	{
		sequenceService = service;
	}//setService


	/**
	 * Crea la secuencia en su medio externo
	 */
	public void execute()
	{
		try
		{
			synchronized(sequenceService)
			{
				System.out.println("A crear["+ sequence.getCode()); System.out.flush();
				sequenceService.add(sequence);
				System.out.println("Creó["+ sequence.getCode()); System.out.flush();
			}
		}catch( Throwable t)
		{
			throw new IllegalStateException("\nNo pudo crear secuencia "+ sequence.getCode()+ ".Razón\n"+ t);
		}

	}//execute


	/**
	 * Combina instrucciones en una sola, si es posible
	 * @param  other La instrucción que será combinada con esta
	 * @return boolean true si esta instruccion se actualizó con la presentada
	 */
	public boolean merge ( Instruction other)
	{
		CreateSequence that = (CreateSequence) other;
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

		if (!(other instanceof CreateSequence ))
			return false;

		CreateSequence that = (CreateSequence) other;
		return this.sequence.getCode().equals(that.sequence.getCode());

	}//equals

	@Override public int hashCode() { return sequence == null? 786037: sequence.hashCode();}

	@Override
	public String toString()
	{
		return "CreateSequence{ sequence["+ sequence.getCode()+ "]}\n";
	}//toString


	@Override  public int compareTo(CreateSequence that)
	{
		return this.equals(that)?  0 :
			that == null?       1 :
				this.getCode().compareTo(that.getCode());

	}// compareTo

}//CreateSequence
