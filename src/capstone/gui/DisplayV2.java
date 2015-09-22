package capstone.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.border.EtchedBorder;


/**
 * A graphical interface that represents a music sequencer.
 *
 * @author Brad Westley and Michael King
 * @version 9/17/15
 */
public class DisplayV2 extends JFrame implements ActionListener {

    private JPanel panel, north, south, east, west, center;
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenuItem newMenuItem, openMenuItem, saveAsMenuItem, exitMenuItem;
    private JButton save, play, stop;

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
        north = new JPanel(new FlowLayout(FlowLayout.LEFT));
        west = new JPanel();
        east = new JPanel(new GridLayout(5,1));
        center = new JPanel();
        south.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        north.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        east.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        west.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        center.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));


        menuBar = new JMenuBar();

        // File Menu, F - Mnemonic
        fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(fileMenu);

        // File->New, N - Mnemonic
        newMenuItem = new JMenuItem("New", KeyEvent.VK_N);
        fileMenu.add(newMenuItem);

        // File->New, O - Mnemonic
        openMenuItem = new JMenuItem("Open...", KeyEvent.VK_O);
        fileMenu.add(openMenuItem);

        // File->New, N - Mnemonic
        saveAsMenuItem = new JMenuItem("Save As...", KeyEvent.VK_S);
        fileMenu.add(saveAsMenuItem);

        // File->New, X - Mnemonic
        exitMenuItem = new JMenuItem("Exit", KeyEvent.VK_X);
        exitMenuItem.addActionListener(this);
        fileMenu.add(exitMenuItem);

        save = new JButton("Save");
        save.setContentAreaFilled(false);
        play = new JButton("Play");
        play.setContentAreaFilled(false);
        stop = new JButton("Stop");
        stop.setContentAreaFilled(false);

        north.add(save);
        north.add(play);
        north.add(stop);

        panel.add(south, BorderLayout.SOUTH);
        panel.add(north, BorderLayout.NORTH);
        panel.add(east, BorderLayout.EAST);
        panel.add(center, BorderLayout.CENTER);
        panel.add(west, BorderLayout.WEST);

        this.setJMenuBar(menuBar);
        this.setContentPane(panel);
        this.setSize(width, height);
        this.setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(exitMenuItem)) {
            System.exit(0);
        }
    }

    public static void	main(String[] args) {
        new DisplayV2("MIPS Sound Sequencer",800,600);
    }
}