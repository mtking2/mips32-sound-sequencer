package capstone.gui;

import capstone.gui.enums.Pitch;
import capstone.gui.enums.ScaleType;

/**
 * A class representation of a musical scale.
 * 
 * @author Brad Westley
 * @author Michael King
 * @version 12.11.15
 */
public class Scale {
	/** The root pitch **/
	private Pitch rootNote;
	/** The type of scale **/
	private ScaleType type;
	
	/**
	 * Creates a scale that starts at the specified root note, and
	 * is of the given type.
	 * 
	 * @param rootNote the note the scale starts  at
	 * @param type the type of scale
	 */
	public Scale(Pitch rootNote, ScaleType type){
		// Ignore octave of pitch
		this.rootNote = rootNote;
		this.type = type;
	}
	
	/**
	 * Convert this scale to a string.
	 * 
	 * @return this scale as a string
	 */
	@Override
	public String toString(){
		return rootNote + " " + type.toString();
	}
	
	/**
	 * Check if this pitch is contained in the scale.
	 * 
	 * @param p the pitch to check
	 * @return if the pitch is in this scale
	 */
	public boolean inScale(Pitch p){
		if(rootNote.equals(p)) return true;
		
		return type.includes(rootNote, p);
	}
}
