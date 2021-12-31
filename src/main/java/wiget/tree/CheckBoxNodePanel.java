package wiget.tree;

import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;

public class CheckBoxNodePanel extends JPanel{
    protected final JLabel label;
    protected final JCheckBox check;

    public CheckBoxNodePanel() {
        this.label=new JLabel();
        this.check=new JCheckBox();
        check.setEnabled(true);
    }

    public CheckBoxNodePanel(JLabel jlabel){
        this.label=jlabel;
        this.check=new JCheckBox();
        check.setEnabled(true);
    }

    public CheckBoxNodePanel(JCheckBox jCheckBox){
        this.label=new JLabel();
        this.check=jCheckBox;
        check.setEnabled(true);
    }

    public CheckBoxNodePanel(JLabel jLabel,JCheckBox jCheckBox){
        this.label=jLabel;
        this.check=jCheckBox;
        check.setEnabled(true);
    }

    public void formalize(){
        this.check.setMargin(JBUI.emptyInsets());
        this.setLayout(new BorderLayout(5,0));
        this.add(check, BorderLayout.WEST);
        this.add(label, BorderLayout.CENTER);
    }

}
