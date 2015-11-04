package capstone.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;

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
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import capstone.gui.utils.ActionListenerFactory;
import capstone.gui.utils.SequencerUtils;

/**
 * A graphical interface that represents a music sequencer.
 *
 * @author Brad Westley and Michael King
 * @version 9/17/15
 */
public class SequencerDisplay extends JFrame implements ActionListener, ChangeListener {
	private NoteCollection notes;

	private JPanel panel, north, west, center;
	private JMenuBar menuBar;
	private JMenu fileMenu, editMenu;
	private JMenuItem newMenuItem, exitMenuItem, saveMenuItem, tempoMenuItem, scaleMenuItem;
	private JButton play, stop, confirm, reset, clearNote;
	private NoteButton previous;
	private JCheckBox restBox;

	private JLabel 	pitchLabel, volumeLabel, 
	durationLabel, instrumentLabel, 
	tempoLabel, scaleLabel;
	private JSlider volume, pitch, duration, instrument;

	public SequencerDisplay(String title, int width, int height){
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle(title);
		this.setResizable(false);

		tempoLabel = new JLabel("Tempo: " + SequencerUtils.tempo);
		scaleLabel = new JLabel("Scale: None");
		
		play = new JButton("Play");
		stop = new JButton("Stop");
		confirm = new JButton("Commit Changes");
		reset = new JButton("Reset Changes");

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
				NoteButton button = new NoteButton(i, j);
				button.addActionListener(this);
				// All notes start as rests, so start with red background
				button.setBackground(Color.RED);
				// Set button text field to pitch
				button.setText(SequencerUtils.intToPitchWithOctave(note.getPitch()));

				if(i == 0 && j == 0){
					// Default note is first note
					previous = button;
					button.setContentAreaFilled(false);
				}

				notes.setNote(i, j, note);

				center.add(button);
			}
		}

		notes.commit();
		
		play.setContentAreaFilled(false);
		play.addActionListener(ActionListenerFactory.getPlayListener(
				center.getComponents(), notes, this));
		stop.setContentAreaFilled(false);
		stop.addActionListener(ActionListenerFactory.getStopListener(this));

		confirm.setContentAreaFilled(false);
		confirm.addActionListener(
				ActionListenerFactory.getConfirmListener(center.getComponents(), 
						notes, confirm, reset, this));

		reset.setContentAreaFilled(false);
		reset.addActionListener(this);

		north.add(play);
		north.add(stop);
		north.add(confirm);
		north.add(reset);
		north.add(tempoLabel);
		north.add(scaleLabel);

		createTrackSelectionArea(west);

		panel.add(north, BorderLayout.NORTH);
		panel.add(center, BorderLayout.CENTER);
		panel.add(west, BorderLayout.WEST);
		
		menuInit();

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
		saveMenuItem.addActionListener(
				ActionListenerFactory.getSaveListener(center.getComponents(), 
						this, 
						notes));
		fileMenu.add(saveMenuItem);

		// File->Exit, X - Mnemonic
		exitMenuItem = new JMenuItem("Exit", KeyEvent.VK_X);
		exitMenuItem.addActionListener(ActionListenerFactory.getExitListener());
		fileMenu.add(exitMenuItem);

		// Edit -> Tempo..., T - Mnemonic
		tempoMenuItem = new JMenuItem("Tempo...", KeyEvent.VK_T);
		tempoMenuItem.addActionListener(
				ActionListenerFactory.getTempoListener(this, tempoLabel));
		editMenu.add(tempoMenuItem);

		// Edit -> Scale..., C - Mnemonic
		scaleMenuItem = new JMenuItem("Scale...", KeyEvent.VK_C);
		scaleMenuItem.addActionListener(
				ActionListenerFactory.getScaleListener(this, scaleLabel));
		editMenu.add(scaleMenuItem);
	}

	public void createTrackSelectionArea(Container cont){

		JPanel subNorth = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JPanel subCenter = new JPanel(new GridBagLayout());
		JPanel subSouth = new JPanel(new FlowLayout());
		clearNote = new JButton("Clear Note");
		clearNote.addActionListener(this);
		subNorth.add(clearNote);

		GridBagConstraints c = new GridBagConstraints();

		Note currentNote = getCurrentNoteFromCollection();

		pitch = new JSlider(0, 127, currentNote.getPitch());
		volume = new JSlider(0, 127, currentNote.getVolume());
		duration = new JSlider(0, 2000, currentNote.getDuration());
		instrument = new JSlider(0, 127, currentNote.getInstrument());

		pitchLabel = new JLabel();
		volumeLabel = new JLabel();
		durationLabel = new JLabel();
		instrumentLabel = new JLabel();

		pitchLabel.setText(SequencerUtils.intToPitchWithOctave(pitch.getValue()));
		volumeLabel.setText("" + volume.getValue());
		durationLabel.setText("" + duration.getValue());
		instrumentLabel.setText("" + instrument.getValue());

		restBox = new JCheckBox("Rest");
		restBox.setSelected(true);

		restBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(!SequencerUtils.ignoreStateChange){
					if(e.getStateChange() == ItemEvent.SELECTED){
						currentNote.makeRest();
						notes.getNote(currentNote.getTrack(), currentNote.getBeat()).makeRest();
						if(notes.hasNoteBeenModified(currentNote.getTrack(), 
								currentNote.getBeat()))
							previous.setBackground(Color.GREEN);
						else
							previous.setBackground(Color.RED);

						getCurrentNoteFromCollection().makeRest();
					} else {
						currentNote.unmakeRest();
						notes.getNote(currentNote.getTrack(), currentNote.getBeat()).unmakeRest();
						if(notes.hasNoteBeenModified(currentNote.getTrack(), 
								currentNote.getBeat()))
							previous.setBackground(Color.GREEN);
						else
							previous.setBackground(null);

						getCurrentNoteFromCollection().unmakeRest();
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

	@Override
	public void actionPerformed(ActionEvent e) {
		Object c = e.getSource();

		if (c instanceof NoteButton) {
			NoteButton button = (NoteButton) c;

			// Disable this note button, enable the last one selected
			previous.setContentAreaFilled(true);
			button.setContentAreaFilled(false);

			// Change background of previous button if
			// it was a rest
			if(notes.getNote(SequencerUtils.track, SequencerUtils.beat).isRest()){
				previous.setBackground(Color.RED);
			} else {
				if(notes.hasNoteBeenModified(SequencerUtils.track, 
						SequencerUtils.beat)){
					previous.setBackground(Color.GREEN);
				} else {
					previous.setBackground(null);
				}
			}

			SequencerUtils.track = button.getTrack();
			SequencerUtils.beat = button.getBeat();

			Note currentNote = getCurrentNoteFromCollection();

			pitch.setValue(currentNote.getPitch());
			volume.setValue(currentNote.getVolume());
			duration.setValue(currentNote.getDuration());
			instrument.setValue(currentNote.getInstrument());

			pitchLabel.setText(SequencerUtils.intToPitchWithOctave(currentNote.getPitch()));
			volumeLabel.setText("" + currentNote.getVolume());
			durationLabel.setText("" + currentNote.getDuration());
			instrumentLabel.setText("" + currentNote.getInstrument());

			SequencerUtils.ignoreStateChange = true;
			restBox.setSelected(currentNote.isRest());
			SequencerUtils.ignoreStateChange = false;

			previous = button;
		} else if (c.equals(clearNote)) {
			Note currentNote = getCurrentNoteFromCollection();

			currentNote = new Note(currentNote.getTrack(), currentNote.getBeat());
			pitch.setValue(currentNote.getPitch());
			volume.setValue(currentNote.getVolume());
			duration.setValue(currentNote.getDuration());
			instrument.setValue(currentNote.getInstrument());
			pitchLabel.setText(SequencerUtils.intToPitchWithOctave(currentNote.getPitch()));
			volumeLabel.setText("" + currentNote.getVolume());
			durationLabel.setText("" + currentNote.getDuration());
			instrumentLabel.setText("" + currentNote.getInstrument());
			previous.setBackground(Color.RED);
		} else if(c.equals(reset)){
			if(notes.isModified()){
				confirm.setContentAreaFilled(false);
				reset.setContentAreaFilled(false);

				Note original = notes.getOriginalNote(SequencerUtils.track, SequencerUtils.beat);

				pitch.setValue(original.getPitch());
				volume.setValue(original.getVolume());
				duration.setValue(original.getDuration());
				instrument.setValue(original.getInstrument());

				pitchLabel.setText(SequencerUtils.intToPitchWithOctave(original.getPitch()));
				previous.setText(SequencerUtils.intToPitchWithOctave(original.getPitch()));
				volumeLabel.setText("" + original.getVolume());
				durationLabel.setText("" + original.getDuration());
				instrumentLabel.setText("" + original.getInstrument());

				notes.reset();

				SequencerUtils.resetNoteBackgrounds(center.getComponents(), notes);
			} else {
				JOptionPane.showMessageDialog(this, "No notes have changed.");
			}
		} 
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (pitch.getValueIsAdjusting()){
			int p = pitch.getValue();
			if(SequencerUtils.scale != null &&
					!SequencerUtils.scale.inScale(SequencerUtils.intToPitch(p))){
				// If not in the scale, the next pitch up will be, so
				// skip this pitch
				pitch.setValue(++p);
			}
			pitchLabel.setText(SequencerUtils.intToPitchWithOctave(p));
			getCurrentNoteFromCollection().setPitch(p);
		} else if (volume.getValueIsAdjusting()){
			int v = volume.getValue();
			volumeLabel.setText("" + v);
			getCurrentNoteFromCollection().setVolume(v);
		} else if (duration.getValueIsAdjusting()){
			int d = duration.getValue();
			durationLabel.setText("" + d);
			getCurrentNoteFromCollection().setDuration(d);
		} else if (instrument.getValueIsAdjusting()){
			int i = instrument.getValue();
			instrumentLabel.setText("" + i);
			for(Note n : notes.getRow(SequencerUtils.track))
				n.setInstrument(i);
			getCurrentNoteFromCollection().setInstrument(i);
		}

		if(!notes.isModified()){
			notes.setModified();
			confirm.setContentAreaFilled(true);
			reset.setContentAreaFilled(true);
		}
	}

	private Note getCurrentNoteFromCollection(){
		return notes.getNote(SequencerUtils.track, SequencerUtils.beat);
	}
}
