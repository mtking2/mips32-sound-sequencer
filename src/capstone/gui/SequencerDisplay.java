package capstone.gui;

import java.awt.BorderLayout;
import java.awt.Color;
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
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import capstone.gui.containers.Buttons;
import capstone.gui.containers.Labels;
import capstone.gui.containers.NoteCollection;
import capstone.gui.containers.Sliders;
import capstone.gui.utils.ActionListenerFactory;
import capstone.gui.utils.InstrumentMenu;
import capstone.gui.utils.SequencerUtils;

/**
 * A graphical interface that represents a music sequencer.
 *
 * @author Brad Westley
 * @author Michael King
 * @version 11.20.2015
 */
public class SequencerDisplay extends JFrame implements ActionListener, ChangeListener {
	/** Generated serialization UID **/
	private static final long serialVersionUID = -8195923281306103591L;
	
	/** Collection of the notes stored in the sequencer **/
	private NoteCollection notes;
	/** Collection of the labels used to display information to the user **/
	private Labels labels;
	/** Collection of the sliders used to modify note values **/
	private Sliders sliders;
	/** Collection of the buttons contained in the sequencer **/
	private Buttons buttons;

	private JPanel panel, north, west, center;
	private JMenuBar menuBar;
	private JMenu fileMenu, editMenu;
	private JMenuItem newMenuItem, exitMenuItem, saveMenuItem, tempoMenuItem, 
		scaleMenuItem, timeSigMenuItem;

	private NoteButton currentButton;
	private JCheckBox restBox;

	//private JFormattedTextField durationLabel;
	private InstrumentMenu instrumentMenu;

	public SequencerDisplay(String title, int width, int height){
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle(title);
		this.setResizable(false);
		
		labels = new Labels();
		
		buttons = new Buttons();
		
		int tracks = SequencerUtils.NUMBER_OF_TRACKS;
		
		// Multiply number of beats in time signature by two so there are 
		// two measures
		int beats = SequencerUtils.tSig.getBeats() * 2;
		
		center = new JPanel(new GridLayout(tracks, beats));
		west = new JPanel(new BorderLayout());

		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		north = new JPanel(new FlowLayout(FlowLayout.LEFT));
		north.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		
		notes = new NoteCollection(tracks, beats);
		
		setupButtons();
		
		west.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		west.setPreferredSize(new Dimension(350, 200));

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

		// Edit -> Scale..., S - Mnemonic
		scaleMenuItem = new JMenuItem("Scale...", KeyEvent.VK_S);
		scaleMenuItem.addActionListener(
				ActionListenerFactory.getScaleListener(this,
						labels.getScaleLabel()));
		editMenu.add(scaleMenuItem);
		
		// Edit -> Time Signature..., I - Mnemonic
		timeSigMenuItem = new JMenuItem("Time Signature...", KeyEvent.VK_I);
		timeSigMenuItem.addActionListener(
				ActionListenerFactory.getTimeSignatureSelectListener(
						this, labels, notes, currentButton, center));
		editMenu.add(timeSigMenuItem);
	}

