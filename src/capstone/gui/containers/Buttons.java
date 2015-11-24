package capstone.gui.containers;

import javax.swing.JButton;

/**
 * Class grouping together the buttons present on the sequencer.
 * 
 * @author Brad Westley
 * @author Michael King
 * @version 12.11.15
 */
public class Buttons {
	/** The button that plays the sequence **/
	private JButton play;
	
	/** The button that stops playing the sequence **/
	private JButton stop;
	
	/** The button that confirms note changes**/
	private JButton confirm;
	
	/** The button that resets note changes **/
	private JButton reset;
	
	/** The button that clears note values for the currently selected note **/
	private JButton clear;
	
	/**
	 * Creates a new button container and initializes all buttons.
	 */
	public Buttons(){
		play = new JButton("Play");
		play.setEnabled(false);
		stop = new JButton("Stop");
		stop.setEnabled(false);
		confirm = new JButton("Commit Changes");
		reset = new JButton("Reset Changes");
		clear = new JButton("Clear Note");
	}
	
	/**
	 * @return the button that plays the sequence
	 */
	public JButton getPlayButton(){	return play;	}
	
	/**
	 * @return the button that stops playing the sequence
	 */
	public JButton getStopButton(){	return stop;	}
	
	/**
	 * @return the button that confirms note changes
	 */
	public JButton getConfirmButton(){	return confirm;	}
	
	/**
	 * @return the button that resets note changes
	 */
	public JButton getResetButton(){	return reset;	}
	
	/**
	 * @return the button that clears note values for the currently selected note
	 */
	public JButton getClearButton(){	return clear;	}
}
