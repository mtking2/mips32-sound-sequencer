package capstone.gui;

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
		SequencerDisplay display = new SequencerDisplay("MIPS Sound Sequencer", 1300, 450);

		display.run();
	}
}