	public void createTrackSelectionArea(){
		JButton clearNote = buttons.getClearButton();
		JPanel subNorth = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JPanel subCenter = new JPanel(new GridBagLayout());
		JPanel subSouth = new JPanel(new FlowLayout());
		clearNote.addActionListener(this);
		subNorth.add(clearNote);
		
		west.removeAll();
        JFormattedTextField duration = labels.getDurationLabel();
		GridBagConstraints c = new GridBagConstraints();
		
		Note currentNote = getCurrentNoteFromCollection();

		sliders = new Sliders(currentNote);
		
		labels.modifyNoteLabels(
				SequencerUtils.intPitchToString(sliders.getPitchValue()),
				String.valueOf(sliders.getVolumeValue()), String.valueOf(sliders.getDurationValue()));


		labels.getPitchLabel().setText(SequencerUtils.intPitchToString(sliders.getPitchValue()));
		labels.getVolumeLabel().setText("" + sliders.getVolumeValue());
		duration.setText("" + sliders.getDurationValue());

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
							currentButton.setBackground(Color.GRAY);
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

		c.gridx = 1;
		c.gridy = 0;
		subCenter.add(restBox,c);

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
        c.weightx = 0.8;
		subCenter.add(sliders.getDurationSlider(), c);

		// Add duration label to panel
		c.gridx++;
        c.weightx = 0.2;
		subCenter.add(duration, c);

		c.gridx = 0;
		c.gridy++;
        subCenter.add(new JLabel("Instrument "), c);
        c.gridx++;
		c.weightx = 0.5;
        c.gridheight = 3;
        c.gridwidth = 2;
        c.ipady = 130;
        instrumentMenu = new InstrumentMenu();
        instrumentMenu.getTree().addTreeSelectionListener(ActionListenerFactory.getInstrumentListener(
                                                    instrumentMenu.getTree(), notes));
        JScrollPane instPane = new JScrollPane(instrumentMenu.getTree());
        subCenter.add(instPane,c);

		// Add listener to sliders so that when they scroll, the value in the
		// JLabel is updated.

		sliders.addListener(this);

		duration.addActionListener(ActionListenerFactory.getDurationListener(
				notes, duration,sliders.getDurationSlider()));

		west.add(subNorth, BorderLayout.NORTH);
		west.add(subCenter, BorderLayout.CENTER);
		west.add(subSouth, BorderLayout.SOUTH);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();

		if (src instanceof NoteButton) {
			NoteButton nextButton = (NoteButton) src;

			// Change background of previous button if
			// it was a rest
			if(notes.getNote(SequencerUtils.track, SequencerUtils.beat).isRest()){
				currentButton.setBackground(Color.GRAY);
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


            if (currentNote.getTreePath()!=null) {
                instrumentMenu.getTree().makeVisible(currentNote.getTreePath());
                instrumentMenu.getTree().scrollPathToVisible(currentNote.getTreePath());
				instrumentMenu.getTree().setSelectionPath(currentNote.getTreePath());
            } else {
                instrumentMenu.collapseTree();
            }

			sliders.setToNote(currentNote);
			
			labels.modifyNoteLabels(currentNote);

			SequencerUtils.ignoreStateChange = true;
			restBox.setSelected(currentNote.isRest());
			SequencerUtils.ignoreStateChange = false;

			currentButton = nextButton;
		} else if (src.equals(buttons.getClearButton())) {
			Note currentNote = getCurrentNoteFromCollection();
            instrumentMenu.collapseTree();
            currentNote.setTreePath(null);
			currentNote = new Note(currentNote.getTrack(), currentNote.getBeat());
			sliders.setToNote(currentNote);
			labels.modifyNoteLabels(currentNote);
			currentButton.setBackground(Color.GRAY);
            notes.setNote(currentNote.getTrack(), currentNote.getBeat(), currentNote);
            SequencerUtils.resetNoteBackgrounds(center.getComponents(),notes);
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
			//labels.modifyDurationLabel("" + d);
            labels.modifyDurationLabel("" + d);
			getCurrentNoteFromCollection().setDuration(d);
		}

		buttons.getConfirmButton().setContentAreaFilled(notes.isModified());
		buttons.getResetButton().setContentAreaFilled(notes.isModified());
	}

	private Note getCurrentNoteFromCollection(){
		return notes.getNote(SequencerUtils.track, SequencerUtils.beat);
	}
	
	public void setupButtons(){
		int tracks = SequencerUtils.NUMBER_OF_TRACKS;
		
		// Multiply number of beats in time signature by two so there are 
		// two measures
		int beats = SequencerUtils.tSig.getBeats() * 2;

		notes.reset(tracks, beats);
		
		center.removeAll();
		center.setLayout(new GridLayout(tracks, beats));

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
				}

				notes.setNote(i, j, note);

				center.add(button);
			}
		}
		
		notes.commit();

		JButton confirm = buttons.getConfirmButton();
		JButton reset = buttons.getResetButton();
		JButton play = buttons.getPlayButton();

		// Reset ActionListeners on buttons
		for(ActionListener l : confirm.getActionListeners())
			confirm.removeActionListener(l);
		
		for(ActionListener l : confirm.getActionListeners())
			reset.removeActionListener(l);
		
		confirm.addActionListener(
				ActionListenerFactory.getConfirmListener(center.getComponents(), 
						notes, confirm, reset, play, this));
		
		reset.addActionListener(this);
		
		confirm.setContentAreaFilled(false);
		reset.setContentAreaFilled(false);
		
		createTrackSelectionArea();
	}
}
