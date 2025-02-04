/**********************************************************************
 *
 * Copyright (c) 2004 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.util;

import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import de.willuhn.datasource.rmi.ResultSetExtractor;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.rmi.DBService;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;


/**
 * Util-Klasse fuer die Konten.
 */
public class KontoUtil
{
  /**
   * Liefert ein Set mit den IDs der Konten, bei denen Buchungen existieren.
   * @param jahr das Geschäftsjahr.
   * @return Set mit den IDs der Konten, bei denen Buchungen existieren.
   */
  public static Set<String> getKontenMitBuchungen(Geschaeftsjahr jahr)
  {
    final Set<String> result = new HashSet<>();

    try
    {
      DBService service = (DBService) Application.getServiceFactory().lookup(Fibu.class,"database");

      for (String column:Arrays.asList("sollkonto_id","habenkonto_id"))
      {
        String sql = "select " + column + " FROM buchung where geschaeftsjahr_id = ? GROUP by " + column;
        ResultSetExtractor rs = new ResultSetExtractor()
        {
          public Object extract(ResultSet rs) throws RemoteException, SQLException
          {
            while (rs.next())
              result.add(rs.getString(1));
            
            return null;
          }
        };
        
        service.execute(sql, new Object[] {Integer.valueOf(jahr.getID())},rs);
      }
    }
    catch (Exception e)
    {
      Logger.error("unable to determine accounts with bookings",e);
    }
    
    return result;
  }
}
