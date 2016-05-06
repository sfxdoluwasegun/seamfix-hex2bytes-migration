package com.sf.hx.util;

public class GlobalState {

	private boolean terminateProcess = false;
	private static GlobalState instance;

	private GlobalState() {
	}

	public static GlobalState getInstance() {
		if (instance == null) {
			synchronized (GlobalState.class) {
				instance = new GlobalState();
			}
		}
		return instance;
	}

	public boolean isTerminateProcess() {
		return terminateProcess;
	}

	public void setTerminateProcess(boolean terminateProcess) {
		this.terminateProcess = terminateProcess;
	}

}
