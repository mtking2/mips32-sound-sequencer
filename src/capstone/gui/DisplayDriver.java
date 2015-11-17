package capstone.gui;

public class DisplayDriver {
	public static void main(String[] args){
		SequencerDisplay display = new SequencerDisplay("MIPS Sound Sequencer", 1480, 450);

		display.run();
	}
}
