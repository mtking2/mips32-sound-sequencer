package capstone.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * A graphical interface that represents a music sequencer.
 *
 * @author Brad Westley and Michael King
 * @version 9/17/15
 */
public class SequencerDisplay extends JFrame implements ActionListener, ChangeListener {
	private NoteCollection notes;

	private int tempo;

	private JOptionPane tempoSelect;

	private JPanel panel, north, west, center;
	private JMenuBar menuBar;
	private JMenu fileMenu, editMenu;
	private JMenuItem newMenuItem, exitMenuItem, saveMenuItem, tempoMenuItem;
	private JButton play, stop, confirm, reset, clearNote;
	private NoteButton previous;
	private JCheckBox restBox;

	private JLabel pitchLabel, volumeLabel, durationLabel, instrumentLabel, tempoLabel;
	private JSlider volume, pitch, duration, instrument;
	private Note currentNote;

	private boolean ignoreStateChange, playing;

	public SequencerDisplay(String title, int width, int height){
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle(title);
		this.setResizable(false);
		menuInit();

		ignoreStateChange = false;
		playing = false;

		tempoSelect = null;

		tempo = 120;
		tempoLabel = new JLabel("Tempo: " + tempo);
		tempoLabel.setAlignmentX(SwingConstants.RIGHT);

		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		north = new JPanel(new FlowLayout(FlowLayout.LEFT));
		west = new JPanel(new BorderLayout());
		north.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		west.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		west.setPreferredSize(new Dimension(315, 200));

		// 4 tracks, 16 beats
		int tracks = 4;
		int beats = 16;

		notes = new NoteCollection(tracks, beats);

		center = new JPanel(new GridLayout(tracks, beats));
		center.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

		for(int i = 0; i < tracks; i++){
			for(int j = 0; j < beats; j++){
				Note note = new Note(i, j);
				NoteButton button = new NoteButton(note);
				button.addActionListener(this);
				// All notes start as rests, so start with red background
				button.setBackground(Color.RED);
				// Set button text field to pitch
				button.setText(intToPitch(note.getPitch()));

				if(i == 0 && j == 0){
					// Default note is first note
					currentNote = note;
					previous = button;
					button.setContentAreaFilled(false);
				}

				notes.setNote(i, j, note);

				center.add(button);
			}
		}

		notes.commit();

		play = new JButton("Play");
		play.setContentAreaFilled(false);
        play.addActionListener(this);
		stop = new JButton("Stop");
		stop.setContentAreaFilled(false);
        stop.addActionListener(this);

		confirm = new JButton("Commit Changes");
		confirm.setContentAreaFilled(false);
		confirm.addActionListener(this);

		reset = new JButton("Reset Changes");
		reset.setContentAreaFilled(false);
		reset.addActionListener(this);

		north.add(play);
		north.add(stop);
		north.add(confirm);
		north.add(reset);
		north.add(tempoLabel);

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

		// Edit Menu, E - Mnemonic
		editMenu = new JMenu("Edit");
		editMenu.setMnemonic(KeyEvent.VK_E);
		menuBar.add(editMenu);

		// File->New, N - Mnemonic
		newMenuItem = new JMenuItem("New", KeyEvent.VK_N);
		fileMenu.add(newMenuItem);

		// File -> Save, S - Mnemonic
		saveMenuItem = new JMenuItem("Save", KeyEvent.VK_S);
		saveMenuItem.addActionListener(this);
		fileMenu.add(saveMenuItem);

		// File->Exit, X - Mnemonic
		exitMenuItem = new JMenuItem("Exit", KeyEvent.VK_X);
		exitMenuItem.addActionListener(this);
		fileMenu.add(exitMenuItem);

		// Edit -> Tempo..., T - Mnemonic
		tempoMenuItem = new JMenuItem("Tempo...", KeyEvent.VK_T);
		tempoMenuItem.addActionListener(this);
		editMenu.add(tempoMenuItem);
	}

	public String exportNotesToMIPS(){
		StringBuilder builder = new StringBuilder();

		// Begin with data section
		builder.append(".data\n\n");

		builder.append("instruments:    .word ");

		for(int i = 0; i < notes.getAll().length; i++){
			if(i > 0) builder.append(", ");

			builder.append(notes.getNote(i, 0).getInstrument());
		}

		builder.append("\n\n");

		for(int i = 0; i < notes.getAll().length; i++){
			builder.append("# TRACK " + (i + 1) + " DATA #\n\n");

			builder.append(notesDataToMIPS(i));

			builder.append("\n");
		}

		builder.append("\n");

		builder.append("tempo:        .word    "
				+ tempo);

		return builder.toString();
	}

