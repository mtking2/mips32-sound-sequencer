package capstone.gui.enums;

import capstone.gui.utils.SequencerUtils;

/**
 * Class enumerating all musical pitches.
 * 
 * @author Brad Westley
 * @author Michael King
 * @version 12.11.15
 */
public enum Pitch {
	C, C_SHARP, D, D_SHARP, E, F, F_SHARP, G, G_SHARP, A, A_SHARP, B, SPECIAL;
	
	/**
	 * Converts the pitch to a string representation.
	 * 
	 * @return the pitch as a string
	 */
	@Override
	public String toString(){
		if(SequencerUtils.flats && isAccidental())
			return asFlat(this);
		
		switch(this){
		case A:			return "A";
		case A_SHARP:	return "A#";
		case B:			return "B";
		case C:			return "C";
		case C_SHARP:	return "C#";
		case D:			return "D";
		case D_SHARP:	return "D#";
		case E:			return "E";
		case F:			return "F";
		case F_SHARP:	return "F#";
		case G:			return "G";
		case G_SHARP:	return "G#";
		case SPECIAL:	return "None";
		default:		return "C";
		}
	}
	
	/**
	 * Convert a string representation of a pitch to its 
	 * enumerated counterpart.
	 * 
	 * @param s the name of a pitch
	 * @return the pitch associated with the given string
	 */
	public Pitch toPitch(String s){
		for(Pitch p : values())
			if(p.toString().equals(s)) return p;
		
		return null;
	}
	
	/**
	 * @return the pitch one half-step above this pitch
	 */
	public Pitch next(){
		switch(this){
		case A:			return A_SHARP;
		case A_SHARP:	return B;
		case B:			return C;
		case C:			return C_SHARP;
		case C_SHARP:	return D;
		case D:			return D_SHARP;
		case D_SHARP:	return E;
		case E:			return F;
		case F:			return F_SHARP;
		case F_SHARP:	return G;
		case G:			return G_SHARP;
		case G_SHARP:	return A;
		case SPECIAL:	return SPECIAL;
		default:		return null;
		}
	}
	
	/**
	 * If this pitch is a sharp, get its string representation as 
	 * if it were a flat.
	 * 
	 * @param p the pitch to display as a flat
	 * @return the string representation of this note as a flat
	 */
	private static String asFlat(Pitch p){
		// If not an accidental, no need to add flat symbol
		if(!p.isAccidental()) return p.toString();

		return p.next() + "b";
	}
	
	/**
	 * @return if this pitch is a sharp or a flat
	 */
	private boolean isAccidental(){
		return (this.equals(A_SHARP) || this.equals(C_SHARP) || 
				this.equals(F_SHARP) || this.equals(D_SHARP) || 
				this.equals(G_SHARP));
	}
}
