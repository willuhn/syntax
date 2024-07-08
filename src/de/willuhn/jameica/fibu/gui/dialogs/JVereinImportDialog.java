package de.willuhn.jameica.fibu.gui.dialogs;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.gui.action.BuchungNeu;
import de.willuhn.jameica.fibu.gui.input.KontoInput;
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Kontoart;
import de.willuhn.jameica.fibu.rmi.Steuer;
import de.willuhn.jameica.fibu.server.Math;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.dialogs.AbstractDialog;
import de.willuhn.jameica.gui.input.Input;
import de.willuhn.jameica.gui.input.LabelInput;
import de.willuhn.jameica.gui.util.ButtonArea;
import de.willuhn.jameica.gui.util.LabelGroup;
import de.willuhn.jameica.hbci.server.VerwendungszweckUtil.Tag;
import de.willuhn.jameica.messaging.StatusBarMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.system.BackgroundTask;
import de.willuhn.jameica.system.OperationCanceledException;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;
import de.willuhn.util.ProgressMonitor;

public class JVereinImportDialog extends AbstractDialog
{
	private I18N i18n;
	private KontoInput konto      = null;
	private LabelInput hinweis    = null;
	private de.jost_net.JVerein.rmi.Buchung[] buchungen = null;
	
	/**
	* ct.
	* @param context die zu importierenden Buchungen
	* @param position
	*/
	public JVereinImportDialog(Object context, int position) {
		super(position);
		
		if (context instanceof de.jost_net.JVerein.rmi.Buchung)
			buchungen = new de.jost_net.JVerein.rmi.Buchung[]{(de.jost_net.JVerein.rmi.Buchung)context};
        else if (context instanceof de.jost_net.JVerein.rmi.Buchung[])
        	buchungen = (de.jost_net.JVerein.rmi.Buchung[]) context;

		this.setSize(650,SWT.DEFAULT);
		
		i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
		this.setTitle(i18n.tr("JVerein Import"));
	}

	/**
	* @see de.willuhn.jameica.gui.dialogs.AbstractDialog#paint(org.eclipse.swt.widgets.Composite)
	*/
	protected void paint(Composite parent) throws Exception {
		LabelGroup group = new LabelGroup(parent,i18n.tr("JVerein Buchungen nach Syntax übernehmen"));
		group.addLabelPair("", getHinweis());
	    group.addLabelPair(i18n.tr("Gegenkonto"), getKonto());
	    
	    ButtonArea b = new ButtonArea(parent,2);
	    b.addButton(i18n.tr("Übernehmen"), new Action()
	    {
	      public void handleAction(Object context) throws ApplicationException
	      {
	    	close();
	        if (buchungen == null || buchungen.length == 0)
	          return;

	        try {
				Konto gegen = (Konto) getKonto().getValue();
				if(gegen == null)
		        	throw new ApplicationException(i18n.tr("Kein Gegenkonto gewählt"));
				Settings.SETTINGS.setAttribute("jvereinImport.gegenkonto." + Settings.getActiveGeschaeftsjahr().getID(),gegen.getID());
			} catch (RemoteException e) {
				throw new ApplicationException(i18n.tr("Fehler bei bestimmen des Gegenkontos"));
			}
	        
	        
	        // Wenn wir mehr als 1 Buchung haben, fuehren wir das
	        // im Hintergrund aus. 
	        Worker worker = new Worker(buchungen);
	        if (buchungen.length > 1)
	        {
	          Application.getController().start(worker);
	        }
	        else
	          worker.run(null);
	      }
	    });
	    b.addButton(i18n.tr("Abbrechen"), new Action()
	    {
	      public void handleAction(Object context) throws ApplicationException
	      {
	        throw new OperationCanceledException();
	      }
	    });
	    
	}

