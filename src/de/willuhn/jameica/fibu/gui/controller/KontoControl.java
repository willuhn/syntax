/**********************************************************************
 *
 * Copyright (c) 2004 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/
package de.willuhn.jameica.fibu.gui.controller;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import de.willuhn.datasource.BeanUtil;
import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.Anfangsbestand;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Kontenrahmen;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Kontoart;
import de.willuhn.jameica.fibu.rmi.Kontotyp;
import de.willuhn.jameica.fibu.rmi.Steuer;
import de.willuhn.jameica.gui.AbstractControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.input.Input;
import de.willuhn.jameica.gui.input.LabelInput;
import de.willuhn.jameica.gui.input.SelectInput;
import de.willuhn.jameica.gui.input.TextInput;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Controller fuer das Konto.
 */
public class KontoControl extends AbstractControl
{
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

	// Fach-Objekte
	private Konto konto			= null;

	// Eingabe-Felder
	private Input name			       = null;
	private TextInput kontonummer  = null;
	private SelectInput steuer		 = null;
	private Input kontoart		     = null;
  private Input kontotyp         = null;
  private Input saldo            = null;
  private Input anfangsbestand   = null;
  
  private Boolean inCurrentKontenrahmen = null;
  

  /**
   * @param view
   */
  public KontoControl(AbstractView view)
  {
    super(view);
  }

	/**
	 * Liefert das Konto.
   * @return Liefert das Konto.
   * @throws RemoteException
   */
  public Konto getKonto() throws RemoteException
	{
		if (konto != null)
			return konto;
		
		konto = (Konto) getCurrentObject();
		if (konto != null)
			return konto;

		// Neues Konto erstellen und gleich dem aktuellen Geschaeftsjahr zuweisen
		konto = (Konto) Settings.getDBService().createObject(Konto.class,null);
		
    Geschaeftsjahr jahr = Settings.getActiveGeschaeftsjahr();
    if (jahr != null)
    {
      konto.setKontenrahmen(jahr.getKontenrahmen());
      konto.setMandant(jahr.getMandant());
    }
    
    // Konto-Art definieren wir mit der haeufigsten Konto-Art vor
    konto.setKontoArt((Kontoart) Settings.getDBService().createObject(Kontoart.class,""+Kontoart.KONTOART_AUFWAND));
    
		return konto;
	}
  
  /**
   * Liefert true, wenn das Konto Teil des Kontenrahmens des aktiven Geschaeftsjahres ist.
   * @return true, wenn das Konto Teil des Kontenrahmens des aktiven Geschaeftsjahres ist.
   * @throws RemoteException
   */
  private boolean inCurrentKontenrahmen() throws RemoteException
  {
    // Damit wir das nicht immer wieder machen muessen.
    // Ein Konto aendert seinen Kontenrahmen ja nicht.
    if (this.inCurrentKontenrahmen != null)
      return this.inCurrentKontenrahmen;
    
    Geschaeftsjahr jahr = Settings.getActiveGeschaeftsjahr();
    if (jahr == null)
      return false;
    
    Kontenrahmen k1 = this.getKonto().getKontenrahmen();
    Kontenrahmen k2 = jahr.getKontenrahmen();
    this.inCurrentKontenrahmen = BeanUtil.equals(k1,k2);
    return this.inCurrentKontenrahmen;
  }

  /**
   * Liefert ein Anzeige-Feld fuer den Saldo.
   * @return Anzeige-Feld.
   * @throws RemoteException
   */
  public Input getSaldo() throws RemoteException
  {
    if (this.saldo != null)
      return this.saldo;
    
    // Saldo basiert auf dem aktuellen Geschaeftsjahr und damit auf dem Kontenrahmen
    if (this.inCurrentKontenrahmen())
    {
      Geschaeftsjahr jahr = Settings.getActiveGeschaeftsjahr();
      saldo = new LabelInput(Settings.DECIMALFORMAT.format(getKonto().getSaldo(jahr)));
      saldo.setComment(jahr.getMandant().getWaehrung());
    }
    else
    {
      saldo = new LabelInput("");
    }
    return saldo;
  }
  
  /**
   * Liefert ein Anzeige-Feld fuer den Anfangsbestand.
   * @return Anzeige-Feld.
   * @throws RemoteException
   */
  public Input getAnfangsbestand() throws RemoteException
  {
    if (this.anfangsbestand != null)
      return this.anfangsbestand;
    
    // Saldo basiert auf dem aktuellen Geschaeftsjahr und damit auf dem Kontenrahmen
    if (this.inCurrentKontenrahmen())
    {
      Geschaeftsjahr jahr = Settings.getActiveGeschaeftsjahr();
      Anfangsbestand ab = getKonto().getAnfangsbestand(jahr);
      anfangsbestand = new LabelInput(Settings.DECIMALFORMAT.format(ab != null ? ab.getBetrag() : 0.0d));
      anfangsbestand.setComment(jahr.getMandant().getWaehrung());
    }
    else
    {
      anfangsbestand = new LabelInput("");
    }
    return anfangsbestand;
  }
  
