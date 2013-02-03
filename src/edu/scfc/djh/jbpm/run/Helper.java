package edu.scfc.djh.jbpm.run;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Helper {
	public static void main(String[] args) throws Exception{
		getFileContent("G:/JavaStudy/java_workspace_demo/autojbpm/src/leave.jpdl.xml");
	}
	/**
	 * 
	 * @param filePath => 完整文件路径
	 * @return String => 原始文件内容
	 * @throws Exception
	 */
	public static String getFileContent(String filePath) throws Exception{
		StringBuffer sb = new StringBuffer();
		File xmlFile = new File(filePath);
		if(xmlFile.exists()){
			try {
				BufferedReader br = new BufferedReader(new FileReader(xmlFile));
				String line = "";
				while(line != null)
				{
					line = br.readLine();
					if(line != null)
						sb.append(line);
				}
			} catch (Exception e) { e.printStackTrace();
			}
		} else{ System.out.println("文件未找到"); }
//		System.out.println(sb.toString());
//		return new CleanProcessXML().doCleanProcessXML(sb.toString());
		return sb.toString();
	}

}
