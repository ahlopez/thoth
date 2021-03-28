package com.f.thoth.backend.data.gdoc.numerator;


import com.f.thoth.backend.service.SequenceService;

/**
 * Representa la acción de cerrar una secuencia
 */
class CloseSequence implements Instruction
{
	private static SequenceService    sequenceService;    // Persistence service
	private        Sequence           sequence;           // Sequence to close

	@SuppressWarnings("unused")
	private CloseSequence() {}   // Elimine constructor nulo

	/**
	 * Obtiene un comando para cerrar una secuencia de numeracion
	 * @param sequence  La secuencia a cerrar
	 */
	public CloseSequence(Sequence sequence)
	{
		if ( sequence == null)
			throw new IllegalArgumentException("Secuencia a guardar no puede ser nula");

		this.sequence = sequence;

	}//CloseSequence


	public static void setService( SequenceService service)
	{
		sequenceService = service;
	}//setService   


	/**
	 * Cierra la secuencia en su medio externo
	 */
	public void execute()
	{
		try
		{
			synchronized(sequenceService)
			{
				sequenceService.update(sequence);
			}
		} catch ( Throwable t)
		{
			throw new IllegalStateException("No pudo cerrar secuencia["+ sequence.getCode()+ "], status["+ sequence.getStatus()+ "]. Razon\n"+ t);
		}

	}//execute


	/**
	 * Combina instrucciones en una sola, si es posible
	 * @param  other La instrucción que será combinada con esta
	 * @return boolean true si esta instruccion se actualizó con la presentada
	 */
	public boolean merge ( Instruction other)
	{
		return false;
	}// merge

}//CloseSequence
