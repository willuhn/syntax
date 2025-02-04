package de.willuhn.jameica.fibu.gui.part;

import java.rmi.RemoteException;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;

import de.willuhn.datasource.GenericIterator;
import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.gui.menus.BuchungSplitListMenu;
import de.willuhn.jameica.fibu.messaging.ObjectChangedMessage;
import de.willuhn.jameica.fibu.rmi.BaseBuchung;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.fibu.rmi.Kontoart;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.formatter.CurrencyFormatter;
import de.willuhn.jameica.gui.formatter.DateFormatter;
import de.willuhn.jameica.gui.formatter.Formatter;
import de.willuhn.jameica.gui.formatter.TableFormatter;
import de.willuhn.jameica.gui.parts.TablePart;
import de.willuhn.jameica.gui.util.Color;
import de.willuhn.jameica.messaging.Message;
import de.willuhn.jameica.messaging.MessageConsumer;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.I18N;

public class BuchungSplitList extends TablePart{

 private final static I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
  
 private MessageConsumer mcChanged    = new ChangedMessageConsumer();
 
 /**
 * Buchungs Liste für Splitbuchungn
 * @param action
 * @throws RemoteException
 */
 public BuchungSplitList(Buchung hauptbuchung,Action action) throws RemoteException
 {
  this((DBIterator)hauptbuchung.getSplitBuchungen(),action);
  }
 
/**
  * ct.
  * @param buchungen die Liste der Buchungen.
  * @param action
  * @throws RemoteException
  */
 private BuchungSplitList(GenericIterator buchungen, Action action) throws RemoteException
 {
   super(buchungen,action);

   addColumn(i18n.tr("Datum"),"datum", new DateFormatter(Settings.DATEFORMAT));
   addColumn(i18n.tr("Beleg"),"belegnummer");
   addColumn(i18n.tr("Text"),"buchungstext");
   addColumn(i18n.tr("Brutto-Betrag"),"bruttoBetrag",new CurrencyFormatter(Settings.getActiveGeschaeftsjahr().getMandant().getWaehrung(), Settings.DECIMALFORMAT));
   addColumn(i18n.tr("Netto-Betrag"),"betrag",new CurrencyFormatter(Settings.getActiveGeschaeftsjahr().getMandant().getWaehrung(), Settings.DECIMALFORMAT));
   addColumn(i18n.tr("Steuer"),"steuer");
   addColumn(i18n.tr("Soll-Konto"),"sollKonto", new KontoFormatter());
   addColumn(i18n.tr("Haben-Konto"),"habenKonto", new KontoFormatter());
   addColumn(i18n.tr("Art"),"sollKonto", new Formatter()
   {
     public String format(Object o)
     {
       if (o == null || !(o instanceof Konto))
         return null;
       try
       {
         Konto k = (Konto) o;
         Kontoart ka = k.getKontoArt();
         if (ka == null)
           return null;
         return ka.getName();
       }
       catch (RemoteException e)
       {
         Logger.error("unable to detect konto art",e);
         return null;
       }
     }
   });
   setContextMenu(new BuchungSplitListMenu());
   setMulti(true);
   setRememberColWidths(true);
   setRememberOrder(true);
   setRememberState(true);
   
   setFormatter(new TableFormatter() {
     public void format(TableItem item)
     {
       if (item == null)
         return;
       BaseBuchung b = (BaseBuchung) item.getData();
       if (b == null)
         return;
       try
       {
         if (b.isGeprueft())
           item.setForeground(Color.SUCCESS.getSWTColor());
         else
           item.setForeground(Color.FOREGROUND.getSWTColor());
       }
       catch (Exception e)
       {
         Logger.error("unable to check buchung",e);
       }
     }
   });
 }

 /**
  * @see de.willuhn.jameica.gui.Part#paint(org.eclipse.swt.widgets.Composite)
  */
 public synchronized void paint(Composite parent) throws RemoteException
 {
   super.paint(parent);
   Application.getMessagingFactory().registerMessageConsumer(this.mcChanged);
   parent.addDisposeListener(new DisposeListener()
   {
     public void widgetDisposed(DisposeEvent e)
     {
       Application.getMessagingFactory().unRegisterMessageConsumer(mcChanged);
     }
   });
 }
 
  /**
  * Formatiert ein Konto huebsch.
  */
 private static class KontoFormatter implements Formatter
 {
   /**
    * @see de.willuhn.jameica.gui.formatter.Formatter#format(java.lang.Object)
    */
   public String format(Object o)
   {
     if (o == null)
       return null;
     if (! (o instanceof Konto))
       return o.toString();
     Konto k = (Konto) o;
     try
     {
       String name = k.getName();
       if (name.length() > 15)
         name = name.substring(0,10) + "...";
       return k.getKontonummer() + " [" + name + "]";
     }
     catch (RemoteException e)
     {
       Logger.error("unable to read konto",e);
       return null;
     }
   }
 }
 
 /**
  * erhaelt Updates ueber geaenderte Buchungen und aktualisiert die Tabelle live.
  */
 private class ChangedMessageConsumer implements MessageConsumer
 {
   /**
    * @see de.willuhn.jameica.messaging.MessageConsumer#autoRegister()
    */
   public boolean autoRegister()
   {
     return false;
   }

   /**
    * @see de.willuhn.jameica.messaging.MessageConsumer#getExpectedMessageTypes()
    */
   public Class[] getExpectedMessageTypes()
   {
     return new Class[]{ObjectChangedMessage.class};
   }

   /**
    * @see de.willuhn.jameica.messaging.MessageConsumer#handleMessage(de.willuhn.jameica.messaging.Message)
    */
   public void handleMessage(Message message) throws Exception
   {
     ObjectChangedMessage m = (ObjectChangedMessage) message;
     final Object buchung = m.getData();
     if (buchung == null || !(buchung instanceof BaseBuchung))
       return;
     
     GUI.getDisplay().syncExec(new Runnable() {
       public void run()
       {
         try
         {
           int index = removeItem(buchung);
           if (index == -1)
             return; // Objekt war nicht in der Tabelle

           // Aktualisieren, in dem wir es neu an der gleichen Position eintragen
          addItem(buchung,index);

          // Wir markieren es noch in der Tabelle
          select(buchung);
         }
         catch (Exception e)
         {
           Logger.error("unable to add object to list",e);
         }
       }
     });
   }
 }


}

