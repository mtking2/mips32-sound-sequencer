package capstone.gui;

import javax.swing.JButton;

import capstone.gui.utils.SequencerUtils;

/**
 * A button that represents a musical note, and allows 
 * the note it contains to be modified when clicked.
 * 
 * @see JButton
 * @author Brad Westley
 * @author Michael King
 * @version 11.20.2015
 */
public class NoteButton extends JButton {
	/** Generated serialization UID **/
	private static final long serialVersionUID = 37648261342304622L;
	
	/** Which instrument track this note is on **/
	private int track;
	/** Which beat this note is on **/
	private int beat;
	
	/**
	 * Creates a new button for modifying a note on a particular 
	 * track and beat.
	 * 
	 * @param track the track the note is on
	 * @param beat the beat the note is on
	 */
	public NoteButton(int track, int beat){
		super();
		setBorder(SequencerUtils.BUTTON_DEFAULT_BORDER);
		
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
}
