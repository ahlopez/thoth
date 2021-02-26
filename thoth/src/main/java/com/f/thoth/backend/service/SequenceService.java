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
     * Obtiene un objeto con base en su identificador
     * @param key Identificador del objeto
     * @return Keyable El objeto solicitado, si se encuentra;
     * null cuando no se encuentra
     */
    public Sequence fetch( Long key)
    {
    	Optional<Sequence> sequence =  sequenceRepository.findById(key);
    	return sequence.isPresent()? sequence.get(): null;
    }//fetch
	
    /**
     * Obtiene un objeto con base en su identificador
     * @param key Identificador del objeto
     * @return Keyable El objeto solicitado, si se encuentra;
     * null cuando no se encuentra
     */
    public Sequence fetch( String key)
    {
    	Optional<Sequence> sequence =  sequenceRepository.findById(key);
    	return sequence.isPresent()? sequence.get(): null;
    }//fetch

    /**
     * Adiciona un nuevo objeto al sistema
     * @param key Identificador del objeto
     * @param object El objeto a adicionar
     */
    public void add(Long key, Sequence object) 
    {
    	sequenceRepository.saveAndFlush(object);
    }//add

    /**
     * Actualiza la información de un objeto en el sistema
     * @param key Identificador del objeto
     * @param object El objeto a actualizar
     * @return el objeto anterior, si existe; null si no existe
     */
    public Sequence update(Long key, Sequence object)
    {
    	sequenceRepository.saveAndFlush(object);
    	return object;
    }//update

    /**
     * Remueve el objeto del proveedor
     * @param key Identificador del objeto
     * @return el objeto removido, si existe; null si no existe
     */
    public Sequence remove( Long key)
    {
    	Optional<Sequence> old = sequenceRepository.findById(key);
    	sequenceRepository.deleteById(key);
    	return old.isPresent()? old.get(): null;
    }//remove

}//SequenceService
