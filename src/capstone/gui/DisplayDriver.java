package capstone.gui;

public class DisplayDriver {
	
	public static void main(String[] args){
		String title = "MIPS Sequencer";
		
		int width = 800;
		int height = 600;
		
		SequencerDisplay display = new SequencerDisplay(title, width, height);
	}

}
