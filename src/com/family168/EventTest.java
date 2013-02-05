package com.family168;

import junit.framework.TestCase;
import org.jbpm.api.*;
import org.jbpm.api.model.ActivityCoordinates;
import org.jbpm.api.task.Task;

import edu.scfc.djh.jbpm.run.Helper;
import edu.scfc.djh.jbpm.xml.node.DOM4jReader;
import edu.scfc.djh.jbpm.xml.node.JBPMNode;

import java.util.*;

public class EventTest extends TestCase {
	ProcessEngine processEngine = Configuration.getProcessEngine();
	RepositoryService repositoryService = processEngine.getRepositoryService();
	ExecutionService executionService = processEngine.getExecutionService();
	TaskService taskService = processEngine.getTaskService();
	
	Scanner scanner = new Scanner(System.in);  // 获取必要的输入
	static int C = 0 ;

	public void deploy() {
		String deploymentId = repositoryService.createDeployment().addResourceFromClasspath("event.jpdl.xml").deploy();

		executionService.startProcessInstanceByKey("event");
	}
	Map<String, Object> param = new HashMap<String, Object>();
	public void execute() throws Exception {
		String username = "";
		
		List<Task> taskList = taskService.findPersonalTasks(username);
		for (Task task : taskList) {
			String taskId = task.getId();			
			try{
				String lineTo = "to end1";
				// taskService.completeTask(taskId, lineTo);
				taskService.completeTask(taskId, lineTo, param);
			} catch(Exception e) { System.out.println("你没选对   0. 0~"); }
		
		}

		if(taskList.size() <= 0) {
			System.out.println("你当前没有待办任务.");
		}
	}
}
