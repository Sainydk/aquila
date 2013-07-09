package ru.prolib.aquila.ib.assembler;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.ib.IBEditableTerminal;
import ru.prolib.aquila.ib.assembler.cache.*;

/**
 * Среднеуровневые функции сборки. 
 */
public class AssemblerMidLvl {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(AssemblerMidLvl.class);
	}
	
	private final AssemblerLowLvl low;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param low набор низкоуровневых функций сборки
	 */
	AssemblerMidLvl(AssemblerLowLvl low) {
		super();
		this.low = low;
	}
	
	/**
	 * Конструктор на основе терминала.
	 * <p>
	 * Создает и использует набор низкоуровневых функций сборки на основании
	 * указанного терминала.
	 * <p>
	 * @param terminal экземпляр терминала
	 */
	AssemblerMidLvl(IBEditableTerminal terminal) {
		this(new AssemblerLowLvl(terminal));
	}
	
	/**
	 * Получить низкоуровневые функции сборки.
	 * <p>
	 * <b>Прим.</b> Служебный метод.
	 * <p>
	 * @return набор методов
	 */
	AssemblerLowLvl getLowLevelAssembler() {
		return low;
	}
	
	/**
	 * Получить терминал.
	 * <p>
	 * @return экземпляр терминала
	 */
	IBEditableTerminal getTerminal() {
		return low.getTerminal();
	}
	
	/**
	 * Получить фасад кэша данных.
	 * <p>
	 * @return кэш данных
	 */
	Cache getCache() {
		return low.getCache();
	}
	
	/**
	 * Согласовать атрибуты инструмента.
	 * <p>
	 * @param entry кэш-запись деталей контракта
	 */
	public void update(ContractEntry entry) {
		try {
			EditableSecurity security = low.getSecurity(entry);
			low.update(security, entry);
			if ( ! security.isAvailable() ) {
				low.startMktData(security, entry);
			}
			low.fireEvents(security);
		} catch ( Exception e ) {
			logger.error("Error update security: ", e);
		}
	}
	
	/**
	 * Согласовать атрибуты портфеля.
	 * <p>
	 * @param entry кэш-запись значения атрибута портфеля
	 */
	public void update(PortfolioValueEntry entry) {
		try {
			EditablePortfolio portfolio = low.getPortfolio(entry.getAccount());
			low.update(portfolio, entry);
			if ( low.isAvailable(portfolio) ) {
				low.fireEvents(portfolio);
			}
		} catch ( Exception e ) {
			logger.error("Error update portfolio: ", e);
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != AssemblerMidLvl.class ) {
			return false;
		}
		AssemblerMidLvl o = (AssemblerMidLvl) other;
		return new EqualsBuilder()
			.append(o.low, low)
			.isEquals();
	}
	
	/**
	 * Согласовать атрибуты позиции.
	 * <p>
	 * @param entry кэш-запись позиции
	 */
	public void update(PositionEntry entry) {
		try {
			Security security = low.getSecurity(entry.getContractId());
			if ( security == null ) {
				return;
			}
			EditablePortfolio portfolio = low.getPortfolio(entry.getAccount());
			EditablePosition position = portfolio.getEditablePosition(security);
			low.update(position, entry);
			low.fireEvents(position);
		} catch ( Exception e ) {
			logger.error("Error update position: ", e);
		}
	}
	
}
