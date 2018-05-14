/**********************************************************************
 *
 * Copyright (c) 2004 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/

package de.willuhn.jameica.fibu.gui.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.rmi.DBService;
import de.willuhn.jameica.fibu.rmi.Kontenrahmen;
import de.willuhn.jameica.fibu.rmi.Mandant;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.dialogs.AbstractDialog;
import de.willuhn.jameica.gui.input.LabelInput;
import de.willuhn.jameica.gui.input.SelectInput;
import de.willuhn.jameica.gui.input.TextInput;
import de.willuhn.jameica.gui.parts.ButtonArea;
import de.willuhn.jameica.gui.util.Color;
import de.willuhn.jameica.gui.util.Container;
import de.willuhn.jameica.gui.util.SimpleContainer;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.system.OperationCanceledException;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Dialog zum Clonen eines Kontenrahmens.
 */
public class KontenrahmenCloneDialog extends AbstractDialog
{
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();
  private final static int WINDOW_WIDTH = 520;

  private Kontenrahmen kontenrahmen = null;
  private Mandant mandant           = null;
  private String name               = null;
  
  private SelectInput inputKontenrahmen = null;
  private SelectInput inputMandant      = null;
  private TextInput inputName           = null;
  private LabelInput error              = null;
  
  /**
   * ct.
   * @param position
   */
  public KontenrahmenCloneDialog(int position)
  {
    this(position,null);
  }

  /**
   * ct.
   * @param position
   * @param template die Kontenrahmen-Vorlage
   */
  public KontenrahmenCloneDialog(int position, Kontenrahmen template)
  {
    super(position);
    this.kontenrahmen = template;
    this.setTitle(i18n.tr("Eigenschaften des neuen Kontenrahmens"));
    this.setSize(WINDOW_WIDTH,SWT.DEFAULT);
  }

  /**
   * @see de.willuhn.jameica.gui.dialogs.AbstractDialog#paint(org.eclipse.swt.widgets.Composite)
   */
  protected void paint(Composite parent) throws Exception
  {
    Container container = new SimpleContainer(parent);
    container.addText(i18n.tr("Bitte wählen Sie die Eigenschaften des neuen Kontenrahmens aus."),true);

    container.addInput(this.getInputKontenrahmen());
    container.addInput(this.getInputMandant());
    container.addInput(this.getInputName());
    container.addInput(this.getError());
    
    ButtonArea buttons = new ButtonArea();
    buttons.addButton(i18n.tr("Übernehmen"),new Action()
    {
      public void handleAction(Object context) throws ApplicationException
      {
        if (apply())
          close();
      }
    },null,false,"ok.png");
    buttons.addButton(i18n.tr("Abbrechen"), new Action()
    {
      public void handleAction(Object context) throws ApplicationException
      {
        throw new OperationCanceledException();
      }
    },null,false,"process-stop.png");
    container.addButtonArea(buttons);

    getShell().setMinimumSize(getShell().computeSize(WINDOW_WIDTH,SWT.DEFAULT));
  }

  /**
   * Checkt und uebernimmt die Daten.
   * @return true, wenn die Daten ok sind.
   */
  private boolean apply()
  {
    try
    {
      this.kontenrahmen = (Kontenrahmen) this.getInputKontenrahmen().getValue();
      this.mandant      = (Mandant) this.getInputMandant().getValue();
      this.name         = (String) this.getInputName().getValue();
      
      if (this.kontenrahmen == null)
      {
        getError().setValue(i18n.tr("Bitte wählen Sie einen Kontenrahmen als Vorlage aus"));
        return false;
      }
      
      if (this.name == null || this.name.trim().length() == 0)
      {
        getError().setValue(i18n.tr("Bitte geben Sie einen Namen für den neuen Kontenrahmen ein"));
        return false;
      }

      DBService service = (DBService) Application.getServiceFactory().lookup(Fibu.class,"database");
      
      // Checken, ob ein Kontenrahmen mit diesem Namen schon existiert
      DBIterator list = service.createList(Kontenrahmen.class);
      list.addFilter("name = ?",new Object[]{this.name});
      if (list.hasNext())
      {
        getError().setValue(i18n.tr("Ein Kontenrahmen mit diesem Namen existiert bereits"));
        return false;
      }
      
      return true;
    }
    catch (Exception e)
    {
      Logger.error("error while applying entered data",e);
      getError().setValue(e.getMessage());
    }
    return false;
  }
  
