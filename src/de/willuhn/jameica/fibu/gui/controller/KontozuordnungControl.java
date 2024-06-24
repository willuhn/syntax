package de.willuhn.jameica.fibu.gui.controller;

import java.rmi.RemoteException;

import org.apache.commons.lang.StringUtils;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.gui.input.KontoInput;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Kontoart;
import de.willuhn.jameica.fibu.rmi.Kontozuordnung;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.jameica.gui.AbstractControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.input.Input;
import de.willuhn.jameica.gui.input.TextInput;
import de.willuhn.jameica.messaging.StatusBarMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Controller für die Zuordnung von Hibiscus-Konten zu SynTAX-Konten.
 */
public class KontozuordnungControl extends AbstractControl
{
	  private final static I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

		private Kontozuordnung buchung 		= null;

	  private Input bezeichnung         = null;
	  private KontoInput kontoAuswahl   = null;
	  private de.willuhn.jameica.hbci.gui.input.KontoInput HbKontoAuswahl = null;
	  
	  /**
	   * @param view
	   */
	  public KontozuordnungControl(AbstractView view)
	  {
	    super(view);
	  }

		/**
		 * Liefert die Buchung.
	   * @return die Buchung.
	   * @throws RemoteException
	   */
	  public Kontozuordnung getBuchung() throws RemoteException
		{
			if (this.buchung != null)
				return this.buchung;
			
			if(getCurrentObject() instanceof Kontozuordnung)
				this.buchung = (Kontozuordnung) getCurrentObject();
			
			if (this.buchung != null)
				return this.buchung;
			
			this.buchung = (Kontozuordnung) Settings.getDBService().createObject(Kontozuordnung.class,null);
	    
	    // Den Parameter geben wir automatisch vor.
		if(getCurrentObject() instanceof Mandant)
			this.buchung.setMandant((Mandant) getCurrentObject());

		return this.buchung;
		}

	  /**
		 * Liefert das Eingabe-Feld zur Auswahl des SollKontos.
	   * @return Eingabe-Feld.
	   * @throws RemoteException
	   */
	  public KontoInput getKontoAuswahl() throws RemoteException
		{
			if (this.kontoAuswahl != null)
				return this.kontoAuswahl;
	    Geschaeftsjahr jahr = Settings.getActiveGeschaeftsjahr();
	    DBIterator list = jahr.getKontenrahmen().getKonten();
	    list.addFilter("(kontoart_id = " + Kontoart.KONTOART_GELD +" OR kontoart_id = " + Kontoart.KONTOART_PRIVAT+")");
	    this.kontoAuswahl = new KontoInput(list, getBuchung().getKonto());
	    this.kontoAuswahl.setName(i18n.tr("SynTAX-Geldkonto"));
	    return this.kontoAuswahl;
	  }
	  
	  /**
		 * Liefert das Eingabe-Feld zur Auswahl des HbKontos.
	   * @return Eingabe-Feld.
	   * @throws RemoteException
	   */
	  public de.willuhn.jameica.hbci.gui.input.KontoInput getHbKontoAuswahl() throws RemoteException
		{
			if (this.HbKontoAuswahl != null)
				return this.HbKontoAuswahl;

	    this.HbKontoAuswahl = new de.willuhn.jameica.hbci.gui.input.KontoInput(getBuchung().getHbKonto(), null);
	    this.HbKontoAuswahl.setName(i18n.tr("Hibiscus-Konto"));
	    return this.HbKontoAuswahl;
	  }

	  /**
	   * Liefert das Eingabe-Feld fuer die Bezeichnung der Vorlage.
	   * @return Eingabe-Feld.
	   * @throws RemoteException
	   */
	  public Input getBezeichnung() throws RemoteException
	  {
	    if (this.bezeichnung != null)
	      return this.bezeichnung;
	    
	    this.bezeichnung = new TextInput(getBuchung().getName());
	    this.bezeichnung.setName(i18n.tr("Bezeichnung der Zuordnung"));
	    this.bezeichnung.setMandatory(true);
	    return this.bezeichnung;
	  }
	  	  
	  /**
	   * Speichert die Buchung.
	   * @param startNew legt fest, ob danach sofort der Dialog zum Erfassen einer neuen Buchung geoeffnet werden soll.
	   */
	  public void handleStore(boolean startNew)
	  {
	    try
	    {
        getBuchung().setName(StringUtils.trimToNull((String)getBezeichnung().getValue()));

        Konto k = (Konto) getKontoAuswahl().getValue();
    	  if (k == null)
    	    throw new ApplicationException(i18n.tr("Bitte wählen Sie ein SynTAX-Geldkonto aus."));
    	  
        if(k.getKontoArt().getKontoArt() != Kontoart.KONTOART_GELD && k.getKontoArt().getKontoArt() != Kontoart.KONTOART_PRIVAT)
          throw new ApplicationException(i18n.tr("Das Konto muss ein Geldkonto sein!"));

        getBuchung().setKonto(k);
	      
	      final de.willuhn.jameica.hbci.rmi.Konto hk = (de.willuhn.jameica.hbci.rmi.Konto) this.getHbKontoAuswahl().getValue();
        if (hk == null)
          throw new ApplicationException(i18n.tr("Bitte wählen Sie ein Hibiscus-Konto aus."));

        getBuchung().setHbKonto(hk);
	      
				getBuchung().store();
				Application.getMessagingFactory().sendMessage(new StatusBarMessage(i18n.tr("Kontozuordnung gespeichert"),StatusBarMessage.TYPE_SUCCESS));
	    }
	    catch (ApplicationException ae)
	    {
	      Application.getMessagingFactory().sendMessage(new StatusBarMessage(ae.getMessage(),StatusBarMessage.TYPE_ERROR));
	    }
	    catch (Exception e)
	    {
	      Logger.error("unable to store kontozuordnung",e);
	      Application.getMessagingFactory().sendMessage(new StatusBarMessage(i18n.tr("Fehler beim Speichern der Kontozuordnung."),StatusBarMessage.TYPE_ERROR));
	    }
	    
	  }
	}
