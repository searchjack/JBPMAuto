package com.family168;

import org.jbpm.api.listener.*;

public class OnEnd1End implements EventListener {
	public void notify(EventListenerExecution execution) {
		System.out.println("------> OnEnd1End.java");
	}
}
