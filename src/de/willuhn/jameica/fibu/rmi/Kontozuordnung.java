package de.willuhn.jameica.fibu.rmi;

import java.rmi.RemoteException;

/*
 * Zuordnung der Hibiscus Konten zu Syntax Konten.
 * Bei der Verwendung von Buchungstemplates wird 
 * die Buchung dem Richtigen Geldkonto zugeordnet
 */
public interface Kontozuordnung extends Transfer{
	 /**
	   * Liefert einen sprechenden Namen fuer die Kontozuordnung.
	   * @return Name.
	   * @throws RemoteException
	   */
	  public String getName() throws RemoteException;
	  
	  /**
	   * Setzt den sprechenden Namen fuer die Kontozuordnung.
	   * @param Name.
	   * @throws RemoteException
	   */
	  public void setName(String name) throws RemoteException;

	  /**
	   * Liefert das Konto.
	   * @return Konto.
	   * @throws RemoteException
	   */
	  public Konto getKonto() throws RemoteException;
	  
	  /**
	   * Setzt den Mandanten.
	   * @param Mandant.
	   * @throws RemoteException
	   */
	  public void setKonto(Konto name) throws RemoteException;
	  
	  /**
	   * Liefert den Mandanten.
	   * @return Mandant.
	   * @throws RemoteException
	   */
	  public Mandant getMandant() throws RemoteException;
	  
	  /**
	   * Setzt den Mandanten.
	   * @param Mandant.
	   * @throws RemoteException
	   */
	  public void setMandant(Mandant mandant) throws RemoteException;
	  
	  /**
	   * Liefert das Hibiscus Konto.
	   * @return Konto.
	   * @throws RemoteException
	   */
	  public de.willuhn.jameica.hbci.rmi.Konto getHbKonto() throws RemoteException;
	  
	  /**
	   * Setzt dase Hibiscus Konto.
	   * @param Konto.
	   * @throws RemoteException
	   */
	  public void setHbKonto(de.willuhn.jameica.hbci.rmi.Konto hbKonto_id) throws RemoteException;
}
