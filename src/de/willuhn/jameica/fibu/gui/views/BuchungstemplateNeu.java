/**********************************************************************
 *
 * Copyright (c) 2004 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/
package de.willuhn.jameica.fibu.gui.views;

import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.gui.action.DBObjectDelete;
import de.willuhn.jameica.fibu.gui.controller.BuchungstemplateControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.extension.Extendable;
import de.willuhn.jameica.gui.parts.Button;
import de.willuhn.jameica.gui.parts.ButtonArea;
import de.willuhn.jameica.gui.util.Container;
import de.willuhn.jameica.gui.util.SimpleContainer;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Erzeugt eine neue Buchung oder bearbeitet eine existierende.
 * @author willuhn
 */
public class BuchungstemplateNeu extends AbstractView implements Extendable
{
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

  private Container container             = null;
  private BuchungstemplateControl control = null;

  /**
   * @see de.willuhn.jameica.gui.AbstractView#bind()
   */
  public void bind() throws Exception
  {
		GUI.getView().setTitle(i18n.tr("Buchungsvorlage bearbeiten"));

    this.control   = new BuchungstemplateControl(this);
    
    this.container = new SimpleContainer(getParent());
    this.container.addHeadline(i18n.tr("Eigenschaften"));
    this.container.addInput(control.getBezeichnung());
    this.container.addInput(control.getSollKontoAuswahl());
    this.container.addInput(control.getHabenKontoAuswahl());
    this.container.addInput(control.getText());
    this.container.addInput(control.getBetrag());
    this.container.addInput(control.getSteuer());

    ButtonArea buttonArea = new ButtonArea();
    buttonArea.addButton(i18n.tr("Löschen"), new DBObjectDelete(), getCurrentObject(),false,"user-trash-full.png");
    buttonArea.addButton(new Button(i18n.tr("Speichern"),new Action() {
      public void handleAction(Object context) throws ApplicationException
      {
        control.handleStore(false);
      }
    },null,true,"document-save.png"));
    buttonArea.paint(getParent());
  }

  
  /**
   * @see de.willuhn.jameica.gui.AbstractView#unbind()
   */
  public void unbind() throws ApplicationException
  {
    this.container = null;
    this.control   = null;
  }


  /**
   * @see de.willuhn.jameica.gui.extension.Extendable#getExtendableID()
   */
  public String getExtendableID()
  {
    return this.getClass().getName();
  }
  
  /**
   * Liefert den Container, in dem sich die Controls befinden.
   * @return der Container mit den Controls.
   */
  public Container getContainer()
  {
    return this.container;
  }
  
  /**
   * Liefert den Controller.
   * @return der Controller.
   */
  public BuchungstemplateControl getControl()
  {
    return this.control;
  }
  
}

/*********************************************************************
 * $Log: BuchungstemplateNeu.java,v $
 * Revision 1.11  2011/05/12 09:10:31  willuhn
 * @R Back-Buttons entfernt
 * @C GUI-Cleanup
 *
 * Revision 1.10  2010-08-27 11:19:40  willuhn
 * @N Import-/Export-Framework incl. XML-Format aus Hibiscus portiert
 *
 * Revision 1.9  2010/06/04 00:33:56  willuhn
 * @B Debugging
 * @N Mehr Icons
 * @C GUI-Cleanup
 *
 * Revision 1.8  2010/06/03 14:26:16  willuhn
 * @N Extension zum Zuordnen von Hibiscus-Kategorien zu SynTAX-Buchungsvorlagen
 * @C Code-Cleanup
 *
 * Revision 1.7  2010/06/02 00:02:59  willuhn
 * @N Mehr Icons
 *
 * Revision 1.6  2010/06/01 23:51:56  willuhn
 * @N Neue Icons - erster Teil
 *
 * Revision 1.5  2010/06/01 16:37:22  willuhn
 * @C Konstanten von Fibu zu Settings verschoben
 * @N Systemkontenrahmen nach expliziter Freigabe in den Einstellungen aenderbar
 * @C Unterscheidung zwischen canChange und isUserObject in UserObject
 * @C Code-Cleanup
 * @R alte CVS-Logs entfernt
 *
 **********************************************************************/