package com.family168;

import org.jbpm.api.listener.*;

public class ProcessEventListener implements EventListener {
	public void notify(EventListenerExecution execution) {
		System.out.println("------> "+ ++EventTest.C);
	}
}
