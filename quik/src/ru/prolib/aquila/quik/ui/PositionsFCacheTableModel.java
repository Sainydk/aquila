package ru.prolib.aquila.quik.ui;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.quik.dde.*;
import ru.prolib.aquila.ui.ClassLabels;

/**
 * Модель таблицы для отображения кэша позиций по деривативам.
 */
public class PositionsFCacheTableModel extends TableModel
	implements EventListener
{
	private static final long serialVersionUID = 3346874631502525141L;
	private final PositionsFCache cache;

	public PositionsFCacheTableModel(ClassLabels labels, Columns columns,
			PositionsFCache cache)
	{
		super(labels, columns);
		this.cache = cache;
	}

	protected void invalidate() {
		rows = cache.getAll();
		super.invalidate();
	}
	
	@Override
	public void start() {
		cache.OnCacheUpdate().addListener(this);		
		super.start();
	}

	@Override
	public void stop() {
		super.stop();
		cache.OnCacheUpdate().removeListener(this);
	}

	@Override
	public void onEvent(Event event) {
		invalidate();
	}

}