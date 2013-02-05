package com.family168;

import org.jbpm.api.listener.*;

public class OnProcessStart implements EventListener {
	public void notify(EventListenerExecution execution) {
		System.out.println("------> OnProcessStart.java");
	}
}
