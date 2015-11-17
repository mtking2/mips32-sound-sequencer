package capstone.gui.containers;

import javax.swing.JButton;

public class Buttons {
	private JButton play, stop, confirm, reset, clear;
	
	public Buttons(){
		play = new JButton("Play");
		play.setEnabled(false);
		stop = new JButton("Stop");
		stop.setEnabled(false);
		confirm = new JButton("Commit Changes");
		reset = new JButton("Reset Changes");
		clear = new JButton("Clear Note");
	}
	
	public JButton getPlayButton(){
		return play;
	}
	
	public JButton getStopButton(){
		return stop;
	}
	
	public JButton getConfirmButton(){
		return confirm;
	}
	
	public JButton getResetButton(){
		return reset;
	}
	
	public JButton getClearButton(){
		return clear;
	}
}