	/**
	* Liefert ein Eingabefeld fuer das Konto.
	* @return Eingabe-Feld.
	* @throws RemoteException
	*/
	private Input getKonto() throws RemoteException {
		if (konto != null)
			return konto;
		
		Geschaeftsjahr jahr = Settings.getActiveGeschaeftsjahr();
		DBIterator konten = jahr.getKontenrahmen().getKonten();
		konten.addFilter("(kontoart_id = " + Kontoart.KONTOART_GELD + " OR kontoart_id = " + Kontoart.KONTOART_PRIVAT + ")");
		
		Konto k = null;
		String id = Settings.SETTINGS.getString("jvereinImport.gegenkonto." + Settings.getActiveGeschaeftsjahr().getID(),null);
	    if (id != null && id.length() > 0)
	    	k = Settings.getDBService().createObject(Konto.class, id);
		konto = new KontoInput(konten,k);
		return konto;
	}

	/**
	* Liefert einen Hinweis-Text.
	* @return Hinweis-Text.
	*/
	private Input getHinweis() {
		 if (this.hinweis != null)
		   return this.hinweis;
		 this.hinweis = new LabelInput(i18n.tr("Die Buchungen werden in das aktive Geschäftsjahr übernommen.\n"+
				 								"Die Buchung wird auf das Konto mit der Nummer der \n"+
				 								"zugeordneten Buchungsart gebucht.\n"+
				 								"Alle Buchungen, auch bereits vorhandene, werden übernommen.\n\n"+
				 								
				 								"Bitte wählen sie hier das zu verwendende Gegenkonto"));
		 return this.hinweis;
	}

	@Override
	protected Object getData() throws Exception {
		return null;
	}
	
  /**
  * Damit koennen wir lange Vorgaenge ggf. im Hintergrund laufen lassen
  */
  private class Worker implements BackgroundTask
  {
    private boolean cancel = false;
    private List<de.jost_net.JVerein.rmi.Buchung> list = null;

    /**
     * ct.
     * @param jBuchungen
     */
    private Worker(de.jost_net.JVerein.rmi.Buchung[] list)
    {
    	this.list = Arrays.asList(list);
    }
    
    /**
     * @see de.willuhn.jameica.system.BackgroundTask#interrupt()
     */
    public void interrupt()
    {
      this.cancel = true;
    }

    /**
     * @see de.willuhn.jameica.system.BackgroundTask#isInterrupted()
     */
    public boolean isInterrupted()
    {
      return this.cancel;
    }

    /**
     * @see de.willuhn.jameica.system.BackgroundTask#run(de.willuhn.util.ProgressMonitor)
     */
    public void run(ProgressMonitor monitor) throws ApplicationException
    {
      try
      {
        int size = this.list.size();
        
        if (monitor != null)
          monitor.setStatusText(i18n.tr("Buche {0} Umsätze",Integer.toString(size)));

        double factor = 100d / size;
        
        int created = 0;
        int error   = 0;
        int skipped = 0;
        
        for (int i=0;i<size;++i)
        {
          de.jost_net.JVerein.rmi.Buchung u = list.get(i);
          
          if (monitor != null)
          {
            monitor.setPercentComplete((int)((i+1) * factor));
            monitor.log("  " + i18n.tr("Erstelle Buchung {0}",Integer.toString(i+1)));
          }

          Buchung buchung = null;
          try
          {
            
            buchung = createBuchung(u,size > 1);
            buchung.store();
            
            created++;
            
          }
          catch (ApplicationException ae)
          {
            // Wenn wir nur eine Buchung hatten und eine
            // ApplicationException, dann fehlen noch Eingaben
            // Da wir nur eine Buchung haben, oeffnen wir
            // die Erfassungsmaske.
            if (size == 1)
            {
              Application.getMessagingFactory().sendMessage(new StatusBarMessage(ae.getMessage(),StatusBarMessage.TYPE_ERROR));
              new BuchungNeu().handleAction(buchung);
              return;
            }
            
            if (monitor != null)
              monitor.log("    " + ae.getMessage());
            error++;
          }
          catch (Exception e)
          {
            Logger.error("unable to import umsatz",e);
            if (monitor != null)
              monitor.log("    " + i18n.tr("Fehler: {0}",e.getMessage()));
            error++;
          }
        }
        
        String text = i18n.tr("Umsatz importiert");
        if (size > 1)
          text = i18n.tr("{0} Umsätze importiert, {1} fehlerhaft, {2} bereits vorhanden", new String[]{Integer.toString(created),Integer.toString(error),Integer.toString(skipped)});
        
        Application.getMessagingFactory().sendMessage(new StatusBarMessage(text,StatusBarMessage.TYPE_SUCCESS));
        if (monitor != null)
        {
          monitor.setStatusText(text);
          monitor.setStatus(ProgressMonitor.STATUS_DONE);
        }
        
      }
      catch (Exception e)
      {
        Logger.error("error while importing objects",e);
        throw new ApplicationException(i18n.tr("Fehler beim Import der Umsätze"));
      }
    }

