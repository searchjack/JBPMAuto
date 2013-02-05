package com.family168;

import org.jbpm.api.listener.*;

public class OnStart1End implements EventListener {
	public void notify(EventListenerExecution execution) {
		System.out.println("------> OnStart1End.java");
	}
}
