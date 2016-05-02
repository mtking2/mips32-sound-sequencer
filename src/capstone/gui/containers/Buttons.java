package capstone.gui.containers;

import capstone.gui.utils.SequencerUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

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

    /** The button that generates a random sequence and populates the tracks */
	private JButton randomize;

	/**
	 * Creates a new button container and initializes all buttons.
	 */
	public Buttons(){
        ImageIcon playIcon = null, stopIcon = null;
        try {
            Image img = ImageIO.read(new File(SequencerUtils.getPathToResources() + "images/play_icon.png"));
            playIcon = new ImageIcon(img.getScaledInstance(25,25, java.awt.Image.SCALE_SMOOTH));

            img = ImageIO.read(new File(SequencerUtils.getPathToResources() + "images/stop_icon.png"));
            stopIcon = new ImageIcon(img.getScaledInstance(25,25, java.awt.Image.SCALE_SMOOTH));

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
		play = new JButton(playIcon);

		play.setEnabled(false);

		stop = new JButton(stopIcon);
		stop.setEnabled(false);

		confirm = new JButton("Commit Changes");
		reset = new JButton("Reset Changes");
		clear = new JButton("Clear Note");
        randomize = new JButton("I'm Feeling Lucky");
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

    /**
     * @return the button that generates a random sequence and populates the tracks
     */
    public JButton getRandomizeButton() { return randomize; }
}
