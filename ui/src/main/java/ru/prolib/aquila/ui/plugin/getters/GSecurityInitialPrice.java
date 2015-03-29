package ru.prolib.aquila.ui.plugin.getters;

import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.data.GDouble;
import ru.prolib.aquila.core.data.ValueException;

public class GSecurityInitialPrice extends GDouble {
	
	@Override
	public Double get(Object obj) throws ValueException {
		Security o = (Security) obj;
		return super.get(o.getInitialPrice());
	}

}
