package edu.scfc.djh.jbpm.xml.node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JBPMNode
{

    // ===========================================================
    // Constants
    // ===========================================================
	// 保存所有节点 -- Transition 除外
    private static List<JBPMNode> nodeTree = new ArrayList<JBPMNode>();

    // ===========================================================
    // Fields
    // ===========================================================

    // 节点属性
    private String type;

    private String assignee;

    private String name;

    private String g;

    private String to;

    private String form;

    private String expr;

    // 划入的所有Transition 节点
    private List<JBPMNode> before;

    // 划出的所有Transition 节点
    private List<JBPMNode> next;

    // ===========================================================
    // Constructors
    // ===========================================================

    public JBPMNode()
    {
    }

    public JBPMNode(String type, String assignee, String name, String g, String to, String form, String expr, List<JBPMNode> before, List<JBPMNode> next) {
        this.type = type;
        this.assignee = assignee;
        this.name = name;
        this.g = g;
        this.to = to;
        this.form = form;
        this.expr = expr;
        this.before = before;
        this.next = next;
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public static List<JBPMNode> getNodeTree()
    {
        return nodeTree;
    }

    public static void setNodeTree(List<JBPMNode> nodeTree)
    {
        JBPMNode.nodeTree = nodeTree;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getG()
    {
        return g;
    }

    public void setG(String g)
    {
        this.g = g;
    }

    public String getTo()
    {
        return to;
    }

    public void setTo(String to)
    {
        this.to = to;
    }

    public String getForm()
    {
        return form;
    }

    public void setForm(String form)
    {
        this.form = form;
    }

    public List<JBPMNode> getBefore()
    {
        return before;
    }

    public void setBefore(List<JBPMNode> before)
    {
        this.before = before;
    }

    public List<JBPMNode> getNext()
    {
        return next;
    }

    public void setNext(List<JBPMNode> next)
    {
        this.next = next;
    }

    public String getAssignee()
    {
        return assignee;
    }

    public void setAssignee(String assignee)
    {
        this.assignee = assignee;
    }

    public String getExpr() {
        return expr;
    }

    public void setExpr(String expr) {
        this.expr = expr;
    }

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================

    /**
     * 在节点树添加节点
     * 
     * @param node
     */
    public static void addToTree(JBPMNode node)
    {
        JBPMNode.nodeTree.add(node);
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}
