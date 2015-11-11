package capstone.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
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
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import capstone.gui.containers.Buttons;
import capstone.gui.containers.Labels;
import capstone.gui.containers.NoteCollection;
import capstone.gui.containers.Sliders;
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
	private Labels labels;
	private Sliders sliders;
	private Buttons buttons;

	private JPanel panel, north, west, center;
	private JMenuBar menuBar;
	private JMenu fileMenu, editMenu;
	private JMenuItem newMenuItem, exitMenuItem, saveMenuItem, tempoMenuItem, scaleMenuItem;
	
	private NoteButton currentButton;
	private JCheckBox restBox;

	public SequencerDisplay(String title, int width, int height){
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle(title);
		this.setResizable(false);
		
		labels = new Labels();
		
		buttons = new Buttons();

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
				button.setText(SequencerUtils.intPitchToString(note.getPitch()));

				if(i == 0 && j == 0){
					// Default note is first note
					currentButton = button;
					button.setBorder(SequencerUtils.BUTTON_SELECTED_BORDER);
				}

				notes.setNote(i, j, note);

				center.add(button);
			}
		}

		notes.commit();
		
		JButton play = buttons.getPlayButton();
		JButton stop = buttons.getStopButton();
		JButton confirm = buttons.getConfirmButton();
		JButton reset = buttons.getResetButton();
		
		play.addActionListener(ActionListenerFactory.getPlayListener(
				center.getComponents(), notes, play, stop, this));
		stop.addActionListener(ActionListenerFactory.getStopListener(this));

		confirm.setContentAreaFilled(false);
		confirm.addActionListener(
				ActionListenerFactory.getConfirmListener(center.getComponents(), 
						notes, confirm, reset, play, this));

		reset.setContentAreaFilled(false);
		reset.addActionListener(this);

		north.add(play);
		north.add(stop);
		north.add(confirm);
		north.add(reset);

		for(Component c : labels.getComponents())
			north.add(c);

		createTrackSelectionArea(west);

		panel.add(north, BorderLayout.NORTH);
		panel.add(center, BorderLayout.CENTER);
		panel.add(west, BorderLayout.WEST);
		
		menuInit();

		this.setContentPane(panel);
		this.setJMenuBar(menuBar);
		this.setSize(width, height);
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
				ActionListenerFactory.getTempoListener(this, 
						labels.getTempoLabel()));
		editMenu.add(tempoMenuItem);

		// Edit -> Scale..., C - Mnemonic
		scaleMenuItem = new JMenuItem("Scale...", KeyEvent.VK_C);
		scaleMenuItem.addActionListener(
				ActionListenerFactory.getScaleListener(this, 
						labels.getScaleLabel()));
		editMenu.add(scaleMenuItem);
	}

	public void createTrackSelectionArea(Container cont){
		JButton clearNote = buttons.getClearButton();
		JPanel subNorth = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JPanel subCenter = new JPanel(new GridBagLayout());
		JPanel subSouth = new JPanel(new FlowLayout());
		clearNote.addActionListener(this);
		subNorth.add(clearNote);

		GridBagConstraints c = new GridBagConstraints();
		
		Note currentNote = getCurrentNoteFromCollection();

		sliders = new Sliders(currentNote);

		labels.modifyNoteLabels(
				SequencerUtils.intPitchToString(sliders.getPitchValue()), 
				String.valueOf(sliders.getVolumeValue()), String.valueOf(sliders.getDurationValue()), 
				String.valueOf(sliders.getInstrumentValue()));

		restBox = new JCheckBox("Rest");
		restBox.setSelected(true);

		restBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(!SequencerUtils.ignoreStateChange){
					boolean selected = e.getStateChange() == ItemEvent.SELECTED;
					
					getCurrentNoteFromCollection().setIfRest(selected);
					
					if(selected){
						if(notes.hasNoteBeenModified(SequencerUtils.track, 
								SequencerUtils.beat))
							currentButton.setBackground(Color.GREEN);
						else
							currentButton.setBackground(Color.RED);
					} else {
						if(notes.hasNoteBeenModified(SequencerUtils.track, 
								SequencerUtils.beat))
							currentButton.setBackground(Color.GREEN);
						else
							currentButton.setBackground(null);
					}

					buttons.getConfirmButton().setContentAreaFilled(
							notes.isModified());
					buttons.getResetButton().setContentAreaFilled(
							notes.isModified());
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
		subCenter.add(sliders.getPitchSlider(), c);


		// Add pitch label to panel
		c.gridx++;
		subCenter.add(labels.getPitchLabel(), c);

		// Add volume slider to panel
		c.gridx = 0;
		c.gridy++;
		subCenter.add(new JLabel("Volume "), c);
		c.gridx++;
		subCenter.add(sliders.getVolumeSlider(), c);

		// Add volume label to panel
		c.gridx++;
		subCenter.add(labels.getVolumeLabel(), c);

		// Add duration slider to panel
		c.gridx = 0;
		c.gridy++;
		subCenter.add(new JLabel("Duration "), c);
		c.gridx++;
		subCenter.add(sliders.getDurationSlider(), c);

		// Add duration label to panel
		c.gridx++;
		subCenter.add(labels.getDurationLabel(), c);

		// Add instrument slider to panel
		c.gridx = 0;
		c.gridy++;
		subCenter.add(new JLabel("Instrument "), c);
		c.gridx++;
		subCenter.add(sliders.getInstrumentSlider(), c);

		// Add instrument label to panel
		c.gridx++;
		subCenter.add(labels.getInstrumentLabel(), c);

		// Add listener to sliders so that when they scroll, the value in the 
		// JLabel is updated.

		sliders.addListener(this);

		cont.add(subNorth, BorderLayout.NORTH);
		cont.add(subCenter, BorderLayout.CENTER);
		cont.add(subSouth, BorderLayout.SOUTH);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();

		if (src instanceof NoteButton) {
			NoteButton nextButton = (NoteButton) src;

			// Disable this note button, enable the last one selected
			// currentButton.setContentAreaFilled(true);
			// nextButton.setContentAreaFilled(false);
			
			currentButton.setBorder(SequencerUtils.BUTTON_DEFAULT_BORDER);
			nextButton.setBorder(SequencerUtils.BUTTON_SELECTED_BORDER);

			// Change background of previous button if
			// it was a rest
			if(notes.getNote(SequencerUtils.track, SequencerUtils.beat).isRest()){
				currentButton.setBackground(Color.RED);
			} else {
				if(notes.hasNoteBeenModified(SequencerUtils.track, 
						SequencerUtils.beat)){
					currentButton.setBackground(Color.GREEN);
				} else {
					currentButton.setBackground(null);
				}
			}

			SequencerUtils.track = nextButton.getTrack();
			SequencerUtils.beat = nextButton.getBeat();

			Note currentNote = getCurrentNoteFromCollection();

			sliders.setToNote(currentNote);
			
			labels.modifyNoteLabels(currentNote);

			SequencerUtils.ignoreStateChange = true;
			restBox.setSelected(currentNote.isRest());
			SequencerUtils.ignoreStateChange = false;

			currentButton = nextButton;
		} else if (src.equals(buttons.getClearButton())) {
			Note currentNote = getCurrentNoteFromCollection();

			currentNote = new Note(currentNote.getTrack(), currentNote.getBeat());
			sliders.setToNote(currentNote);
			labels.modifyNoteLabels(currentNote);
			currentButton.setBackground(Color.RED);
		} else if(src.equals(buttons.getResetButton())){
			if(notes.isModified()){
				buttons.getConfirmButton().setContentAreaFilled(false);
				buttons.getResetButton().setContentAreaFilled(false);

				Note original = notes.getOriginalNote(SequencerUtils.track, 
													  SequencerUtils.beat);

				sliders.setToNote(original);

				currentButton.setText(SequencerUtils.intPitchToString(original.getPitch()));
				labels.modifyNoteLabels(original);

				notes.reset();

				SequencerUtils.resetNoteBackgrounds(center.getComponents(), notes);
			} else {
				JOptionPane.showMessageDialog(this, "No notes have changed.");
			}
		} 
	}
	
	public void run(){
		setVisible(true);
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (sliders.isPitchAdjusting()){
			int p = sliders.getPitchValue();
			if(SequencerUtils.scale != null &&
					!SequencerUtils.scale.inScale(SequencerUtils.intToPitch(p))){
				// If not in the scale, the next pitch up will be, so
				// skip this pitch
				sliders.setPitchValue(++p);
			}
			labels.modifyPitchLabel(SequencerUtils.intPitchToString(p));
			getCurrentNoteFromCollection().setPitch(p);
		} else if (sliders.isVolumeAdjusting()){
			int v = sliders.getVolumeValue();
			labels.modifyVolumeLabel("" + v);
			getCurrentNoteFromCollection().setVolume(v);
		} else if (sliders.isDurationAdjusting()){
			int d = sliders.getDurationValue();
			labels.modifyDurationLabel("" + d);
			getCurrentNoteFromCollection().setDuration(d);
		} else if (sliders.isInstrumentAdjusting()){
			int i = sliders.getInstrumentValue();
			labels.modifyInstrumentLabel("" + i);
			for(Note n : notes.getRow(SequencerUtils.track))
				n.setInstrument(i);
			getCurrentNoteFromCollection().setInstrument(i);
		}

		buttons.getConfirmButton().setContentAreaFilled(notes.isModified());
		buttons.getResetButton().setContentAreaFilled(notes.isModified());
	}

	private Note getCurrentNoteFromCollection(){
		return notes.getNote(SequencerUtils.track, SequencerUtils.beat);
	}
}
