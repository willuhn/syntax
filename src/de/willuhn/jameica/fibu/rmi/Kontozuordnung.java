package de.willuhn.jameica.fibu.rmi;

import java.rmi.RemoteException;

/**
 * Zuordnung der Hibiscus Konten zu Syntax Konten.
 * Bei der Verwendung von Buchungstemplates wird 
 * die Buchung dem Richtigen Geldkonto zugeordnet
 */
public interface Kontozuordnung extends Transfer
{
	 /**
	   * Liefert einen sprechenden Namen fuer die Kontozuordnung.
	   * @return Name.
	   * @throws RemoteException
	   */
	  public String getName() throws RemoteException;
	  
	  /**
	   * Setzt den sprechenden Namen fuer die Kontozuordnung.
	   * @param name Name.
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
	   * Setzt das Konto.
	   * @param konto das Konto.
	   * @throws RemoteException
	   */
	  public void setKonto(Konto konto) throws RemoteException;
	  
	  /**
	   * Liefert den Mandanten.
	   * @return Mandant.
	   * @throws RemoteException
	   */
	  public Mandant getMandant() throws RemoteException;
	  
	  /**
	   * Setzt den Mandanten.
	   * @param mandant Mandant.
	   * @throws RemoteException
	   */
	  public void setMandant(Mandant mandant) throws RemoteException;
	  
	  /**
	   * Liefert die ID des Hibscus-Konto.
	   * @return die ID des Hibiscus-Kontos.
	   * @throws RemoteException
	   */
	  public String getHibiscusKontoId() throws RemoteException;
	  
	  /**
	   * Speichert die ID des Hibiscus Kontos.
	   * @param id die ID des Hibiscus-Kontos.
	   * @throws RemoteException
	   */
	  public void setHibiscusKontoId(String id) throws RemoteException;
}
