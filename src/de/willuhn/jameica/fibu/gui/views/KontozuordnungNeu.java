package de.willuhn.jameica.fibu.gui.views;

import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.gui.action.DBObjectDelete;
import de.willuhn.jameica.fibu.gui.controller.KontozuordnungControl;
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
 * @author henken
 */
public class KontozuordnungNeu extends AbstractView implements Extendable
{
	  private final static I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

	  private Container container             = null;
	  private KontozuordnungControl control = null;

	  /**
	   * @see de.willuhn.jameica.gui.AbstractView#bind()
	   */
	  public void bind() throws Exception
	  {
			GUI.getView().setTitle(i18n.tr("Kontozuordnung bearbeiten"));

	    this.control   = new KontozuordnungControl(this);
	    
	    this.container = new SimpleContainer(getParent());
	    this.container.addHeadline(i18n.tr("Eigenschaften"));
	    this.container.addInput(control.getBezeichnung());
	    this.container.addInput(control.getKontoAuswahl());
	    this.container.addInput(control.getHbKontoAuswahl());

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
	  public KontozuordnungControl getControl()
	  {
	    return this.control;
	  }
	  
	}