package capstone.gui.enums;

import capstone.gui.utils.SequencerUtils;

/**
 * A representation of different musical scales.
 * 
 * @author Brad Westley
 * @author Michael King
 * @version 12.11.15
 */
public enum ScaleType {
	IONIAN("Major (Ionian)", SequencerUtils.IONIAN), 
	DORIAN("Dorian", SequencerUtils.DORIAN), 
	PHRYGIAN("Phrygian", SequencerUtils.PHRYGIAN), 
	LYDIAN("Lydian", SequencerUtils.LYDIAN), 
	MIXOLYDIAN("Mixolydian", SequencerUtils.MIXOLYDIAN), 
	AEOLIAN("Minor (Aeolian)", SequencerUtils.AEOLIAN), 
	LOCRIAN("Locrian", SequencerUtils.LOCRIAN);

	/** The name of the scale **/
	private String name;
	/** The pitches contained in the scale, such that the scale starts at C **/
	private Pitch[] pitches;
	
	/**
	 * Creates a scale type with the specified name and 
	 * contained pitches.
	 * 
	 * @param name the name of the scale
	 * @param pitches the pitches contained in the scale
	 */
	private ScaleType(String name, Pitch[] pitches){
		this.name = name;
		this.pitches = pitches;
	}
	
	/**
	 * Get the name of this scale type.
	 * 
	 * @return the name of this scale type
	 */
	@Override
	public String toString(){
		return name;
	}
	
	/**
	 * See if this scale includes the specified pitch, if the scale
	 * begins at the specified root note.
	 * 
	 * @param rootNote the root note of the scale
	 * @param p the pitch to check
	 * @return if the pitch is included in the scale at the specified root note
	 */
	public boolean includes(Pitch rootNote, Pitch p){
		// Since we stored scale data as in the key of C,
		// transpose the notes until they match the right key
		if(rootNote != Pitch.C){
			rootNote = rootNote.next();
			p = p.next();
			return includes(rootNote, p);
		}
		
		for(int i = 0; i < pitches.length; i++)
			if(pitches[i].equals(p)) return true;
		
		return false;
	}
}
