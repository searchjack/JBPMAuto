package edu.scfc.djh.jbpm;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.jbpm.api.Configuration;
import org.jbpm.api.ExecutionService;
import org.jbpm.api.ProcessEngine;
import org.jbpm.api.ProcessInstance;
import org.jbpm.api.RepositoryService;
import org.jbpm.api.TaskService;
import org.jbpm.api.model.ActivityCoordinates;

public class Test extends TestCase
{
    public void testMe()
    {
        ProcessEngine processEngine = Configuration.getProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();

        ExecutionService executionService = processEngine.getExecutionService();
        TaskService taskService = processEngine.getTaskService();

        Map map = new HashMap();
        map.put("owner", "user");

        ProcessInstance processInstance = executionService.findProcessInstanceById("leave-1");
        Set<String> activityNames = processInstance.findActiveActivityNames();
        ActivityCoordinates ac = repositoryService.getActivityCoordinates(processInstance.getProcessDefinitionId(),activityNames.iterator().next());
        ac.getX();
        ac.getY();
        ac.getWidth();
        ac.getHeight();

//        repositoryService.get
        

        ProcessInstance pi = executionService.startProcessInstanceById("leave-1", map);
        Set<String> names = pi.findActiveActivityNames();
        for (String name : names)
        {
            System.out.println(name);
        }
    }
}
