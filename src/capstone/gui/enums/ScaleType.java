package capstone.gui.enums;

import capstone.gui.utils.SequencerUtils;

/**
  * Represents the enumeration of several musical scales.
  *	
  *	@author Brad Westley
  *	@author Michael King
  *	@version 11.20.15
 **/
public enum ScaleType {
	IONIAN("Ionian", SequencerUtils.IONIAN), 
	DORIAN("Dorian", SequencerUtils.DORIAN), 
	PHRYGIAN("Phrygian", SequencerUtils.PHRYGIAN), 
	LYDIAN("Lydian", SequencerUtils.LYDIAN), 
	MIXOLYDIAN("Mixolydian", SequencerUtils.MIXOLYDIAN), 
	AEOLIAN("Aeolian", SequencerUtils.AEOLIAN), 
	LOCRIAN("Locrian", SequencerUtils.LOCRIAN);

	/** The name of the scale **/
	private String name;
	/** If root note is C, the pitches in the scale **/
	private Pitch[] pitches;
	
	/**
	  *	Creates a new scale type enumeration with the given name and 
	  * included pitches.
	  *
	  * @param name the name of the scale type
	  * @param pitches the pitches included in the scale with root note of C
	 **/
	private ScaleType(String name, Pitch[] pitches){
		this.name = name;
		this.pitches = pitches;
	}
	
	/**
	  *	Returns this scale type as a string.
	  *
	  * @return this scale as a string
	 **/
	@Override
	public String toString(){
		return name;
	}
	
	/**
	  * Finds if the given pitch is in this scale type, when the scale 
	  * begins at the given root note.
	  *
	  * @param rootNote the note that the scale starts at
	  * @param p the pitch to check is in scale
	  * @return if the given pitch is in the scale
	 **/
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
