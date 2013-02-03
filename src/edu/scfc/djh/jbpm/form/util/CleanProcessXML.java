package edu.scfc.djh.jbpm.form.util;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.scfc.djh.jbpm.xml.node.DOM4jReader;
import edu.scfc.djh.jbpm.xml.node.JBPMNode;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * 处理一下 j4 项目生成的流程文件 .xml , 使之能正确的在 jbpm 4.4 环境下运行
 * 
 * @author admin
 *
 */
public class CleanProcessXML
{

    public static void main(String[] args) throws Exception
    {

        String xml_str = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<process xmlns=\"http://jbpm.org/4.4/jpdl\" name=\"action\">\n" +
                "<start g=\"275,137,48,48\" name=\"start1\">\n" +
                "<transition to=\"task1\" g=\"0,0\" name=\"to task1\">\n" +
                "</transition>\n" +
                "</start>\n" +
                "<end g=\"272,504,48,48\" name=\"end1\">\n" +
                "</end>\n" +
//                "<task g=\"263,248,62,39\" assignee=\"#{owner}\" name=\"task1\">\n" +
                "<task g=\"263,248,62,39\" name=\"task1\">\n" +
                "<transition to=\"task2\" g=\"0,0\" name=\"to task2\">\n" +
                "</transition>\n" +
                "</task>\n" +
                "<task g=\"249,335,62,39\" assignee=\"manager\" name=\"task2\">\n" +
                "<transition to=\"task3\" g=\"0,0\" name=\"to task3\">\n" +
                "</transition>\n" +
                "</task>\n" +
                "<task g=\"263,417,62,39\" assignee=\"boss\" name=\"task3\">\n" +
                "<transition to=\"end1\" g=\"0,0\" name=\"to end1\">\n" +
                "</transition>\n" +
                "</task>\n" +
                "</process>" ;


//        doCleanProcessXML(xml_str) ;
        System.out.println("-->\n" + new  CleanProcessXML().doCleanProcessXML(xml_str)) ;



    }

    /**
     *
     * @param xml_str   xml 文件内容
     * @return
     * @throws Exception
     */
    public String doCleanProcessXML(String xml_str) throws  Exception
    {
        DOM4jReader d = new DOM4jReader() ;
        List<JBPMNode> tree = d.parseToTree(xml_str);
        Iterator<JBPMNode> iter = tree.iterator();

        JBPMNode n ;
        // start 节点的 transition 的 to 属性值
        String startTo = null ;
        while(iter.hasNext())
        {
            // 处理当前普通节点，  使之能正确运行在 jbpm 4.4
            n = iter.next() ;
            // 处理 start 节点
            if(n.getType().equals("start".toLowerCase()))
            {
                List<JBPMNode> nextNodes = n.getNext() ;
                for(JBPMNode m : nextNodes)
                {
                    if(m.getName() != null && m.getName().trim().length() > 0)
                    {
                        if(startTo == null && nextNodes.size() == 1)
                        {
                            startTo = m.getTo() ; // 将 transtion 的 to 值保存
                        }

                    }
                }
            }
        }


        // 处理 xml 内容
        iter = tree.iterator() ;
        while(iter.hasNext())
        {
            // 处理当前普通节点，  使之能正确运行在 jbpm 4.4
            n = iter.next() ;
            n.setName(n.getName().replace(" ", "_"));
            if(n.getNext().size() > 0)
            {
                for(JBPMNode j : n.getNext())
                {
                    j.setName(j.getName().replace(" ", "_"));
                    j.setTo(j.getTo().replace(" ", "_"));
                }
            }

            // 处理 start 节点
            if(n.getType().equals("start".toLowerCase()))
            {
                List<JBPMNode> nextNodes = n.getNext() ;
                for(JBPMNode m : nextNodes)
                {
                    if(m.getName() != null && m.getName().trim().length() > 0)
                    {
                        m.setName(m.getName().replace(" ", "_"));
                        m.setTo(m.getTo().replace(" ", "_"));

                        if(startTo == null && nextNodes.size() == 1)
                        {
                            startTo = m.getTo() ; // 将 transtion 的 to 值保存
                        }

                    }
                }
            }

            System.out.println(n.getName());
            if(startTo != null && n.getName().toLowerCase().equals(startTo))   // 如果 start 只指向一个节点，  则修改其 assignee 属性
            {
                n.setAssignee("#{owner}") ;
            }


        }

        // 将处理后的 xml 生成文本
        String xml_res = parseTreeToXML(tree.iterator());
//        System.out.println("CleanProcessXML  处理后的  --->" + xml_res);
        return xml_res ;
    }

