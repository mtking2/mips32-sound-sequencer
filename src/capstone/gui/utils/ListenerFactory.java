package capstone.gui.utils;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.EventListener;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import capstone.gui.Note;
import capstone.gui.NoteButton;
import capstone.gui.SequencerDisplay;
import capstone.gui.containers.Labels;
import capstone.gui.containers.NoteCollection;
import capstone.gui.enums.TimeSignature;


/**
  *	A factory object that creates objects that implement {@link EventListener} 
  * for the various components of the graphical user interface.
  *	
  * @see EventListener
  *	@author Brad Westley
  *	@author Michael King
  *	@version 11.20.15
  */
public class ListenerFactory {
	/**
	 * Creates the {@link ActionListener} that brings up a window to 
	 * select a new time signature.
	 * 
	 * @see ActionListener
	 * @param parent the sequencer display
	 * @param labels the container that stores the sequencer labels
	 * @param notes the notes that the user has entered
	 * @param currentButton the currently selected button
	 * @param center the panel containing the note button objects
	 * @return the created time signature selection action listener
	 */
	public static ActionListener getTimeSignatureSelectListener(
			SequencerDisplay parent, Labels labels, NoteCollection notes, 
			JButton currentButton, JPanel center){
		return new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// Check if notes are entered, prompt user to delete all
				
				if(!notes.allRests()){
					int input = JOptionPane.showConfirmDialog(parent, 
							"All notes will be reset if you select a new "
							+ "time signature.  Continue?", "Confirm Note Reset", 
							JOptionPane.YES_NO_OPTION);
					
					if(input != JOptionPane.YES_OPTION) return;
				}

				// Get the names of the time signatures
				String[] objects = new String[TimeSignature.values().length];

				for(int i = 0; i < objects.length; i++)
					objects[i] = TimeSignature.values()[i].getName();

				String input =  (String)
						JOptionPane.showInputDialog(parent, "Select a new time signature:", 
								"Time Signature Select", JOptionPane.QUESTION_MESSAGE, null, 
								objects, "4/4");

				// Get the time signature that was chosen
				for(TimeSignature tSig : TimeSignature.values())
					if(tSig.getName().equals(input)) SequencerUtils.tSig = tSig;

				labels.modifyTimeSignatureLabel();

				parent.setupButtons(false);
			}
		};
	}

	/**
	 * Creates an {@link ActionListener} that will change how notes are displayed, from sharps
	 * to flats and vice versa.
	 *
	 * @param labels the object containing the display's notes
	 * @param components the components contained in the center panel (all NoteButtons)
	 * @param notes the notes that the user has entered
	 * @param currentButton the currently selected button
	 * @see ActionListener
	 * @return the created ActionListener that changes between flats and sharps
	 */
	public static ActionListener getFlatListener(Labels labels,
			Component[] components, NoteCollection notes,
			NoteButton currentButton){
		return new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				boolean selected =
						((JCheckBoxMenuItem) e.getSource()).getState();

				SequencerUtils.flats = selected;

				NoteButton[] buttons = new NoteButton[components.length];

				for(int i = 0; i < components.length; i++)
					buttons[i] = (NoteButton) components[i];

				for(NoteButton button : buttons){
					Note note = notes.getNote(
							button.getTrack(), button.getBeat());

					if(!note.isRest())
						button.setText(
								SequencerUtils.intPitchToString(note.getPitch()));
				}

                if (SequencerUtils.scale == null)
                    labels.getScaleLabel().setText("Scale: None");
                else
				    labels.getScaleLabel().setText("Scale: "
						    + SequencerUtils.scale);

				Note current = notes.getNote(
						currentButton.getTrack(), currentButton.getBeat());

				labels.modifyPitchLabel(SequencerUtils.intPitchToString(current.getPitch()));
			}
		};
	}

    /**
     *	Creates the {@link ActionListener} that creates a new, empty sequence.
     *
     * @see ActionListener
     * @param display the parent container
     * @return the created exit ActionListener
     */
    public static ActionListener getNewListener (SequencerDisplay display) {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int option = JOptionPane.showConfirmDialog(display, "Are you sure you would like to reset the sequence?");
                if(option == JOptionPane.YES_OPTION){
                    display.setupButtons(false);
                    display.update(display.getGraphics());
                    display.repaint();
                    display.setVisible(true);
                    display.buttonToggle(display.getButtons().getPlayButton(), false);
                }

            }
        };
    }

	/**
	 *	Creates the {@link ActionListener} that exits the program.
	 *
	 * @see ActionListener
	 * @return the created exit ActionListener
	 */
	public static ActionListener getExitListener(){
		return new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Files.delete(Paths.get(
							SequencerUtils.getPathToMIPS() 
							+ "randomizer.asm"));
				} catch (IOException ex) {
					System.out.println("Problem deleting randomizer file: "
							+ ex.getMessage());
				}
				if (SequencerUtils.playProc != null)
					SequencerUtils.playProc.destroy();
				System.exit(0);
			}
		};
	}

	/**
	 *	Creates the {@link ActionListener} that saves track data to a file.
	 *
	 * @see ActionListener
	 * @param parent the parent container
	 * @param components the components contained in the center panel (all NoteButtons)
	 * @param notes the notes that the user has entered
	 * @return the created save ActionListener
	 */
	public static ActionListener getSaveAsListener(Component[] components,
			Component parent, NoteCollection notes){
		return new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				notes.commit();
				SequencerUtils.resetNoteBackgrounds(components, notes);
				

				
				try {
                    String file = "data.mss";
					if(SequencerUtils.currentFileName != null){
						file = SequencerUtils.currentFileName;
					}
					SequencerUtils.toFile(parent, notes, file);

				} catch (IOException ioe) {
					JOptionPane.showMessageDialog(parent, ioe.getMessage());
				}
			}
		};
	}

    /**
     * Creates the {@link ActionListener} that loads a previously save .mss data file.
     * @param parent the parent container
     * @param notes the notes that the user has entered
     * @return the created load ActionListener
     */
	public static ActionListener getLoadListener(SequencerDisplay parent, 
			NoteCollection notes){
		return new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {

				try {
					SequencerUtils.loadFile(parent, notes, null);
                    parent.buttonToggle(parent.getButtons().getPlayButton(), true);
				} catch (IOException ioe) {
					JOptionPane.showMessageDialog(parent, ioe.getMessage());
				}
			}
			
		};
	}

	/**
	 *	Creates the {@link ActionListener} that plays a sequence.
	 *
	 * @see ActionListener
	 * @param components the components contained in the center panel (all NoteButtons)
	 * @param notes the notes that the user has entered
	 * @param parent the parent container
	 * @return the created play ActionListener
	 */
	public static ActionListener getPlayListener(Component[] components,
			NoteCollection notes, SequencerDisplay parent){
		return new ActionListener(){
			public void actionPerformed(ActionEvent e){
				notes.commit();
                parent.buttonToggle(parent.getButtons().getStopButton(), true);
                parent.buttonToggle(parent.getButtons().getPlayButton(), false);
                parent.buttonToggle(parent.getButtons().getRandomizeButton(), false);

				SequencerUtils.resetNoteBackgrounds(components, notes);

				if(notes.allRests()){
					JOptionPane.showMessageDialog(parent, "All notes are rests; "
							+ "there is nothing to play.");
				} else if(SequencerUtils.playing){
					JOptionPane.showMessageDialog(parent, "The sequence is already playing.");
				} else {
					SequencerUtils.playing = true;
					
					try {
						String file;
						
						if(SequencerUtils.currentFileName == null){
							file = "temp.mss";
						} else {
							file = SequencerUtils.currentFileName;
						}

						SequencerUtils.saveDataFile(notes, file);
                        SequencerUtils.saveMipsFile(file);
					} catch (IOException ex) {
						JOptionPane.showMessageDialog(parent, ex.getMessage());
					}
					
					try {
                        // old attempt to have playing process inherit STDOUT and STDERR to be used for debugging
                        /*
                        ProcessBuilder pb = new ProcessBuilder("java -jar \""+SequencerUtils.getPathToResources()+
                                                    "Mars40_CGP2.jar\" \""+SequencerUtils.getPathToMIPS()+"mips.asm\"");
                        //pb.directory(new File(SequencerUtils.getPathToResources()));
                        pb.inheritIO();
                        Process playProc = pb.start();
                        playProc.waitFor();
                        */


						String playPath =   "java -jar "+SequencerUtils.getPathToResources()+
                                "lib/Mars40_CGP2.jar " +SequencerUtils.getPathToMIPS()+"mips.asm";

						//System.out.println(playPath); // debugging print
						SequencerUtils.playProc = Runtime.getRuntime().exec(playPath);


						ActionListener doStop = new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
                                SequencerUtils.playProc.destroy();
                                parent.buttonToggle(parent.getButtons().getStopButton(), false);
                                parent.buttonToggle(parent.getButtons().getPlayButton(), true);
                                parent.buttonToggle(parent.getButtons().getRandomizeButton(), true);
								if(SequencerUtils.currentFileName == null){
									try {
										Files.deleteIfExists(
											Paths.get(SequencerUtils.getPathToDataStorage() + "temp.mss"));
									} catch (IOException ioe){
										JOptionPane.showMessageDialog(parent, 
												ioe.getMessage());
									}
								}
                                SequencerUtils.playing = false;
							}
						};
						parent.getButtons().getStopButton().addActionListener(doStop);
					} catch (IOException ex) {
						System.out.println(ex.getMessage());
					}
				}
			}
		};
	}

	/**
	 *	Creates the {@link ActionListener} that stops a playing sequence.
	 *
	 * @see ActionListener
	 * @param parent the parent container
	 * @return the created stop ActionListener
	 */
	public static ActionListener getStopListener(Component parent){
		return new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				if(!SequencerUtils.playing)
					JOptionPane.showMessageDialog(parent, "The sequence is already stopped.");
				else
					SequencerUtils.playing = false;
			}
		};
	}

	/**
	 * Creates the {@link ActionListener} that commits the changes to notes.
	 *
	 * @see ActionListener
	 * @param components the components contained in the center panel (all NoteButtons)
	 * @param notes the notes that the user has entered
	 * @param parent the parent container
	 * @return the created commit ActionListener
	 */
	public static ActionListener getConfirmListener(Component[] components,
			NoteCollection notes, SequencerDisplay parent){
		return new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				if(notes.isModified()){
                    parent.buttonToggle(parent.getButtons().getConfirmButton(), false);
                    parent.buttonToggle(parent.getButtons().getResetButton(), false);
                    parent.buttonToggle(parent.getButtons().getPlayButton(), true);

					notes.commit();
					SequencerUtils.resetNoteBackgrounds(components,notes);
				} else {
					JOptionPane.showMessageDialog(parent, "No notes have changed.");
				}
				SequencerUtils.resetNoteBackgrounds(components, notes);
                parent.update(parent.getGraphics());
			}
		};
	}

	/**
	 * Creates an {@link ActionListener} that sets the duration text field.
	 * 
	 * @see ActionListener
	 * @param notes the notes that the user has entered
	 * @param self the duration text field
	 * @param duration the duration slider
	 * @return the created duration listener
	 */
	public static ActionListener getDurationListener(NoteCollection notes, JTextField self, JSlider duration) {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int d = Integer.parseInt(self.getText());
				if (d>=0) {
					notes.getNote(SequencerUtils.track, SequencerUtils.beat).setDuration(d);
					duration.setValue(d);
				}
			}
		};
	}

	/**
	 * Creates the {@link TreeSelectionListener} that sets a track's instrument.
	 * 
	 * @see TreeSelectionListener
	 * @param tree the tree that lists all instruments
	 * @param notes the notes that the user has entered
	 * @return the created instrument selection listener
	 */
	public static TreeSelectionListener getInstrumentListener(JTree tree, NoteCollection notes) {
		return new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)
						tree.getLastSelectedPathComponent();


				if (node != null) {
					Object selection = node.getUserObject();
					//System.out.println(selection);
					Object value = SequencerUtils.instrumentMap.getReverseMap().get(selection);
					if (value!=null) {
						int i = (int) value;
						//System.out.println(i);
						SequencerUtils.selectedInstrument = i;
						for(Note n : notes.getRow(SequencerUtils.track)) {
							n.setInstrument(i);
							n.setTreePath(new TreePath(node.getPath()));
						}
						notes.getNote(SequencerUtils.track, SequencerUtils.beat).setInstrument(i);
					}
				}
			}
		};
	}

	/**
	 *	Creates the action listener that brings up the window to select a new tempo.
	 *
	 * @see ActionListener
	 * @param parent the parent component this listener's object is contained in
	 * @param tempoLabel the label that displays the tempo
	 * @return the created tempo selection ActionListener
	 */
	public static ActionListener getTempoListener(Component parent, JLabel tempoLabel){
		return new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				String answer = JOptionPane.showInputDialog(parent,
						"Enter a new tempo:",
						SequencerUtils.tempo);

				try {
					int tempo = Integer.parseInt(answer);

					if(tempo > 0 && tempo <= 300){
						SequencerUtils.tempo = tempo;
						tempoLabel.setText("Tempo: " + tempo + " bpm");
					} else {
						SequencerUtils.showTempoError(parent);
					}
				} catch (NumberFormatException ex){
					SequencerUtils.showTempoError(parent);
				}
			}
		};
	}
	
	/**
	  *	Creates the action listener that brings up a window to select a new scale.
	  *
	  * @see ActionListener
	  * @param parent the parent component this listener's object is contained in
	  * @param scaleLabel the label that displays the current scale
	  * @return the created scale selection ActionListener
	  */
	public static ActionListener getScaleListener(Component parent, 
			JLabel scaleLabel){
		return new ActionListener(){
			public void actionPerformed(ActionEvent e){
				SequencerUtils.promptUserForNewScale(parent, scaleLabel);
			}
		};
	}


    /**
     * Creates the action listener that makes a call to our randomizer MIPS program
     * through Mars++ and then makes calls responsible for loading the data of the generated
     * sequence into the GUI o display.
     * @param parent the parent container
     * @param notes the notes that the user has entered
     * @return the created randomizer ActionListener
     */
	public static ActionListener getRandomizeListener(SequencerDisplay parent, NoteCollection notes) {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    // old attempt to have randomizer process inherit STDOUT and STDERR to be used for debugging
                    /*
                    String mips = SequencerUtils.getPathToMIPS();
                    String randomizePath = "java -jar "+mips+"Mars40_CGP2.jar "+mips+"randomizer.asm";
                    ProcessBuilder pb = new ProcessBuilder(randomizePath);
                    pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
                    pb.redirectError(ProcessBuilder.Redirect.INHERIT);
                    Process p = pb.start();
                    */

                    String randomizePath =  "java -jar "+SequencerUtils.getPathToResources()+
                                            "lib/Mars40_CGP2.jar "+SequencerUtils.getPathToMIPS()+"randomizer.asm";

                    //System.out.println(randomizePath); // debugging print

                    Process randProc = Runtime.getRuntime().exec(randomizePath);
                    randProc.waitFor();
                    randProc.destroyForcibly();
                    try {
                        SequencerUtils.loadFile(parent, notes, SequencerUtils.getPathToDataStorage()+"generated.mss");
                    } catch (IndexOutOfBoundsException ex) {
                        JOptionPane.showMessageDialog(parent, "There was a glitch in the matrix when randomizing a sequence!\n\n" +
                                "Mars/Mars++ is not a perfect MIPS simulator unfortunately :(\n\n" +
                                "Try waiting on the process to finish or generating another one :)");
                        parent.buttonToggle(parent.getButtons().getPlayButton(), false);
                    }

                } catch (IOException | InterruptedException ex) {
                    System.out.println(ex.getMessage());
                }

                parent.buttonToggle(parent.getButtons().getRandomizeButton(), true);
                parent.buttonToggle(parent.getButtons().getPlayButton(), true);
            }
        };
    }
}
