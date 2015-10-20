package capstone.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * A graphical interface that represents a music sequencer.
 *
 * @author Brad Westley and Michael King
 * @version 9/17/15
 */
public class SequencerDisplay extends JFrame implements ActionListener {
	private static final String PITCH_NAME = "Pitch";
	private static final String VOLUME_NAME = "Volume";
	private static final String DURATION_NAME = "Duration";
	private static final String INSTRUMENT_NAME = "Instrument";

    private JPanel panel, north, west, center;
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenuItem newMenuItem, exitMenuItem;
    private JButton play, stop, previous;
    
    private Note currentNote;
    private ValueType type;

    public SequencerDisplay(String title, int width, int height){
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle(title);
        this.setResizable(false);
        menuInit();
        
        type = ValueType.PITCH;	// Set to pitch as default

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
        		if(e.getSource() instanceof NoteButton){
        			NoteButton button = (NoteButton) e.getSource();
        			
        			currentNote = button.getNote();
        			
        			// Disable this note button, enable the last one selected
        			button.setEnabled(false);
        			previous.setEnabled(true);
        			
        			previous = button;
        		}
        	}
        };
        
        // center = new NoteTable(tracks, beats, south);
        center.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

        for(int i = 0; i < tracks; i++)
        	for(int j = 0; j < beats; j++){
        		NoteButton button = new NoteButton(i, j);
        		
        		button.addActionListener(listener);
        		
        		if(i == 0 && j == 0){
        			// Default note is first note
        			currentNote = button.getNote();	
        			button.setEnabled(false);
        			previous = button;
        		}
        		
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
        JSlider slider = new JSlider(0,127);
        track.setLayout(new GridBagLayout());
        
        JButton pitchButton = new JButton(PITCH_NAME);
        JButton volumeButton = new JButton(VOLUME_NAME);
        JButton durationButton = new JButton(DURATION_NAME);
        JButton instrumentButton = new JButton(INSTRUMENT_NAME);

        TitledBorder trackTitle = BorderFactory.createTitledBorder(
        		BorderFactory.createEtchedBorder(EtchedBorder.RAISED), 
        			"Track " + (trackNumber + 1));
        trackTitle.setTitleJustification(TitledBorder.LEFT);
        track.setBorder(trackTitle);
        
        JLabel value = new JLabel();
        
        // Add pitch button to panel
        c.gridx = 0;
        c.gridy = 0;
        track.add(pitchButton, c);
        
        ActionListener pitchListener = new ActionListener(){
        	@Override
        	public void actionPerformed(ActionEvent e){
        		value.setText("" + currentNote.getPitch());
        		
        		slider.setValue(currentNote.getPitch());
        		
        		switch(type){
        		case INSTRUMENT:
        			instrumentButton.setEnabled(true);
        			break;
        		case VOLUME:
        			volumeButton.setEnabled(true);
        			break;
        		case DURATION:
        			durationButton.setEnabled(true);
        			break;
        		case PITCH:
        		}
        		
        		pitchButton.setEnabled(false);
        		
        		type = ValueType.PITCH;
        	}
        };
        
        pitchButton.addActionListener(pitchListener);
        
        c.gridx++;
        track.add(volumeButton, c);
        
        ActionListener volumeListener = new ActionListener(){
        	@Override
        	public void actionPerformed(ActionEvent e){
        		value.setText("" + currentNote.getVolume());
        		
        		slider.setValue(currentNote.getVolume());
        		
        		switch(type){
        		case PITCH:
        			pitchButton.setEnabled(true);
        			break;
        		case INSTRUMENT:
        			instrumentButton.setEnabled(true);
        			break;
        		case DURATION:
        			durationButton.setEnabled(true);
        			break;
        		case VOLUME:
        		}
        		
        		volumeButton.setEnabled(false);
        		
        		type = ValueType.VOLUME;
        	}
        };
        
        volumeButton.addActionListener(volumeListener);

        // Add duration button to panel
        c.gridx++;
        track.add(durationButton, c);
        
        ActionListener durationListener = new ActionListener(){
        	@Override
        	public void actionPerformed(ActionEvent e){
        		value.setText("" + currentNote.getDuration());
        		
        		slider.setValue(currentNote.getDuration());
        		
        		switch(type){
        		case PITCH:
        			pitchButton.setEnabled(true);
        			break;
        		case VOLUME:
        			volumeButton.setEnabled(true);
        			break;
        		case INSTRUMENT:
        			instrumentButton.setEnabled(true);
        			break;
        		case DURATION:
        		}
        		
        		durationButton.setEnabled(false);
        		
        		type = ValueType.DURATION;
        	}
        };
        
        durationButton.addActionListener(durationListener);
        
        // Add instrument button to panel
        c.gridx++;
        track.add(instrumentButton, c);
        
        ActionListener instrumentListener = new ActionListener(){
        	@Override
        	public void actionPerformed(ActionEvent e){
        		value.setText("" + currentNote.getInstrument());
        		
        		slider.setValue(currentNote.getInstrument());
        		
        		switch(type){
        		case PITCH:
        			pitchButton.setEnabled(true);
        			break;
        		case VOLUME:
        			volumeButton.setEnabled(true);
        			break;
        		case DURATION:
        			durationButton.setEnabled(true);
        			break;
        		case INSTRUMENT:
        		}
        		
        		instrumentButton.setEnabled(false);
        		
        		type = ValueType.INSTRUMENT;
        	}
        };
        
        instrumentButton.addActionListener(instrumentListener);
        
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy++;
        track.add(value, c);
        
        // Add listener to slider so that when it scrolls, the value in the 
        // JLabel is updated.
        
        ChangeListener sliderListener = new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				if(slider.getValueIsAdjusting())	
					value.setText("" + slider.getValue());	
			}
        	
        };
        
        slider.addChangeListener(sliderListener);
        
        // Add slider to track
        c.gridwidth = 2;
        c.gridx++;
        track.add(slider,c);
        
        ActionListener saveListener = new ActionListener(){
        	@Override
        	public void actionPerformed(ActionEvent e){
        		switch(type){
        		case PITCH:
        			currentNote.setPitch(slider.getValue());
        			value.setText("" + currentNote.getPitch());
        			break;
        		case VOLUME:
        			currentNote.setVolume(slider.getValue());
        			value.setText("" + currentNote.getVolume());
        			break;
        		case DURATION:
        			currentNote.setDuration(slider.getValue());
        			value.setText("" + currentNote.getDuration());
        			break;
        		case INSTRUMENT:
        			currentNote.setInstrument(slider.getValue());
        			value.setText("" + currentNote.getInstrument());
        			break;
        		}
        	}
        };
        
        JButton save = new JButton("Save");
        c.gridwidth = 1;
        c.gridx = c.gridx + 2; 
        save.addActionListener(saveListener);
        track.add(save, c);
        
        return track;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(exitMenuItem)) {
            System.exit(0);
        }
    }
}