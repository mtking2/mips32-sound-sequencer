package capstone.gui.utils;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;


/**
 * Created by michael on 11/6/15.
 */
public class InstrumentMenu extends DefaultMutableTreeNode {
    /** Generated serialization UID **/
	private static final long serialVersionUID = -8656648598223743768L;
	
	private JTree tree;
    private DefaultMutableTreeNode root;

    public InstrumentMenu() {
        super();
        root = new DefaultMutableTreeNode("Instrument Family:");
        init();
        tree = new JTree(root);
    }

    public void init() {

        String type;

        int cnt = 0, i = 0;
        while (SequencerUtils.instrumentMap.containsKey("type"+cnt)) {
            type = (String)SequencerUtils.instrumentMap.get("type" + cnt++);
            DefaultMutableTreeNode family = new DefaultMutableTreeNode(type);
            for(int j = 0; j<InstrumentMap.FAMILY_SIZE;j++,i++) {
                family.add(new DefaultMutableTreeNode(SequencerUtils.instrumentMap.get(i)));
            }
            root.add(family);
        }
    }

    public void collapseTree() {
        for (int i = tree.getRowCount() - 1; i > 0; i--) {
            tree.collapseRow(i);
        }
        tree.scrollPathToVisible(new TreePath(root.getPath()));
        tree.clearSelection();
    }

    public JTree getTree() {
        return tree;
    }

}