  /**
   * @see de.willuhn.jameica.gui.dialogs.AbstractDialog#getData()
   */
  protected Object getData() throws Exception
  {
    return null;
  }
  
  /**
   * Liefert das Auswahlfeld fuer den Vorlagekontenrahmen.
   * @return Auswahlfeld.
   * @throws Exception
   */
  private SelectInput getInputKontenrahmen() throws Exception
  {
    if (this.inputKontenrahmen == null)
    {
      DBService service = (DBService) Application.getServiceFactory().lookup(Fibu.class,"database");
      DBIterator list = service.createList(Kontenrahmen.class);
      list.setOrder("order by name");
      this.inputKontenrahmen = new SelectInput(list,this.kontenrahmen);
      this.inputKontenrahmen.setAttribute("name");
      this.inputKontenrahmen.setMandatory(true);
      this.inputKontenrahmen.setName(i18n.tr("Kontenrahmen-Vorlage"));
      this.inputKontenrahmen.setPleaseChoose(i18n.tr("Bitte wählen..."));
    }
    return this.inputKontenrahmen;
  }
  
  /**
   * Liefert das Auswahlfeld fuer den zugeordneten Mandanten.
   * @return Auswahlfeld.
   * @throws Exception
   */
  private SelectInput getInputMandant() throws Exception
  {
    if (this.inputMandant == null)
    {
      DBService service = (DBService) Application.getServiceFactory().lookup(Fibu.class,"database");
      DBIterator list = service.createList(Mandant.class);
      list.setOrder("order by firma");
      this.inputMandant = new SelectInput(list,null);
      this.inputMandant.setAttribute("firma");
      this.inputMandant.setName(i18n.tr("Zuordnung des Mandanten"));
      this.inputMandant.setPleaseChoose(i18n.tr("für alle Mandanten zugänglich"));
    }
    return this.inputMandant;
  }
  
  /**
   * Liefert das Eingabefeld fuer den Namen des Kontenrahmen.
   * @return Eingabefeld.
   * @throws Exception
   */
  public TextInput getInputName() throws Exception
  {
    if (this.inputName == null)
    {
      this.inputName = new TextInput(null);
      this.inputName.setName(i18n.tr("Bezeichnung des Kontenrahmens"));
      this.inputName.setMandatory(true);
      this.inputName.setMaxLength(100);
    }
    return this.inputName;
  }
  
  /**
   * Liefert ein Label fuer Fehlermeldungen.
   * @return Label.
   */
  public LabelInput getError()
  {
    if (this.error == null)
    {
      this.error = new LabelInput("");
      this.error.setColor(Color.ERROR);
      this.error.setName("");
    }
    return this.error;
  }
  
  /**
   * Liefert den ausgewaehlten Kontenrahmen.
   * @return der ausgewaehlte Kontenrahmen.
   */
  public Kontenrahmen getKontenrahmen()
  {
    return this.kontenrahmen;
  }
  
  /**
   * Liefert den optionalen Mandanten.
   * @return der Mandant.
   */
  public Mandant getMandant()
  {
    return this.mandant;
  }
  
  /**
   * Liefert den Namen des Kontenrahmens.
   * @return Name des Kontenrahmens.
   */
  public String getName()
  {
    return this.name;
  }
}



/**********************************************************************
 * $Log: KontenrahmenCloneDialog.java,v $
 * Revision 1.1  2011/03/21 11:17:27  willuhn
 * @N BUGZILLA 1004
 *
 **********************************************************************/