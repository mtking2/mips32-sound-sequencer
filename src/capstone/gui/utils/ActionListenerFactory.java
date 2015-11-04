package capstone.gui.utils;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSlider;

import capstone.gui.NoteCollection;
import capstone.gui.Scale;

public class ActionListenerFactory {
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
	
	public static ActionListener getExitListener(){
		return new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		};
	}
	
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
	
	public static ActionListener getPlayListener(Component[] components, 
			NoteCollection notes, Component parent){
		return new ActionListener(){
			public void actionPerformed(ActionEvent e){
				notes.commit();

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
						String playPath = "java -jar src/capstone/mips/Mars40_CGP2.jar src/capstone/mips/DrumBeatExample.asm";
						//System.out.println(playPath);
						Runtime.getRuntime().exec(playPath);
					} catch (IOException ex) {
						System.out.println(ex.getMessage());
					}
				}
			}
		};
	}
	
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
	
	public static ActionListener getConfirmListener(Component[] components, 
			NoteCollection notes, JButton confirm, JButton reset, Component parent){
		return new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				if(notes.isModified()){
					confirm.setContentAreaFilled(false);
					reset.setContentAreaFilled(false);
					notes.commit();
					SequencerUtils.resetNoteBackgrounds(components, notes);
				} else {
					JOptionPane.showMessageDialog(parent, "No notes have changed.");
				}
			}
		};
	}

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
						tempoLabel.setText("Tempo: " + tempo);
					} else {
						SequencerUtils.showTempoError(parent);
					}
				} catch (NumberFormatException ex){
					SequencerUtils.showTempoError(parent);
				}
			}
		};
	}
	
	public static ActionListener getScaleListener(Component parent, 
			JLabel scaleLabel){
		return new ActionListener(){
			public void actionPerformed(ActionEvent e){
				SequencerUtils.promptUserForNewScale(parent, scaleLabel);
			}
		};
	}
}