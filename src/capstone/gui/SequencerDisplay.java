package capstone.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.*;
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
    private JButton play, stop, saveBtn, pitchBtn, volumeBtn, durationBtn, instrumentBtn, previous;
    
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
        west = new JPanel();
        north.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        west.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        west.setPreferredSize(new Dimension(290,150));

        // 4 tracks, 16 beats
        int tracks = 4;
        int beats = 16;
        
        center = new JPanel(new GridLayout(tracks, beats));
        center.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

        for(int i = 0; i < tracks; i++)
        	for(int j = 0; j < beats; j++){
        		NoteButton button = new NoteButton(i, j);
        		
        		button.addActionListener(this);
        		
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
        
        pitchBtn = new JButton(PITCH_NAME);
        volumeBtn = new JButton(VOLUME_NAME);
        durationBtn = new JButton(DURATION_NAME);
        instrumentBtn = new JButton(INSTRUMENT_NAME);
        
        pitchValue = new JLabel();
        volumeValue = new JLabel();
        durationValue = new JLabel();
        instrumentValue = new JLabel();
        
        slider = new JSlider(0, 127);
        sliderValue = new JLabel();

		c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(5,0,5,0);
        c.ipady = 10;
        c.ipadx = 25;
        
        // Add pitch button to panel
        c.gridx = 0;
        c.gridy = 0;
        cont.add(pitchBtn, c);
        
        pitchBtn.addActionListener(this);
        
        // Add pitch label to panel
        c.gridx++;
        pitchValue.setText("" + currentNote.getPitch());
        cont.add(pitchValue, c);
        
        // Add volume button to panel
        c.gridx = 0;
        c.gridy++;
        cont.add(volumeBtn, c);
        
        volumeBtn.addActionListener(this);
        
        // Add volume label to panel
        c.gridx++;
        volumeValue.setText("" + currentNote.getVolume());
        cont.add(volumeValue, c);

        // Add duration button to panel
        c.gridx = 0;
        c.gridy++;
        cont.add(durationBtn, c);
        
        durationBtn.addActionListener(this);
        
        // Add duration label to panel
        c.gridx++;
        durationValue.setText("" + currentNote.getDuration());
        cont.add(durationValue, c);
        
        // Add instrument button to panel
        c.gridx = 0;
        c.gridy++;
        cont.add(instrumentBtn, c);
        
        instrumentBtn.addActionListener(this);
        
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
        cont.add(Box.createHorizontalStrut(5));
        cont.add(new JSeparator(SwingConstants.VERTICAL));
        cont.add(Box.createHorizontalStrut(5));

        saveBtn = new JButton("Save");
        c.gridwidth = 2;
        c.gridx = 0;
        c.gridy++; 
        saveBtn.addActionListener(this);
        cont.add(saveBtn, c);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object c = e.getSource();
        if (c.equals(exitMenuItem)) {
            System.exit(0);
        } else if (c.equals(pitchBtn)||c.equals(volumeBtn)||c.equals(durationBtn)||c.equals(instrumentBtn)) {

            pitchBtn.setEnabled(true);
            volumeBtn.setEnabled(true);
            durationBtn.setEnabled(true);
            instrumentBtn.setEnabled(true);

            if (c.equals(pitchBtn)) {
                pitchBtn.setEnabled(false);
                sliderValue.setText("" + currentNote.getPitch());
                slider.setValue(currentNote.getPitch());
                type = ValueType.PITCH;
            } else if (c.equals(volumeBtn)) {
                volumeBtn.setEnabled(false);
                sliderValue.setText("" + currentNote.getVolume());
                slider.setValue(currentNote.getVolume());
                type = ValueType.VOLUME;
            } else if (c.equals(durationBtn)) {
                durationBtn.setEnabled(false);
                sliderValue.setText("" + currentNote.getDuration());
                slider.setValue(currentNote.getDuration());
                type = ValueType.DURATION;
            } else if (c.equals(instrumentBtn)) {
                instrumentBtn.setEnabled(false);
                sliderValue.setText("" + currentNote.getInstrument());
                slider.setValue(currentNote.getInstrument());
                type = ValueType.INSTRUMENT;
            }

        } else if (c instanceof NoteButton) {
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
        } else if (c.equals(saveBtn)) {
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
    }
}