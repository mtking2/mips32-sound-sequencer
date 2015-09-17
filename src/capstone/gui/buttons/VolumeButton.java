package capstone.gui.buttons;

import javax.swing.JButton;

public class VolumeButton extends JButton {
	private int volume;
	
	public VolumeButton(String name, int volume){
		super(name);
		
		this.volume = volume;
	}
}
