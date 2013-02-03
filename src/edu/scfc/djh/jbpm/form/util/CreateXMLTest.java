package edu.scfc.djh.jbpm.form.util;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class CreateXMLTest {
    //创建XML文档
    public static void main (String args[]) {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("root");

        root.addElement("author")
                .addAttribute("name", "James")
                .addAttribute("location", "UK")
                .addText("James Strachan");

        root.addElement("author")
                .addAttribute("name", "Bob")
                .addAttribute("location", "US")
                .addText("Bob McWhirter");

        System.out.println(document.asXML()) ;
//        return document;
    }
}