	/**
	 * Liefert das Eingabe-Feld fuer den Namen.
   * @return Eingabe-Feld.
   * @throws RemoteException
   */
  public Input getName() throws RemoteException
	{
		if (name != null)
			return name;
		name = new TextInput(getKonto().getName(),255);
		name.setMandatory(true);
		name.setEnabled(getKonto().canChange());
		return name;
	}

	/**
	 * Liefert das Eingabe-Feld fuer die Kontonummer.
	 * @return Eingabe-Feld.
	 * @throws RemoteException
	 */
	public Input getKontonummer() throws RemoteException
	{
		if (kontonummer != null)
			return kontonummer;
		kontonummer = new TextInput(getKonto().getKontonummer(),6);
		kontonummer.setValidChars("0123456789");
		kontonummer.setMandatory(true);
		kontonummer.setEnabled(getKonto().canChange());
		return kontonummer;
	}

	/**
	 * Liefert das Eingabe-Feld fuer die Steuer.
	 * @return Eingabe-Feld.
	 * @throws RemoteException
	 */
	public Input getSteuer() throws RemoteException
	{
		if (steuer != null)
			return steuer;

    DBIterator list = Settings.getDBService().createList(Steuer.class);
    Kontenrahmen kr = this.getKonto().getKontenrahmen();
    List<Steuer> found = new ArrayList<Steuer>();
    while (list.hasNext())
    {
      Steuer s = (Steuer) list.next();
      Konto k = s.getSteuerKonto();
      if (k == null)
        continue;
      if (BeanUtil.equals(k.getKontenrahmen(),kr))
        found.add(s);
    }
    steuer = new SelectInput(found,getKonto().getSteuer());
    steuer.setPleaseChoose("<" + i18n.tr("Keine Steuer") + ">");

    // Deaktivieren, wenn Konto nicht steuerpflichtig
    Kontoart ka = getKonto().getKontoArt();

    steuer.setEnabled(getKonto().canChange() && ka != null && ka.isSteuerpflichtig());
		return steuer;
	}

	/**
	 * Liefert das Eingabe-Feld fuer die Konto-Art.
   * @return Eingabe-Feld.
   * @throws RemoteException
   */
  public Input getKontoart() throws RemoteException
	{
		if (kontoart != null)
			return kontoart;

		Kontoart ka = getKonto().getKontoArt();
		kontoart = new SelectInput(Settings.getDBService().createList(Kontoart.class),ka);
    kontoart.setEnabled(getKonto().canChange());
		kontoart.setMandatory(true);
    kontoart.addListener(new Listener() {
      public void handleEvent(Event event)
      {
        Kontoart k = (Kontoart) kontoart.getValue();
        if (k == null)
          return;
        try
        {
          // Wenn die Konto-Art steuerpflichtig, muessen wir den Steuersatz freischalten
          getSteuer().setEnabled(k.isSteuerpflichtig() && getKonto().canChange());

          // Wenn es ein Steuerkonto ist, dann Kontotyp freischalten
          getKontotyp().setEnabled(k.getKontoArt() == Kontoart.KONTOART_STEUER && getKonto().canChange());
        }
        catch (RemoteException e)
        {
          Logger.error("unable to update tax field",e);
          GUI.getStatusBar().setErrorText(i18n.tr("Fehler beim Aktualisieren des Steuersatzes"));
        }
      }
    });
    
		return kontoart;
	}

  /**
   * Liefert das Eingabe-Feld fuer den Konto-Typ.
   * @return Eingabe-Feld.
   * @throws RemoteException
   */
  public Input getKontotyp() throws RemoteException
  {
    if (kontotyp != null)
      return kontotyp;

    Kontotyp kt = getKonto().getKontoTyp();
    kontotyp = new SelectInput(Settings.getDBService().createList(Kontotyp.class),kt);

    Kontoart ka = getKonto().getKontoArt();
    kontotyp.setEnabled(getKonto().canChange() && ka != null && ka.getKontoArt() == Kontoart.KONTOART_STEUER);
    return kontotyp;
  }

  /**
   * Speichert das Konto.
   */
  public void handleStore()
  {
    try {
      if (!getKonto().canChange())
        throw new ApplicationException(i18n.tr("Konto ist ein System-Konto und darf nicht geändert werden"));

      getKonto().setName((String) getName().getValue());
      getKonto().setKontonummer((String) getKontonummer().getValue());
      Kontoart art = (Kontoart) getKontoart().getValue();
      getKonto().setKontoArt(art);
      if (art.isSteuerpflichtig())
        getKonto().setSteuer((Steuer) getSteuer().getValue());
      else
        getKonto().setSteuer(null);

      if (art.getKontoArt() == Kontoart.KONTOART_STEUER)
        getKonto().setKontoTyp((Kontotyp) getKontotyp().getValue());
      else
        getKonto().setKontoTyp(null);
      
      // und jetzt speichern wir.
      getKonto().store();
      GUI.getStatusBar().setSuccessText(i18n.tr("Konto gespeichert."));
    }
    catch (ApplicationException e1)
    {
      GUI.getView().setErrorText(e1.getLocalizedMessage());
    }
    catch (Exception e)
    {
			Logger.error("unable to store konto",e);
      GUI.getView().setErrorText("Fehler beim Speichern des Kontos.");
    }
    
  }
}