    private String parseTreeToXML(Iterator<JBPMNode> iter)
    {

        Document document = DocumentHelper.createDocument();
        Element root = null;
        JBPMNode n ;
        while (iter.hasNext())
        {
            n = iter.next();
            Element ele = null ;

            /*          当没有 transition 节点时             */
            if( root == null && n.getType() != null  && n.getType().trim().toLowerCase().equals("process")) // 建立根节点
            {
                root = document.addElement(n.getType().trim()) ;
                root.addAttribute("xmlns", "http://jbpm.org/4.3/jpdl") ;
                root.addAttribute("name", n.getName()) ;
            } else if(n.getType() != null  && n.getType().trim().toLowerCase().equals("start"))  // 建立 start 节点
            {
                if(root != null)
                {
                    ele = root.addElement(n.getType());
                    //  添加 name 属性
                    if(n.getName() != null && n.getName().trim().length() > 0)
                    {
                        ele.addAttribute("name", n.getName());
                    }
                    // 添加 g 属性
                    if(n.getG() != null && n.getG().length() > 0)
                    {
                        ele.addAttribute("g", n.getG()) ;
                    }
                }
            } else
            {   // 其它类型节点 ， 如 :  task , decision            --> 普通节点
                ele = root.addElement(n.getType()) ;

                //  添加 name 属性
                if(n.getName() != null && n.getName().trim().length() > 0)
                {
                    ele.addAttribute("name", n.getName());
                }
                // 添加 g 属性
                if(n.getG() != null && n.getG().length() > 0)
                {
                    ele.addAttribute("g", n.getG()) ;
                }
                //  添加 to 属性
                if(n.getTo() != null && n.getTo().trim().length() > 0)
                {
                    ele.addAttribute("to" , n.getTo()) ;
                }
                //  添加 assignee 属性
                if(n.getAssignee() != null && n.getAssignee().trim().length() > 0)
                {
                    ele.addAttribute("assignee" , n.getAssignee()) ;
                }
                //  添加 form 属性
                if(n.getForm() != null && n.getForm().trim().length() > 0)
                {
                    ele.addAttribute("form" , n.getForm()) ;
                }
                //  添加 expr 属性
                if(n.getExpr() != null && n.getExpr().trim().length() > 0)
                {
                    ele.addAttribute("expr" , n.getExpr()) ;
                }

            }







            /*            如果有 transition 节点时               */
            List<JBPMNode> nextNodes = n.getNext() ;
            if(nextNodes.size() > 0)
            {
                for (JBPMNode m : nextNodes)
                {
                    Element transition = ele.addElement(m.getType()) ;
                    //  添加 name 属性
                    if(nextNodes.size() >= 2 && m.getName() != null && m.getName().trim().length() > 0)
                    {
                        transition.addAttribute("name", m.getName());
                    }
                    // 添加 g 属性
                    if(m.getG() != null && m.getG().length() > 0)
                    {
                        transition.addAttribute("g", m.getG()) ;
                    }
                    //  添加 to 属性
                    if(m.getTo() != null && m.getTo().trim().length() > 0)
                    {
                        transition.addAttribute("to" , m.getTo()) ;
                    }
                }
            }
        }





//        while(iter.hasNext())
//        {
//            n = iter.next();
//            // 创建根节点
//            if (root == null && n.getType().trim().toLowerCase().equals("process".toLowerCase()))
//            {
//                root = document.addElement(n.getType()) ;
//                //  添加 xmlns 属性
//                root.addAttribute("xmlns", "http://jbpm.org/4.4/jpdl") ;
//                //  添加 name 属性
//                if(n.getName() != null && n.getName().trim().length() > 0)
//                {
//                    root.addAttribute("name", n.getName());
//                }
//            } else
//            {
//                Element ele = root.addElement(n.getType()) ;
//
//                //  添加 name 属性
//                if(n.getName() != null && n.getName().trim().length() > 0)
//                {
//                    ele.addAttribute("name", n.getType());
//                }
//                // 添加 g 属性
//                if(n.getG() != null && n.getG().length() > 0)
//                {
//                    ele.addAttribute("g", n.getG()) ;
//                }
//                //  添加 to 属性
//                if(n.getTo() != null && n.getTo().trim().length() > 0)
//                {
//                    ele.addAttribute("to" , n.getTo()) ;
//                }
//
//                // 二级节点的内部节点
//                if(n.getNext().size() > 0)
//                {
//                    List<JBPMNode> nextNodes = n.getNext();
//                    Iterator<JBPMNode> iterNext = nextNodes.iterator();
//                    while(iterNext.hasNext())
//                    {
//                        JBPMNode node_ = iterNext.next();
//                        Element subEle = ele.addElement(node_.getType());
//
//                        //  添加 name 属性   ,  如果内部只有一个 Transition 就不添加 name 属性
//                        if(n.getName() != null && n.getName().trim().length() > 0 && nextNodes.size() >= 2)
//                        {
//                            subEle.addAttribute("name", n.getName());
//                        }
//                        // 添加 g 属性
//                        if(n.getG() != null && n.getG().length() > 0)
//                        {
//                            subEle.addAttribute("g", n.getG()) ;
//                        }
//                        //  添加 to 属性
//                        if(n.getTo() != null && n.getTo().trim().length() > 0)
//                        {
//                            subEle.addAttribute("to" , n.getTo()) ;
//                        }
//
//                    }
//                }
//            }
//        }

        return document.asXML() ;

    }


    private String parseTreeToXML(List<JBPMNode> tree)
    {
        Document document = DocumentHelper.createDocument();
        for(JBPMNode j : tree)
        {
            Element ele = document.addElement(j.getType()) ;

            // 添加 xmlns 属性
            if(j.getType().toLowerCase().equals("process"))
            {
                ele.addAttribute("xmlns", "http://jbpm.org/4.4/jpdl") ;
            }
            //  添加 name 属性
            if(j.getName().trim().length() > 0)
            {
                ele.addAttribute("name", j.getName());
            }
            // 添加 g 属性
            if(j.getG().length() > 0)
            {
                ele.addAttribute("g", j.getG()) ;
            }
            //  添加 to 属性
            if(j.getTo().trim().length() > 0)
            {
                ele.addAttribute("to" , j.getTo()) ;
            }

        }

        return document.asXML() ;

    }

}
