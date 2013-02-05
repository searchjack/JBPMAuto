package edu.scfc.djh.jbpm.run;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

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
	
	public static String getZipXMLContent(String zipFilePath) {
		StringBuffer sb = new StringBuffer();
		InputStream ins = null;
		
		try {
			ZipFile zf = new ZipFile(new File(zipFilePath));
			Enumeration<? extends ZipEntry> ent = zf.entries();
			while(ent.hasMoreElements()) {
				ZipEntry ze = ent.nextElement();
				if(ze.getName().endsWith(".xml")) {  // 取得以 .xml 结尾的文件
					ins = zf.getInputStream(ze);
				}
			}
			
			BufferedReader br = new BufferedReader(new InputStreamReader(ins));
			String str = "";
			while(str != null) {
				str = br.readLine();
				if(str != null) {
					sb.append(str);
				}
			}
		} catch (ZipException e) { e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); }
		
		return sb.toString();
	}

}
