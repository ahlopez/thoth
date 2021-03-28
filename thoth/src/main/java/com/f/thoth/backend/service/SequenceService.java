package com.f.thoth.backend.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.f.thoth.backend.data.cache.Cache;
import com.f.thoth.backend.data.gdoc.numerator.Sequence;
import com.f.thoth.backend.repositories.SequenceRepository;

@Service
public class SequenceService implements Cache.Fetcher<String, Sequence>
{
	private SequenceRepository sequenceRepository;

	@Autowired
	public SequenceService( SequenceRepository sequenceRepository)
	{
		this.sequenceRepository = sequenceRepository;
	}//SequenceService constructor

	/**
	 * Obtiene una secuencia con base en su identificador 
	 * @param key Identificador (primary key) de la secuencia
	 * @return secuencia solicitada, si se encuentra; null cuando no se encuentra
	 */
	public Sequence fetch( Long key)
	{
		Optional<Sequence> sequence =  sequenceRepository.findById(key);
		return sequence.isPresent()? sequence.get(): null;
	}//fetch

	/**
	 * Obtiene una secuencia con base en su identificador
	 * @param key Codigo (business key)  de la secuencia
	 * @return  Secuencia solicitada, si se encuentra; null cuando no se encuentra
	 */
	public Sequence fetch( String key)
	{
		Optional<Sequence> sequence =  sequenceRepository.findByCode(key);
		return sequence.isPresent()? sequence.get(): null;
	}//fetch

	/**
	 * Adiciona una nueva secuencia al sistema
	 * @param sequence La secuencia a adicionar
	 */
	public void add(Sequence sequence) 
	{
		System.out.println("seqService.add("+ sequence.toString()); System.out.flush();
		sequenceRepository.saveAndFlush(sequence);
		System.out.println("seqService.added("+ sequence.getCode()+ "]"); System.out.flush();		
	}//add

	/**
	 * Adiciona una nueva secuencia al sistema
	 * @param key Identificador (primary key) de la secuencia
	 * @param sequence La secuencia a adicionar
	 */
	public void add(Long key, Sequence sequence) 
	{
		sequenceRepository.saveAndFlush(sequence);
	}//add

	/**
	 * Adiciona una nueva secuencia al sistema
	 * @param key Identificador (business key) de la secuencia
	 * @param sequence La secuencia a adicionar
	 */
	public void add(String key, Sequence sequence) 
	{
		sequenceRepository.saveAndFlush(sequence);
	}//add

	/**
	 * Actualiza la información de una secuencia en el sistema
	 * @param sequence La secuencia a actualizar
	 * @return Estado anterior de la secuencia, si existe; null si no existe
	 */
	public Sequence update(Sequence sequence)
	{
		sequenceRepository.saveAndFlush(sequence);
		return sequence;
	}//update

	/**
	 * Actualiza la información de una secuencia en el sistema
	 * @param key Identificador (business key) de la secuencia
	 * @param sequence La secuencia a actualizar
	 * @return Estado anterior de la secuencia, si existe; null si no existe
	 */
	public Sequence update(String key, Sequence sequence)
	{
		sequenceRepository.saveAndFlush(sequence);
		return sequence;
	}//update

	/**
	 * Remueve la secuencia del proveedor
	 * @param key Identificador (primary key) de la secuencia a remover
	 * @return Secuencia removida, si existe; null si no existe
	 */
	public Sequence remove( Long key)
	{
		Optional<Sequence> old = sequenceRepository.findById(key);
		sequenceRepository.deleteById(key);
		return old.isPresent()? old.get(): null;
	}//remove

	/**
	 * Remueve la secuencia del proveedor
	 * @param key Identificador (business key) de la secuencia a remover
	 * @return Secuencia removida, si existe; null si no existe
	 */
	public Sequence remove( String key)
	{
		Optional<Sequence> old = sequenceRepository.findByCode(key);
		if ( old.isPresent())
		{
			Sequence sequence =old.get();	
			sequenceRepository.deleteById(sequence.getId());
			return sequence;
		}
		return null;
	}//remove

}//SequenceService
