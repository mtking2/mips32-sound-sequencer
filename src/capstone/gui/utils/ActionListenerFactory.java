package capstone.gui.utils;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSlider;

import capstone.gui.Scale;
import capstone.gui.containers.NoteCollection;

/**
  *	A factory object that creates {@link java.awt.event.ActionListener} objects for the various components of
  * the graphical user interface.
  *	
  * @see java.awt.event.ActionListener
  *	@author Brad Westley
  *	@author Michael King
  *	@version 11.20.15
  */
public class ActionListenerFactory {
	/**
	  *	Creates the action listener that sets the tempo after tempo selection.
	  *
	  * @see java.awt.event.ActionListener
	  * @param parent the parent component that this listener's object is contained in
	  * @return the created tempo setting ActionListener
	  */
	public static ActionListener getTempoSelectListener(Component parent){
		return new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				try{
					SequencerUtils.tempo = Integer.parseInt(e.getActionCommand());
				} catch(NumberFormatException ex){
					SequencerUtils.showTempoError(parent);
				}
			}
		};
	}
	
	/**
	  *	Creates the action listener that exits the program.
	  *
	  * @see java.awt.event.ActionListener
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
	  * @see java.awt.event.ActionListener
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
	  * @see java.awt.event.ActionListener
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
	  * @see java.awt.event.ActionListener
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
	  * @see java.awt.event.ActionListener
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
					confirm.setContentAreaFilled(false);
					reset.setContentAreaFilled(false);
                    play.setEnabled(true);
					notes.commit();
					SequencerUtils.resetNoteBackgrounds(components, notes);
				} else {
					JOptionPane.showMessageDialog(parent, "No notes have changed.");
				}
			}
		};
	}

	/**
	  *	Creates the action listener that brings up the window to select a new tempo.
	  *
	  * @see java.awt.event.ActionListener
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
	  * @see java.awt.event.ActionListener
	  * @param parent the parent component this listener's object is contained in
	  * @param tempoLabel the label that displays the current scale
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
