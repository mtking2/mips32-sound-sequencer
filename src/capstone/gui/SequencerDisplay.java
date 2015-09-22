package capstone.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.*;
import javax.swing.border.EtchedBorder;

/**
 * A graphical interface that represents a music sequencer.
 *
 * @author Brad Westley and Michael King
 * @version 9/17/15
 */
public class SequencerDisplay extends JFrame implements ActionListener {

    private JPanel panel, north, south, east, west, center;
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenuItem newMenuItem, openMenuItem, exitMenuItem;
    private JButton play, stop;

    public SequencerDisplay(String title, int width, int height){
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle(title);
        this.setResizable(false);
        menuInit();

        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        south = new JPanel();
        north = new JPanel(new FlowLayout(FlowLayout.LEFT));
        west = new JPanel(new GridLayout(5,1));
        east = new JPanel();
        south.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        north.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        east.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        west.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));


        // 4 tracks, 16 beats
        
        int tracks = 4;
        int beats = 16;
        
        center = new JPanel(new GridLayout(tracks, beats));
        
        ActionListener listener = new ActionListener(){
        	@Override
        	public void actionPerformed(ActionEvent e){
        		// Get track and beat number from command
        		
        		String command = e.getActionCommand();
        		
        		int track = Integer.parseInt(
        						// From beginning to /
        						command.substring(0, command.indexOf('/')));
        		
        		int beat = Integer.parseInt(
        						command.substring(
        								// From / to end
        								command.indexOf('/') + 1, command.length()));
        		
        		// TODO alter buttons based on what was clicked
        	}
        };
        
        // center = new NoteTable(tracks, beats, south);
        center.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

        for(int i = 0; i < tracks; i++)
        	for(int j = 0; j < beats; j++){
        		NoteButton button = new NoteButton(i, j);
        		
        		button.addActionListener(listener);
        		
        		center.add(button);
        	}

        play = new JButton("Play");
        play.setContentAreaFilled(false);
        stop = new JButton("Stop");
        stop.setContentAreaFilled(false);

        north.add(play);
        north.add(stop);


        west.add(new JSeparator());

        JPanel track1Set = new JPanel();
        track1Set.setLayout(new BoxLayout(track1Set, BoxLayout.Y_AXIS));
        track1Set.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        track1Set.add(new JSlider());
        track1Set.add(new JButton("Btn"));
        west.add(track1Set);


        west.add(new JButton("Btn"));
        west.add(new JButton("Btn"));
        west.add(new JButton("Btn"));

        
        panel.add(south, BorderLayout.SOUTH);
        panel.add(north, BorderLayout.NORTH);
        panel.add(east, BorderLayout.EAST);
        panel.add(center, BorderLayout.CENTER);
        panel.add(west, BorderLayout.WEST);

        this.setContentPane(panel);
        this.setJMenuBar(menuBar);
        this.setSize(width, height);
        this.setVisible(true);

    }

    private void menuInit() {
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

        // File->New, X - Mnemonic
        exitMenuItem = new JMenuItem("Exit", KeyEvent.VK_X);
        exitMenuItem.addActionListener(this);
        fileMenu.add(exitMenuItem);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(exitMenuItem)) {
            System.exit(0);
        }
    }
}