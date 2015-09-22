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

    private JPanel panel, north, west, center, track1, track2, track3, track4;
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
        north = new JPanel(new FlowLayout(FlowLayout.LEFT));
        west = new JPanel(new GridLayout(4,1));
        north.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
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

        for(int i = 0; i < tracks; i++)
        	west.add(createTrack(i));

        panel.add(north, BorderLayout.NORTH);
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
    
    public JPanel createTrack(int trackNumber){
    	GridBagConstraints c = new GridBagConstraints();

        JPanel track = new JPanel();
        track.setLayout(new GridBagLayout());

        TitledBorder trackTitle = BorderFactory.createTitledBorder(
        		BorderFactory.createEtchedBorder(EtchedBorder.RAISED), 
        			"Track " + (trackNumber + 1));
        trackTitle.setTitleJustification(TitledBorder.LEFT);
        track.setBorder(trackTitle);
        
        JButton pitchButton = new JButton("Pitch");
        c.gridx = 0;
        c.gridy = 0;
        track.add(pitchButton, c);
        
        JButton volumeButton = new JButton("Volume");
        c.gridx++;
        track.add(volumeButton, c);

        JButton durationButton = new JButton("Duration");
        c.gridx++;
        track.add(durationButton, c);
        
        JButton instrumentButton = new JButton("Instrument");
        c.gridx++;
        track.add(instrumentButton, c);
        
        JSlider slider = new JSlider(0,127);
        c.gridwidth = 4;
        c.gridx = 0;
        c.gridy++;
        track.add(slider,c);
        
        return track;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(exitMenuItem)) {
            System.exit(0);
        }
    }
}