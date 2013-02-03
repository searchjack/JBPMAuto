package demo;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class ZipTest {
	public static void main(String[] args) throws ZipException, IOException {
		new ZipFile(new File(""));
	}

}
