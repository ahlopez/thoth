package com.f.thoth.backend.data.cache;

import java.util.Hashtable;
import java.util.Map;

/** Implementa rol Caché del patrón caché.   Grand(1998) */
public class Cache<K,T>
{

  /**
   * cache                    - El caché de objetos
   * mru                      - most recently used object. Cabeza de lista doblemente encadenada
   * lru                      - least recently used object. Cola de lista doblemente encadenada
   * currentCacheSize         - Número de objetos actualmente en el caché
   * DEFAULT_MAX_CACHE_SIZE   - Tamaño default del caché
   */
  private Map<K,LinkedList<K,T>>  cache;
  private LinkedList<K,T>         mru;
  private LinkedList<K,T>         lru;
  private int                     currentCacheSize;
  private int                     maxSize;

  private final static int DEFAULT_MAX_CACHE_SIZE = 100;

  /** Obtiene una instancia del caché */
  public Cache( ) { reset( DEFAULT_MAX_CACHE_SIZE);}

  /**
   * Obtiene una instancia del caché
   * @param size Tamaño máximo en que se espera llenar el caché.
   */
  public Cache( int size) { reset(size);}

  /**
   * Reinicializa el caché
   * @param size Tamaño máximo en que se espera llenar el caché.
   * @throw IllegalArgumentException cuando {@code size <= 0}
   */
  public void reset( int size)
  {
    if ( size <= 0 )
      throw new IllegalArgumentException("Tamaño del caché debe ser un entero positivo. Es("+ size+ ")");

    this.maxSize          = size;
    this.cache            = new Hashtable<>((int)(size * 1.33), (float)0.75);
    this.mru              = null;
    this.lru              = null;
    this.currentCacheSize = 0;
  }//reset


  /**
   * Adiciona objetos al caché, de acuerdo con su política.
   * Cuando el caché lo determine, los objetos pueden ser borrados del mismo
   * para hacer campo para nuevos objetos
   * @param obj - The candidate for addition to the caché
   */
  public void add(K key, T obj)
  {
    if ( cache.get(key) == null )
    {
      LinkedList<K,T> newLink = new LinkedList<>();
      newLink.key             = key;
      newLink.object          = obj;
      newLink.next            = mru;
      newLink.previous        = null;
      if ( mru != null)
        mru.previous = newLink;

      if ( lru == null)
        lru = newLink;
      else
      {
        if( currentCacheSize >= maxSize)
        {
          cache.remove(lru.key);
          lru      = lru.previous;
          lru.next = null;
          currentCacheSize --;
        }
      }
      mru = newLink;
      cache.put(key, newLink);
      currentCacheSize++;
    } else
    {  // add no debe llamarse para objetos existentes ... Haga el objeto el mru
      fetch(key);
    }

  } // add(Object)


  /**
   * Obtiene un objeto con base en su identificador
   * @id Identificador único del objeto
   * @return objeto solicitado, cuando se encuentra;
   * null si no se encuentra
   */
  public T fetch(K key)
  {
    LinkedList<K,T> foundLink = cache.get(key);
    if ( foundLink == null )
      return null;

    // Haga el objeto el mru
    if ( mru != foundLink )
    {
      foundLink.previous.next = foundLink.next;

      if ( foundLink.next != null )
        foundLink.next.previous = foundLink.previous;

      if ( lru == foundLink)
        lru = foundLink.previous;

      foundLink.previous = null;
      foundLink.next     = mru;
      mru.previous       = foundLink;
      mru                = foundLink;
    } // if currentCacheSize > 1

    return foundLink.object;

  } // fetch(Key)


  /**
   * Remplaza un objeto del caché por otro
   * @param object El nuevo objeto
   * @return La versión antigua del objeto, si existe;
   * null si no existe
   */
  public T replace( K key, T object)
  {
    LinkedList<K,T> foundLink = cache.get(key);
    if ( foundLink == null )
      return null;

    T oldObject = foundLink.object;
    foundLink.object  = object;
    fetch( key);
    return oldObject;
  }//update


  /**
   * Remueve un objeto del caché
   * @param key Identificador del objeto a remover
   * @return El objeto removido, si existe; null si no existe
   */
  public T remove( K key)
  {
    LinkedList<K,T> foundLink = cache.remove(key);
    if ( foundLink == null )
      return null;

    // remueva el objeto de la lista de uso
    if ( mru == lru )
    {  // the only object in the caché
      mru     =   lru  = null;
      currentCacheSize = 0;
    } else if ( mru == foundLink )
    { // most used
      mru          = mru.next;
      mru.previous = null;
    } else if ( lru == foundLink )
    {  // least used
      lru      = lru.previous;
      lru.next = null;
    } else
    {  // halfway in the list
      foundLink.next.previous = foundLink.previous;
      foundLink.previous.next = foundLink.next;
    }

    currentCacheSize--;
    return foundLink.object;
  }//remove(key)

  /**
   * Aplana la estructura del caché en un String
   * @return String que representa el caché
   */
  public String toString()
  {
    StringBuffer b = new StringBuffer();
    b.append("[Cache - size("+ cache.keySet().size()+ ")")
     .append( " maxSize("+ maxSize+ ")")
     .append( " mru(").append(mru == null? "null": (mru.object == null? "nulo": mru.key)).append(")")
     .append( " lru(").append(lru == null? "null": (lru.object == null? "nulo": lru.key)).append(")")
     .append( "\n");

    for( LinkedList<K,T> node= mru; node != null; node = node.next)
      b.append(" Objeto (") .append(node.object == null? "null" : node.key).append(") ")
       .append(" next(")    .append(node.next == null? "null": node.next.key).append(") ")
       .append(" previous(").append(node.previous == null? "null" : node.previous.key).append(")\n");

    b.append("]\n");
    return b.toString();

  }//toString

  /** Limpia el caché  */
  public void clear()
  {
     cache.clear();
     this.mru     = null;
     this.lru     = null;
  }//clear




  // :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
  // La estructura de información para manejo de uso

  /**
   * Lista doblemente encadenada para manejar el uso de
   * los objetos en el caché
   */
  private class LinkedList<L,O>
  {
    public L               key;
    public O               object;
    public LinkedList<L,O> previous;
    public LinkedList<L,O> next;
  } // class LinkedList

  // ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
  // Proveedor de objetos para el caché

  /** Proveedor de objetos para el caché */
  public interface Fetcher<K,T>
  {
    /**
     * Obtiene un objeto con base en su identificador
     * @param key Identificador del objeto
     * @return Keyable El objeto solicitado, si se encuentra;
     * null cuando no se encuentra
     */
    public T fetch( K key);

    /**
     * Adiciona un nuevo objeto al sistema
     * @param key Identificador del objeto
     * @param object El objeto a adicionar
     */
    public void add(K key, T object);

    /**
     * Actualiza la información de un objeto en el sistema
     * @param key Identificador del objeto
     * @param object El objeto a actualizar
     * @return el objeto anterior, si existe; null si no existe
     */
    public T update(K key, T object);

    /**
     * Remueve el objeto del proveedor
     * @param id Identificador del objeto
     * @return el objeto removido, si existe; null si no existe
     */
    public T remove( K id);
  }//Fetcher

} // class Cache
