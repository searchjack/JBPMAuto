package edu.scfc.djh.jbpm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.api.Configuration;
import org.jbpm.api.ExecutionService;
import org.jbpm.api.ProcessDefinition;
import org.jbpm.api.ProcessEngine;
import org.jbpm.api.ProcessInstance;
import org.jbpm.api.RepositoryService;
import org.jbpm.api.TaskService;
import org.jbpm.api.task.Task;
import org.jbpm.test.JbpmTestCase;

public class Jump extends JbpmTestCase
{

    private String deploymentId;

    /**
     * 发布流程
     */
    protected void setUp() throws Exception
    {
        deploymentId = repositoryService.createDeployment()
                .addResourceFromClasspath("").deploy();
    }

    protected void tearDown() throws Exception
    {
        repositoryService.deleteDeploymentCascade(deploymentId);
    }

    public void testname() throws Exception
    {
        ProcessEngine processEngine = Configuration.getProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        

        ExecutionService executionService = processEngine.getExecutionService();
        TaskService taskService = processEngine.getTaskService();



        // repositoryService.createDeployment().addResourceFromClasspath("leave.jpdl.xml").deploy();
        
        //ZipInputStream zis = new ZipInputStream(this.getClass().getResourceAsStream("/leave.zip"));
        //repositoryService.createDeployment().addResourcesFromZipInputStream(zis).deploy();


        /**
         * 已发布流程
         */
        List<ProcessDefinition> pdList = repositoryService.createProcessDefinitionQuery().list();
        for (ProcessDefinition pd : pdList)
        {
            pd.getId();
            pd.getName();
            pd.getVersion();
            pd.getDeploymentId();
            pd.getId();

            // remove
            repositoryService.deleteDeploymentCascade(pd.getDeploymentId());
            // start
            Map map = new HashMap();
            map.put("owner", "user");
                // <task assignee="#{owner}" form="request.jsp" g="172,118,92,52" name="申请">
                //  <transition to="经理审批"/>
                // </task>
            executionService.startProcessInstanceById(pd.getId(), map);
        }



        /**
         * 活动的流程
         */
        List<ProcessInstance> piList = executionService.createProcessInstanceQuery().list();
        for (ProcessInstance pi : piList)
        {
            pi.getId();
            pi.findActiveActivityNames();
            pi.getState();
            // 以图形方式查看流程
            // <td><a href="view.jsp?id=<%=pi.getId() %>">view</a></td>
        }


        /**
         * 待办任务
         */
        List<Task> taskList = taskService.findPersonalTasks("user");
        for (Task task : taskList)
        {
            task.getId();
            task.getName();
            // <td><a href="<%=task.getFormResourceName() %>?id=<%=task.getId() %>">view</a></td>

            // 处理任务
            String taskId = task.getId();
            String result = "批准"; // 选择下一步的去向
            taskService.completeTask(taskId, result);
        }

    }

}
