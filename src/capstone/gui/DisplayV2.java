package capstone.gui;

import javax.swing.*;
import java.awt.*;

/**
 * A graphical interface that represents a music sequencer.
 *
 * @author Brad Westley and Michael King
 * @version 9/17/15
 */
public class DisplayV2 extends JFrame {

    private JPanel panel, north, south, east, west, center;
    private JButton play, stop;

    /**
     * Creates and displays a window with a certain name,
     * height, and width.
     *
     * @param title the name of the program
     */
    public DisplayV2(String title, int width, int height){
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle(title);


        panel = new JPanel();
        panel.setLayout(new BorderLayout());

        south = new JPanel();
        north = new JPanel();
        west = new JPanel();
        east = new JPanel();
        center = new JPanel();

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

    public static void	main(String[] args) {
        new DisplayV2("MIPS Sound Sequencer",800,600);
    }
}