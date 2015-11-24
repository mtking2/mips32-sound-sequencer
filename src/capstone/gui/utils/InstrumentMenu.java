package capstone.gui.utils;


import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;


/**
 * This class represents a menu that is specific to the General MIDI Level 1 Sound Set.
 * This class is used in conjunction with {@link InstrumentMap} to create and populate
 * a {@link JTree} of all instrument families and instruments in the
 * General MIDI Level 1 Sound Set.
 *
 * @author Brad Westley
 * @author Michael King
 * @version 11.4.2015
 */
public class InstrumentMenu {

    /** Serves as the main structure for the instrument menu. */
    private JTree tree;

    /** The root of all the elements contained in the instrument menu. */
    private DefaultMutableTreeNode root;

    /** Constructs and initializes the instrument menu */
    public InstrumentMenu() {
        root = new DefaultMutableTreeNode("Instrument Family:");
        init();
        tree = new JTree(root);
    }

    /**
     * Helper method responsible for constructing the tree by retrieving each element
     * from the {@link InstrumentMap}.
     */
    private void init() {
        String type;
        int cnt = 0, i = 0;
        while (SequencerUtils.instrumentMap.containsKey("type"+cnt)) {
            type = (String)SequencerUtils.instrumentMap.get("type" + cnt++);
            DefaultMutableTreeNode family = new DefaultMutableTreeNode(type);
            for(int j = 0; j<InstrumentMap.FAMILY_SIZE;j++,i++)
                family.add(new DefaultMutableTreeNode(SequencerUtils.instrumentMap.get(i)));
            root.add(family);
        }
    }

    /** Restores the tree back to the original view by collapsing every node. */
    public void collapseTree() {
        for (int i = tree.getRowCount() - 1; i > 0; i--)
            tree.collapseRow(i);
        tree.scrollPathToVisible(new TreePath(root.getPath()));
        tree.clearSelection();
    }

    /** @return the {@link JTree} object associated with this instrument menu. */
    public JTree getTree() {
        return tree;
    }

}
