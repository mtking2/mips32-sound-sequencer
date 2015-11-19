package capstone.gui.utils;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import capstone.gui.Note;
import capstone.gui.NoteButton;
import capstone.gui.SequencerDisplay;
import capstone.gui.TimeSignature;
import capstone.gui.containers.Labels;
import capstone.gui.containers.NoteCollection;

/**
  *	A factory object that creates {@link ActionListener} objects for the various components of
  * the graphical user interface.
  *	
  * @see ActionListener
  *	@author Brad Westley
  *	@author Michael King
  *	@version 11.20.15
  */
public class ActionListenerFactory {
	/**
	 * Creates the action listener that brings up a window to 
	 * select a new time signature.
	 * 
	 * @see ActionListener
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

				parent.setupButtons();
			}
		};
	}

	/**
	 * Creates an ActionListener that will change how notes are displayed, from sharps
	 * to flats and vice versa.
	 *
	 * @param labels the object containing the display's notes
	 * @param components the components contained in the center panel (all NoteButtons)
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

				labels.modifyPitchLabel(SequencerUtils.intPitchToString(
						current.getPitch()).toString());
			}
		};
	}

	/**
	 *	Creates the action listener that exits the program.
	 *
	 * @see ActionListener
	 * @return the created exit ActionListener
	 */
	public static ActionListener getExitListener(){
		return new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		};
	}

	/**
	 *	Creates the action listener that saves track data to a file.
	 *
	 * @see ActionListener
	 * @param components the components displaying the track data
	 * @param parent the parent component this listener's object is contained in
	 * @param notes the collection of data for each track
	 * @return the created save ActionListener
	 */
	public static ActionListener getSaveListener(Component[] components,
			Component parent, NoteCollection notes){
		return new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				notes.commit();
				SequencerUtils.resetNoteBackgrounds(components, notes);
				SequencerUtils.toFile(parent, notes);
			}
		};
	}

	/**
	 *	Creates the action listener that plays a sequence.
	 *
	 * @see ActionListener
	 * @param components the components displaying the track data
	 * @param notes the collection of data for each track
	 * @param self the 'play' button itself
	 * @param stop the 'stop' button
	 * @param parent the parent component this listener's object is contained in
	 * @return the created play ActionListener
	 */
	public static ActionListener getPlayListener(Component[] components,
			NoteCollection notes, JButton self, JButton stop,Component parent){
		return new ActionListener(){
			public void actionPerformed(ActionEvent e){
				notes.commit();
				stop.setEnabled(true);
				self.setEnabled(false);
				SequencerUtils.resetNoteBackgrounds(components, notes);

				if(notes.allRests()){
					JOptionPane.showMessageDialog(parent, "All notes are rests; "
							+ "there is nothing to play.");
				} else if(SequencerUtils.playing){
					JOptionPane.showMessageDialog(parent, "The sequence is already playing.");
				} else {
					SequencerUtils.playing = true;
					SequencerUtils.toFile(parent, notes);
					try {
						String playPath = "java -jar src/capstone/mips/Mars40_CGP2.jar src/capstone/mips/mips.asm";
						//System.out.println(playPath);
						Process playProc = Runtime.getRuntime().exec(playPath);

						ActionListener doStop = new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								playProc.destroy();
								stop.setEnabled(false);
								self.setEnabled(true);
							}
						};
						stop.addActionListener(doStop);
					} catch (IOException ex) {
						System.out.println(ex.getMessage());
					}
				}
			}
		};
	}

	/**
	 *	Creates the action listener that stops a playing sequence.
	 *
	 * @see ActionListener
	 * @param parent the parent component this listener's object is contained in
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
	 *	Creates the action listener that commits the changes to notes.
	 *
	 * @see ActionListener
	 * @param components the components displaying the track data
	 * @param notes the collection of data for each track
	 * @param confirm the 'confirm changes' button
	 * @param reset the 'reset' button
	 * @param play the 'play' button
	 * @param parent the parent component this listener's object is contained in
	 * @return the created commit ActionListener
	 */
	public static ActionListener getConfirmListener(Component[] components, 
			NoteCollection notes, JButton confirm, JButton reset, JButton play, Component parent){
		return new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				if(notes.isModified()){
					confirm.setEnabled(false);
					reset.setEnabled(false);
					play.setEnabled(true);
					notes.commit();
					SequencerUtils.resetNoteBackgrounds(components, notes);
				} else {
					JOptionPane.showMessageDialog(parent, "No notes have changed.");
				}
			}
		};
	}


	public static ActionListener getDurationListener(NoteCollection notes, JTextField self, JSlider duration) {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int d = Integer.parseInt(self.getText());
				if (d>=0) {
					notes.getNote(SequencerUtils.track, SequencerUtils.beat).setDuration(d);
					duration.setValue(d);
				} else {

				}
			}
		};
	}

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
}
