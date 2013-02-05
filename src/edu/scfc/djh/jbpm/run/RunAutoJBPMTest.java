package edu.scfc.djh.jbpm.run;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.zip.ZipInputStream;

import junit.framework.TestCase;

import org.jbpm.api.Configuration;
import org.jbpm.api.ExecutionService;
import org.jbpm.api.ProcessDefinition;
import org.jbpm.api.ProcessEngine;
import org.jbpm.api.ProcessInstance;
import org.jbpm.api.RepositoryService;
import org.jbpm.api.TaskService;
import org.jbpm.api.model.ActivityCoordinates;
import org.jbpm.api.task.Task;

import edu.scfc.djh.jbpm.xml.node.DOM4jReader;
import edu.scfc.djh.jbpm.xml.node.JBPMNode;

public class RunAutoJBPMTest extends TestCase {

	// 流程引擎
	ProcessEngine processEngine = Configuration.getProcessEngine();
	RepositoryService repositoryService = processEngine.getRepositoryService();
	ExecutionService executionService = processEngine.getExecutionService();
	TaskService taskService = processEngine.getTaskService();
	
	Scanner scanner = new Scanner(System.in);  // 获取必要的输入

	public void deploy() throws Exception {
	    // 发布流程
//		String deploy_id = repositoryService.createDeployment().addResourceFromClasspath("leave.jpdl.xml").deploy();

	    ZipInputStream zis = new ZipInputStream(new RunAutoJBPMTest().getClass().getResourceAsStream("/leave.zip"));    // 取得流程文件输入流
		String deploy_id = repositoryService.createDeployment().addResourcesFromZipInputStream(zis).deploy();            // 部署
		
		String deployedProcessID = findDeployProcessNameByDeloyId(deploy_id);
		System.out.println("是否启动流程： \t  deploy_id => "+ deploy_id +"		deployName => "+ deployedProcessID);
		System.out.println("1   : 是 \n其它 : 否");
		if(scanner.nextInt() == 1) {
			startProcessInstanceById(deployedProcessID);
			
			System.out.println("流程已启动, 进入自动执行  > > >");
			autoExecuteProcess();
		}
		
		findActiveProcess();
		removeAllDefinitionProcess();
		System.out.println("已自动删除所有定义流程");
	}
	public void startProcessInstanceById(String deployName) {
//		ProcessInstance processInstance = executionService.startProcessInstanceByKey("leave");		
		ProcessInstance processInstance = executionService.startProcessInstanceById(deployName);
	}
	public void start() {
//		ProcessInstance processInstance = executionService.startProcessInstanceByKey("leave");		
		ProcessInstance processInstance = executionService.startProcessInstanceById("leave-1");
	}
	
