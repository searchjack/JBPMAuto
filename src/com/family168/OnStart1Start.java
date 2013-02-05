package com.family168;

import org.jbpm.api.listener.*;

public class OnStart1Start implements EventListener {
	public void notify(EventListenerExecution execution) {
		System.out.println("------> OnStart1Start.java");
	}
}
