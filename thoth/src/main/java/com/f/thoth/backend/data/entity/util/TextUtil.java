package com.f.thoth.backend.data.entity.util;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utilitario para el manejo de texto
 */
public class TextUtil
{
   /*
    *  sf   -   Formato simple para edicion de fechas
    */
   private static final Locale APP_LOCALE = Locale.US;
   private static final SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
   private static final DateTimeFormatter FULL_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy", APP_LOCALE);

    /**
     * Decide si el texto presentado es nulo, o vacio
     * @param text Texto a examinar
     * @return true si el texto es nulo o vacio
     */
    public static boolean isEmpty( String text)
    {
        return text == null || text.length() == 0;
    }//isEmpty


    /**
     * Decide si el texto presentado es no nulo ni vacio
     * @param text Texto a examinar
     * @return true si el texto no es nulo y no es vacio
     */
    public static boolean isNotEmpty( String text)
    {
        return text != null && text.length() > 0;

    }//isNotEmpty


    /**
     * Obtiene una cadena concatenada consigo misma un numero determinado de veces
     *
     * @param str Cadena a repetir
     * @param times Numero de veces que se repite
     * @return Resultado de la concatenacion
     */
    public static String repeat(String str, int times)
    {
        if ( str == null || times < 0 )
            return null;

        StringBuilder b = new StringBuilder( str.length() * times);
        for ( int i = 0; i < times; i++ )
            b.append(str);

        return b.toString();
    }// repeat

   /**
    * Convierte el primer caracter de una cadena a mayusuculas
    * @param str cadena a la cual se le quiere convertir la primera letra a mayusculas
    * @return
    */
    public static String sentenceCase(String str)
    {
        return (isEmpty(str) || str.length()<=1) ?
            str :
                String.valueOf(str.charAt(0)).toUpperCase()+str.substring(1);
    }//sentenceCase



    /**
     * Determina si una cadena es un identificador válido
     * @param str La cadena a examinar
     * @return true si es un identificador válido; falso si no lo es
     */
    public static boolean isIdentifier(String str)
    {
       boolean isId = isNotEmpty(str) && Character.isJavaIdentifierStart(str.charAt(0));
       if ( isId )
       {
          for ( int i = 1; isId && i < str.length(); i++ )
             isId = Character.isJavaIdentifierPart(str.charAt(i));
       }

       return isId;
    }// isIdentifier

    /**
     * Remplaza todas las ocurrencias de un string source por el string target,
     * que se encuentran en un string base.<BR>
     *
     * @param base String base donde se debe realizar el remplazo
     * @param source String a remplazar
     * @param target  String por el cual debe ser remplazado
     * @return El string base con el resultado de los remplazos;<BR>
     *         Si alguno de los parametros es null retorna null
     */
    public static String replace(String base, String source, String target)
    {
       StringBuilder result= replace (new StringBuilder( base), source, target);
       return result.toString();
    }// replace

    /**
     * Remplaza todas las ocurrencias de un string source por el string target,
     * que se encuentran en un string base.<BR>
     *
     * @param base StringBuilder base donde se debe realizar el remplazo
     * @param source String a remplazar
     * @param target  String por el cual debe ser remplazado
     * @return El string base con el resultado de los remplazos;<BR>
     *         Si alguno de los parametros es null retorna null
     */
    public static StringBuilder replace(StringBuilder base, String source, String target)
    {
       if ( base == null )
          return null;

       if ( source != null && target != null )
       {
          for ( int loc = base.indexOf(source); loc >= 0; loc = base.indexOf(source) )
             base.replace(loc, loc + source.length(), target);
       }
       return base;

    }// replace

    /**
     * Obtiene una fecha formateada AAA/MM/DD
     * @param year Año de la fecha
     * @param month Mes de la fecha (0 es Enero)
     * @param day Día del mes
     * @return String con la fecha formateada
     */
    public static String formatDate( int year, int month, int day)
    {
         GregorianCalendar theDate = new GregorianCalendar(year, month, day);
         return    sf.format(theDate.getTime());

    }//formatDate
    
