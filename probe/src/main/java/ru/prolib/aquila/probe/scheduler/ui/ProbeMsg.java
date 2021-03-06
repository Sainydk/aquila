package ru.prolib.aquila.probe.scheduler.ui;

import ru.prolib.aquila.core.text.Messages;
import ru.prolib.aquila.core.text.MsgID;

public class ProbeMsg {
	static final String SECTION_ID = "Probe";
	
	static {
		Messages.registerLoader(SECTION_ID, ProbeMsg.class.getClassLoader());
	}
	
	static MsgID newMsgID(String messageID) {
		return new MsgID(SECTION_ID, messageID);
	}
	
	public static final MsgID TOOLBAR_TITLE = newMsgID("TOOLBAR_TITLE");
	
	/**
	 * Tooltip of the "options" button.
	 */
	public static final MsgID BTN_TTIP_OPTIONS = newMsgID("BTN_TTIP_OPTIONS");
	
	/**
	 * Tooltip of the "pause" button.
	 */
	public static final MsgID BTN_TTIP_PAUSE = newMsgID("BTN_TTIP_PAUSE");
	
	/**
	 * Tooltip of the "run" button.
	 */
	public static final MsgID BTN_TTIP_RUN = newMsgID("BTN_TTIP_RUN");
	
	/**
	 * Tooltip of the "run up to time" button. 
	 */
	public static final MsgID BTN_TTIP_RUN_TIME = newMsgID("BTN_TTIP_RUN_TIME");
	
	/**
	 * Tooltip of the "run to the end of interval" button.
	 */
	public static final MsgID BTN_TTIP_RUN_INTERVAL = newMsgID("BTN_TTIP_RUN_INTERVAL");
	
	/**
	 * Tooltip of the "run step" button.
	 */
	public static final MsgID BTN_TTIP_RUN_STEP = newMsgID("BTN_TTIP_RUN_STEP");
	
	/**
	 * Tooltip of the "view list of scheduled tasks" button.
	 */
	public static final MsgID BTN_TTIP_LIST = newMsgID("BTN_TTIP_LIST");
	
	public static final MsgID SOD_DIALOG_TITLE = newMsgID("SOD_DIALOG_TITLE");
	public static final MsgID SOD_EXEC_SPEED = newMsgID("SOD_EXEC_SPEED");
	public static final MsgID SOD_EXEC_SPEED0 = newMsgID("SOD_EXEC_SPEED0");
	public static final MsgID SOD_EXEC_SPEED1 = newMsgID("SOD_EXEC_SPEED1");
	public static final MsgID SOD_EXEC_SPEED2 = newMsgID("SOD_EXEC_SPEED2");
	public static final MsgID SOD_EXEC_SPEED4 = newMsgID("SOD_EXEC_SPEED4");
	public static final MsgID SOD_EXEC_SPEED8 = newMsgID("SOD_EXEC_SPEED8");
	public static final MsgID SOD_INTERVAL = newMsgID("SOD_INTERVAL");
	public static final MsgID SOD_INTERVAL_1MIN = newMsgID("SOD_INTERVAL_1MIN");
	public static final MsgID SOD_INTERVAL_5MIN = newMsgID("SOD_INTERVAL_5MIN");
	public static final MsgID SOD_INTERVAL_10MIN = newMsgID("SOD_INTERVAL_10MIN");
	public static final MsgID SOD_INTERVAL_15MIN = newMsgID("SOD_INTERVAL_15MIN");
	public static final MsgID SOD_INTERVAL_30MIN = newMsgID("SOD_INTERVAL_30MIN");
	public static final MsgID SOD_INTERVAL_1HOUR = newMsgID("SOD_INTERVAL_1HOUR");
	
	public static final MsgID SSP_CURRENT_TIME = newMsgID("SSP_CURRENT_TIME");
	public static final MsgID SSP_CURRENT_MODE = newMsgID("SSP_CURRENT_MODE");
	public static final MsgID SSP_CUTOFF_TIME = newMsgID("SSP_CUTOFF_TIME");
	public static final MsgID SSP_MODE_CLOSE = newMsgID("SSP_MODE_CLOSE");
	public static final MsgID SSP_MODE_WAIT = newMsgID("SSP_MODE_WAIT");
	public static final MsgID SSP_MODE_RUN = newMsgID("SSP_MODE_RUN");
	public static final MsgID SSP_MODE_STEP = newMsgID("SSP_MODE_STEP");
	public static final MsgID SSP_MODE_CUTOFF = newMsgID("SSP_MODE_CUTOFF");
	public static final MsgID SSP_EXEC_SPEED = newMsgID("SSP_EXEC_SPEED");
	
	public static final MsgID STD_DIALOG_TITLE = newMsgID("STD_DIALOG_TITLE");
	public static final MsgID STD_TREE_ROOT = newMsgID("STD_TREE_ROOT");
	public static final MsgID STD_DEFAULT_FILTER = newMsgID("STD_DEFAULT_FILTER");
	public static final MsgID STD_MENU_FILTERS = newMsgID("STD_MENU_FILTERS");
	public static final MsgID STD_MENU_FILTER_SETUP = newMsgID("STD_MENU_FILTER_SETUP");
	
	public static final MsgID STD_SUS_FILTER_MAIN_MENU = newMsgID("STD_SUS_FILTER_MAIN_MENU");
	public static final MsgID STD_SUS_FILTER_DIALOG_TITLE = newMsgID("STD_SUS_FILTER_DIALOG_TITLE");

}