	String username = "owner";		// owner	manager		boss
	public void findMyProcess() throws Exception{
		List<Task> taskList = taskService.findPersonalTasks(username );
		System.out.println("taskList - size:"+ taskList.size());
		DOM4jReader d = new DOM4jReader();
		for (Task task : taskList) {
			ProcessInstance processInstance = executionService.findProcessInstanceById(task.getExecutionId());
			Set<String> activityNames = processInstance.findActiveActivityNames();
			ActivityCoordinates ac = repositoryService.getActivityCoordinates(processInstance.getProcessDefinitionId(),activityNames.iterator().next());
			System.out.println(username +": 需要处理的流程:\t   < taskId:"+ task.getId() +"\t Name:"+ task.getName());

		    List<JBPMNode> tree = d.parseToTree(Helper.getZipXMLContent("G:/JavaStudy/java_workspace_demo/autojbpm/src/leave.zip"));
			List<JBPMNode> lines = d.getTransition(task.getName(), ac.getX() +","+ ac.getY() +","+ ac.getWidth() +","+ ac.getHeight());

			if(lines != null && lines.size() > 0) {
				System.out.println("\t 可使用的按钮有： count:"+ lines.size());
                Iterator<JBPMNode> iterLine = lines.iterator();
                for(JBPMNode node : lines) {
                	System.out.println("\t\t"+ node.getType() +"> 按钮名 => "+ node.getName());
                }
            } else { System.out.println("没有可使用的按钮"); } // 当返回值为null 时,表明当前节点只包含一个 transiation,此时可直接执行 taskService.completeTask(taskId)
		}
	}
	public void autoExecuteProcess() throws Exception {
		boolean quit = false;
		String allUsers = "owner|manager|boss";
		String c ;  // choice
		while(quit == false) {
			System.out.println("请输入用户名   : ");
			c = scanner.next();
			if(allUsers.contains(c)) {   // 当输入有效用户名，处理用户任务
				this.username = c;
				executeMyProcess();
			} else { System.out.println("已退出"); return; }          // 当输入无效用户名，退出
		}
	}
	String paramKey = "day";               // key                                 // decision 节点需要
	int paramValue = 5;                    // value  执行时变量， 单位：day         // decision 节点需要
	Map<String, Object> param = new HashMap<String, Object>();
	public void executeMyProcess() throws Exception{
		param.put(paramKey, paramValue);   // 一点点初始化
		
		int lineCount = 0;
		
		List<Task> taskList = taskService.findPersonalTasks(username );
		DOM4jReader d = new DOM4jReader();
		String taskId = "";
		String lineTo = "";
		int count = 0; Map<Integer, String> choices = new HashMap<Integer, String>();
		for (Task task : taskList) {
			ProcessInstance processInstance = executionService.findProcessInstanceById(task.getExecutionId());
			Set<String> activityNames = processInstance.findActiveActivityNames();
			ActivityCoordinates ac = repositoryService.getActivityCoordinates(processInstance.getProcessDefinitionId(),activityNames.iterator().next());
			
		    List<JBPMNode> tree = d.parseToTree(Helper.getZipXMLContent("G:/JavaStudy/java_workspace_demo/autojbpm/src/leave.zip"));
			List<JBPMNode> lines = d.getTransition(task.getName(), ac.getX() +","+ ac.getY() +","+ ac.getWidth() +","+ ac.getHeight());

			if(lines != null && lines.size() > 0) {
				System.out.println("可使用的按钮有： count:"+ lines.size());
                for(JBPMNode node : lines) {
                	choices.put(++count, node.getName());
                	System.out.println(node.getType() +" 按钮名  : "+ count +"=> "+ node.getName());  // 当返回值为null 时,表明当前节点只包含一个 transiation,此时可直接执行 taskService.completeTask(taskId)
                }
                lineCount = lines.size();
            } else { System.out.println("没有使用的按钮"); }
			
			taskId = task.getId();
			
			if(lineCount == 1){ // 若当前节点的 transiation 数等于 1
				System.out.println("当前节点只有一个 transiation ,默认继续向后执行  > > >");
				// taskService.completeTask(taskId);
				taskService.completeTask(taskId, param);
			} else if(lineCount > 1) { // 若当前节点的 transiation 数大于 1
				System.out.println("当前节点有多个 transiation ,请选择一个决定执行：");
				try{
					lineTo = choices.get(scanner.nextInt());
					// taskService.completeTask(taskId, lineTo);
					taskService.completeTask(taskId, lineTo, param);
				} catch(Exception e) { System.out.println("你没选对   0. 0~"); }
			} else { System.out.println("这不科学 :-("); }
		}
		
		if(taskList.size() <= 0) {
			System.out.println("你当前没有待办任务.");
		}
		
	}
	public void findDeployProcess(){
	    List<ProcessDefinition> pdList = repositoryService.createProcessDefinitionQuery().list();
	    if(pdList.size() <= 0){
	    	System.out.println("没有部署任何流程");
	    } else {
			for (ProcessDefinition pd : pdList) {
		        System.out.println("已部署流程:\t Id:"+ pd.getId() +
		        		"\t Name:"+ pd.getName() +
		        		"\t Version:"+ pd.getVersion() +
		        		"\t DeployId:"+ pd.getDeploymentId());
	        }
	    }
	}
	public String findDeployProcessNameByDeloyId(String deploy_id) throws Exception{
		List<ProcessDefinition> pdList = repositoryService.createProcessDefinitionQuery().list();
	    if(pdList.size() <= 0){
	    	System.out.println("没有部署任何流程");
	    } else {
			for (ProcessDefinition pd : pdList) {
		        System.out.println("已部署流程:\t Id:"+ pd.getId() +
		        		"\t Name:"+ pd.getName() +
		        		"\t Version:"+ pd.getVersion() +
		        		"\t DeployId:"+ pd.getDeploymentId());
		        if(deploy_id.equals(pd.getDeploymentId())) {
		        	System.out.println("找到已部署流程: id => "+ pd.getId());
		        	return pd.getId();
		        }
	        }
	    }
	    
	    throw new Exception("没有找到该流程");
	}
	public void findActiveProcess(){
		List<ProcessInstance> piList = executionService.createProcessInstanceQuery().list();
		if(piList.size() <= 0){
			System.out.println("无活动流程");
		} else {
			System.out.println("活动流程数为 ： "+ piList.size());
			for (ProcessInstance pi : piList) {
				System.out.println("活动流程:\t Id:"+ pi.getId() +
						"\t ActiveActivityNames:"+pi.findActiveActivityNames() +
						"\t State:"+ pi.getState());
			}
		}
	}
	public void removeAllDefinitionProcess(){
		List<ProcessDefinition> pdList = repositoryService.createProcessDefinitionQuery().list();
		System.out.println("存在流程定义数 ： DefinitionProcess - count:" + pdList.size());
		for (ProcessDefinition pd : pdList) {
		        repositoryService.deleteDeploymentCascade(pd.getDeploymentId());
        }
		pdList = repositoryService.createProcessDefinitionQuery().list();
		System.out.println("存在流程定义数 ： DefinitionProcess - count:" + pdList.size());
	}
	public void killActiveProcessAll(){
		List<ProcessInstance> piList = executionService.createProcessInstanceQuery().list();
		System.out.println("piList - size:"+ piList.size());
	    for(ProcessInstance pInstance : piList)
	    {
//	        System.out.println("\t"+ pInstance.getId());
//	        System.out.println("\t"+ pInstance.findActiveActivityNames());
//	        System.out.println("\t"+ pInstance.getState());
	        executionService.endProcessInstance(pInstance.getId(), "cancle");
	    }
	    piList = executionService.createProcessInstanceQuery().list();
	    System.out.println("piList - size:"+ piList.size());
	}

}
