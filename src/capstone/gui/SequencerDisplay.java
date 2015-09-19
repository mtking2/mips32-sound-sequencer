package capstone.gui;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;

/**
 * A graphical interface that represents a music sequencer.
 *
 * @author Brad Westley and Michael King
 * @version 9/17/15
 */
public class SequencerDisplay extends JFrame {

    private JPanel panel, north, south, east, west;
    private JTable center;
    private JButton play, stop;

    /**
     * Creates and displays a window with a certain name,
     * height, and width.
     *
     * @param title the name of the program
     */
    public SequencerDisplay(String title, int width, int height){
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle(title);
        this.setResizable(false);

        panel = new JPanel();
        
        panel.setLayout(new BorderLayout());

        south = new JPanel();
        north = new JPanel();
        west = new JPanel();
        east = new JPanel();
        
        
        // 4 tracks, 16 beats
        
        int tracks = 4;
        int beats = 16;
        
        center = new NoteTable(tracks, beats, south);
        
        for(int i = 0; i < tracks; i++){
        	for(int j = 0; j < beats; j++){
        		center.setValueAt(i + "/" + j, i, j);
        	}
        }

        play = new JButton("Play");
        stop = new JButton("Stop");

        north.add(play);
        north.add(stop);
        
        panel.add(south, BorderLayout.SOUTH);
        panel.add(north, BorderLayout.NORTH);
        panel.add(east, BorderLayout.EAST);
        panel.add(center, BorderLayout.CENTER);
        panel.add(west, BorderLayout.WEST);

        this.setContentPane(panel);
        this.setSize(width, height);
        this.setVisible(true);

    }
}