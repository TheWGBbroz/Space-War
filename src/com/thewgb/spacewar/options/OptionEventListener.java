package com.thewgb.spacewar.options;

public interface OptionEventListener {
	public default void optionChangeEvent(String key, Object oldValue, Object newValue) {
	}
	public default void optionSaveEvent(boolean succeed) {
	}
}
