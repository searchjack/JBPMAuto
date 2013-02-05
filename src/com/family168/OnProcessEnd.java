package com.family168;

import org.jbpm.api.listener.*;

public class OnProcessEnd implements EventListener {
	public void notify(EventListenerExecution execution) {
		System.out.println("------> OnProcessEnd.java");
	}
}
