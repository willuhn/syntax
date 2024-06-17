/**********************************************************************
 *
 * Copyright (c) 2024 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/

import java.util.HashMap;
import java.util.Map;
import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.rmi.Buchung;
import de.willuhn.jameica.fibu.rmi.Buchungstemplate;
import de.willuhn.jameica.fibu.rmi.Geschaeftsjahr;
import de.willuhn.jameica.fibu.rmi.Konto;
import de.willuhn.jameica.fibu.rmi.Kontotyp;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.jameica.fibu.rmi.Steuer;
import de.willuhn.jameica.fibu.server.AbstractUpdate;
import de.willuhn.jameica.fibu.server.Math;
import de.willuhn.logging.Logger;
import de.willuhn.sql.version.UpdateProvider;
import de.willuhn.util.ApplicationException;

/**
 * Update der Steuersätze auf neues Datenbank-Modell.
 */
public class mckoiUpdate0011 extends AbstractUpdate
{

  /**
   * @see de.willuhn.sql.version.Update#execute(de.willuhn.sql.version.UpdateProvider)
   */
  @Override
  public void execute(UpdateProvider provider) throws ApplicationException
  {
	  
    this.execute(db -> {
    	
    	//Neue Spalte steuer_id anlegen
    	db.executeUpdate("ALTER TABLE buchung ADD steuer_id INT(10) NULL", null);
    	db.executeUpdate("ALTER TABLE buchung ADD CONSTRAINT fk_buchung_steuer FOREIGN KEY (steuer_id) REFERENCES steuer (id)", null);
    	
    	//Um die alten Steuersätze holen zu können kopieren wir die Spalte vorläufig (einen Umbenenn befehl der bei McKoi funktioniert habe ich nicht gefunden)
    	db.executeUpdate("ALTER TABLE buchung ADD steuer_alt double NULL", null);
    	db.executeUpdate("UPDATE buchung SET steuer_alt = steuer", null);
    	
    	db.executeUpdate("ALTER TABLE buchungstemplate add steuer_id INT(10) NULL", null);
    	db.executeUpdate("ALTER TABLE buchungstemplate add CONSTRAINT fk_buchungstemplate_steuer FOREIGN KEY (steuer_id) REFERENCES steuer (id)", null);
    	
        final Math math = new Math();
        
        final DBIterator<Mandant> list = db.createList(Mandant.class);
        while (list.hasNext())
        {
	        final Mandant m = list.next();
	        Logger.info("migriere Mandant " + m.getFirma());
	        
	        //Alle vorhandenen Steuersätze holen
	    	final Map<String,Steuer> steuerMap = new HashMap<>();
	    	DBIterator<Steuer> steuern = db.createList(Steuer.class);
	    	
	    	steuern.addFilter("mandant_id is null or mandant_id = "+m.getID());
	        while (steuern.hasNext())
	        {
	          final Steuer s = (Steuer) steuern.next();
	          //über den Umweg des Steuerkontos prüfen wir, ob es der richtige Kontenrahmen ist
	          if(!s.getSteuerKonto().getKontenrahmen().equals(((Geschaeftsjahr)m.getGeschaeftsjahre().next()).getKontenrahmen()))
	        	  continue;
	          steuerMap.put(Double.toString(s.getSatz())+s.getSteuerKonto().getKontoTyp().getKontoTyp(),s);
	        }
	        
	        final DBIterator<Geschaeftsjahr> jahre = m.getGeschaeftsjahre();
	        while(jahre.hasNext())
	        {
	              final Geschaeftsjahr jahr = jahre.next();
			      DBIterator<Buchung> buchungen = jahr.getHauptBuchungen(true);
			      while (buchungen.hasNext())
			      {
			        final Buchung b = buchungen.next();
			        final Konto soll = b.getSollKonto();
		            final Konto haben = b.getHabenKonto();
		            
		            final Steuer ss = soll.getSteuer();
		            final Steuer sh = haben.getSteuer();
		            
		            final Steuer steuer = ss != null ? ss : sh;
		            
		            if (steuer == null)
		              continue;
		            
			        final double steuerSatz = (Double)b.getAttribute("steuer_alt");
			        
			        if(steuerSatz < 0.1d)
			        	continue;
			        
			        if (math.abs(steuerSatz - steuer.getSatz()) < 0.01)
		            {
		              // Wir können die Steuer direkt in der Buchung speichern
			          //Da die Buchungsengine nicht gestartet ist, fürhen wir hier SQL direkt durch
		              db.executeUpdate("UPDATE buchung set steuer_id = ? WHERE id = ?", new Object[] {new Integer(steuer.getID()),new Integer(b.getID())});
		              
		              //b.setSteuer(steuer);
		              //b.store();
		              Logger.debug("Speichere Steuersatz " + steuerSatz + " in Buchung " + b.getBelegnummer());
		            }
		            else
		            {
		            	final Konto sk = steuer.getSteuerKonto();
		                final String key = Double.toString(math.round(steuerSatz)) + sk.getKontoTyp().getKontoTyp();

		                Steuer sNew = steuerMap.get(key);
		                
		                // Steuersatz neu anlegen, falls wir den noch nicht haben
		                if (sNew == null)
		                {
		                  sNew = db.createObject(Steuer.class,null);
		                  sNew.setMandant(m);//oder global?
		                  if(sk.getKontoTyp().getKontoTyp() == Kontotyp.KONTOTYP_AUSGABE)
		                	  sNew.setName("Vorsteuer "+steuerSatz+"%");
		                  else sNew.setName("Umsatzsteuer "+steuerSatz+"%");
		                  sNew.setSatz(steuerSatz);
		                  sNew.setSteuerKonto(sk);
		                  sNew.setUstNrBemessung(steuer.getUstNrBemessung());
		                  sNew.setUstNrSteuer(steuer.getUstNrSteuer());
		                  sNew.store();

		                  steuerMap.put(key,sNew);
		                  Logger.info("Steuersatz " + steuerSatz + " neu angelegt für Buchung " + b.getBelegnummer());
		                }
		                //Da die Buchungsengine nicht gestartet ist, fürhen wir hier SQL direkt durch
		                db.executeUpdate("UPDATE buchung set steuer_id = ? WHERE id = ?", new Object[] {new Integer(sNew.getID()),new Integer(b.getID())});
		                //b.setSteuer(sNew);
		                //b.store();
		                Logger.debug("Speichere neuen Steuersatz " + steuerSatz + " in Buchung " + b.getBelegnummer());
		             }
			      }
	        }
	        //und nochmal das ganze für die Buchungstemplates
	        final DBIterator<Buchungstemplate> templates = m.getBuchungstemplates();
	        while (templates.hasNext())
		      {
		        final Buchungstemplate b = templates.next();
		        final Konto soll = b.getSollKonto();
	            final Konto haben = b.getHabenKonto();
	            
	            final Steuer ss = soll.getSteuer();
	            final Steuer sh = haben.getSteuer();
	            
	            final Steuer steuer = ss != null ? ss : sh;
	            
	            if (steuer == null)
	              continue;
	            
		        final double steuerSatz = (Double)b.getAttribute("steuer");
		        
		        if(steuerSatz < 0.1d)
		        	continue;
		        
		        if (math.abs(steuerSatz - steuer.getSatz()) < 0.01)
	            {
	              // Wir können die Steuer direkt in der Buchung speichern
		          
	              b.setSteuer(steuer);
	              b.store();
	              Logger.debug("Speichere Steuersatz " + steuerSatz + " in Buchungstemplate " + b.getName());
	            }
	            else
	            {
	            	final Konto sk = steuer.getSteuerKonto();
	                final String key = Double.toString(math.round(steuerSatz)) + sk.getKontoTyp().getKontoTyp();

	                Steuer sNew = steuerMap.get(key);
	                
	                // Steuersatz neu anlegen, falls wir den noch nicht haben
	                if (sNew == null)
	                {
	                  sNew = db.createObject(Steuer.class,null);
	                  sNew.setMandant(m);//oder global?
	                  if(sk.getKontoTyp().getKontoTyp() == Kontotyp.KONTOTYP_AUSGABE)
	                	  sNew.setName("Vorsteuer "+steuerSatz+"%");
	                  else sNew.setName("Umsatzsteuer "+steuerSatz+"%");
	                  sNew.setSatz(steuerSatz);
	                  sNew.setSteuerKonto(sk);
	                  sNew.setUstNrBemessung(steuer.getUstNrBemessung());
	                  sNew.setUstNrSteuer(steuer.getUstNrSteuer());
	                  sNew.store();

	                  steuerMap.put(key,sNew);
	                  Logger.info("Steuersatz " + steuerSatz + " neu angelegt für Buchungstemplate " + b.getName());
	                }
	                
	                b.setSteuer(sNew);
	                b.store();
	                Logger.debug("Speichere neuen Steuersatz " + steuerSatz + " in Buchungstemplate " + b.getName());
	             }
		      }
        }
      return null;
    });
  }

  /**
   * @see de.willuhn.sql.version.Update#getName()
   */
  @Override
  public String getName()
  {
    return "Update der Steuersätze auf neues Datenbank-Modell";
  }

}
