/**********************************************************************
 *
 * Copyright (c) 2004 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.gui.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import de.willuhn.jameica.fibu.Settings;
import de.willuhn.logging.Logger;

/**
 * Wir haben das Java-Dateformat ueberschrieben, damit wir mehrere
 * Datumsformate en bloc testen koennen.
 */
public class CustomDateFormat extends SimpleDateFormat
{
  /**
   * ct.
   */
  public CustomDateFormat()
  {
    super("dd.MM.yyyy");
  }

  /**
   * @see java.text.DateFormat#parse(java.lang.String)
   */
  public Date parse(String source) throws ParseException
  {
    // BUGZILLA 122
    DateFormat df = null;
    switch (source.length())
    {
      case 10:
        df = Settings.DATEFORMAT;
        break;
      case 8:
        df = Settings.FASTDATEFORMAT;
        break;
      case 6:
        df = Settings.BUCHUNGDATEFORMAT;
        break;
      case 4:
        try
        {
          // TODO: Das funktioniert nicht mehr, weil DateInput intern vorher  DateUtil.convert2Date(text) aufruft. Und das haengt automatisch das aktuelle Jahr an
          Calendar cal = Calendar.getInstance();
          cal.setTime(Settings.getActiveGeschaeftsjahr().getBeginn());
          source += cal.get(Calendar.YEAR);
          df = Settings.FASTDATEFORMAT;
          break;
        }
        catch (Exception e)
        {
          Logger.error("unable to read active gj",e);
          throw new ParseException("error while parsing date",0);
        }
      default:
        throw new ParseException("unknown date format: " + source,0);
    }

    // Parsen
    return df.parse(source);
    
  }
}


/*********************************************************************
 * $Log: CustomDateFormat.java,v $
 * Revision 1.5  2011/01/03 13:23:53  willuhn
 * *** empty log message ***
 *
 * Revision 1.4  2010-06-01 16:37:22  willuhn
 * @C Konstanten von Fibu zu Settings verschoben
 * @N Systemkontenrahmen nach expliziter Freigabe in den Einstellungen aenderbar
 * @C Unterscheidung zwischen canChange und isUserObject in UserObject
 * @C Code-Cleanup
 * @R alte CVS-Logs entfernt
 *
 * Revision 1.3  2007/02/27 15:46:17  willuhn
 * @N Anzeige des vorherigen Kontostandes im Kontoauszug
 *
 * Revision 1.2  2007/01/04 13:03:49  willuhn
 * @C javadoc
 *
 * Revision 1.1  2006/10/10 22:30:07  willuhn
 * @C DialogInput gegen DateInput ersetzt
 *
 **********************************************************************/