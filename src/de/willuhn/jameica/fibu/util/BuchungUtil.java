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

import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.datasource.rmi.ResultSetExtractor;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.BaseBuchung;
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.fibu.rmi.DBService;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.util.DateUtil;
import de.willuhn.logging.Logger;
import de.willuhn.util.I18N;


/**
 * Util-Klasse fuer die Buchungen.
 */
public class BuchungUtil
{
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

  /**
   * Liefert eine Map mit Key=Hauptkonto-ID und Value=Summe der Nebenbuchungen.
   * @param jahr das Jahr.
   * @param von Start-Datum.
   * @param bis End-Datum.
   * @return Map mit Key=Hauptkonto-ID und Value=Summe der Nebenbuchungen.
   * @throws RemoteException
   */
  public static Map<String,Double> getNebenbuchungSummen(Geschaeftsjahr jahr, Date von, Date bis) throws RemoteException
  {
    if (jahr == null)
      throw new RemoteException(i18n.tr("Kein Geschäftsjahr angegeben"));

    if (von != null && !jahr.check(von))
      throw new RemoteException(i18n.tr("Das Start-Datum {0} befindet sich ausserhalb des angegebenen Geschäftsjahres", Settings.DATEFORMAT.format(von)));

    if (bis != null && !jahr.check(bis))
      throw new RemoteException(i18n.tr("Das End-Datum {0} befindet sich ausserhalb des angegebenen Geschäftsjahres", Settings.DATEFORMAT.format(bis)));

    Date start = null;
    if (von != null)
      start = DateUtil.startOfDay(von);

    Date end = null;
    if (bis != null)
      end = DateUtil.endOfDay(bis);

    final Map<String,Double> result = new HashMap<>();

    final List params = new ArrayList();
    params.add(jahr.getID());
    
    try
    {
      DBService service = (DBService) Application.getServiceFactory().lookup(Fibu.class,"database");

      String sql = "SELECT buchung_id,betrag FROM buchung WHERE geschaeftsjahr_id = ? AND buchung_id IS NOT NULL";
      
      if (start != null)
      {
        sql += " AND " + service.getSQLTimestamp("datum") + " >= ?";
        params.add(start.getTime());
      }
      if (end != null)
      {
        sql += " AND " + service.getSQLTimestamp("datum") + " <= ?";
        params.add(end.getTime());
      }
      
      sql += " ORDER BY datum,belegnummer";

      ResultSetExtractor rs = new ResultSetExtractor()
      {
        public Object extract(ResultSet rs) throws RemoteException, SQLException
        {
          while (rs.next())
          {
            final String id = Integer.toString(rs.getInt(1));
            double value = rs.getDouble(2);
            
            // Checken, ob wir schon einen Betrag haben
            final Double sum = result.get(id);
            if (sum != null)
              value += sum.doubleValue();
            
            result.put(id,value);
          }
          return null;
        }
      };
      service.execute(sql,params.toArray(),rs);
    }
    catch (Exception e)
    {
      Logger.error("unable to determine bookings",e);
    }
    
    return result;
  }

  public static Map<String, Boolean> isSplit(Geschaeftsjahr jahr, Date von, Date bis) throws RemoteException{
	  /*if (jahr == null)
	      throw new RemoteException(i18n.tr("Kein Geschäftsjahr angegeben"));

	    final Map<Integer,Boolean> result = new HashMap<>();

	    DBIterator hauptbuchungen = jahr.getHauptBuchungen(von, bis);
	    while (hauptbuchungen.hasNext()) {
	    	Buchung b = (Buchung)hauptbuchungen.next();
	    	Object o = b.getAttribute("split_id");
	    	Integer i = (Integer)o;
	    	//final Integer id = (Integer)(((Buchung)hauptbuchungen.next()).getAttribute("split_id"));
	    	if(o == null)
	    		continue;
    		// Checken, ob wir schon einen Betrag haben
	    	//final String s = (String)h;
	    	//BaseBuchung bb = (BaseBuchung)o;
            final Boolean split = result.get(i);
            if (split == null)
            	result.put(i,true);
	    }
	    return result;*/
	    
	    if (jahr == null)
	        throw new RemoteException(i18n.tr("Kein Geschäftsjahr angegeben"));

	      if (von != null && !jahr.check(von))
	        throw new RemoteException(i18n.tr("Das Start-Datum {0} befindet sich ausserhalb des angegebenen Geschäftsjahres", Settings.DATEFORMAT.format(von)));

	      if (bis != null && !jahr.check(bis))
	        throw new RemoteException(i18n.tr("Das End-Datum {0} befindet sich ausserhalb des angegebenen Geschäftsjahres", Settings.DATEFORMAT.format(bis)));

	      Date start = null;
	      if (von != null)
	        start = DateUtil.startOfDay(von);

	      Date end = null;
	      if (bis != null)
	        end = DateUtil.endOfDay(bis);

	      final Map<String,Boolean> result = new HashMap<>();

	      final List params = new ArrayList();
	      params.add(jahr.getID());
	      
	      try
	      {
	        DBService service = (DBService) Application.getServiceFactory().lookup(Fibu.class,"database");

	        String sql = "SELECT id,(SELECT count(id) FROM buchung WHERE split_id = b.id) FROM buchung b WHERE geschaeftsjahr_id = ?";
	        
	        if (start != null)
	        {
	          sql += " AND " + service.getSQLTimestamp("datum") + " >= ?";
	          params.add(start.getTime());
	        }
	        if (end != null)
	        {
	          sql += " AND " + service.getSQLTimestamp("datum") + " <= ?";
	          params.add(end.getTime());
	        }
	        
	        sql += " ORDER BY datum,belegnummer";

	        ResultSetExtractor rs = new ResultSetExtractor()
	        {
	          public Object extract(ResultSet rs) throws RemoteException, SQLException
	          {
	            while (rs.next())
	            {
	              final String id = rs.getString(1);
	              boolean value = rs.getBoolean(2);
	              if(!value)
	            	  continue;
	              // Checken, ob wir schon einen Eintrag haben
	              final Boolean split = result.get(id);
	              if (split == null)
	              	result.put(id,true);
	            }
	            return null;
	          }
	        };
	        service.execute(sql,params.toArray(),rs);
	      }
	      catch (Exception e)
	      {
	        Logger.error("unable to determine bookings",e);
	      }
	      
	      return result;
  }
}
