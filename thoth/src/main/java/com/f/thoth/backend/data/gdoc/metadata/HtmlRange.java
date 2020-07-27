package com.f.thoth.backend.data.gdoc.metadata;

import java.io.IOException;
import java.io.StringReader;

import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTMLEditorKit.ParserCallback;

/**
 * Representa un rango de valores html
 */
public class HtmlRange implements Range
{
   private static HTMLEditorKit.Parser parser;
   private static HTMLEditorKit.ParserCallback callback;

   // ------------- Constructors ------------------
   public HtmlRange()
   {
      HTMLDocument doc = new HTMLDocument();
      parser           = doc.getParser();
      callback         = new ParserCallback();
   }//HtmlRange

   // --------------- Logic ------------------------------

   public boolean in(Object value)
   {
      if (value == null ||  !(value instanceof String))
         return false;

      StringReader reader = new StringReader( (String)value);
      try
      {
         parser.parse( reader, callback, true);
         return true;
      }catch( IOException ioe)
      {
         return false;
      }

   }//in

}//HtmlRange