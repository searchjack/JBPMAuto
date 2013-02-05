package com.family168;

import org.jbpm.api.listener.*;

public class OnState1End implements EventListener {
	public void notify(EventListenerExecution execution) {
		System.out.println("------> OnState1End.java");
	}
}
