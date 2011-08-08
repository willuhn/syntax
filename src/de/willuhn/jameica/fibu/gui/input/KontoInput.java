/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/input/KontoInput.java,v $
 * $Revision: 1.5 $
 * $Date: 2011/08/08 10:44:36 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.gui.input;

import java.rmi.RemoteException;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.gui.dialogs.KontoAuswahlDialog;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.input.ButtonInput;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.system.OperationCanceledException;
import de.willuhn.logging.Logger;
import de.willuhn.util.I18N;

/**
 * Vorkonfigurierter DialogInput fuer eine Konto-Auswahl,
 * @author willuhn
 */
public class KontoInput extends ButtonInput
{
  private I18N i18n   = null;
  
  private DBIterator konten = null;
  private Konto konto       = null;
  private Text text         = null;
  
  private boolean inProgress = false;

  /**
   * ct.
   * @param konto
   * @throws RemoteException
   */
  public KontoInput(Konto konto) throws RemoteException
  {
    this(Settings.getActiveGeschaeftsjahr().getKontenrahmen().getKonten(),konto);
  }

  /**
   * @param list
   * @param konto
   */
  public KontoInput(DBIterator list, Konto konto)
  {
    this.i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
    this.konten = list;
    this.konto = konto;
    setComment("");
    
    // Listener fuer den Button
    this.addButtonListener(new Listener()
    {
      public void handleEvent(Event event)
      {
        KontoAuswahlDialog d = new KontoAuswahlDialog(konten,KontoAuswahlDialog.POSITION_MOUSE);
        try
        {
          inProgress = true;
          setValue((Konto) d.open());
        }
        catch (OperationCanceledException oce)
        {
          // ignore
        }
        catch (Exception e)
        {
          Logger.error("error while displaying dialog",e);
        }
        finally
        {
          inProgress = false;
        }
      }
    });
    
    // Listener fuer das Text-Eingabe-Feld.
    this.addListener(new Listener()
    {
      public void handleEvent(Event event)
      {
        if (inProgress || text == null || text.isDisposed())
          return; // Damit der Listener nicht doppelt ausgeloest wird, wenn's ueber den Button kommt

        String s = text.getText();
        if (s == null || s.length() == 0)
        {
          setValue(null);
          return;
        }
        
        try
        {
          DBIterator dbi = Settings.getActiveGeschaeftsjahr().getKontenrahmen().getKonten();
          dbi.addFilter("kontonummer = ?", s);
          if (!dbi.hasNext())
          {
            GUI.getView().setErrorText(i18n.tr("Das Konto {0} wurde nicht gefunden",s));
            return;
          }
          setValue((Konto) dbi.next());
        }
        catch (RemoteException e)
        {
          Logger.error("unable to load konto",e);
        }
      }
    });
  }

  /**
   * @see de.willuhn.jameica.gui.input.ButtonInput#getClientControl(org.eclipse.swt.widgets.Composite)
   */
  public Control getClientControl(Composite parent)
  {
    if (text != null)
      return text;

    this.text = GUI.getStyleFactory().createText(parent);
    setValue(konto);
    return this.text;
  }

  /**
   * @see de.willuhn.jameica.gui.input.Input#getValue()
   */
  public Object getValue()
  {
    return konto;
  }

  /**
   * @see de.willuhn.jameica.gui.input.Input#setValue(java.lang.Object)
   */
  public void setValue(Object value)
  {
    this.konto = (Konto) value;
    if (text != null && !text.isDisposed())
    {
      try
      {
        Geschaeftsjahr jahr = Settings.getActiveGeschaeftsjahr();
        text.setText(this.konto == null ? "" : this.konto.getKontonummer());
        setComment(this.konto == null ? "" : i18n.tr("Saldo: {0} {1} [{2}]",new String[]{Settings.DECIMALFORMAT.format(konto.getSaldo(jahr)), jahr.getMandant().getWaehrung(), konto.getName()}));
      }
      catch (RemoteException e)
      {
        Logger.error("unable to set widget text",e);
      }
    }
  }

}


/*********************************************************************
 * $Log: KontoInput.java,v $
 * Revision 1.5  2011/08/08 10:44:36  willuhn
 * @C compiler warnings
 *
 * Revision 1.4  2010-06-01 16:37:22  willuhn
 * @C Konstanten von Fibu zu Settings verschoben
 * @N Systemkontenrahmen nach expliziter Freigabe in den Einstellungen aenderbar
 * @C Unterscheidung zwischen canChange und isUserObject in UserObject
 * @C Code-Cleanup
 * @R alte CVS-Logs entfernt
 *
 * Revision 1.3  2007/01/04 12:58:50  willuhn
 * @B wrong type for kontonummer
 *
 * Revision 1.2  2006/05/29 17:30:26  willuhn
 * @N a lot of debugging
 *
 * Revision 1.1  2005/10/06 17:27:59  willuhn
 * @N KontoInput
 * @N Einstellungen
 *
 *********************************************************************/