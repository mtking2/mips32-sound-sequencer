package capstone.gui.buttons;

import javax.swing.JButton;

public class DurationButton extends JButton {
	private int duration;
	
	public DurationButton(String name, int duration){
		super(name);
		
		this.duration = duration;
	}
}