	private String notesDataToMIPS(int track){
		StringBuilder builder = new StringBuilder();

		// Add pitch array
		builder.append("pitchArray" + (track + 1) + ":    .word ");

		for(int i = 0; i < notes.getRow(track).length; i++){
			if(i > 0) builder.append(", ");

			if(!notes.getNote(track, i).isRest()){
				builder.append(notes.getNote(track, i).getPitch());
			} else {
				// -1 is value designating a rest
				builder.append(-1);
			}
		}

		builder.append("\n");

		// Add volume array
		builder.append("volumeArray" + (track + 1) + ":    .word ");

		for(int i = 0; i < notes.getRow(track).length; i++){
			if(i > 0) builder.append(", ");

			builder.append(notes.getNote(track, i).getVolume());
		}

		builder.append("\n");

		// Add duration array
		builder.append("durationArray" + (track + 1) + ":    .word ");

		for(int i = 0; i < notes.getRow(track).length; i++){
			if(i > 0) builder.append(", ");

			builder.append(notes.getNote(track, i).getDuration());
		}

		builder.append("\n");

		return builder.toString();
	}

	public void createTrackSelectionArea(Container cont){

		JPanel subNorth = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JPanel subCenter = new JPanel(new GridBagLayout());
		JPanel subSouth = new JPanel(new FlowLayout());
		clearNote = new JButton("Clear Note");
		clearNote.addActionListener(this);
		subNorth.add(clearNote);

		GridBagConstraints c = new GridBagConstraints();

		pitch = new JSlider(0, 127, currentNote.getPitch());
		volume = new JSlider(0, 127, currentNote.getVolume());
		duration = new JSlider(0, 2000, currentNote.getDuration());
		instrument = new JSlider(0, 127, currentNote.getInstrument());

		pitchLabel = new JLabel();
		volumeLabel = new JLabel();
		durationLabel = new JLabel();
		instrumentLabel = new JLabel();

		pitchLabel.setText(intToPitch(pitch.getValue()));
		volumeLabel.setText("" + volume.getValue());
		durationLabel.setText("" + duration.getValue());
		instrumentLabel.setText("" + instrument.getValue());

		restBox = new JCheckBox("Rest");
		restBox.setSelected(true);

		restBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(!ignoreStateChange){
					if(e.getStateChange() == ItemEvent.SELECTED){
						currentNote.makeRest();
						notes.getNote(currentNote.getTrack(), currentNote.getBeat()).makeRest();
						if(notes.hasNoteBeenModified(currentNote.getTrack(), 
								currentNote.getBeat()))
							previous.setBackground(Color.GREEN);
						else
							previous.setBackground(Color.RED);
					} else {
						currentNote.unmakeRest();
						notes.getNote(currentNote.getTrack(), currentNote.getBeat()).unmakeRest();
						if(notes.hasNoteBeenModified(currentNote.getTrack(), 
								currentNote.getBeat()))
							previous.setBackground(Color.GREEN);
						else
							previous.setBackground(null);
					}


					if(!notes.isModified()){
						notes.setModified();
						confirm.setContentAreaFilled(true);
						reset.setContentAreaFilled(true);
					}
				}
			}
		});

		c.anchor = GridBagConstraints.CENTER;
		c.insets = new Insets(5,2,5,0);
		c.ipady = 10;
		c.fill = GridBagConstraints.HORIZONTAL;
		//c.ipadx = 2;
		c.weightx = 0.5;

		c.gridx = 0;
		c.gridy = 0;
		subCenter.add(restBox);


		// Add pitch slider to panel
		c.gridx = 0;
		c.gridy++;
		subCenter.add(new JLabel("Pitch "), c);
		c.gridx++;
		subCenter.add(pitch, c);


		// Add pitch label to panel
		c.gridx++;
		subCenter.add(pitchLabel, c);

		// Add volume slider to panel
		c.gridx = 0;
		c.gridy++;
		subCenter.add(new JLabel("Volume "), c);
		c.gridx++;
		subCenter.add(volume, c);

		// Add volume label to panel
		c.gridx++;
		subCenter.add(volumeLabel, c);

		// Add duration slider to panel
		c.gridx = 0;
		c.gridy++;
		subCenter.add(new JLabel("Duration "), c);
		c.gridx++;
		subCenter.add(duration, c);

		// Add duration label to panel
		c.gridx++;
		subCenter.add(durationLabel, c);

		// Add instrument slider to panel
		c.gridx = 0;
		c.gridy++;
		subCenter.add(new JLabel("Instrument "), c);
		c.gridx++;
		subCenter.add(instrument, c);

		// Add instrument label to panel
		c.gridx++;
		subCenter.add(instrumentLabel, c);

		// Add listener to slider so that when it scrolls, the value in the 
		// JLabel is updated.

		pitch.addChangeListener(this);
		volume.addChangeListener(this);
		duration.addChangeListener(this);
		instrument.addChangeListener(this);

		cont.add(subNorth, BorderLayout.NORTH);
		cont.add(subCenter, BorderLayout.CENTER);
		cont.add(subSouth, BorderLayout.SOUTH);
	}

    private void toFile() {
        File file = new File(getPathToGUI() + "mips.asm");

        byte[] data = exportNotesToMIPS().getBytes();
        byte[] code = null;

        try {
            code = Files.readAllBytes(
                    Paths.get(getPathToGUI() + "SequencerStem.asm"));
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error reading from file SequencerStem.asm:\n"
                            + ex.getMessage());
            return;
        }

        // Combine track data and template into one array

        byte[] contents = new byte[data.length + code.length];

        for (int i = 0; i < data.length; i++)
            contents[i] = data[i];

        for (int i = data.length; i < data.length + code.length; i++)
            contents[i] = code[i - data.length];

        try {
            Files.write(Paths.get(file.getPath()),
                    contents,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.WRITE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error saving to file:\n" + ex.getMessage());
        }
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		Object c = e.getSource();
        Runtime rt = Runtime.getRuntime();
        if (c.equals(exitMenuItem)) {
			System.exit(0);
		} else if(c.equals(saveMenuItem)) {
			toFile();
		} else if (c.equals(tempoMenuItem)) {
			String answer = JOptionPane.showInputDialog(this,
					"Enter a new tempo:",
					tempo);

			try {
				tempo = Integer.parseInt(answer);
			} catch (NumberFormatException ex){
				tempoError();
			}

			if(tempo > 0 && tempo <= 300){
				tempo = Integer.parseInt(answer);
				tempoLabel.setText("Tempo: " + tempo);
			} else {
				tempoError();
			}
		} else if (c instanceof NoteButton) {
			NoteButton button = (NoteButton) c;

			currentNote = button.getNote();

			// Disable this note button, enable the last one selected
			previous.setContentAreaFilled(true);
			button.setContentAreaFilled(false);

			pitch.setValue(currentNote.getPitch());
			volume.setValue(currentNote.getVolume());
			duration.setValue(currentNote.getDuration());
			instrument.setValue(currentNote.getInstrument());

			pitchLabel.setText(intToPitch(currentNote.getPitch()));
			volumeLabel.setText("" + currentNote.getVolume());
			durationLabel.setText("" + currentNote.getDuration());
			instrumentLabel.setText("" + currentNote.getInstrument());

			ignoreStateChange = true;
			restBox.setSelected(currentNote.isRest());
			ignoreStateChange = false;

			previous = button;
		} else if (c.equals(clearNote)) {
			currentNote = new Note(currentNote.getTrack(), currentNote.getBeat());
			pitch.setValue(currentNote.getPitch());
			volume.setValue(currentNote.getVolume());
			duration.setValue(currentNote.getDuration());
			instrument.setValue(currentNote.getInstrument());
			pitchLabel.setText(intToPitch(currentNote.getPitch()));
			volumeLabel.setText("" + currentNote.getVolume());
			durationLabel.setText("" + currentNote.getDuration());
			instrumentLabel.setText("" + currentNote.getInstrument());
			if(currentNote.isRest())
				previous.setBackground(Color.RED);
			else
				previous.setBackground(null);
		} else if (c.equals(tempoSelect)){
			try{
				tempo = Integer.parseInt(e.getActionCommand());
			} catch(NumberFormatException ex){
				tempoError();
			}
		} else if(c.equals(confirm)){
			if(notes.isModified()){
				confirm.setContentAreaFilled(false);
				reset.setContentAreaFilled(false);
				notes.commit();
				resetNoteBackgrounds();
			} else {
				JOptionPane.showMessageDialog(this, "No notes have changed.");
			}
		} else if(c.equals(reset)){
			if(notes.isModified()){
				confirm.setContentAreaFilled(false);
				reset.setContentAreaFilled(false);

				Note original = notes.getOriginalNote(currentNote.getTrack(), 
						currentNote.getBeat());

				pitch.setValue(original.getPitch());
				volume.setValue(original.getVolume());
				duration.setValue(original.getDuration());
				instrument.setValue(original.getInstrument());

				pitchLabel.setText(intToPitch(original.getPitch()));
				previous.setText(intToPitch(original.getPitch()));
				volumeLabel.setText("" + original.getVolume());
				durationLabel.setText("" + original.getDuration());
				instrumentLabel.setText("" + original.getInstrument());

				notes.reset();

				resetNoteBackgrounds();
			} else {
				JOptionPane.showMessageDialog(this, "No notes have changed.");
			}
		} else if(c.equals(play)){
			if(notes.allRests()){
				JOptionPane.showMessageDialog(this, "All notes are rests; "
						+ "there is nothing to play.");
			} else if(playing){
				JOptionPane.showMessageDialog(this, "The sequence is already playing.");
			} else {
				playing = true;
                toFile();
                try {
                    String playPath = "java -jar src/capstone/gui/Mars40_CGP2.jar src/capstone/gui/DrumBeatExample.asm";
                    //System.out.println(playPath);
                    rt.exec(playPath);
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
			}
		} else if(c.equals(stop)){
			if(!playing)
				JOptionPane.showMessageDialog(this, "The sequence is already stopped.");
			else
				playing = false;
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (pitch.getValueIsAdjusting()){
			int p = pitch.getValue();
			pitchLabel.setText(intToPitch(p));
			currentNote.setPitch(p);
		} else if (volume.getValueIsAdjusting()){
			int v = volume.getValue();
			volumeLabel.setText("" + v);
			currentNote.setVolume(v);
		} else if (duration.getValueIsAdjusting()){
			int d = duration.getValue();
			durationLabel.setText("" + d);
			currentNote.setDuration(d);
		} else if (instrument.getValueIsAdjusting()){
			int i = instrument.getValue();
			instrumentLabel.setText("" + i);
			for(Note n : notes.getRow(currentNote.getTrack()))
				n.setInstrument(i);
		}

		if(!notes.isModified()){
			notes.setModified();
			confirm.setContentAreaFilled(true);
			reset.setContentAreaFilled(true);
		}
	}

	private String getPathToGUI(){
		StringBuilder builder = new StringBuilder();

		builder.append(System.getProperty("user.dir"));

		builder.append(File.separator + "src");
		builder.append(File.separator + "capstone");
		builder.append(File.separator + "gui" + File.separator);

		return builder.toString();
	}

	private void tempoError(){
		JOptionPane.showMessageDialog(this, "Please enter a valid tempo value from 1 - 300.");
	}

	private String intToPitch(int pitch){
		// 12 half-notes are in an octave
		int octave = (pitch - (pitch % 12)) / 12;
		int note = pitch % 12;

		String letter = null;

		switch(note){
		case 0:	letter = "C";
		break;
		case 1:	letter = "C#";
		break;
		case 2:	letter = "D";
		break;
		case 3:	letter = "D#";
		break;
		case 4:	letter = "E";
		break;
		case 5:	letter = "F";
		break;
		case 6:	letter = "F#";
		break;
		case 7:	letter = "G";
		break;
		case 8:	letter = "G#";
		break;
		case 9:	letter = "A";
		break;
		case 10:	letter = "A#";
		break;
		case 11:	letter = "B";
		break;
		default:	letter = "C";
		}

		return letter + octave;
	}

	private void resetNoteBackgrounds(){
		for(Component c : center.getComponents()){
			if(c instanceof NoteButton){
				NoteButton button = (NoteButton) c;

				if(button.getNote().isRest())
					c.setBackground(Color.RED);
				else
					c.setBackground(null);

				button.setText(intToPitch(button.getNote().getPitch()));
			}
		}

	}
}
