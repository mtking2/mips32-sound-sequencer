package capstone.gui.enums;

/**
  *	Enumerator designating the different musical
  * pitches and their names.
  *	
  *	@author Brad Westley
  *	@author Michael King
  *	@version 11.20.15
  */
public enum Pitch {
	C("C"), 
	C_SHARP("C#"), 
	D("D"), 
	D_SHARP("D#"), 
	E("E"), 
	F("F"), 
	F_SHARP("F#"), 
	G("G"), 
	G_SHARP("G#"), 
	A("A"), 
	A_SHARP("A#"), 
	B("B"), 
	SPECIAL("None");
	
	/** The name of the pitch **/
	private String name;
	
	/**
	  *	Constructs a new pitch enumeration with the given name.
	  *
	  * @param name the name of the pitch
	  */
	private Pitch(String name){
		this.name = name;
	}
	
	/**
	  *	Converts this pitch object to a string.
	  *
	  * @return this pitch as a string
	  */
	@Override
	public String toString(){
		return name;
	}
	
	/**
	  *	Find the pitch that this string designates, and return it.
	  *
	  * @param s the name of the pitch
	  * @return the pitch object whose name is the given string
	  */
	public Pitch toPitch(String s){
		for(Pitch p : values())
			if(p.toString().equals(s)) return p;
		
		return null;
	}
	
	/**
	  *	Finds the next pitch one half-step up from this pitch.
	  *
	  * @return the pitch one half-step higher than this pitch
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
}
