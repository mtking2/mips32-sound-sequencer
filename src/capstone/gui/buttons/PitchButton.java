package capstone.gui.buttons;

import javax.swing.JButton;

public class PitchButton extends JButton {
	private int pitch;
	
	public PitchButton(int pitch, String name){
		super(name);
		
		this.pitch = pitch;
	}
}
