package net.riccardocossu.i18split.base.driver;

import net.riccardocossu.i18split.base.model.DataRow;

public interface OutputDriver extends FilterDriver {

	void writeRow(DataRow data)
}
