package capstone.gui.enums;

import capstone.gui.utils.SequencerUtils;

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
	/** If scale starts at C, the pitches in the scale **/
	private Pitch[] pitches;
	
	private ScaleType(String name, Pitch[] pitches){
		this.name = name;
		this.pitches = pitches;
	}
	
	@Override
	public String toString(){
		return name;
	}
	
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
