/**********************************************************************
 *
 * Copyright (c) 2024 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import org.apache.commons.lang.StringUtils;

import de.willuhn.jameica.fibu.Settings;
import de.willuhn.logging.Logger;

/**
 * Hilfsklasse mit Zahlen.
 */
public class NumberUtil
{
	/**
	 * Versucht, einen Text extra fehlertolerant als Dezimalzahl zu parsen.
	 * Die Funktion toleriert auch Waehrungskennzeichen.
	 * @param number die Zahl.
	 * @return die geparste Dezimalzahl.
	 */
	public static BigDecimal parse(Object number)
	{
	  if (number == null)
	    return null;
	  
	  if (number instanceof BigDecimal)
	    return (BigDecimal) number;
	  
	  if (number instanceof Double)
	    return new BigDecimal((Double) number);
	  
	  try
	  {
	    // 1. Whitespaces entfernen
	    String value = StringUtils.trimToNull(StringUtils.deleteWhitespace(number.toString()));
	    if (value == null)
	      return null;
	    
	    // 2. "+"-Zeichen entfernen. Kann am Anfang oder Ende stehen
	    if (value.contains("+"))
	      value = StringUtils.replace(value,"+","");

	    // 3. Wenn ueberhaupt keine Ziffern enthalten sind, koennen wir es sein lassen.
      if (!value.matches(".*?[\\d].*?"))
        return null;
      
	    // 4. Wenn hinten noch Text dran steht, dann entfernen (haeufig ein Waehrungskennzeichen)
	    int space = value.lastIndexOf(" ");
	    if (space > 0)
	    {
	      String s = StringUtils.deleteWhitespace(value.substring(space+1));
	      if (s.matches("[^0-9,.-]{1,20}"))
	        value = value.substring(0,space);
	    }

	    // 5. Wenn vorn noch Text dran steht, dann entfernen (haeufig ein Waehrungskennzeichen)
	    space = value.indexOf(" ");
	    if (space > 0)
	    {
	      String s = StringUtils.deleteWhitespace(value.substring(0,space));
        if (s.matches("[^0-9,.-]{1,20}"))
	        value = value.substring(space+1);
	    }
	    
      // 6. Waehrungskennzeichen ohne Space am Anfang oder Ende entfernen
      value = value.replaceAll("^[$¤]|EUR","");
      value = value.replaceAll("[$¤]|EUR$","");

	    // 7. "-" am Ende nach vorn verschieben
	    if (value.endsWith("-"))
	    {
        value = value.substring(0,value.length()-1); // hinten entfernen, wenn es schon vorn dran steht
	      if (!value.startsWith("-"))
	        value = "-" + value; // Und vorn dran schreiben, wenn es da nicht schon steht
	    }
	    
	     // 8. Alle verbliebenen Whitespaces entfernen - falls mittendrin noch welche waren
      value = StringUtils.trimToNull(StringUtils.deleteWhitespace(value));
      if (value == null)
        return null;

	    final DecimalFormat df = Settings.DECIMALFORMAT;
      
	    Number n = df.parse(value);
	    if (n instanceof BigDecimal)
	      return (BigDecimal) n;
	    
	    if (n instanceof Double)
	    {
	      Double d = (Double) n;
	      if (d.isInfinite() || d.isNaN())
	      {
	        Logger.warn("invalid decimal number (infinite or NaN): " + number);
          return null;
	      }
	    }

	    // BigDecimal erwartet "." als Dezimaltrenner. Wir muessen also noch
	    // das Komma gegen Punkt verwenden, wenn das DecimalFormat auf einem Locale basiert
	    // welches Komma verwendet
      char komma = df.getDecimalFormatSymbols().getDecimalSeparator();
	    return new BigDecimal(value.replace(komma,'.'));
	  }
	  catch (Exception e)
	  {
	    Logger.warn("unable to parse text as decimal number: " + number);
	    return null;
	  }
	}
}
