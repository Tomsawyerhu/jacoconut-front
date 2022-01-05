package wiget;

import api.JacoconutApi;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import storage.CodeLink;
import storage.model.TestCaseLinking;
import storage.model.TestClassLinking;
import utils.OSAdapter;
import wiget.hook.CalculateCoverageHook;
import wiget.hook.ToolWindowHook;
import wiget.tree.*;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Plugin window
 */
public class JacoconutMainToolWindow {
    private JSplitPane toolWindowContent;
    private JScrollPane treeScroll;
    private JPanel left;
    private JPanel right;
    private CheckBoxTree testcaseTree;
    private JButton coverageButton;
    private JScrollPane codeArea;
    private JTextArea codes;
    private List<ToolWindowHook> hooks;

    public JacoconutMainToolWindow(ToolWindow toolWindow, List<ToolWindowHook> hooks){
        this(toolWindow);
        this.hooks=hooks;
    }

    public JacoconutMainToolWindow(ToolWindow toolWindow){
        initialTestcaseTree();
        initialTreeArea();
        initialButton();
        initialLeft();

        initialCodeArea();
        initialRight();

        initialToolWindow();

        addListeners();
    }

    public JSplitPane getContent(){
        return this.toolWindowContent;
    }

    /*
     * 初始化整体布局
     */
    private void initialToolWindow(){
        this.toolWindowContent=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,left,right);
        this.toolWindowContent.setDividerLocation(0.4);
        this.toolWindowContent.setContinuousLayout(true);
    }

    /*
     * 初始化用例树
     */
    private void initialTestcaseTree(){
        //初始化根节点
       TestRootCell root= new TestRootCell("testcase tree");
        //填充用例
//        for(int i=0;i<10;i+=1){
//            TestClassCell testClassCell=CellRenderer.generateTestClassCell(i +".class");
//            for(int j=0;j<3;j+=1){
//                testClassCell.add(CellRenderer.generateTestCaseCell(j+".func"));
//            }
//            root.add(testClassCell);
//        }
        //fixme 拆分到service
        //真实数据
        for(TestClassLinking classLinking: CodeLink.PROJECT_TEST_CLASS_LINK.get()){
            TestClassCell cell=new TestClassCell(classLinking.getName(),classLinking.getPath());
            for(TestCaseLinking caseLinking:CodeLink.PROJECT_TEST_CASE_LINK.get()){
                if(caseLinking.getOwner().equals(classLinking)){
                    cell.add(new TestCaseCell(caseLinking.getName(),caseLinking.getRange().begin.line,caseLinking.getRange().end.line,caseLinking.getRange().begin.column,caseLinking.getRange().end.column,classLinking.getPath()));
                };
            }
            root.add(cell);
        }
        //fixme 拆分到service

        this.testcaseTree=new CheckBoxTree(root);
    }

    /*
     * 初始化用例树所在滚动区域
     */
    private void initialTreeArea(){
        this.treeScroll =new JBScrollPane(this.testcaseTree,ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        this.treeScroll.setBorder(BorderFactory.createLineBorder(JBColor.RED));

    }

    /*
     * 初始化按钮
     */
    private void initialButton(){
        this.coverageButton=new JButton("Coverage Calculate");
        this.coverageButton.setBorder(BorderFactory.createLineBorder(JBColor.RED));
        //this.coverageButton.setSize(20,10);
    }

    /*
     * 初始化代码显示区域
     */
    private void initialCodeArea(){
        this.codes=new JTextArea();
        this.codeArea=new JBScrollPane(codes,ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        this.codes.setEditable(false);
        //填充数据
        //this.codes.setText("Line1.\nLine2.\nLine3.\nLine4.\n");
    }

    private void initialLeft(){
        this.left=new JPanel();
        this.left.setBorder(BorderFactory.createLineBorder(JBColor.RED));
//        GridBagLayout gbl=new GridBagLayout();
//        GridBagConstraints gbc=new GridBagConstraints();
//        gbl.columnWeights=new double[]{0};
//        gbl.rowWeights=new double[]{0.9,0.1};
//
//        this.left.setLayout(gbl);
//
//        gbc.gridx=0;
//        gbc.gridy=0;
//        gbc.fill=GridBagConstraints.BOTH;
//        gbl.setConstraints(treeScroll,gbc);
//        this.left.add(treeScroll);
//
//        gbc.gridx=0;
//        gbc.gridy=1;
//        gbc.fill=GridBagConstraints.NONE;
//        gbl.setConstraints(coverageButton,gbc);
//        this.left.add(coverageButton);
        this.left.setLayout(new BorderLayout(0,5));
        this.left.add(treeScroll,BorderLayout.CENTER);
        this.left.add(coverageButton,BorderLayout.SOUTH);
    }

    private void initialRight(){
        this.right=new JPanel();
        this.right.setLayout(new BorderLayout());
        right.setBorder(BorderFactory.createLineBorder(JBColor.RED));
        this.right.add(this.codeArea,BorderLayout.CENTER);
    }

    private void addListeners(){
        //添加mouse listener
        JacoconutMainToolWindow that=this;
        this.testcaseTree.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                //左双击跳转
                if (e.getClickCount() == 2&&e.getButton()==MouseEvent.BUTTON1) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                            that.testcaseTree.getLastSelectedPathComponent();
                    if (node == null) return;
                    // Cast nodeInfo to your object and do whatever you want
                    if(node instanceof TestClassCell||node instanceof TestCaseCell){
                        //jump to class
                        BufferedReader reader=null;
                        that.codes.setText("");
                        int caretPosition=0;
                        try {
                            if(node instanceof TestClassCell){
                                reader=new BufferedReader(new FileReader(((TestClassCell) node).getClassPath()));
                            }else{
                                reader=new BufferedReader(new FileReader(((TestCaseCell) node).getClassPath()));
                            }
                            String line="";
                            int i=0;
                            while(null!=(line = reader.readLine())){
                                that.codes.append(line+"\n");
                                i+=1;
                                if(node instanceof TestCaseCell&&i<((TestCaseCell) node).getEndLine()){
                                    caretPosition+=line.length()+1;
                                }
                            }
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        that.codes.setCaretPosition(caretPosition);
                        that.codes.requestFocus();
                    }
                }
            }
        });

        //click coverage calculate button
        this.coverageButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //clean last result
                JacoconutApi.reset();
                TreeCell root=that.testcaseTree.getRoot();
                List<TreeCell> testcases= root.collect();
                List<String> testcasePath=new ArrayList<>();
                for(TreeCell cell:testcases){
                    if(cell.isTestCase()){
                        testcasePath.add((OSAdapter.formalizeFilePath(((TestCaseCell)cell).getClassPath()).concat("/").concat(((TestCaseCell)cell).getName())));
                    }
                }
                //Messages.showInfoMessage(String.join("\n", testcasePath), "Testcases");
                for(ToolWindowHook hook:hooks){
                    if(hook instanceof CalculateCoverageHook){
                        hook.recall(testcasePath.toArray());
                    }
                }
            }
        });
    }


}
