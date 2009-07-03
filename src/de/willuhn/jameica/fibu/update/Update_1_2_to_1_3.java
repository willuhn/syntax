/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/update/Attic/Update_1_2_to_1_3.java,v $
 * $Revision: 1.4 $
 * $Date: 2009/07/03 10:52:19 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.update;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.rmi.DBService;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Steuer;
import de.willuhn.jameica.fibu.server.DBServiceImpl;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;
import de.willuhn.util.ProgressMonitor;

/**
 * Fuehrt das Update von Syntax 1.2 nach 1.3 durch.
 */
public class Update_1_2_to_1_3 implements Update
{

  /**
   * @see de.willuhn.jameica.fibu.update.Update#update(de.willuhn.util.ProgressMonitor, double, double)
   */
  public void update(ProgressMonitor monitor, double oldVersion, double newVersion) throws ApplicationException
  {
    if (oldVersion != 1.2d && newVersion != 1.3d)
    {
      Logger.info("skip update " + this.getClass().getName());
      return;
    }
    
    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

    Konto konto       = null;
    Steuer st         = null;
    DBService service = null;
    DBIterator list   = null;
    try
    {
      service = new DBServiceImpl();
      service.start();

      ////////////////////////////////////////////////////////////////////////////
      // SKR 03
      monitor.setStatusText(i18n.tr("Aktualisiere Kontenrahmen SKR03"));
      konto = (Konto) service.createObject(Konto.class,"1");
      konto.transactionBegin();
      
      list = service.createList(Steuer.class);
      list.addFilter("name=?",new String[]{"Vorsteuer 19%"});
      list.addFilter("steuerkonto_id=" + konto.getID());
      
      if (!list.hasNext())
      {
        st = (Steuer) service.createObject(Steuer.class,null);
        st.setName("Vorsteuer 19%");
        st.setSatz(19);
        st.setSteuerKonto(konto);
        st.store();
        service.executeUpdate("update konto set steuer_id=" + st.getID() + " where steuer_id=1",new Object[0]);
        monitor.addPercentComplete(1);
      }

      
      konto = (Konto) service.createObject(Konto.class,"3");

      list = service.createList(Steuer.class);
      list.addFilter("name=?",new String[]{"Umsatzsteuer 19%"});
      list.addFilter("steuerkonto_id=" + konto.getID());
      
      if (!list.hasNext())
      {
        st = (Steuer) service.createObject(Steuer.class,null);
        st.setName("Umsatzsteuer 19%");
        st.setSatz(19);
        st.setSteuerKonto(konto);
        st.store();
        service.executeUpdate("update konto set steuer_id=" + st.getID() + " where steuer_id=3",new Object[0]);
        monitor.addPercentComplete(1);
      }


      konto = (Konto) service.createObject(Konto.class,"206");
      konto.setName("Erlöse (keine UST)");
      konto.store();
      monitor.addPercentComplete(1);
      
      konto = (Konto) service.createObject(Konto.class,"207");
      konto.setName("Erlöse (ermäßigte UST)");
      konto.store();
      monitor.addPercentComplete(1);

      konto = (Konto) service.createObject(Konto.class,"208");
      konto.setName("Erlöse (volle UST)");
      konto.store();
      monitor.addPercentComplete(1);
      ////////////////////////////////////////////////////////////////////////////

    
      ////////////////////////////////////////////////////////////////////////////
      // SKR 04
      monitor.setStatusText(i18n.tr("Aktualisiere Kontenrahmen SKR04"));
      konto = (Konto) service.createObject(Konto.class,"1001");

      list = service.createList(Steuer.class);
      list.addFilter("name=?",new String[]{"Vorsteuer 19%"});
      list.addFilter("steuerkonto_id=" + konto.getID());
      
      if (!list.hasNext())
      {
        st = (Steuer) service.createObject(Steuer.class,null);
        st.setName("Vorsteuer 19%");
        st.setSatz(19);
        st.setSteuerKonto(konto);
        st.store();
        service.executeUpdate("update konto set steuer_id=" + st.getID() + " where steuer_id=1001",new Object[0]);
        monitor.addPercentComplete(1);
      }

      
      konto = (Konto) service.createObject(Konto.class,"1003");

      list = service.createList(Steuer.class);
      list.addFilter("name=?",new String[]{"Umsatzsteuer 19%"});
      list.addFilter("steuerkonto_id=" + konto.getID());
      
      if (!list.hasNext())
      {
        st = (Steuer) service.createObject(Steuer.class,null);
        st.setName("Umsatzsteuer 19%");
        st.setSatz(19);
        st.setSteuerKonto(konto);
        st.store();
        service.executeUpdate("update konto set steuer_id=" + st.getID() + " where steuer_id=1003",new Object[0]);
        monitor.addPercentComplete(1);
      }

      konto = (Konto) service.createObject(Konto.class,"1204");
      konto.setName("Erlöse (keine UST)");
      konto.store();
      monitor.addPercentComplete(1);
      
      konto = (Konto) service.createObject(Konto.class,"1205");
      konto.setName("Erlöse (ermäßigte UST)");
      konto.store();
      monitor.addPercentComplete(1);

      konto = (Konto) service.createObject(Konto.class,"1206");
      konto.setName("Erlöse (volle UST)");
      konto.store();
      monitor.addPercentComplete(1);
      ////////////////////////////////////////////////////////////////////////////

      ////////////////////////////////////////////////////////////////////////////
      // Hibiscus-Anbindung
      try
      {
        service.executeUpdate("alter table buchung add hb_umsatz_id char(7) NULL",new Object[0]);
        service.executeUpdate("CREATE INDEX idx_buchung_hb_umsatz_id ON buchung(hb_umsatz_id)",new Object[0]);
      }
      catch (Exception e)
      {
        Logger.warn("hibiscus connector allready added");
      }
      ////////////////////////////////////////////////////////////////////////////

      monitor.setStatusText(i18n.tr("Beende Transaktion"));
      konto.transactionCommit();
    }
    catch (ApplicationException ae)
    {
      if (konto != null)
      {
        monitor.log(i18n.tr("Rolle Transaktion zurück"));
        try
        {
          konto.transactionRollback();
        }
        catch (Exception e)
        {
          Logger.error("unable to rollback transaction",e);
        }
      }
      throw ae;
    }
    catch (Exception e)
    {
      if (konto != null)
      {
        monitor.log(i18n.tr("Rolle Transaktion zurück"));
        try
        {
          konto.transactionRollback();
        }
        catch (Exception e2)
        {
          Logger.error("unable to rollback transaction",e2);
        }
      }
      throw new ApplicationException(i18n.tr("Fehler beim Update der Datenbank"),e);
    }
    finally
    {
      if (service != null)
      {
        try
        {
          service.stop(true);
        }
        catch (Exception e)
        {
          Logger.error("unable to close dbservice",e);
        }
      }
    }
  }
}


/*********************************************************************
 * $Log: Update_1_2_to_1_3.java,v $
 * Revision 1.4  2009/07/03 10:52:19  willuhn
 * @N Merged SYNTAX_1_3_BRANCH into HEAD
 *
 * Revision 1.2  2007/02/27 15:50:44  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2006/12/27 15:23:33  willuhn
 * @C merged update 1.3 and 1.4 to 1.3
 *
 * Revision 1.1  2006/12/27 14:42:23  willuhn
 * @N Update fuer MwSt.-Erhoehung
 *
 **********************************************************************/