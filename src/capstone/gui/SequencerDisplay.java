package capstone.gui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import capstone.gui.containers.Buttons;
import capstone.gui.containers.Labels;
import capstone.gui.containers.NoteCollection;
import capstone.gui.containers.Sliders;
import capstone.gui.utils.InstrumentMenu;
import capstone.gui.utils.ListenerFactory;
import capstone.gui.utils.SequencerUtils;
import com.alee.laf.WebLookAndFeel;



/**
 * A graphical interface that represents a music sequencer.
 *
 * @author Brad Westley
 * @author Michael King
 * @version 12.10.15
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
	
	/** Sequencer main panel **/
	private JPanel panel;

	/** Panel on the sequencer. **/
	private JPanel north, west, center, centerSubCenter, centerSubWest;
	
	/** The top menu **/
	private JMenuBar menuBar;
	
	/** Top-level menu selection **/
	private JMenu fileMenu, editMenu, viewMenu;
	
	/** Menu item on the top menu **/
	private JMenuItem newMenuItem, exitMenuItem, saveMenuItem, loadMenuItem,
		tempoMenuItem, scaleMenuItem, timeSigMenuItem, flatMenuItem;
	
	/** The currently selected button **/
	private NoteButton currentButton;
	
	/** The check box designating if the current note is a rest **/
	private JCheckBox restBox;

	/** The menu of all instruments **/
	private InstrumentMenu instrumentMenu;

	/**
	 * Create a new display with the given title, width, height.
	 * 
	 * @param title the title of the display
	 * @param width how many pixels wide to make the display
	 * @param height how many pixels high to make the display
	 */
	public SequencerDisplay(String title, int width, int height){

        //this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                sequencerExit();
            }
        });

		this.setTitle(title);
		//this.setResizable(false);

		try {
            UIManager.setLookAndFeel (new WebLookAndFeel());
            //UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }



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
		north = new JPanel();
        JPanel northSubWest = new JPanel(new FlowLayout(FlowLayout.LEFT));
		north.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		north.setLayout(new BorderLayout());
		
		notes = new NoteCollection(tracks, beats);
        centerSubCenter = new JPanel(new GridLayout(tracks, beats));
        centerSubWest = new JPanel(new GridLayout(tracks, 1));

        menuInit();
        setupButtons(false);

		west.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		west.setPreferredSize(new Dimension(350, 200));

		northSubWest.add(buttons.getPlayButton());
		northSubWest.add(buttons.getStopButton());
		northSubWest.add(buttons.getConfirmButton());
		northSubWest.add(buttons.getResetButton());


        for(Component c : labels.getTopComponents())
            northSubWest.add(c);
        center.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

        north.add(buttons.getRandomizeButton(), BorderLayout.EAST);
        north.add(northSubWest, BorderLayout.WEST);
		panel.add(north, BorderLayout.NORTH);
		panel.add(center, BorderLayout.CENTER);
		panel.add(west, BorderLayout.WEST);

		this.setContentPane(panel);
		this.setJMenuBar(menuBar);
		this.setSize(width, height);
	}

    /**
     * Responsible for ending the playing process of the sequence is playing
     * and then terminating the program.
     */
    public void sequencerExit() {
        if (SequencerUtils.playProc != null)
            SequencerUtils.playProc.destroy();
        System.exit(0);
    }

	/**
	 * Initialize the menu at the top of the display.
	 */
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
		
		// View Menu, V - Mnemonic
		viewMenu = new JMenu("View");
		viewMenu.setMnemonic(KeyEvent.VK_V);
		menuBar.add(viewMenu);

		// File->New, N - Mnemonic
		newMenuItem = new JMenuItem("New", KeyEvent.VK_N);
        newMenuItem.addActionListener(ListenerFactory.getNewListener(this));
		fileMenu.add(newMenuItem);

		// File -> Save As, A - Mnemonic
		saveMenuItem = new JMenuItem("Save As", KeyEvent.VK_S);
		saveMenuItem.addActionListener(
				ListenerFactory.getSaveAsListener(center.getComponents(), this, notes));
		fileMenu.add(saveMenuItem);
		
		// File -> Load, L - Mnemonic
		loadMenuItem = new JMenuItem("Load", KeyEvent.VK_L);
		loadMenuItem.addActionListener(
				ListenerFactory.getLoadListener(this, notes));
		fileMenu.add(loadMenuItem);

		// File->Exit, X - Mnemonic
		exitMenuItem = new JMenuItem("Exit", KeyEvent.VK_X);
		exitMenuItem.addActionListener(ListenerFactory.getExitListener());
		fileMenu.add(exitMenuItem);

		// Edit -> Tempo..., T - Mnemonic
		tempoMenuItem = new JMenuItem("Tempo...", KeyEvent.VK_T);
		tempoMenuItem.addActionListener(
				ListenerFactory.getTempoListener(this,
						labels.getTempoLabel()));
		editMenu.add(tempoMenuItem);

		// Edit -> Scale..., C - Mnemonic
		scaleMenuItem = new JMenuItem("Scale...", KeyEvent.VK_C);
		scaleMenuItem.addActionListener(
				ListenerFactory.getScaleListener(this,
						labels.getScaleLabel()));
		editMenu.add(scaleMenuItem);
		
		// Edit -> Time Signature..., I - Mnemonic
		timeSigMenuItem = new JMenuItem("Time Signature...", KeyEvent.VK_I);
		timeSigMenuItem.addActionListener(
				ListenerFactory.getTimeSignatureSelectListener(
						this, labels, notes, currentButton, center));
		editMenu.add(timeSigMenuItem);
		
		// View -> Flats, L - Mnemonic
		flatMenuItem = new JCheckBoxMenuItem("Use Flats", false);
		flatMenuItem.setMnemonic(KeyEvent.VK_L);
		flatMenuItem.addActionListener(
				ListenerFactory.getFlatListener(
						labels, center.getComponents(), notes, currentButton));
		viewMenu.add(flatMenuItem);
	}
	
	public void resetLabels(){
		labels.modifyTempoLabel();
		labels.getScaleLabel().setText("Scale: None");
		labels.modifyTimeSignatureLabel();
	}


    public void buttonToggle(JButton button, boolean state)    {
        button.setEnabled(state);
    }

    public Buttons getButtons() {
        return buttons;
    }

	/**
	 * Create the area where the notes are displayed.
	 */
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
        subNorth.add(restBox,c);

		restBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(!SequencerUtils.ignoreStateChange){
					boolean selected = e.getStateChange() == ItemEvent.SELECTED;
					
					Note n = getCurrentNoteFromCollection();
					
					n.setIfRest(selected);
					
					if(selected){
						if(notes.hasNoteBeenModified(SequencerUtils.track, 
								SequencerUtils.beat)) {
                            //currentButton.setBackground(Color.GREEN);
                            currentButton.setBottomBgColor(Color.GREEN);
                            currentButton.setTopBgColor(Color.GREEN);
                        } else {
                            //currentButton.setBackground(Color.gray);
                            currentButton.setBottomBgColor(Color.lightGray);
                            currentButton.setTopBgColor(Color.lightGray);
                        }
					} else {
						if(notes.hasNoteBeenModified(SequencerUtils.track, 
								SequencerUtils.beat)){
							//currentButton.setBackground(Color.GREEN);
                            currentButton.setBottomBgColor(Color.GREEN);
                            currentButton.setTopBgColor(Color.GREEN);
							currentButton.setIcon(null);
							currentButton.setText(SequencerUtils.intPitchToString(n.getPitch()));
						} else {
							//currentButton.setBackground(null);
                            currentButton.setDefaultColor();
                            //currentButton.setBottomBgColor(Color.)
							currentButton.setIcon(null);
				            currentButton.setText(SequencerUtils.intPitchToString(n.getPitch()));
						}
					}

					buttons.getConfirmButton().setEnabled(
							notes.isModified());
					buttons.getResetButton().setEnabled(
							notes.isModified());
				}
			}
		});

		c.anchor = GridBagConstraints.CENTER;
		c.insets = new Insets(5,2,5,0);
		c.ipady = 10;
		c.fill = GridBagConstraints.HORIZONTAL;


		// Add pitch slider to panel
		c.gridx = 0;
		c.gridy=0;
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
        instrumentMenu.getTree().addTreeSelectionListener(ListenerFactory.getInstrumentListener(
                                                    instrumentMenu.getTree(), notes));
        JScrollPane instPane = new JScrollPane(instrumentMenu.getTree());
        subCenter.add(instPane,c);

		// Add listener to sliders so that when they scroll, the value in the
		// JLabel is updated.

		sliders.addListener(this);

		duration.addActionListener(ListenerFactory.getDurationListener(
				notes, duration,sliders.getDurationSlider()));

		west.add(subNorth, BorderLayout.NORTH);
		west.add(subCenter, BorderLayout.CENTER);
		west.add(subSouth, BorderLayout.SOUTH);
	}

	/**
	 * Fires when an action has been performed on a component that the 
	 * display is listening to.
	 * 
	 * @param e the event that was performed
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();

		if (src instanceof NoteButton) {
			NoteButton nextButton = (NoteButton) src;

            // Change background of previous button if
			// it was a rest
			if(notes.getNote(SequencerUtils.track, SequencerUtils.beat).isRest()){
				//currentButton.setBackground(Color.GRAY);
                currentButton.setBottomBgColor(Color.lightGray);
                currentButton.setTopBgColor(Color.lightGray);
			} else {
				if(notes.hasNoteBeenModified(SequencerUtils.track, 
						SequencerUtils.beat)){
					//currentButton.setBackground(Color.GREEN);
                    currentButton.setBottomBgColor(Color.green);
                    currentButton.setTopBgColor(Color.green);
				} else {
					//currentButton.setBackground(null);
                    currentButton.setDefaultColor();
				}
			}

            //nextButton.setBackground(Color.WHITE);
            nextButton.setDefaultColor();
            //nextButton.setTopBgColor(Color.WHITE);
            nextButton.setSelected(true);
            currentButton.setSelected(false);

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

			//currentButton.setBackground(Color.GRAY);
            currentButton.setBottomBgColor(Color.lightGray);
            currentButton.setTopBgColor(Color.lightGray);
            currentButton.setText(null);

            SequencerUtils.setRestIcon(currentButton);
            notes.setNote(currentNote.getTrack(), currentNote.getBeat(), currentNote);
            SequencerUtils.resetNoteBackgrounds(center.getComponents(), notes);
			if (notes.allRests())
                buttons.getPlayButton().setEnabled(false);
		} else if(src.equals(buttons.getResetButton())){
			if(notes.isModified()){

				Note original = notes.getOriginalNote(SequencerUtils.track,
													  SequencerUtils.beat);

				sliders.setToNote(original);

				currentButton.setText(SequencerUtils.intPitchToString(original.getPitch()));
				labels.modifyNoteLabels(original);

				notes.reset();

				SequencerUtils.resetNoteBackgrounds(center.getComponents(), notes);
				buttons.getConfirmButton().setEnabled(false);
				buttons.getResetButton().setEnabled(false);
			} else {
				JOptionPane.showMessageDialog(this, "No notes have changed.");
			}
		} 
	}
	
	/**
	 * Brings up the sequencer window.
	 */
	public void run(){
		setVisible(true);
	}

	/**
	 * Fired when a change occurs to one of the sliders in the sequencer.
	 * 
	 * @param e the event that occurred
	 */
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
			String pitchAsString = SequencerUtils.intPitchToString(p);
			labels.modifyPitchLabel(pitchAsString);
			getCurrentNoteFromCollection().setPitch(p);
			currentButton.setText(pitchAsString);
		} else if (sliders.isVolumeAdjusting()){
			int v = sliders.getVolumeValue();
			labels.modifyVolumeLabel("" + v);
			getCurrentNoteFromCollection().setVolume(v);
		} else if (sliders.isDurationAdjusting()){
			int d = sliders.getDurationValue();
            labels.modifyDurationLabel("" + d);
			getCurrentNoteFromCollection().setDuration(d);
		}


		buttons.getConfirmButton().setEnabled(notes.isModified());
		buttons.getResetButton().setEnabled(notes.isModified());
	}

	/**
	 * Get the currently selected note.
	 * 
	 * @return the currently selected note
	 */
	private Note getCurrentNoteFromCollection() {
		return notes.getNote(SequencerUtils.track, SequencerUtils.beat);
	}
	
	/**
	 * Set up the note buttons in the center panel.
	 * 
	 * @param loading if loading a file, false if starting up for the first time
	 */
	public void setupButtons(boolean loading){
		// Remove all components from center to make sure
		// no buttons are present already
		center.removeAll();
		
		int tracks = SequencerUtils.NUMBER_OF_TRACKS;
		
		// Multiply number of beats in time signature by two so there are 
		// two measures
		int beats = SequencerUtils.tSig.getBeats() * 2;

		if(!loading) notes.reset(tracks, beats);
		
		center.removeAll();
        center.setLayout(new BorderLayout());
        centerSubCenter = new JPanel(new GridLayout(tracks, beats));
        centerSubWest = new JPanel(new GridLayout(tracks, 1));


		for(int i = 0; i < tracks; i++){
			for(int j = 0; j < beats; j++){
				Note note;
				
				if(loading)
					note = notes.getNote(i, j);
				else
					note = new Note(i, j);
				
				NoteButton button = new NoteButton(i, j);
				button.addActionListener(this);
				
				if(note.isRest()) {
                    //button.setForeground(Color.GRAY);
                    button.setBottomBgColor(Color.lightGray);
                    button.setTopBgColor(Color.lightGray);
                } else {
                    button.setDefaultColor();
                    //button.setBackground(null);
                }
				
				if(note.isRest()) 
					SequencerUtils.setRestIcon(button);
				else
					// Set button text field to pitch
					button.setText(SequencerUtils.intPitchToString(note.getPitch()));

				if(i == 0 && j == 0){
					// Default note is first note
					currentButton = button;
					// Set first note as selected
                    //button.setBackground(Color.white);
					button.setBottomBgColor(Color.WHITE);
                    button.setTopBgColor(Color.WHITE);
					button.setSelected(true);
				}

				if(!loading) notes.setNote(i, j, note);

                centerSubCenter.add(button);
			}
            centerSubWest.add(new JLabel("Track "+(i+1)+"\n"));
		}

        center.add(centerSubCenter, BorderLayout.CENTER);
        center.add(centerSubWest, BorderLayout.WEST);

        notes.commit();

		JButton confirm = buttons.getConfirmButton();
		JButton reset = buttons.getResetButton();
		JButton play = buttons.getPlayButton();
        JButton stop = buttons.getStopButton();
		JButton randomize = buttons.getRandomizeButton();

		// Reset ActionListeners on buttons
		for(ActionListener l : confirm.getActionListeners())
			confirm.removeActionListener(l);
		
		for(ActionListener l : reset.getActionListeners())
			reset.removeActionListener(l);

        for(ActionListener l : play.getActionListeners())
            play.removeActionListener(l);

        for(ActionListener l : randomize.getActionListeners())
            randomize.removeActionListener(l);

        for(ActionListener l : flatMenuItem.getActionListeners())
            flatMenuItem.removeActionListener(l);

        for(ActionListener l : newMenuItem.getActionListeners())
            newMenuItem.removeActionListener(l);

		confirm.addActionListener(
				ListenerFactory.getConfirmListener(center.getComponents(),
						notes, this));
		
		reset.addActionListener(this);
        play.addActionListener(ListenerFactory.getPlayListener(
                center.getComponents(), notes, this));

        // no longer really needed since the action listener for stop button is now created dynamically in ListenerFactory.java
        //stop.addActionListener(ListenerFactory.getStopListener(this));
        newMenuItem.addActionListener(ListenerFactory.getNewListener(this));
        flatMenuItem.addActionListener(
                ListenerFactory.getFlatListener(
                        labels, centerSubCenter.getComponents(), notes, currentButton));
        randomize.addActionListener(ListenerFactory.getRandomizeListener(this, notes));

		confirm.setEnabled(false);
		reset.setEnabled(false);
		
		createTrackSelectionArea();


	}



}
