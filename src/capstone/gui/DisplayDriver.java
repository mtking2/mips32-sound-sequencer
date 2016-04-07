package capstone.gui;

import java.io.IOException;

import javax.swing.JOptionPane;

import capstone.gui.utils.SequencerUtils;

/**
 * Creates the window containing the sequencer and runs it.
 * 
 * @author Brad Westley
 * @author Michael King
 * @version 12.11.15
 */
public class DisplayDriver {
	
	/**
	 * Main method, starts sequencer and runs it.
	 * 
	 * @param args unused
	 */
	public static void main(String[] args){
		SequencerDisplay display = new SequencerDisplay("MIPS Sound Sequencer", 1400, 450);		
		
		try {
			SequencerUtils.saveRandomizerFile();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(display, 
					"Error saving randomizer to file: " + e.getMessage());
		}
		
		display.run();
	}
}