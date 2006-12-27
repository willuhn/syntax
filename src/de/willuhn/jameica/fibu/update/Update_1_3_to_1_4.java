/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/update/Attic/Update_1_3_to_1_4.java,v $
 * $Revision: 1.1 $
 * $Date: 2006/12/27 14:42:23 $
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
 * Fuehrt das Update von Syntax 1.3 nach 1.4 durch.
 */
public class Update_1_3_to_1_4 implements Update
{

  /**
   * @see de.willuhn.jameica.fibu.update.Update#update(de.willuhn.util.ProgressMonitor, double, double)
   */
  public void update(ProgressMonitor monitor, double oldVersion, double newVersion) throws ApplicationException
  {
    if (oldVersion != 1.3 || newVersion != 1.4)
      return;

    I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

    Konto konto       = null;
    Steuer st         = null;
    DBService service = null;
    DBIterator konten = null;
    try
    {
      service = new DBServiceImpl();
      service.start();

      ////////////////////////////////////////////////////////////////////////////
      // SKR 03
      monitor.setStatusText(i18n.tr("Aktualisiere Kontenrahmen SKR03"));
      konto = (Konto) service.createObject(Konto.class,"1");
      
      konto.transactionBegin();
      st = (Steuer) service.createObject(Steuer.class,null);
      st.setName("Vorsteuer 19%");
      st.setSatz(19);
      st.setSteuerKonto(konto);
      st.store();
      monitor.addPercentComplete(1);
      
      konten = service.createList(Konto.class);
      konten.addFilter("steuer_id=1");
      while (konten.hasNext())
      {
        konto = (Konto) konten.next();
        konto.setSteuer(st);
        konto.store();
        monitor.addPercentComplete(1);
      }
      
      konto = (Konto) service.createObject(Konto.class,"3");
      st = (Steuer) service.createObject(Steuer.class,null);
      st.setName("Umsatzsteuer 19%");
      st.setSatz(19);
      st.setSteuerKonto(konto);
      st.store();
      monitor.addPercentComplete(1);

      konten = service.createList(Konto.class);
      konten.addFilter("steuer_id=3");
      while (konten.hasNext())
      {
        konto = (Konto) konten.next();
        konto.setSteuer(st);
        konto.store();
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
      st = (Steuer) service.createObject(Steuer.class,null);
      st.setName("Vorsteuer 19%");
      st.setSatz(19);
      st.setSteuerKonto(konto);
      st.store();
      monitor.addPercentComplete(1);

      konten = service.createList(Konto.class);
      konten.addFilter("steuer_id=1001");
      while (konten.hasNext())
      {
        konto = (Konto) konten.next();
        konto.setSteuer(st);
        konto.store();
        monitor.addPercentComplete(1);
      }
      
      konto = (Konto) service.createObject(Konto.class,"1003");
      st = (Steuer) service.createObject(Steuer.class,null);
      st.setName("Umsatzsteuer 19%");
      st.setSatz(19);
      st.setSteuerKonto(konto);
      st.store();
      monitor.addPercentComplete(1);

      konten = service.createList(Konto.class);
      konten.addFilter("steuer_id=1003");
      while (konten.hasNext())
      {
        konto = (Konto) konten.next();
        konto.setSteuer(st);
        konto.store();
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
 * $Log: Update_1_3_to_1_4.java,v $
 * Revision 1.1  2006/12/27 14:42:23  willuhn
 * @N Update fuer MwSt.-Erhoehung
 *
 **********************************************************************/