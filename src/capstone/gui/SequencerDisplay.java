package capstone.gui;

import java.awt.BorderLayout;
import java.awt.Container;
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
    
    private JLabel pitchValue;
    private JLabel volumeValue;
    private JLabel durationValue;
    private JLabel instrumentValue;
    
    private JSlider slider;
    private JLabel sliderValue;
    
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
        			
        			pitchValue.setText("" + currentNote.getPitch());
        			volumeValue.setText("" + currentNote.getVolume());
        			durationValue.setText("" + currentNote.getDuration());
        			instrumentValue.setText("" + currentNote.getInstrument());
        			
        			// Move slider to new note's value
        			switch(type){
					case DURATION:
						slider.setValue(currentNote.getDuration());
						sliderValue.setText("" + currentNote.getDuration());
						break;
					case INSTRUMENT:
						slider.setValue(currentNote.getInstrument());
						sliderValue.setText("" + currentNote.getInstrument());
						break;
					case PITCH:
						slider.setValue(currentNote.getPitch());
						sliderValue.setText("" + currentNote.getPitch());
						break;
					case VOLUME:
						slider.setValue(currentNote.getVolume());
						sliderValue.setText("" + currentNote.getVolume());
        			}
        			
        			previous = button;
        		}
        	}
        };
        
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

        createTrackSelectionArea(west);

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
    
    public void createTrackSelectionArea(Container cont){
    	GridBagConstraints c = new GridBagConstraints();

        cont.setLayout(new GridBagLayout());
        
        JButton pitchButton = new JButton(PITCH_NAME);
        JButton volumeButton = new JButton(VOLUME_NAME);
        JButton durationButton = new JButton(DURATION_NAME);
        JButton instrumentButton = new JButton(INSTRUMENT_NAME);
        
        pitchValue = new JLabel();
        volumeValue = new JLabel();
        durationValue = new JLabel();
        instrumentValue = new JLabel();
        
        slider = new JSlider(0, 127);
        sliderValue = new JLabel();
        
        // Add pitch button to panel
        c.gridx = 0;
        c.gridy = 0;
        cont.add(pitchButton, c);
        
        ActionListener pitchListener = new ActionListener(){
        	@Override
        	public void actionPerformed(ActionEvent e){
        		sliderValue.setText("" + currentNote.getPitch());
        		
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
        
        // Add pitch label to panel
        c.gridx++;
        pitchValue.setText("" + currentNote.getPitch());
        cont.add(pitchValue, c);
        
        // Add volume button to panel
        c.gridx = 0;
        c.gridy++;
        cont.add(volumeButton, c);
        
        ActionListener volumeListener = new ActionListener(){
        	@Override
        	public void actionPerformed(ActionEvent e){
        		sliderValue.setText("" + currentNote.getVolume());
        		
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
        
        // Add volume label to panel
        c.gridx++;
        volumeValue.setText("" + currentNote.getVolume());
        cont.add(volumeValue, c);

        // Add duration button to panel
        c.gridx = 0;
        c.gridy++;
        cont.add(durationButton, c);
        
        ActionListener durationListener = new ActionListener(){
        	@Override
        	public void actionPerformed(ActionEvent e){
        		sliderValue.setText("" + currentNote.getDuration());
        		
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
        
        // Add duration label to panel
        c.gridx++;
        durationValue.setText("" + currentNote.getDuration());
        cont.add(durationValue, c);
        
        // Add instrument button to panel
        c.gridx = 0;
        c.gridy++;
        cont.add(instrumentButton, c);
        
        ActionListener instrumentListener = new ActionListener(){
        	@Override
        	public void actionPerformed(ActionEvent e){
        		sliderValue.setText("" + currentNote.getInstrument());
        		
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
        
        // Add instrument label to panel
        c.gridx++;
        instrumentValue.setText("" + currentNote.getInstrument());
        cont.add(instrumentValue, c);
        
        // Add listener to slider so that when it scrolls, the value in the 
        // JLabel is updated.
        
        ChangeListener sliderListener = new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				if(slider.getValueIsAdjusting())	
					sliderValue.setText("" + slider.getValue());	
			}
        	
        };
        
        slider.addChangeListener(sliderListener);
        
        // Add slider to track
        c.gridx = 0;
        c.gridy++;
        cont.add(slider,c);
        
        // Add slider label
        c.gridx++;
        cont.add(sliderValue, c);
        
        ActionListener saveListener = new ActionListener(){
        	@Override
        	public void actionPerformed(ActionEvent e){
        		switch(type){
        		case PITCH:
        			currentNote.setPitch(slider.getValue());
        			sliderValue.setText("" + currentNote.getPitch());
        			pitchValue.setText("" + currentNote.getPitch());
        			break;
        		case VOLUME:
        			currentNote.setVolume(slider.getValue());
        			sliderValue.setText("" + currentNote.getVolume());
        			volumeValue.setText("" + currentNote.getVolume());
        			break;
        		case DURATION:
        			currentNote.setDuration(slider.getValue());
        			sliderValue.setText("" + currentNote.getDuration());
        			durationValue.setText("" + currentNote.getDuration());
        			break;
        		case INSTRUMENT:
        			currentNote.setInstrument(slider.getValue());
        			sliderValue.setText("" + currentNote.getInstrument());
        			instrumentValue.setText("" + currentNote.getInstrument());
        			break;
        		}
        	}
        };
        
        JButton save = new JButton("Save");
        c.gridwidth = 2;
        c.gridx = 0;
        c.gridy++; 
        save.addActionListener(saveListener);
        cont.add(save, c);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(exitMenuItem)) {
            System.exit(0);
        }
    }
}