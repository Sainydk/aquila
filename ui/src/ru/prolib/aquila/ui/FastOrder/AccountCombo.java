package ru.prolib.aquila.ui.FastOrder;

import java.util.Vector;

import javax.swing.JComboBox;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * Селектор торгового счета.
 */
public class AccountCombo extends JComboBox implements Starter, EventListener {
	private static final long serialVersionUID = -7501535410571675108L;
	private final Portfolios portfolios;
	private final Vector<Portfolio> list = new Vector<Portfolio>();

	public AccountCombo(Portfolios portfolios) {
		super();
		this.portfolios = portfolios;
	}

	@Override
	public void onEvent(Event event) {
		if ( event.isType(portfolios.OnPortfolioAvailable()) ) {
			addPortfolio(((PortfolioEvent) event).getPortfolio());
		}
	}

	@Override
	public void start() {
		portfolios.OnPortfolioAvailable().addListener(this);
		list.clear();
		removeAllItems();
		for ( Portfolio portfolio : portfolios.getPortfolios() ) {
			addPortfolio(portfolio);
		}
	}

	@Override
	public void stop() {
		portfolios.OnPortfolioAvailable().removeListener(this);
	}
	
	/**
	 * Добавить портфель для выбора.
	 * <p>
	 * @param portfolio портфель
	 */
	private void addPortfolio(Portfolio portfolio) {
		if ( ! list.contains(portfolio) ) {
			list.add(portfolio);
			addItem(portfolio.getAccount());
		}
	}
	
	/**
	 * Получить торговый счет выбранного портфеля.
	 * <p>
	 * @return торговый счет или null, если портфель не выбран
	 */
	public Account getSelectedAccount() {
		Portfolio portfolio = getSelectedPortfolio();
		return portfolio != null ? portfolio.getAccount() : null;
	}
	
	/**
	 * Получить экземпляр выбранного портфеля.
	 * <p>
	 * @return выбранный портфель или null, если портфель не выбран
	 */
	public Portfolio getSelectedPortfolio() {
		int index = getSelectedIndex();
		return index >= 0 ? list.get(index) : null;
	}
	
	/**
	 * Проверить факт выбора портфеля.
	 * <p>
	 * @return true - портфель выбран, false - портфель не выборан
	 */
	public boolean isSelected() {
		return getSelectedPortfolio() != null;
	}

}
