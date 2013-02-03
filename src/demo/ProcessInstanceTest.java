package demo;

import java.util.List;

import org.jbpm.api.*;

import junit.framework.TestCase;

/**
 * 流程实例
 * @author leiwei 2012-3-20
 *
 */
public class ProcessInstanceTest extends TestCase {

	//流程引擎
	ProcessEngine processEngine;

	public ProcessInstanceTest() {
		processEngine = Configuration.getProcessEngine();
	}

	/**
	 *  发布流程定义
	 */
	protected void setUp() {
		//流程资源服务的接口，如流程定义发布、查询、删除等
		processEngine.getRepositoryService().createDeployment()
		                .addResourceFromClasspath("helloworld.jpdl.xml").deploy();
	}
	
	/**
	 * 发布新流程
	 */
	public void newProcessInstance() {
		//用于操作人工任务的服务，可以进行任务创建、查询、获取、提交完成、保存、删除等操作
		ExecutionService executionService = processEngine.getExecutionService();
		ProcessInstance processInstance = executionService.startProcessInstanceByKey("helloworld");
		
		//查看流程是否已经结束
		System.out.println("流程是否已经结束:"+processInstance.isEnded());
		
		//直接跳过等待，直接结束
		processInstance = executionService.signalExecutionById(processInstance.getId());
		System.out.println("流程是否已经结束:"+processInstance.isEnded());
	}
	
	/**
	 * 终止流程
	 */
	public void endProcessInstance() {
		ExecutionService executionService = processEngine.getExecutionService();
		ProcessInstance processInstance = executionService.startProcessInstanceByKey("helloworld");
		executionService.endProcessInstance(processInstance.getId(), "cancle");
	}
	
	/**
	 * 删除流程
	 */
	public void deleteProcessInstance() {
		ExecutionService executionService = processEngine.getExecutionService();
		ProcessInstance processInstance = executionService.startProcessInstanceByKey("helloworld");
		executionService.deleteProcessInstanceCascade(processInstance.getId());
	}
	
	/**
	 * 查看流程
	 */
	public void selectProcessInstance() {
		ExecutionService executionService = processEngine.getExecutionService();
		ProcessInstance processInstance1 = executionService.startProcessInstanceByKey("helloworld");
		ProcessInstance processInstance2 = executionService.startProcessInstanceByKey("helloworld");
		List<ProcessInstance> processInstanceList= executionService
		                            .createProcessInstanceQuery().list();
		
		for (ProcessInstance processInstance : processInstanceList) {
			System.out.println("流程的个数："+processInstance.getId());
		}
	}
	
}
