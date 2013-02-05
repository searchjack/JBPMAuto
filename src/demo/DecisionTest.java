package demo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.zip.ZipInputStream;

import junit.framework.TestCase;

import org.jbpm.api.Configuration;
import org.jbpm.api.ExecutionService;
import org.jbpm.api.ProcessEngine;
import org.jbpm.api.ProcessInstance;
import org.jbpm.api.RepositoryService;
import org.jbpm.api.TaskService;
import org.jbpm.api.model.ActivityCoordinates;
import org.jbpm.api.task.Task;

import edu.scfc.djh.jbpm.run.Helper;
import edu.scfc.djh.jbpm.run.RunAutoJBPMTest;
import edu.scfc.djh.jbpm.xml.node.DOM4jReader;
import edu.scfc.djh.jbpm.xml.node.JBPMNode;

public class DecisionTest extends TestCase {

	// 流程引擎
	ProcessEngine processEngine = Configuration.getProcessEngine();
	RepositoryService repositoryService = processEngine.getRepositoryService();
	ExecutionService executionService = processEngine.getExecutionService();
	TaskService taskService = processEngine.getTaskService();

	public void deploy() {
//		String deploy_id = repositoryService.createDeployment().addResourceFromClasspath("leave.jpdl.xml").deploy();

		ZipInputStream zis = new ZipInputStream(new RunAutoJBPMTest().getClass().getResourceAsStream("/leave.zip"));
		String deploy_id = repositoryService.createDeployment().addResourcesFromZipInputStream(zis).deploy();
	}
	public void start() {
		ProcessInstance processInstance = executionService.startProcessInstanceById("leave-1");
	}
	/*
	 * 
	 * 
	 * 对于处理 decision 节点
	 * 		| 必须提供所需的参 数名->参数值
	 * 		| 必须提供表达式   -- < 在用户界面做一个小小的功能，让用户轻松设计出此表达式 >
	 */
	String username = "boss";		        // owner	manager		boss
	String paramName = "day";
	int paramValue = 5;                    // 执行时变量， 单位：day
	Map<String, Object> param = new HashMap<String, Object>();
	public void executeProcess() throws Exception {
		param.put(paramName, paramValue);  // 一点点初始化
		
		int lineCount = 0;
		
		List<Task> taskList = taskService.findPersonalTasks(username);
		DOM4jReader d = new DOM4jReader();
		String taskId = "";
		String lineTo = "";
		int count = 0; Map<Integer, String> choices = new HashMap<Integer, String>();
		for (Task task : taskList) {
			ProcessInstance processInstance = executionService.findProcessInstanceById(task.getExecutionId());
			Set<String> activityNames = processInstance.findActiveActivityNames();
			ActivityCoordinates ac = repositoryService.getActivityCoordinates(processInstance.getProcessDefinitionId(),activityNames.iterator().next());
			
//		    List<JBPMNode> tree = d.parseToTree(Helper.getFileContent("G:/JavaStudy/java_workspace_demo/autojbpm/src/leave.jpdl.xml"));
		    List<JBPMNode> tree = d.parseToTree(Helper.getZipXMLContent("G:/JavaStudy/java_workspace_demo/autojbpm/src/leave.zip"));
			List<JBPMNode> lines = d.getTransition(task.getName(), ac.getX() +","+ ac.getY() +","+ ac.getWidth() +","+ ac.getHeight());
			System.out.println(task.getName() +"-->"+ ac.getX() +","+ ac.getY() +","+ ac.getWidth() +","+ ac.getHeight());

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
				taskService.completeTask(taskId, param);
			} else if(lineCount > 1) { // 若当前节点的 transiation 数大于 1
				System.out.println("当前节点有多个 transiation ,请选择一个决定执行：");
				Scanner scanner = new Scanner(System.in);
				try{
					lineTo = choices.get(scanner.nextInt());
					taskService.completeTask(taskId, lineTo, param);
//					taskService.completeTask(taskId, lineTo);
				} catch(Exception e) { System.out.println("你没选对   0. 0~"); }
			} else { System.out.println("这不科学 :-("); }
		}
	}

	public void getDetail() throws Exception {
		DOM4jReader d = new DOM4jReader();
//		List<JBPMNode> tree = d.parseToTree(Helper.getFileContent("G:/JavaStudy/java_workspace_demo/autojbpm/src/leave.jpdl.xml"));
		List<JBPMNode> tree = d.parseToTree(Helper.getZipXMLContent("G:/JavaStudy/java_workspace_demo/autojbpm/src/leave.zip"));
//		List<JBPMNode> lines = d.getTransition("task.getName()", "ac.getX() + , + ac.getY() + , + ac.getWidth() + , + ac.getHeight()");
		List<JBPMNode> lines = d.getTransition("exclusive1", "200,308,48,48");
		JBPMNode currentNode = d.getCurrentNode("exclusive1", "200,308,48,48");
		System.out.println(currentNode.getType() +""+ currentNode.getExpr());

//		if(lines != null && lines.size() > 0) {
//			System.out.println("\t 可使用的按钮有： count:"+ lines.size());
//            Iterator<JBPMNode> iterLine = lines.iterator();
//            for(JBPMNode node : lines) {
//            	System.out.println("\t\t"+ node.getType() +"> 按钮名 => "+ node.getName());
//            }
//        } else { System.out.println("没有使用的按钮"); }
	}
}
