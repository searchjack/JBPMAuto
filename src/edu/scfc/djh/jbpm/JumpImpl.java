package edu.scfc.djh.jbpm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.zip.ZipInputStream;

import junit.framework.TestCase;

import org.jbpm.api.Configuration;
import org.jbpm.api.ExecutionService;
import org.jbpm.api.ProcessDefinition;
import org.jbpm.api.ProcessEngine;
import org.jbpm.api.ProcessInstance;
import org.jbpm.api.RepositoryService;
import org.jbpm.test.JbpmTestCase;

public class JumpImpl extends TestCase
{

    private String deploymentId;
    ProcessEngine processEngine = Configuration.getProcessEngine();
    
    RepositoryService repositoryService = processEngine.getRepositoryService();

    @Override
    protected void setUp() throws Exception
    {
        // 第一种发布方式
//         deploymentId = repositoryService.createDeployment().addResourceFromClasspath("leave.jpdl.xml").deploy();

        // 第二种发布方式
//        ZipInputStream zis = new ZipInputStream(this.getClass().getResourceAsStream("/leave.zip"));
//        repositoryService.createDeployment().addResourcesFromZipInputStream(zis).deploy();
    }

    @Override
    protected void tearDown() throws Exception
    {
//        repositoryService.deleteDeploymentCascade(deploymentId);
    }

    public void testGo()
    {
        List<ProcessDefinition> pdList = repositoryService.createProcessDefinitionQuery().list();
        for (ProcessDefinition pd : pdList)
        {
            pd.getId();
            pd.getName();
            pd.getVersion();
            pd.getDeploymentId();
            System.out.println("请要选择启动的流程 ID:" + pd.getId());

            // remove
            // repositoryService.deleteDeploymentCascade(pd.getDeploymentId());
        }
        // start process
        Map map = new HashMap();
        map.put("owner", "user");
            // <task assignee="#{owner}" form="request.jsp" g="172,118,92,52" name="申请">
            //  <transition to="经理审批"/>
            // </task>
        Scanner in = new Scanner(System.in) ;
        ExecutionService executionService = processEngine.getExecutionService();
        ProcessInstance pi = executionService.startProcessInstanceById(in.next(), map);
        System.out.println("pid _" + pi.getId());

//        executionService.deleteProcessInstanceCascade(pi.getId());
//        executionService.deleteProcessInstance(pi.getId());
//        executionService.endProcessInstance(pi.getId(), "cancle");

        System.out.println("是否结束:" + pi.isEnded());

    }
}
