package ru.prolib.aquila.ui.plugin;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.ui.*;
import ru.prolib.aquila.ui.form.SelectSecurityDialog;
import ru.prolib.aquila.ui.msg.SecurityMsg;
import ru.prolib.aquila.ui.plugin.getters.GSecurity;
import ru.prolib.aquila.ui.wrapper.*;

/**
 * Плагин отображающий доступные инструменты в виде таблицы на вкладке.
 * <p>
 * 2013-02-28<br>
 * $Id: UISecuritiesPlugin.java 558 2013-03-04 17:21:48Z whirlwind $
 */
public class UISecuritiesPlugin implements AquilaPlugin {
	public static final String TEXT_SECT = SecurityMsg.SECTION_ID;
	public static final String TITLE = SecurityMsg.SECURITIES_TITLE;
	public static final String MENU_SECURITIES = SecurityMsg.SECURITIES_MENU;
	
	private Terminal terminal;
	private SecuritiesTableCols cols = new SecuritiesTableCols();
	private TableModel model = new TableModelImpl(new GSecurity());
	private Table tb;
	private JPanel panel = new JPanel(new BorderLayout());
	
	public UISecuritiesPlugin() {
		super();		
	}
	
	public JPanel getPanel() {
		return panel;
	}
	
	public SecuritiesTableCols getTableCols() {
		return cols;
	}
	
	public void setTableCols(SecuritiesTableCols cols) {
		this.cols = cols;
	}
	
	public TableModel getModel() {
		return model;
	}

	@Override
	public void
		initialize(ServiceLocator locator, Terminal terminal, String arg)
	{
		this.terminal = terminal;
	}
	
	public Terminal getTerminal() {
		return terminal;
	}

	@Override
	public void createUI(final AquilaUI facade) throws Exception {
		EventSystem ev = ((ServiceLocator) facade).getEventSystem();
		EventDispatcher dispatcher = ev.createEventDispatcher();
		ClassLabels text = facade.getTexts().get(TEXT_SECT);
		
		cols.addColumnsToModel(model, text);

		DataSourceEventTranslator onRowAvailableListener = new DataSourceEventTranslator(
				dispatcher, dispatcher.createType());
		model.setOnRowAvailableListener(onRowAvailableListener);
		
		
		DataSourceEventTranslator onRowChangedListener = new DataSourceEventTranslator(
				dispatcher, dispatcher.createType());
		model.setOnRowChangedListener(onRowChangedListener);		
		
		tb = new TableImpl(model, dispatcher, dispatcher.createType());		
		tb.start();
		
		facade.addTab(text.get(TITLE), panel);
        panel.add(new JScrollPane(tb.getUnderlayed()));
		
        facade.getMainMenu().addMenu(MENU_SECURITIES, text.get(MENU_SECURITIES));
        // Test security selector dialog
        facade.getMainMenu().getMenu(MENU_SECURITIES)
        	.addBottomItem("SELECT_SECURITY", "Select security (test)")
        	.getUnderlyingObject().addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					SelectSecurityDialog dialog = new SelectSecurityDialog(facade.getMainFrame(), facade.getTexts());
					dialog.add(terminal);
					dialog.pack();
					dialog.setModal(true);
					dialog.setVisible(true);
					Security security = dialog.getSelectedSecurity();
					JOptionPane.showMessageDialog(null, "Selected: "
						+ (security == null ? null : security.getDescriptor()));
				}
        });
	}
	
	@Override
	public void start() throws StarterException {
		terminal.OnSecurityAvailable()
			.addListener(model.getOnRowAvailableListener());
		terminal.OnSecurityChanged()
			.addListener(model.getOnRowChangedListener());
	}
	
	@Override
	public void stop() throws StarterException {
		terminal.OnSecurityAvailable()
			.removeListener(model.getOnRowAvailableListener());
		terminal.OnSecurityChanged()
			.removeListener(model.getOnRowChangedListener());
	}

}
