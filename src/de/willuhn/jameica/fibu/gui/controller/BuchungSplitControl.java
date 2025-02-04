package de.willuhn.jameica.fibu.gui.controller;

import java.rmi.RemoteException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.gui.AbstractControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.input.DateInput;
import de.willuhn.jameica.gui.input.DecimalInput;
import de.willuhn.jameica.gui.input.Input;
import de.willuhn.jameica.gui.input.IntegerInput;
import de.willuhn.jameica.gui.input.LabelInput;
import de.willuhn.jameica.gui.input.TextInput;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;


/**
 * Controler für Splitbuchung
 * @author henken
 */
public class BuchungSplitControl extends AbstractControl
{
	
	private de.willuhn.jameica.system.Settings settings = new de.willuhn.jameica.system.Settings(BuchungSplitControl.class);

	  // Fachobjekte
		private Buchung buchung 		= null;

		// Eingabe-Felder
		private Input	text					   = null;
		private Input betrag        = null;
		private Input belegnummer		   = null;

	  private LabelInput summe				    = null;
	  
	  private DateInput datum               = null;
	  
	  private I18N i18n;

	  /**
	   * @param view
	   */
	  public BuchungSplitControl(AbstractView view)
	  {
	    super(view);
	    i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
	  }

		/**
		 * Liefert die Buchung.
	   * @return die Buchung.
	   * @throws RemoteException
	   */
	  public Buchung getBuchung() throws RemoteException
	  {
			if (buchung != null)
				return buchung;
			
			buchung = (Buchung) getCurrentObject();
			if (buchung != null)
				return buchung;
			
			buchung = (Buchung) Settings.getDBService().createObject(Buchung.class,null);
			buchung.setGeschaeftsjahr(Settings.getActiveGeschaeftsjahr());
			return buchung;
			
		}
		/**
		 * Liefert das Eingabe-Feld fuer den Buchungstext.
	   * @return Eingabe-Feld.
	   * @throws RemoteException
	   */
	  public Input getText() throws RemoteException
		{
			if (text != null)
				return text;
			
			text = new TextInput(getBuchung().getText());
			text.setEnabled(!getBuchung().getGeschaeftsjahr().isClosed());
	    text.setMandatory(true);
			return text;
		}

	  /**
	   * Liefert das Eingabe-Feld fuer den Kommentar.
	   * @return Eingabe-Feld.
	   * @throws RemoteException
	   */
	  public Input getBetrag() throws RemoteException
	  {
	    if (betrag != null)
	      return betrag;
	    betrag = new DecimalInput(getBuchung().getBetrag(), Settings.DECIMALFORMAT);
	    betrag.setComment(Settings.getActiveGeschaeftsjahr().getMandant().getWaehrung());
	    betrag.setEnabled(!getBuchung().getGeschaeftsjahr().isClosed());
	    return betrag;
	  }
	  
	  /**
		 * Liefert das Eingabe-Feld fuer die Belegnummer.
		 * @return Eingabe-Feld.
		 * @throws RemoteException
		 */
		public Input getBelegnummer() throws RemoteException
		{
			if (belegnummer != null)
				return belegnummer;
			
			belegnummer = new IntegerInput(getBuchung().getBelegnummer());
			belegnummer.setMandatory(true);
			belegnummer.setEnabled(!getBuchung().getGeschaeftsjahr().isClosed());
			return belegnummer;
		}

		/**
		 * Liefert das Eingabe-Feld fuer das Datum.
	   * @return Eingabe-Feld.
	   * @throws RemoteException
	   */
	  public DateInput getDatum() throws RemoteException
		{
			if (datum != null)
				return datum;

	    Date d = getBuchung().getDatum();
	    if (d == null)
	    {
	      // BUGZILLA 122
	      String s = settings.getString("buchung.date.last." + Settings.getActiveGeschaeftsjahr().getID(),null);
	      if (s != null && s.length() > 0)
	      {
	        try
	        {
	          d = Settings.DATEFORMAT.parse(s);
	        }
	        catch (ParseException e)
	        {
	          Logger.error("unable to parse date",e);
	        }
	      }
	      else
	      {
	        d = new Date();
	      }
	    }

	    datum = new DateInput(d,Settings.CUSTOM_DATEFORMAT);
	    datum.setTitle(i18n.tr("Datum"));
	    datum.setText(i18n.tr("Bitte wählen Sie das Datum für diese Buchung"));
	    datum.setMandatory(true);
	    datum.setComment("");

	    
	    // Mit dem Listener ergaenzen wir den Wochentag als Kommentar
	    datum.addListener(new Listener() {
	      public void handleEvent(Event event)
	      {
	        Date d = (Date) datum.getValue();

	        if (d == null)
	          return;

	        // Wochentag ergaenzen
	        Calendar cal = Calendar.getInstance(Application.getConfig().getLocale());
	        cal.setTime(d);
	        int i = cal.get(Calendar.DAY_OF_WEEK) - 1;
	        if (i < 0 || i >= Settings.WEEKDAYS.length)
	          return;

	        datum.setComment(i18n.tr(Settings.WEEKDAYS[i]));
	      }
	    
	    });
	    datum.setEnabled(!getBuchung().getGeschaeftsjahr().isClosed());
	    return datum;
	  }
	  
		/**
		 * Liefert das Eingabe-Feld fuer die Summe.
		 * @return Eingabe-Feld.
		 * @throws RemoteException
		 */
		public LabelInput getSumme() throws RemoteException
		{
			if (summe != null)
				return summe;
			
			//Betrag aller bisherigen SplitBuchungen berechnen
	        double betrag = 0;
	    	Buchung hauptBuchung = getBuchung();
	        DBIterator i = hauptBuchung.getSplitBuchungen();
	        while(i.hasNext())
	        {
	        	Buchung b = (Buchung) i.next();
		       betrag +=  b.getBruttoBetrag();
	        }
			summe = new LabelInput(Settings.DECIMALFORMAT.format(betrag));
			summe.setComment(buchung.getGeschaeftsjahr().getMandant().getWaehrung());
			return summe;
		}
		
	  /**
	   * Speichert die Buchung.
	   */
	  public void handleStore()
	  {
	    try {
	      //Einige Werte anpassen
	      getBuchung().setText((String)getText().getValue());
	      getBuchung().setBetrag(((Double)getBetrag().getValue()).doubleValue());
	      
	      // und jetzt speichern wir.
		  getBuchung().store();
	      GUI.getStatusBar().setSuccessText(i18n.tr("Buchung Nr. {0} gespeichert.",""+getBuchung().getBelegnummer()));
	    }
	    catch (ApplicationException e1)
	    {
	      GUI.getView().setErrorText(e1.getLocalizedMessage());
	    }
	    catch (Throwable t)
	    {
				Logger.error("unable to store buchung",t);
	      GUI.getView().setErrorText("Fehler beim Speichern der Buchung.");
	    }
	    
	  }
}
