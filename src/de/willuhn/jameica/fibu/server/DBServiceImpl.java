/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/server/DBServiceImpl.java,v $
 * $Revision: 1.13 $
 * $Date: 2006/06/13 22:52:10 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.server;

import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;

import de.willuhn.jameica.fibu.rmi.DBService;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.ResultSetExtractor;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;

/**
 * Datenbank-Service fuer Fibu.
 */
public class DBServiceImpl extends de.willuhn.datasource.db.DBServiceImpl implements DBService
{

  private HashMap jahre = new HashMap();
  private Geschaeftsjahr jahr = null;
  
  /**
   * ct.
   * @throws RemoteException
   */
  public DBServiceImpl() throws RemoteException
  {
    super(null,null,null,null);
//    super(
//      SETTINGS.getString("jdbc.driver","com.mckoi.JDBCDriver"),
//      SETTINGS.getString("jdbc.url",":jdbc:mckoi:local://" + Application.getPluginLoader().getPlugin(Fibu.class).getResources().getWorkPath() + "/db/db.conf"),
//      SETTINGS.getString("jdbc.username","fibu"),
//      SETTINGS.getString("jdbc.password","fibu")
//    );
    this.setClassloader(Application.getClassLoader());
    this.setClassFinder(Application.getClassLoader().getClassFinder());
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.DBService#setActiveGeschaeftsjahr(de.willuhn.jameica.fibu.rmi.Geschaeftsjahr)
   */
  public void setActiveGeschaeftsjahr(Geschaeftsjahr jahr) throws RemoteException
  {
    try
    {
      this.jahre.put(getClientHost(),jahr);
    }
    catch (ServerNotActiveException e)
    {
      // hu, wir laufen wohl lokal. Also koennen wir auch das lokale Geschaeftsjahr nehmen
      this.jahr = jahr;
    }
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.DBService#getActiveGeschaeftsjahr()
   */
  public Geschaeftsjahr getActiveGeschaeftsjahr() throws RemoteException
  {
    try
    {
      return (Geschaeftsjahr) this.jahre.get(getClientHost());
    }
    catch (Exception e)
    {
      // lokaler Modus, also lokales Geschaeftsjahr nehmen
      return this.jahr;
    }
  }

  /**
   * @see de.willuhn.jameica.fibu.rmi.DBService#getSQLTimestamp(java.lang.String)
   */
  public String getSQLTimestamp(String content) throws RemoteException
  {
    // TODO Scheisse, ist das haesslich ;)
//    String s = SETTINGS.getString("sql.function.timestamp","tonumber({0})");
//    return s.replaceAll("\\{0\\}",content);
    return null;
  }
  
  /**
   * @see de.willuhn.jameica.fibu.rmi.DBService#execute(java.lang.String, java.lang.Object[], de.willuhn.jameica.fibu.rmi.ResultSetExtractor)
   */
  public Object execute(String sql, Object[] params, ResultSetExtractor extractor) throws RemoteException
  {
    PreparedStatement ps = null;
    ResultSet rs = null;
    try
    {
      ps = getConnection().prepareStatement(sql);
      if (params != null)
      {
        for (int i=0;i<params.length;++i)
        {
          Object o = params[i];
          if (o == null)
            ps.setNull((i+1), Types.NULL);
          else
            ps.setObject((i+1),params[i]);
        }
      }

      rs = ps.executeQuery();
      return extractor.extract(rs);
    }
    catch (SQLException e)
    {
      Logger.error("error while executing sql statement",e);
      throw new RemoteException("error while executing sql statement: " + e.getMessage(),e);
    }
    finally
    {
      if (rs != null)
      {
        try
        {
          rs.close();
        }
        catch (Throwable t)
        {
          Logger.error("error while closing resultset",t);
        }
      }
      if (ps != null)
      {
        try
        {
          ps.close();
        }
        catch (Throwable t2)
        {
          Logger.error("error while closing statement",t2);
        }
      }
    }
  }

  /**
   * Ueberschrieben, damit der Service nur gestartet wird, wenn die DB eingerichtet ist.
   * @see de.willuhn.datasource.Service#start()
   */
  public synchronized void start() throws RemoteException
  {
    if (de.willuhn.jameica.fibu.Settings.isFirstStart())
    {
      Logger.info("first start: skipping db service");
      return;
    }
    super.start();
  }
}


/*********************************************************************
 * $Log: DBServiceImpl.java,v $
 * Revision 1.13  2006/06/13 22:52:10  willuhn
 * @N Setup wizard redesign and code cleanup
 *
 * Revision 1.12  2006/06/12 14:08:29  willuhn
 * @N DB-Wizard
 *
 * Revision 1.11  2006/05/30 23:22:55  willuhn
 * @C Redsign beim Laden der Buchungen. Jahresabschluss nun korrekt
 *
 * Revision 1.10  2006/05/29 23:05:07  willuhn
 * *** empty log message ***
 *
 * Revision 1.9  2006/05/08 22:44:18  willuhn
 * @N Debugging
 *
 * Revision 1.8  2006/03/27 20:26:53  willuhn
 * *** empty log message ***
 *
 * Revision 1.7  2006/01/06 00:05:51  willuhn
 * @N MySQL Support
 *
 * Revision 1.6  2006/01/05 17:40:30  willuhn
 * @N mysql support
 *
 * Revision 1.5  2006/01/04 16:04:33  willuhn
 * @B gj/mandant handling (insb. Loeschen)
 *
 * Revision 1.4  2005/10/20 23:03:44  willuhn
 * @N network support
 *
 * Revision 1.3  2005/10/18 23:28:55  willuhn
 * @N client/server tauglichkeit
 *
 * Revision 1.2  2005/08/15 23:38:27  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2005/08/08 21:35:46  willuhn
 * @N massive refactoring
 *
 **********************************************************************/