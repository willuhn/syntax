/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/update/Attic/Update_1_3_to_1_4.java,v $
 * $Revision: 1.5 $
 * $Date: 2008/04/17 23:10:56 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.update;

import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

import de.willuhn.datasource.rmi.ResultSetExtractor;
import de.willuhn.jameica.fibu.rmi.DBService;
import de.willuhn.jameica.fibu.server.DBServiceImpl;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
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
    if (oldVersion != 1.3d && newVersion != 1.4d)
    {
      Logger.info("ueberspringe Update " + this.getClass().getName());
      return;
    }
    
    final Hashtable kontenrahmen = new Hashtable();

    DBService service = null;
    try
    {
      service = new DBServiceImpl();
      service.start();
      final DBService fs = service;

      //////////////////////////////////////////////////////////////////////////
      // 1) Checken, ob das Update schon lief
      Logger.info("Pruefe, ob Update noetig");
      if (service.execute("select * from kontenrahmen where mandant_id is not null", new Object[0], new ResultSetExtractor() {
        public Object extract(ResultSet rs) throws RemoteException, SQLException {return rs.next() ? new Object() : null;}
      
      }) != null) {
        Logger.info("Datenbank-Update von 1.3 auf 1.4 bereits ausgefuehrt");
        return;
      }
      monitor.addPercentComplete(10);
      //////////////////////////////////////////////////////////////////////////
      
      //////////////////////////////////////////////////////////////////////////
      // 2) Kontenrahmen
      
      // Ermitteln aller Geschaeftsjahre, deren Kontenrahmen noch ein System-Kontenrahmen ist
      Logger.info("Lese existierende Kontenrahmen");
      service.execute("select geschaeftsjahr.*,kontenrahmen.name from geschaeftsjahr, kontenrahmen " +
                      "where geschaeftsjahr.kontenrahmen_id = kontenrahmen.id " +
                      "and kontenrahmen.mandant_id is null", new Object[0], new ResultSetExtractor() {
        public Object extract(ResultSet rs) throws RemoteException, SQLException
        {
          while (rs.next())
          {
            // Haben wir den Kontenrahmen schon fuer den Mandanten kopiert?
            String name = rs.getString("name");
            final int krid    = rs.getInt("kontenrahmen_id");
            final int mid     = rs.getInt("mandant_id");

            if (kontenrahmen.get(krid + ":" + mid) != null)
              continue; // haben wir schon
            
            // ID des Mandanten an den Kontenrahmen-Namen haengen, weil der
            // Kontenrahmen-Name unique ist
            name += " (Mandant " + mid + ")";
            
            Logger.info("Kopiere Kontenrahmen [ID: " + krid + "] zu Mandant [ID: " + mid + "]");

            // Kopieren des Kontenrahmen auf den Mandanten
            fs.executeUpdate("insert into kontenrahmen (name,mandant_id) values (?,?)", new Object[]{name,new Integer(mid)});
            final Integer i = (Integer)fs.execute("select max(id) as id from kontenrahmen", new Object[0],new ResultSetExtractor() {
              public Object extract(ResultSet rs1) throws RemoteException, SQLException {
                rs1.next();
                return new Integer(rs1.getInt("id"));
              }
            
            });
            Logger.info("Neue Kontenrahmen-ID: " + i);
            
            // Kopieren der System-Konten auf den neuen Kontenrahmen
            fs.execute("select * from konto where kontenrahmen_id=? and mandant_id is null", new Object[]{new Integer(krid)}, new ResultSetExtractor() {
              public Object extract(ResultSet rs2) throws RemoteException, SQLException
              {
                while (rs2.next())
                {
                  Logger.info("Kopiere Konto " + rs2.getString("kontonummer") + " in neuen Kontenrahmen " + i);
                  fs.executeUpdate("insert into konto (kontoart_id,kontotyp_id,kontonummer,name,kontenrahmen_id,steuer_id) " +
                      "values (?,?,?,?,?,?)", new Object[]{rs2.getObject("kontoart_id"),
                                                           rs2.getObject("kontotyp_id"),
                                                           rs2.getString("kontonummer"),
                                                           rs2.getString("name"),
                                                           i,
                                                           rs2.getObject("steuer_id")});
                }
                return null;
              }
            
            });
            kontenrahmen.put(krid + ":" + mid,i);
          }
          return null;
        }
      
      });
      
      // Benutzerdefinierte Konten auf die neuen Kontenrahmen-IDs verschieben
      Logger.info("Pruefe auf benutzerdefinierte Konten");
      service.execute("select * from konto where mandant_id is not null", new Object[0],new ResultSetExtractor() {
        public Object extract(ResultSet rs) throws RemoteException, SQLException
        {
          while (rs.next())
          {
            int kid  = rs.getInt("id");
            int krid = rs.getInt("kontenrahmen_id");
            int mid  = rs.getInt("mandant_id");
            Integer newKr  = (Integer) kontenrahmen.get(krid + ":" + mid);
            Logger.info("Verschiebe Konto " + rs.getString("kontonummer") + " in Kontenrahmen " + newKr);
            fs.executeUpdate("update konto set mandant_id=null, kontenrahmen_id=? where id=?", new Object[]{new Integer(krid),
                                                                                                            new Integer(kid)});
          }
          return null;
        }
      });
      
      // Mandant entfernen, ist nicht mehr noetig, da ueber den KR eindeutig
      Logger.info("Leere Spalte konto.mandant_id");
      service.executeUpdate("update konto set mandant_id=null", new Object[0]);
      
      // TODO Das ist in MySQL leider nicht ohne weiteres moeglich,
      // da man den Constraint nur loeschen kann, wenn man dessen
      // ID kennt ("show create table konto"). Da der aber dynamisch
      // vergeben wurde, geht das nicht. Somit bleibt die Spalte
      // als Leiche erhalten und wird irgendwann spaeter mal geloescht
      // service.executeUpdate("alter table konto drop foreign key mandant_id",new Object[0]);
      // service.executeUpdate("alter table konto drop index idx_konto_mandant",new Object[0]);
      // service.executeUpdate("alter table konto drop mandant_id",new Object[0]);

      
      monitor.addPercentComplete(10);
      //
      //////////////////////////////////////////////////////////////////////////


      
      
      //////////////////////////////////////////////////////////////////////////
      // 3) Steuer-Saetze

      // Kontenrahmen hinzufuegen
      Logger.info("Neue Spalte steuer.kontenrahmen_id");
      service.executeUpdate("alter table steuer add kontenrahmen_id int(10) NULL",new Object[0]);
      // TODO: NOT NULL + Index + Constraint

      // Neue Spalte fuer Kontonummer
      Logger.info("Neue Spalte steuer.konto");
      service.executeUpdate("alter table steuer add konto varchar(4) NULL",new Object[0]);
      // TODO: NOT NULL + Index

      // Konten umbiegen
      //////////////////////////////////////////////////////////////////////////
      // TODO: Hier gehts weiter:
      Logger.info("Kopiere System-Steuersaetze");
      service.execute("select * from steuer where mandant_id is null", new Object[0], new ResultSetExtractor() {
        public Object extract(ResultSet rs) throws RemoteException, SQLException
        {
          while (rs.next())
          {
            Integer sid = new Integer(rs.getInt("id"));
            Integer mid = new Integer(rs.getInt("mandant_id"));
            Integer kid = new Integer(rs.getInt("steuerkonto_id"));
            
            // Jetzt brauchen wir den alten Kontenrahmen des Steuer-Kontos
            Object[] o = (Object[]) fs.execute("select kontonummer,kontenrahmen_id from konto where id=?",new Object[]{kid}, new ResultSetExtractor() {
              public Object extract(ResultSet arg0) throws RemoteException, SQLException {
                arg0.next();
                return new Object[]{new Integer(arg0.getInt("kontonummer")),new Integer(arg0.getInt("kontenrahmen_id"))};
              }
            });
            
            // Mit der koennen wir jetzt herausfinden, wie der neue Kontenrahmen
            // des Mandanten lautet
            Integer newKr = (Integer) kontenrahmen.get(mid + ":" + o[1]);
            Logger.info("Verschiebe steuer fuer Konto " + o[0] + " auf Kontenrahmen " + newKr);
            fs.executeUpdate("update steuer set kontenrahmen_id = ?,konto = ? where id = ?", new Object[]{newKr,o[0],sid});
          }
          return null;
        }
      
      });
      
      // Mandant entfernen
      Logger.info("Leere Spalte steuer.mandant_id");
      service.executeUpdate("update steuer set mandant_id=null", new Object[0]);
      // TODO: Problematik siehe konto
      // service.executeUpdate("alter table steuer drop constraint fk_steuer_mandant",new Object[0]);
      // service.executeUpdate("alter table steuer drop index idx_steuer_mandant",new Object[0]);
      // service.executeUpdate("alter table steuer drop mandant_id",new Object[0]);
      
      // steuerkonto_id entfernen
      Logger.info("Leere Spalte steuer.steuerkonto_id");
      service.executeUpdate("alter table steuer drop constraint fk_steuer_konto",new Object[0]);
      service.executeUpdate("alter table steuer drop index idx_steuer_steuerkonto",new Object[0]);
      service.executeUpdate("alter table steuer drop steuerkonto_id",new Object[0]);
      monitor.addPercentComplete(10);
      //
      //////////////////////////////////////////////////////////////////////////


      
      //////////////////////////////////////////////////////////////////////////
      // 4) Buchungen
      service.executeUpdate("alter table buchung add sollkonto varchar(4) NULL",new Object[0]);
      service.executeUpdate("alter table buchung add habenkonto varchar(4) NULL",new Object[0]);
      // TODO NOT NULL + Index

      // Buchungen migrieren
      service.execute("select id,kontonummer from konto", new Object[0], new ResultSetExtractor() {
        public Object extract(ResultSet rs) throws RemoteException, SQLException
        {
          while (rs.next())
          {
            Integer id = new Integer(rs.getInt("id"));
            String k   = rs.getString("kontonummer");
            fs.executeUpdate("update buchung set sollkonto = ? where sollkonto_id = ?", new Object[]{k,id});
            fs.executeUpdate("update buchung set habenkonto = ? where habenkonto_id = ?", new Object[]{k,id});
          }
          return null;
        }
      
      });
      
      // Alte Spalten loeschen
      service.executeUpdate("alter table buchung drop constraint fk_buchung_sk",new Object[0]);
      service.executeUpdate("alter table buchung drop index idx_buchung_sk",new Object[0]);
      service.executeUpdate("alter table buchung drop sollkonto_id",new Object[0]);

      service.executeUpdate("alter table buchung drop constraint fk_buchung_hk",new Object[0]);
      service.executeUpdate("alter table buchung drop index idx_buchung_hk",new Object[0]);
      service.executeUpdate("alter table buchung drop habenkonto_id",new Object[0]);
      monitor.addPercentComplete(10);
      //
      //////////////////////////////////////////////////////////////////////////


      //////////////////////////////////////////////////////////////////////////
      // 5) Anfangsbestaende
      service.executeUpdate("alter table konto_ab add konto varchar(4) NULL",new Object[0]);
      // TODO NOT NULL + Index

      // Buchungen migrieren
      service.execute("select id,kontonummer from konto", new Object[0], new ResultSetExtractor() {
        public Object extract(ResultSet rs) throws RemoteException, SQLException
        {
          while (rs.next())
          {
            Integer id = new Integer(rs.getInt("id"));
            String k   = rs.getString("kontonummer");
            fs.executeUpdate("update konto_ab set konto = ? where konto_id = ?", new Object[]{k,id});
          }
          return null;
        }
      
      });
      
      // Alte Spalten loeschen
      service.executeUpdate("alter table konto_ab drop constraint fk_kontoab_konto",new Object[0]);
      service.executeUpdate("alter table konto_ab drop index idx_kontoab_konto",new Object[0]);
      service.executeUpdate("alter table konto_ab drop konto_id",new Object[0]);
      monitor.addPercentComplete(10);
      //
      //////////////////////////////////////////////////////////////////////////
      

      //////////////////////////////////////////////////////////////////////////
      // 6) Anlagevermoegen
      service.executeUpdate("alter table anlagevermoegen add abschreibungskonto varchar(4) NULL",new Object[0]);
      service.executeUpdate("alter table anlagevermoegen add konto varchar(4) NULL",new Object[0]);
      // TODO NOT NULL + Index

      // Buchungen migrieren
      service.execute("select id,kontonummer from konto", new Object[0], new ResultSetExtractor() {
        public Object extract(ResultSet rs) throws RemoteException, SQLException
        {
          while (rs.next())
          {
            Integer id = new Integer(rs.getInt("id"));
            String k   = rs.getString("kontonummer");
            fs.executeUpdate("update anlagevermoegen set abschreibungskonto = ? where k_abschreibung_id = ?", new Object[]{k,id});
            fs.executeUpdate("update anlagevermoegen set konto = ? where konto_id = ?", new Object[]{k,id});
          }
          return null;
        }
      
      });
      
      // Alte Spalten loeschen
      service.executeUpdate("alter table anlagevermoegen drop constraint fk_av_abschreibung",new Object[0]);
      service.executeUpdate("alter table anlagevermoegen drop index idx_av_k_abschreibung",new Object[0]);
      service.executeUpdate("alter table anlagevermoegen drop k_abschreibung_id",new Object[0]);

      service.executeUpdate("alter table anlagevermoegen drop constraint fk_av_konto",new Object[0]);
      service.executeUpdate("alter table anlagevermoegen drop index idx_av_konto",new Object[0]);
      service.executeUpdate("alter table anlagevermoegen drop konto_id",new Object[0]);
      monitor.addPercentComplete(10);
      // 
      //////////////////////////////////////////////////////////////////////////
      
      
      
      //////////////////////////////////////////////////////////////////////////
      // 7) Buchungstemplates
      service.executeUpdate("alter table buchungstemplate add sollkonto varchar(4) NULL",new Object[0]);
      service.executeUpdate("alter table buchungstemplate add habenkonto varchar(4) NULL",new Object[0]);
      // TODO NOT NULL + Index

      // Buchungen migrieren
      service.execute("select id,kontonummer from konto", new Object[0], new ResultSetExtractor() {
        public Object extract(ResultSet rs) throws RemoteException, SQLException
        {
          while (rs.next())
          {
            Integer id = new Integer(rs.getInt("id"));
            String k   = rs.getString("kontonummer");
            fs.executeUpdate("update buchungstemplate set sollkonto = ? where sollkonto_id = ?", new Object[]{k,id});
            fs.executeUpdate("update buchungstemplate set habenkonto = ? where habenkonto_id = ?", new Object[]{k,id});
          }
          return null;
        }
      
      });
      
      // Kontenrahmen umbiegen
      service.execute("select * from buchungstemplate", new Object[0], new ResultSetExtractor() {
        public Object extract(ResultSet rs) throws RemoteException, SQLException
        {
          while (rs.next())
          {
            Integer bid = new Integer(rs.getInt("id"));
            Integer mid = new Integer(rs.getInt("mandant_id"));
            Integer kid = new Integer(rs.getInt("kontenrahmen_id"));
            
            // Mit der koennen wir jetzt herausfinden, wie der neue Kontenrahmen
            // des Mandanten lautet
            Integer newKr = (Integer) kontenrahmen.get(mid + ":" + kid);
            fs.executeUpdate("update buchungstemplate set kontenrahmen_id = ? where id = ?", new Object[]{newKr,bid});
          }
          return null;
        }
      
      });

      // Alte Spalten loeschen
      service.executeUpdate("alter table buchungstemplate drop constraint fk_buchungt_mandant",new Object[0]);
      service.executeUpdate("alter table buchungstemplate drop index idx_bt_mandant",new Object[0]);
      service.executeUpdate("alter table buchungstemplate drop mandant_id",new Object[0]);

      service.executeUpdate("alter table buchungstemplate drop constraint fk_buchungt_sk",new Object[0]);
      service.executeUpdate("alter table buchungstemplate drop index idx_bt_sk",new Object[0]);
      service.executeUpdate("alter table buchungstemplate drop sollkonto_id",new Object[0]);

      service.executeUpdate("alter table buchungstemplate drop constraint fk_buchungt_hk",new Object[0]);
      service.executeUpdate("alter table buchungstemplate drop index idx_bt_hk",new Object[0]);
      service.executeUpdate("alter table buchungstemplate drop habenkonto_id",new Object[0]);
      monitor.addPercentComplete(10);
      //
      //////////////////////////////////////////////////////////////////////////

      //////////////////////////////////////////////////////////////////////////
      // 8) Geschaeftsjahre
      // Kontenrahmen umbiegen
      service.execute("select * from geschaeftsjahr", new Object[0], new ResultSetExtractor() {
        public Object extract(ResultSet rs) throws RemoteException, SQLException
        {
          while (rs.next())
          {
            Integer gid = new Integer(rs.getInt("id"));
            Integer mid = new Integer(rs.getInt("mandant_id"));
            Integer kid = new Integer(rs.getInt("kontenrahmen_id"));
            
            // Mit der koennen wir jetzt herausfinden, wie der neue Kontenrahmen
            // des Mandanten lautet
            Integer newKr = (Integer) kontenrahmen.get(mid + ":" + kid);
            fs.executeUpdate("update geschaeftsjahr set kontenrahmen_id = ? where id = ?", new Object[]{newKr,gid});
          }
          return null;
        }
      
      });
      // Alte Spalten loeschen
      service.executeUpdate("alter table geschaeftsjahr drop constraint fk_gj_mandant",new Object[0]);
      service.executeUpdate("alter table geschaeftsjahr drop index idx_gj_mandant",new Object[0]);
      service.executeUpdate("alter table geschaeftsjahr drop mandant_id",new Object[0]);
      monitor.addPercentComplete(10);
      //
      //////////////////////////////////////////////////////////////////////////
    }
    catch (Exception e)
    {
      e.printStackTrace();
      try
      {
        Logger.flush();
        Logger.close();
        Thread.sleep(1000l);
      } catch (Exception e1) {}
      System.exit(1);
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
 * Revision 1.5  2008/04/17 23:10:56  willuhn
 * *** empty log message ***
 *
 * Revision 1.4  2008/02/22 10:41:41  willuhn
 * @N Erweiterte Mandantenfaehigkeit (IN PROGRESS!)
 *
 **********************************************************************/