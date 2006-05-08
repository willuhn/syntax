/**********************************************************************
 * $Source: /cvsroot/syntax/syntax/src/de/willuhn/jameica/fibu/gui/action/BuchungGeprueft.java,v $
 * $Revision: 1.1 $
 * $Date: 2006/05/08 15:41:57 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.gui.action;

/**
 * Setzt Buchungen auf den Status geprueft.
 */
public class BuchungGeprueft extends AbstractBuchungGeprueft
{

  /**
   * @see de.willuhn.jameica.fibu.gui.action.AbstractBuchungGeprueft#getNewState()
   */
  boolean getNewState()
  {
    return true;
  }

}


/*********************************************************************
 * $Log: BuchungGeprueft.java,v $
 * Revision 1.1  2006/05/08 15:41:57  willuhn
 * @N Buchungen als geprueft/ungeprueft markieren
 * @N Link Anlagevermoegen -> Buchung
 *
 **********************************************************************/