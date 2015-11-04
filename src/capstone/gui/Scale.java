package capstone.gui;

import capstone.gui.enums.Pitch;
import capstone.gui.enums.ScaleType;

public class Scale {
	/** The root pitch **/
	private Pitch rootNote;
	/** What type of scale this is **/
	private ScaleType type;
	
	public Scale(Pitch rootNote, ScaleType type){
		// Ignore octave of pitch
		this.rootNote = rootNote;
		this.type = type;
	}
	
	public String toString(){
		return rootNote + " " + type.toString();
	}
	
	public boolean inScale(Pitch p){
		if(rootNote.equals(p)) return true;
		
		return type.includes(rootNote, p);
	}
}
