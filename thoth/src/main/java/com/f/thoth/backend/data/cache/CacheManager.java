package com.f.thoth.backend.data.cache;


/** Implementa el rol de manager de un patrón caché (Grand 1998) */
public class CacheManager<K,T>                     // K= Key type,  T=Cached object type
{

  /** cache - El caché de objetos que está siendo administrado por este manager */
  private Cache<K,T> cache;

  /** server - El proveedor de un objeto cuando no se encuentra en el caché */
  private Cache.Fetcher<K,T> server;

  /**
   * Obtiene una instancia del caché
   * @param server - Proveedor de objetos cuando no están en el caché
   * @param size   - Tamaño del caché
   */
  public CacheManager(Cache.Fetcher<K,T> server, int size)
  {
    reset( server, size);
  }//CacheManager


  /**
   * Reinicializa el administrador de caché
   * 
   * @param server - Proveedor de objetos cuando no están en el caché
   * @param size   - Tamaño del caché
   * @throw NullPointerException cuando {@code server == null}
   */
  public synchronized void reset(Cache.Fetcher<K,T> server, int size)
  {
    if ( server == null )
      throw new NullPointerException("El proveedor de objetos del caché no puede ser nulo");

    this.server = server;
    if ( size < 1 )
      this.cache  = new Cache<>( );
    else
      this.cache  = new Cache<>( size);

  }//reset
  

  /**
   * Adiciona un nuevo objeto al sistema
   * 
   * @param key    Identificador único del objeto a adicionar
   * @param object El objeto a adicionar
   */
  public synchronized void add(K key, T object)
  {
	if (cache.fetch(key) == null)
	{
		cache.add (key, object);
      server.add(key, object);
	}
  }//add
  

  /**
   * Actualiza un objeto en el sistema
   * 
   * @param key    Identificador único del objeto a actualizar
   * @param object El nuevo objeto con la información actualizada
   */
  public synchronized T update( K key, T object)
  {
    server.update(key, object);
    return cache.replace(key, object);
  }// update
  

  /**
   * Obtiene un objeto dado su identificador
   * 
   * @param key Identificador del objeto
   * @return El objeto solicitado si se encuentra; null si el objeto no se encuentra
   */
  public synchronized T fetch( K key)
  {
    T theObject = cache.fetch(key);
    if ( theObject == null )
    {
      theObject = server.fetch(key);
      if ( theObject != null )
        cache.add(key, theObject);
    }
    return theObject;
  } // fetch
  

  /**
   * Remueve un objeto del caché y de su servidor
   * 
   * @param key Identificador del objeto
   * @return El objeto removido, si existe; null si no existe
   */
  public synchronized T remove( K key)
  {
    server.remove( key);
    return cache.remove( key);
  }//remove
  

  /**
   * Remueve un objeto del caché de forma que si se vuelve a solicitar
   * deberá ser cargado de nuevo
   * @param key Identificador del objeto
   * @return El objeto removido, si existe; null si no existe
   */
  public synchronized T invalidate( K key)
  {
    return cache.remove(key);
  }//invalidate
  

  /**
   * Aplana la estructura del objeto en un String
   * @return String que representa el objeto
   */
  public synchronized String toString()
  {
         return cache.toString()+ server.toString();
  }//toString


} // CacheManager
