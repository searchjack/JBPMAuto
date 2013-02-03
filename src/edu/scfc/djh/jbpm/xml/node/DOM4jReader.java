package edu.scfc.djh.jbpm.xml.node;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class DOM4jReader
{
    private String fileName = "";
    private String filePath = "";

    private List<JBPMNode> beforeLines ;
    private List<JBPMNode> subLines ;

    public DOM4jReader()
    {
        beforeLines = new ArrayList<JBPMNode>() ;
        subLines =  new ArrayList<JBPMNode>() ;
    }
    public DOM4jReader(String fileName)
    {
        this.fileName = fileName ;

        beforeLines = new ArrayList<JBPMNode>() ;
        subLines =  new ArrayList<JBPMNode>() ;
    }
    public DOM4jReader(String fileName, String filePath)
    {
        this.fileName = fileName ;
        this.filePath = filePath + fileName ;

        beforeLines = new ArrayList<JBPMNode>() ;
        subLines =  new ArrayList<JBPMNode>() ;
    }

    /**
     * 解析xml
     *
     * @return 解析xml文件得到的需要对账的文件名
     */
    public String parserXmlTwo()
    {
        File inputXml = new File(filePath);
        SAXReader saxReader = new SAXReader();
        try
        {
            Document document = saxReader.read(inputXml);
            Element root = document.getRootElement();
            getChilds(root);

        } catch (DocumentException e)
        {
            e.printStackTrace();
        }

        return "";
    }

    private void getChilds(Element ele)
    {
        for (Iterator iterEle = ele.elementIterator(); iterEle.hasNext();)
        {
            Element nextEle = (Element) iterEle.next();

            // 读取属性
            System.out.println("" + nextEle.getName() + ":");
            Iterator iterRoot = nextEle.attributeIterator();
            while (iterRoot.hasNext())
            {
                Attribute att = (Attribute) iterRoot.next();
                System.out.println("\t" + att.getName() + ":" + nextEle.attribute(att.getName()).getValue());
            }

            if (nextEle.elementIterator().hasNext())
            {
                getChilds(nextEle);
            }
        }

        return;
    }

    /**
     * 查找属性值
     * 
     * @param fileName
     * @param nodeName
     * @param attributeName
     * @return
     */
    public String findEleAttribute(String fileName, String nodeName, String attributeName)
    {
        String value = "";
        String filePath = null;
        
        if(this.fileName.equals(fileName))
        {
            filePath = this.filePath;
        } else
        {
            URL url = this.getClass().getClassLoader().getResource(fileName);
            filePath = url.getFile();            
        }

        File inputXml = new File(filePath);
        SAXReader saxReader = new SAXReader();
        try
        {
            Document document = saxReader.read(inputXml);
            Element root = document.getRootElement();
            
            // 检查根节点是否为需要查询的目标
            if(root.getName() != null && root.getName().equals(nodeName))
            {
                return root.attributeValue(attributeName);
            }
            
            // 查询子节点
            value = findChild(root, nodeName, attributeName);

        } catch (DocumentException e) {e.printStackTrace(); }

        System.out.println("*********" + this.getClass() + ":138 Line__>" + value);

        return value;
    }

    private String findChild(Element ele, String nodeName, String attributeName)
    {
        // 查询子节点
        if(ele.getName() != null && ele.getName().equals(nodeName))
        {
            System.out.println("value: " + ele.attributeValue(attributeName));
        }
        
        for (Iterator iterEle = ele.elementIterator(); iterEle.hasNext();)
        {
            Element nextEle = (Element) iterEle.next();

            // 读取属性
            Iterator iterRoot = nextEle.attributeIterator();
            while (iterRoot.hasNext())
            {
                Attribute att = (Attribute) iterRoot.next();
                System.out.println("\t" + att.getName() + ":" + nextEle.attribute(att.getName()).getValue());
            }

            if (nextEle.elementIterator().hasNext())
            {
                findChild(nextEle, nodeName, attributeName);
            }
        }

        return "";
    }

    /**
     * @return 节点集合
     */
    public List<JBPMNode> parseToTree()
    {
        File inputXml = new File(filePath);
        SAXReader saxReader = new SAXReader();
        try
        {
            Document document = saxReader.read(inputXml);
            Element root = document.getRootElement();

            // 把根节点添加到 tree
            List<JBPMNode> before = this.beforeLines; // 划入的所有Transition 节点
            List<JBPMNode> next = getSubNode(root); // 划出的所有Transition 节点
            JBPMNode node = new JBPMNode(root.getName(), root.attributeValue("assignee"), root.attributeValue("name"), root.attributeValue("g"), root.attributeValue("to"), root.attributeValue("form"), root.attributeValue("expr"), before, next);
            JBPMNode.addToTree(node);

            //
            getTree(root);

        } catch (DocumentException e) {e.printStackTrace();}

        return JBPMNode.getNodeTree();
    }

    /**
     *
     * @param xml_str  xml 字符串内容
     * @return
     * @throws Exception
     */
    public List<JBPMNode> parseToTree(String xml_str) throws Exception
    {
        // 移除节点树中已有的元素
        JBPMNode.getNodeTree().clear() ;
        ByteArrayInputStream bais = new ByteArrayInputStream(xml_str.getBytes()) ;

        SAXReader saxReader = new SAXReader();
        try
        {
            Document document = saxReader.read(bais);
            Element root = document.getRootElement();

            // 把根节点添加到 tree
            List<JBPMNode> before = this.beforeLines; // 划入的所有Transition 节点
            List<JBPMNode> next = getSubNode(root); // 划出的所有Transition 节点
            JBPMNode node = new JBPMNode(root.getName(), root.attributeValue("assignee"), root.attributeValue("name"), root.attributeValue("g"), root.attributeValue("to"), root.attributeValue("form"), root.attributeValue("expr"), before, next);
            JBPMNode.addToTree(node);

            //
            getTree(root);

        } catch (DocumentException e) {e.printStackTrace();}

        return JBPMNode.getNodeTree();
    }

    private void getTree(Element ele)
    {
        @SuppressWarnings("unused")
        List<JBPMNode> JBPMNodeTree = JBPMNode.getNodeTree();

        for (Iterator iterEle = ele.elementIterator(); iterEle.hasNext();)
        {
            Element nextEle = (Element) iterEle.next();

            if(!(nextEle.getName().toLowerCase().trim().equals("transition")))  // 不是 Transition 时
            {
                // 把节点添加到 tree
                List<JBPMNode> before = this.beforeLines; // 划入的所有Transition 节点
                List<JBPMNode> next = getSubNode(nextEle); // 划出的所有Transition 节点
                JBPMNode node = new JBPMNode(nextEle.getName(), nextEle.attributeValue("assignee"), nextEle.attributeValue("name"), nextEle.attributeValue("g"), nextEle.attributeValue("to"), nextEle.attributeValue("form"), nextEle.attributeValue("expr"), before, next);
                JBPMNode.addToTree(node);
            }

            if (nextEle.elementIterator().hasNext())
            {
                getTree(nextEle);
            }
        }

        return;
    }
    
    private List<JBPMNode> getSubNode(Element subEle)
    {
        subLines = new ArrayList<JBPMNode>();
        Iterator<Element> iterEle = subEle.elementIterator();

        Element nextEle;
        while (iterEle.hasNext())
        {
            nextEle = iterEle.next();
            if(nextEle.getName().equals("transition"))
            {
                JBPMNode node = new JBPMNode(
                        nextEle.getName(),
                        nextEle.attributeValue("assignee"),
                        nextEle.attributeValue("name"),
                        nextEle.attributeValue("g"), 
                        nextEle.attributeValue("to"),
                        nextEle.attributeValue("form"),
                        nextEle.attributeValue("expr"),
                        null,
                        null);
                subLines.add(node);
            }
        }

        return subLines;
    }
    
    /**
     * 根据 jbpm 节点的 name, g 属性找到相应节点，并反回节点的 Transition
     * 
     * @param name 节点的 name 属性
     * @param g 节点的 g 属性
     */
    public List<JBPMNode> getTransition(String name, String g)
    {
        if(JBPMNode.getNodeTree().size() != 0)
        {
            Iterator<JBPMNode> iterTree = JBPMNode.getNodeTree().iterator();
            while(iterTree.hasNext())
            {
                JBPMNode nextNodes = iterTree.next();
                if(nextNodes.getName().equals(name) && nextNodes.getG().equals(g))
                {
                    return nextNodes.getNext();
                }
            }
        }

        return null;        
    }



    /**
     * 测试main方法
     * 
     * @param args
     */
    public static void main(String[] args)
    {
        DOM4jReader d = new DOM4jReader();

        // 
//        d.parserXmlTwo();

        // find
//        System.out.println("__" + d.findEleAttribute("leave.jpdl.xml", "process", "name") + "__");
//        d.findEleAttribute("leave.jpdl.xml", "task", "name");

        // parse xml to JBPMTree
        @SuppressWarnings("unused")
        List<JBPMNode> tree = d.parseToTree();
        
//        List<JBPMNode> lines = d.getTransition(父节点的 name 值 , 父节点的 g 值);   //  得到其节点 --> 最终需要得到 name 值
        List<JBPMNode> lines = d.getTransition("申请" , "172,108,92,52");
        Iterator<JBPMNode> iter = lines.iterator();
        while(iter.hasNext())
        {
            JBPMNode n = iter.next();
            System.out.println("_TO:" + n.getTo()) ;
        }
        
//        Iterator<JBPMNode> iter = tree.iterator();
//        int i = 0 ;
//        while(iter.hasNext())
//        {
//            System.out.println("____" +i+ "____");
//            JBPMNode next = iter.next();
//            System.out.println("name:" + next.getName());
//            System.out.println("form:" + next.getForm());
//            System.out.println("g:" + next.getG());
//            System.out.println("to:" + next.getTo());
//            System.out.println("type:" + next.getType());
//            System.out.println("assignee:" + next.getAssignee());
//            
//            // int j = 1 ;
//            // System.out.println("befor:" + next.getBefore());
//            
//            int k = 1 ;
//            System.out.println("next:");
//            System.out.println("\t Transition |->");
//            for (JBPMNode node : next.getNext())
//            {
//                System.out.println("\t____" +k+ "____");
//                System.out.println("\tname:" + node.getName());
//                System.out.println("\tform:" + node.getForm());
//                System.out.println("\tg:" + node.getG());
//                System.out.println("\tto:" + node.getTo());
//                System.out.println("\ttype:" + node.getType());
//                System.out.println("\tassignee:" + node.getAssignee());
//                System.out.println("\t____" +k+ "____");
//                k ++ ;
//            }
//            System.out.println("____" +i+ "____");
//            i ++ ;
//        }

//        Iterator<JBPMNode> iter = tree.iterator();
//        while(iter.hasNext())
//        {
//            JBPMNode next = iter.next();
//            next.getType();
//            next.getName();
//            next
//        }

    }
}

