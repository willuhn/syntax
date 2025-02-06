/**********************************************************************
 *
 * Copyright (c) 2004 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.gui.boxes;

import java.rmi.RemoteException;

import org.eclipse.swt.widgets.Composite;

import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.Settings;
import de.willuhn.jameica.fibu.migration.McKoiToH2MigrationTask;
import de.willuhn.jameica.fibu.server.DBSupportMcKoiImpl;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.boxes.AbstractBox;
import de.willuhn.jameica.gui.parts.Button;
import de.willuhn.jameica.gui.parts.InfoPanel;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Wizard fuer die H2-Migration.
 */
public class H2Migration extends AbstractBox
{
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
  
  /**
   * @see de.willuhn.jameica.gui.boxes.Box#isActive()
   */
  public boolean isActive()
  {
    return super.isActive() && isEnabled();
  }
  
  /**
   * @see de.willuhn.jameica.gui.boxes.Box#getDefaultEnabled()
   */
  public boolean getDefaultEnabled()
  {
    return true;
  }
  
  /**
   * @see de.willuhn.jameica.gui.boxes.Box#getDefaultIndex()
   */
  public int getDefaultIndex()
  {
    return 0;
  }
  
  /**
   * @see de.willuhn.jameica.gui.boxes.Box#getName()
   */
  public String getName()
  {
    return "SynTAX: " + i18n.tr("Datenbank-Migration von McKoi zu H2");
  }
  
  /**
   * @see de.willuhn.jameica.gui.boxes.Box#isEnabled()
   */
  public boolean isEnabled()
  {
    return super.isEnabled() && this.migrationRequired();
  }
  
  /**
   * Prüft, ob die Migration nötig ist.
   * @return true, wenn sie nötig ist.
   */
  private boolean migrationRequired()
  {
    // Generell deaktiviert?
    if (!Settings.SETTINGS.getBoolean("migration.h2",true))
      return false;
    
    // Checken, ob Migration schon lief
    if (Settings.SETTINGS.getString("migration.mckoi-to-h2",null) != null)
      return false; // lief bereits

    // Checken, ob ueberhaupt die McKoi-Datenbank genutzt wird
    String driver = Settings.SETTINGS.getString("database.support.class",null);
    if (driver == null || !driver.equals(DBSupportMcKoiImpl.class.getName()))
      return false;

    return true;
  }
  
  /**
   * @see de.willuhn.jameica.gui.Part#paint(org.eclipse.swt.widgets.Composite)
   */
  public void paint(Composite parent) throws RemoteException
  {
    final InfoPanel panel = new InfoPanel();
    panel.setTitle(i18n.tr("Datenbank-Migration von McKoi zu H2"));
    panel.setIcon("dialog-information-large.png");
    panel.setText(i18n.tr("Ihre SynTAX-Installation verwendet noch das veraltete Datenbank-Format McKoi. Klicken Sie auf \"Datenbank jetzt migrieren\", um die Datenbank auf das moderne H2-Format umzustellen, welches auch von Hibiscus verwendet wird."));
    panel.addButton(new Button(i18n.tr("Datenbank jetzt migrieren"),new Action() {
      
      @Override
      public void handleAction(Object context) throws ApplicationException
      {
        Logger.warn("starting database migration from mckoi to h2");
        Application.getController().start(new McKoiToH2MigrationTask());
      }
    },null,false,"go-next.png"));
    panel.paint(parent);

  }

  /**
   * @see de.willuhn.jameica.gui.boxes.AbstractBox#getHeight()
   */
  public int getHeight()
  {
    return 150;
  }

}
