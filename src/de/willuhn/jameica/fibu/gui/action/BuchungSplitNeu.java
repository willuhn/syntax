package de.willuhn.jameica.fibu.gui.action;

import java.rmi.RemoteException;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.fibu.rmi.HilfsBuchung;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Action zum Splitten einer Buchung.
 * @author henken
 */
public class BuchungSplitNeu implements Action
{

  /**
   * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
   */
  public void handleAction(Object context) throws ApplicationException
  {
	Buchung buchung = null;
	Buchung hauptBuchung = null;
	//Hilfsbuchungen können nicht gesplittet werden
    if (context != null && !(context instanceof HilfsBuchung))
    {
    	try
        {
	      if (context instanceof Buchung) {
	    	  if(((Buchung)context).getSplitHauptBuchung() != null)
	    		  hauptBuchung = ((Buchung)context).getSplitHauptBuchung();
	    	  else
	    		  hauptBuchung = (Buchung) context;
	      }
        }
        catch (RemoteException e)
        {
          Logger.error("unable to load hauptbuchung",e);
          I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
          throw new ApplicationException(i18n.tr("Fehler beim Laden der Buchung"));
        }
		try {
			//Einige Werte der Hauptbuchung übernehmen
			buchung = (Buchung) Settings.getDBService().createObject(Buchung.class,null);
	        buchung.setGeschaeftsjahr(Settings.getActiveGeschaeftsjahr());
	        
	        buchung.setBelegnummer(hauptBuchung.getBelegnummer());
	        buchung.setDatum(hauptBuchung.getDatum());
	        buchung.setHibiscusUmsatzID(hauptBuchung.getHibiscusUmsatzID());
	        buchung.setText(hauptBuchung.getText());
	        buchung.setHabenKonto(hauptBuchung.getHabenKonto());
	        buchung.setSollKonto(hauptBuchung.getSollKonto());
	        buchung.setSteuer(hauptBuchung.getSteuer());
	        buchung.setSteuerObject(hauptBuchung.getSteuerObject());
	        buchung.setGeprueft(hauptBuchung.isGeprueft());
	        
	        //Betrag aller bisherigen SplitBuchungen berechnen, daraus den Restbetrag errechnen
	        double betrag = 0d;
	        DBIterator i = hauptBuchung.getSplitBuchungen();
	        while(i.hasNext())
	        {
	        	Buchung b = (Buchung) i.next();
		       betrag +=  b.getBruttoBetrag();
	        }
	        double betrag_alt = 0;
	        //Hier müssen wir bei neuen Splitbuchungen den Bruttobetrag nehmen, bei vorhanden den Netto Betrag da sonst die Splitbuchungen mit reingerechnet werden
	        if(!hauptBuchung.getSplitBuchungen().hasNext())
	        	betrag_alt = hauptBuchung.getBruttoBetrag();
	        else betrag_alt = hauptBuchung.getBetrag();
	        buchung.setBruttoBetrag(betrag_alt-betrag);
	        
	        //Buchung als Splitbuchung setzen
	        buchung.setSplitBuchung(hauptBuchung.getID());
		}
		catch (Exception e)
	    {
	      Logger.error("unable to create buchung",e);
	    }
    }
    GUI.startView(de.willuhn.jameica.fibu.gui.views.BuchungNeu.class,buchung);
  }
}
