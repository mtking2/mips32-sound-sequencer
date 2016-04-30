package capstone.gui;

import com.alee.laf.button.WebButton;

import javax.swing.*;
import java.awt.*;


/**
 * A button that represents a musical note, and allows 
 * the note it contains to be modified when clicked.
 * 
 * @see JButton
 * @author Brad Westley
 * @author Michael King
 * @version 12.11.15
 */
public class NoteButton extends WebButton {
	/** Generated serialization UID **/
	private static final long serialVersionUID = 37648261342304622L;
	
	/** Which instrument track this note is on **/
	private int track;
	/** Which beat this note is on **/
	private int beat;
    /** The default color for this button **/
    private Color defaultTopColor;
    private Color defaultBottomColor;
	
	/**
	 * Creates a new button for modifying a note on a particular 
	 * track and beat.
	 * 
	 * @param track the track the note is on
	 * @param beat the beat the note is on
	 */
	public NoteButton(int track, int beat){
		super();
        this.defaultTopColor = this.getTopBgColor();
        this.defaultBottomColor = this.getBottomBgColor();
		this.track = track;
		this.beat = beat;
	}
	
	/**
	 * Get the track this note is on.
	 * 
	 * @return the track this note is on
	 */
	public int getTrack(){
		return track;
	}
	
	/**
	 * Get the beat this note is on.
	 * 
	 * @return the beat this note is on
	 */
	public int getBeat(){
		return beat;
	}

    public void setDefaultColor() {
        this.setTopBgColor(defaultTopColor);
        this.setBottomBgColor(defaultBottomColor);
    }
}