    /**
     * Obtiene la fecha formateada dd.mm.yyyy
     * @param date Fecha a formatear
     * @return String fecha en formato dd.mm.yyyy
     */
    public static String formatDate( LocalDate date)
    {
        return date == null? "---": 
               date.equals(LocalDate.MAX)? "-MAX-":
               date.equals(LocalDate.MIN)? "-MIN-":
               date.format(FULL_DATE_FORMATTER);
    }//formatDate

    /**
     * Obtiene una fecha formateada AAA/MM/DD
     * @param date fecha a formatear
     * @return String con la fecha formateada
     */
    public static String formatDate( GregorianCalendar date)
    {
         return    sf.format(date.getTime());
    }//formatDate

    /**
     * Obtiene una fecha formateada AAA/MM/DD
     * @param date fecha a formatear
     * @return String con la fecha formateada
     */
    public static String formatDate( Date date)
    {
         return    sf.format(date.getTime());
    }//formatDate


    /**
     * Convierte un entero a una cadena de longitud especìfica, agregando ceros
     * por la izquierda
     *
     * @source El número que hay que convertir
     * @len La longitud deseada de la cadena
     * */
    public static String pad(int source, int len)
    {
       String number = String.valueOf(source);
       return "0000000000000000000000000000000000".
       substring(0, len - number.length())+
       number;
    }// pad

    /**
     * Determina si una cadena es un nombre válido para un nodo.
     * Los nodos, contrario a los identificadores, pueden empezar por
     * números, y contener el caracter '-'
     * @param source String a examinar
     * @return true si es válida; false si no lo es
     */
    public static boolean isValidNodeName( String source)
    {
      if ( source == null)
         return false;

      return source.matches("[a-zA-Z0-9_-]{1,255}");

    }//isValidNodeName

    /**
     * Determina si una cadena es un nombre válido.
     * @param source String a examinar
     * @return true si es válido; false si no lo es
     */
    public static boolean isValidName( String source)
    {
      if ( source == null)
         return false;

      return source.matches("([a-z][a-zA-Z0-9_-]){1,255}");

    }//isValidName

    /**
     * Determina si una cadena es alfanumérica,
     * incluyendo los caracteres '_' y '-'.
     * @param source String a examinar
     * @return true si es válido; false si no lo es
     */
    public static boolean isAlphaNumeric( String source)
    {
    	if (source == null)
    		return false;
    	
    	return source.matches("[a-zA-Z0-9_]([a-zA-Z0-9_-]){0,255}");
    }//isAlphaNumeric

    /**
     * Determina si una cadena presentada es una ruta válida
     * @param source Cadena a examinar
     * @return true si es válida; false si no lo es
     */
    public static boolean isValidPath( String source)
    {
      if ( source == null)
         return false;

      return  source.matches( "([/]([a-zA-Z0-9_-]{1,255})){1,50}");

    }//isValidPath

    /**
     * Verifica si un string tiene el formato de una dirección email
     * @param source  String a verificar
     * @return  true si es una dirección válida, false si no lo es
     */
    private static Pattern emailPattern = Pattern.compile("^(.+)@(.+)$");
    public static boolean isValidEmail( String source)
    {
      Matcher matcher = emailPattern.matcher(source);
      return matcher.matches();
    }//isValidEmail


    /**
     * Asegura que el nombre tenga un formato correcto.
     * Si nulo, lo cambia por [Desconocido]
     * @param name Nombre a revisar
     * @return Nombre ajustado (Sentence case)
     */
    public static String  nameTidy( String name)
    {
       if ( name == null){
          name = "[Desconocido]";
       }else {
          name = name.trim().toLowerCase();
          if (name.length() == 0)
             name = "[Desconocido]";
       }

       return sentenceCase(name);
    }//nameTidy


}//TextUtil
