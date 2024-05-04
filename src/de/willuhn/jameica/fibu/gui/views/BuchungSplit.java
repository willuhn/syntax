package de.willuhn.jameica.fibu.gui.views;

import de.willuhn.jameica.fibu.Fibu;
import de.willuhn.jameica.fibu.gui.action.BuchungDelete;
import de.willuhn.jameica.fibu.gui.action.BuchungNeu;
import de.willuhn.jameica.fibu.gui.action.BuchungSplitNeu;
import de.willuhn.jameica.fibu.gui.controller.BuchungSplitControl;
import de.willuhn.jameica.fibu.gui.part.BuchungSplitList;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.parts.Button;
import de.willuhn.jameica.gui.parts.ButtonArea;
import de.willuhn.jameica.gui.util.Container;
import de.willuhn.jameica.gui.util.Headline;
import de.willuhn.jameica.gui.util.SimpleContainer;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Splitet eine neue Buchung
 * @author henken
 */
public class BuchungSplit extends AbstractView
{
	
	  private final static I18N i18n = Application.getPluginLoader().getPlugin(Fibu.class).getResources().getI18N();

	  /**
	   * @see de.willuhn.jameica.gui.AbstractView#bind()
	   */
	  public void bind() throws Exception
	  {
	    final BuchungSplitControl control = new BuchungSplitControl(this);
	    
	    GUI.getView().setTitle(i18n.tr("Splitbuchung"));

	    Container group = new SimpleContainer(getParent());
	    group.addHeadline(i18n.tr("Hauptbuchung"));
	    group.addLabelPair(i18n.tr("Datum"),           control.getDatum());
	    group.addLabelPair(i18n.tr("Text"),            control.getText());
	    group.addLabelPair(i18n.tr("Beleg-Nr."),       control.getBelegnummer());
	    group.addLabelPair(i18n.tr("Ursprünglicher Gesamtbetrag"),           control.getBetrag());
	    group.addSeparator();

        group.addLabelPair(i18n.tr("Summe Splitbuchungen"), 		control.getSumme());
	    
	    ButtonArea buttons = new ButtonArea();
	    
	    Button neu = new Button(i18n.tr("Neue Splitbuchung"), new BuchungSplitNeu(),control.getBuchung(),true,"list-add.png");
	    neu.setEnabled(!control.getBuchung().getGeschaeftsjahr().isClosed());
	    buttons.addButton(neu);
	    
	    Button delete = new Button(i18n.tr("Löschen"), new BuchungDelete(),getCurrentObject(),false,"user-trash-full.png");
	    delete.setEnabled(!control.getBuchung().getGeschaeftsjahr().isClosed());
	    buttons.addButton(delete);
	    
	    Button store = new Button(i18n.tr("Speichern"), new Action()
	    {
	      public void handleAction(Object context) throws ApplicationException
	      {
	        control.handleStore();
	      }
	    },null,true,"document-save.png");
	    store.setEnabled(!control.getBuchung().getGeschaeftsjahr().isClosed());
	    buttons.addButton(store);
	    
	    control.getBelegnummer().setEnabled(false);
	    control.getDatum().setEnabled(false);
	    
	    buttons.paint(getParent());

	    new Headline(getParent(),i18n.tr("Splitbuchungen"));
	   new BuchungSplitList(control.getBuchung(),new BuchungNeu()).paint(getParent());

	  }
}