	private Buchung createBuchung(de.jost_net.JVerein.rmi.Buchung u, boolean b) throws ApplicationException, RemoteException {

		Geschaeftsjahr jahr =  Settings.getActiveGeschaeftsjahr();

		if(u.getBuchungsart() == null)
			throw new ApplicationException(i18n.tr("Buchung ist keiner Buchungsart zugeordnet"));
		
		int kontoNr = u.getBuchungsart().getNummer();
		
		DBIterator<Konto> kI = jahr.getKontenrahmen().getKonten();
		kI .addFilter("kontonummer = " + Integer.toString(kontoNr));
		if(!kI.hasNext())
			throw new ApplicationException(i18n.tr("Kein Konto zu Kontoart {0} gefunden",Integer.toString(kontoNr)));
		
		Konto konto = kI.next();
		
	    final Buchung buchung = (Buchung) Settings.getDBService().createObject(Buchung.class,null);
	    buchung.setGeschaeftsjahr(jahr);
	    buchung.setBelegnummer(buchung.getBelegnummer()); // Das erzeugt eine neue Belegnummer
	    buchung.setKommentar(u.getKommentar());
	    
	    buchung.setText(u.getZweck() + ", " + u.getName());
	    
	    Konto gegenKonto = (Konto) getKonto().getValue();
		if(gegenKonto == null)
			throw new ApplicationException(i18n.tr("Kein Gegenkonto gewählt"));
		   
        if(konto.getKontoArt().getKontoArt() == Kontoart.KONTOART_ERLOES && u.getBetrag() >= 0.01d) {
        	buchung.setSollKonto(gegenKonto);
    		buchung.setHabenKonto(konto);
        }
        else
        {
        	buchung.setSollKonto(konto);
    		buchung.setHabenKonto(gegenKonto);
        }
    	
			
		Steuer s = null;
		if(konto.getKontoArt().getKontoArt() == Kontoart.KONTOART_ERLOES || konto.getKontoArt().getKontoArt() == Kontoart.KONTOART_AUFWAND)
			s = konto.getSteuer();
		else if(gegenKonto.getKontoArt().getKontoArt() == Kontoart.KONTOART_ERLOES || gegenKonto.getKontoArt().getKontoArt() == Kontoart.KONTOART_AUFWAND)
			s = gegenKonto.getSteuer();
		buchung.setSteuerObject(s);
		
		double satz = 0;
		if(s != null)
			satz = s.getSatz();
		buchung.setSteuer(satz);
		
		double betrag = java.lang.Math.abs(u.getBetrag());
		
		//Netto und Brutto setzen
		buchung.setBruttoBetrag(betrag);
		buchung.setBetrag(new Math().netto(betrag,satz));
	    
	    Date date = u.getDatum();
	    if (date != null)
	      buchung.setDatum(date);

	    return buchung;
	}
  }
}
