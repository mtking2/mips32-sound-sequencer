package capstone.gui;

public class DisplayDriver {
	public static void main(String[] args){
		SequencerDisplay display = new SequencerDisplay("MIPS Sound Sequencer", 1280, 400);
		
		display.run();
	}
}
