package capstone.gui.enums;

public enum Pitch {
	C, C_SHARP, D, D_SHARP, E, F, F_SHARP, G, G_SHARP, A, A_SHARP, B, SPECIAL;
	
	public String toString(){
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
	
	public Pitch toPitch(String s){
		for(Pitch p : values())
			if(p.toString().equals(s)) return p;
		
		return null;
	}
	
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
