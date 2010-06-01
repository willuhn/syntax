/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/UpdateProvider.java,v $
 * $Revision: 1.1 $
 * $Date: 2010/06/01 17:42:03 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.ProgressMonitor;

/**
 * Implementierung eines Update-Providers.
 */
public class UpdateProvider implements de.willuhn.sql.version.UpdateProvider
{
  private Connection conn = null;

  /**
   * ct
   * @param conn Datenbank-Verbindung.
   */
  public UpdateProvider(Connection conn)
  {
    this.conn = conn;
  }

  /**
   * @see de.willuhn.sql.version.UpdateProvider#getConnection()
   */
  public synchronized Connection getConnection() throws ApplicationException
  {
    return this.conn;
  }

  /**
   * @see de.willuhn.sql.version.UpdateProvider#getCurrentVersion()
   */
  public int getCurrentVersion() throws ApplicationException
  {
    PreparedStatement st = null;
    ResultSet rs = null;
    try
    {
      st = this.conn.prepareStatement("select version from version where name = ?");
      st.setString(1,"db");
      rs = st.executeQuery();
      rs.next();
      return rs.getInt(1);
    }
    catch (Exception e)
    {
      // Versionstabelle gibts noch nicht. Dann wird sie mit update0001 erstellt
      return 0;
    }
    finally
    {
      if (rs != null) {
        try {
          rs.close();
        }
        catch (Exception e)
        {
          Logger.error("unable to close resultset",e);
        }
      }
      if (st != null) {
        try {
          st.close();
        }
        catch (Exception e)
        {
          Logger.error("unable to close statement",e);
        }
      }
    }
  }

  /**
   * @see de.willuhn.sql.version.UpdateProvider#getProgressMonitor()
   */
  public ProgressMonitor getProgressMonitor()
  {
    return Application.getController().getApplicationCallback().getStartupMonitor();
  }

  /**
   * @see de.willuhn.sql.version.UpdateProvider#getUpdatePath()
   */
  public File getUpdatePath() throws ApplicationException
  {
    return new File(Application.getPluginLoader().getManifest(Fibu.class).getPluginDir(),"updates");
  }

  /**
   * @see de.willuhn.sql.version.UpdateProvider#setNewVersion(int)
   */
  public void setNewVersion(int newVersion) throws ApplicationException
  {
    Logger.info("applying new version: " + newVersion);

    PreparedStatement st = null;
    try
    {
      st = this.conn.prepareStatement("update version set version = ? where name = ?");
      st.setInt(1,newVersion);
      st.setString(2,"db");
      if (st.executeUpdate() != 1)
        throw new ApplicationException("database update failed");
    }
    catch (SQLException e)
    {
      throw new ApplicationException("unable to update database version: " + e.getMessage(),e);
    }
    finally
    {
      if (st != null) {
        try {
          st.close();
        }
        catch (Exception e)
        {
          Logger.error("unable to close statement",e);
        }
      }
    }
  }
}


/*********************************************************************
 * $Log: UpdateProvider.java,v $
 * Revision 1.1  2010/06/01 17:42:03  willuhn
 * @N Neues Update-Verfahren via UpdateProvider
 *
 **********************************************************************/