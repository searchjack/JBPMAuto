package com.family168;

import org.jbpm.api.listener.*;

public class OnEnd1Start implements EventListener {
	public void notify(EventListenerExecution execution) {
		System.out.println("------> OnEnd1Start.java");
	}
}