/*********************************************************************
 * $Log: KontoControl.java,v $
 * Revision 1.34  2011/12/19 21:42:01  willuhn
 * @N BUGZILLA 1166
 *
 * Revision 1.33  2011/12/08 22:34:12  willuhn
 * @N BUGZILLA 1157
 *
 * Revision 1.32  2010-11-30 23:10:57  willuhn
 * @C Nur Zahlen in der Kontonummer zulassen
 *
 * Revision 1.31  2010-08-30 14:36:11  willuhn
 * *** empty log message ***
 *
 * Revision 1.30  2010/06/07 16:34:22  willuhn
 * @N Code zum Aendern der UST-Voranmelde-Kennzeichen im Steuersatz
 *
 * Revision 1.29  2010/06/01 16:37:22  willuhn
 * @C Konstanten von Fibu zu Settings verschoben
 * @N Systemkontenrahmen nach expliziter Freigabe in den Einstellungen aenderbar
 * @C Unterscheidung zwischen canChange und isUserObject in UserObject
 * @C Code-Cleanup
 * @R alte CVS-Logs entfernt
 *
 * Revision 1.28  2010/02/08 15:39:48  willuhn
 * @N Option "Geschaeftsjahr abschliessen" in Kontextmenu des Geschaeftsjahres
 * @N Zweispaltiges Layout in Mandant-Details - damit bleibt mehr Platz fuer die Reiter unten drunter
 * @N Anzeige von Pflichtfeldern
 *
 * Revision 1.27  2009/07/03 10:52:18  willuhn
 * @N Merged SYNTAX_1_3_BRANCH into HEAD
 *
 * Revision 1.25  2006/01/02 15:18:29  willuhn
 * @N Buchungs-Vorlagen
 *
 * Revision 1.24  2006/01/02 01:54:07  willuhn
 * @N Benutzerdefinierte Konten
 *
 * Revision 1.23  2005/09/26 23:52:00  willuhn
 * *** empty log message ***
 *
 * Revision 1.22  2005/09/26 15:15:39  willuhn
 * *** empty log message ***
 *
 * Revision 1.21  2005/08/29 12:17:29  willuhn
 * @N Geschaeftsjahr
 *
 * Revision 1.20  2005/08/25 23:00:02  willuhn
 * *** empty log message ***
 *
 * Revision 1.19  2005/08/16 17:39:24  willuhn
 * *** empty log message ***
 *
 * Revision 1.18  2005/08/15 23:38:28  willuhn
 * *** empty log message ***
 *
 * Revision 1.17  2005/08/10 17:48:03  willuhn
 * @C refactoring
 *
 * Revision 1.16  2005/08/08 22:54:16  willuhn
 * @N massive refactoring
 *
 * Revision 1.15  2005/08/08 21:35:46  willuhn
 * @N massive refactoring
 *
 * Revision 1.14  2004/02/25 23:11:29  willuhn
 * *** empty log message ***
 *
 * Revision 1.13  2004/02/24 22:48:08  willuhn
 * *** empty log message ***
 *
 * Revision 1.12  2004/02/18 13:51:09  willuhn
 * *** empty log message ***
 *
 * Revision 1.11  2004/01/29 00:06:46  willuhn
 * *** empty log message ***
 *
 * Revision 1.10  2004/01/28 00:37:32  willuhn
 * *** empty log message ***
 *
 * Revision 1.9  2004/01/27 23:54:18  willuhn
 * *** empty log message ***
 *
 * Revision 1.8  2004/01/27 21:38:06  willuhn
 * @C refactoring finished
 *
 * Revision 1.7  2004/01/27 00:09:10  willuhn
 * *** empty log message ***
 *
 * Revision 1.6  2004/01/03 18:07:22  willuhn
 * @N Exception logging
 *
 * Revision 1.5  2003/12/15 19:08:04  willuhn
 * *** empty log message ***
 *
 * Revision 1.4  2003/12/12 01:28:07  willuhn
 * *** empty log message ***
 *
 * Revision 1.3  2003/12/11 21:00:35  willuhn
 * @C refactoring
 *
 * Revision 1.2  2003/12/10 23:51:53  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2003/12/05 17:11:58  willuhn
 * @N added GeldKonto, Kontoart
 *
 * Revision 1.1  2003/12/01 20:29:00  willuhn
 * @B filter in DBIteratorImpl
 * @N InputFelder generalisiert
 *
 **********************************************************************/