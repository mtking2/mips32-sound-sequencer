package capstone.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

/**
 * A graphical interface that represents a music sequencer.
 *
 * @author Brad Westley and Michael King
 * @version 9/17/15
 */
public class SequencerDisplay extends JFrame implements ActionListener {

    private JPanel panel, north, south, east, west, center, track1, track2, track3, track4;
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenuItem newMenuItem, openMenuItem, exitMenuItem;
    private JButton play, stop;
    
    private Note currentNote;

    public SequencerDisplay(String title, int width, int height){
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle(title);
        this.setResizable(false);
        menuInit();

        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        south = new JPanel();
        north = new JPanel(new FlowLayout(FlowLayout.LEFT));
        west = new JPanel(new GridLayout(4,1));
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
        		// Get note that was clicked
        		
        		if(e.getSource() instanceof NoteButton)
        			currentNote = ((NoteButton) e.getSource()).getNote();
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

        //west.add(new JSeparator());

        trackSetup();

        
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

        // File->New, X - Mnemonic
        exitMenuItem = new JMenuItem("Exit", KeyEvent.VK_X);
        exitMenuItem.addActionListener(this);
        fileMenu.add(exitMenuItem);
    }

    private void trackSetup() {
        GridBagConstraints c = new GridBagConstraints();

        track1 = new JPanel();
        track1.setLayout(new GridBagLayout());

        TitledBorder trackTitle = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED), "Track 1");
        trackTitle.setTitleJustification(TitledBorder.LEFT);
        track1.setBorder(trackTitle);

        JSlider instSlider1 = new JSlider(0,127,0);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 3;
        c.gridx = 0;
        c.gridy = 0;
        track1.add(instSlider1,c);

        JSlider pitchSlider1 = new JSlider(0,127,60);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10,0,0,0);
        c.gridx = 0;
        c.gridy = 1;
        track1.add(pitchSlider1,c);

        JSlider volSlider1 = new JSlider(0,127,127);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 2;
        track1.add(volSlider1,c);

        west.add(track1);
        //////////////////////////////////////////////////////////////////////

        c = new GridBagConstraints();
        track2 = new JPanel();
        track2.setLayout(new GridBagLayout());

        trackTitle = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED), "Track 2");
        trackTitle.setTitleJustification(TitledBorder.LEFT);
        track2.setBorder(trackTitle);

        JSlider instSlider2 = new JSlider(0,127);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 3;
        c.gridx = 0;
        c.gridy = 0;
        track2.add(instSlider2,c);

        JSlider pitchSlider2 = new JSlider(0,127);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10,0,0,0);
        c.gridx = 0;
        c.gridy = 1;
        track2.add(pitchSlider2,c);

        JSlider volSlider2 = new JSlider(0,127);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 2;
        track2.add(volSlider2,c);

        west.add(track2);
        //////////////////////////////////////////////////////////////////////

        c = new GridBagConstraints();
        track3 = new JPanel();
        track3.setLayout(new GridBagLayout());

        trackTitle = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED), "Track 3");
        trackTitle.setTitleJustification(TitledBorder.LEFT);
        track3.setBorder(trackTitle);

        JSlider instSlider3 = new JSlider(0,127);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 3;
        c.gridx = 0;
        c.gridy = 0;
        track3.add(instSlider3,c);

        JSlider pitchSlider3 = new JSlider(0,127);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10,0,0,0);
        c.gridx = 0;
        c.gridy = 1;
        track3.add(pitchSlider3,c);

        JSlider volSlider3 = new JSlider(0,127);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 2;
        track3.add(volSlider3,c);

        west.add(track3);
        //////////////////////////////////////////////////////////////////////

        c = new GridBagConstraints();
        track4 = new JPanel();
        track4.setLayout(new GridBagLayout());

        trackTitle = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED), "Track 4");
        trackTitle.setTitleJustification(TitledBorder.LEFT);
        track4.setBorder(trackTitle);

        JSlider instSlider4 = new JSlider(0,127);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 3;
        c.gridx = 0;
        c.gridy = 0;
        track4.add(instSlider4,c);

        JSlider pitchSlider4 = new JSlider(0,127);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10,0,0,0);
        c.gridx = 0;
        c.gridy = 1;
        track4.add(pitchSlider4,c);

        JSlider volSlider4 = new JSlider(0,127);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 2;
        track4.add(volSlider4,c);

        west.add(track4);


    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(exitMenuItem)) {
            System.exit(0);
        }
    }
}