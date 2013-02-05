package demo;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class ZipTest {
	public static void main(String[] args) throws ZipException, IOException {
		InputStream ins = null;
		StringBuffer sb = new StringBuffer();
		
		ZipFile zf = new ZipFile(new File("G:/JavaStudy/java_workspace_demo/autojbpm/src/leave.zip"));
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
		
		System.out.println(sb.toString());
	}

}
