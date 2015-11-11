package capstone.gui.enums;

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
	
	private String name;
	
	private Pitch(String name){
		this.name = name;
	}
	
	@Override
	public String toString(){
		return name;
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
	
	public String asFlat(Pitch p){
		if(!p.equals(A_SHARP) || !p.equals(C_SHARP) || p.equals(F_SHARP)
				|| !p.equals(D_SHARP) || !p.equals(G_SHARP)){
			return p.toString();
		}
		
		return p.next() + "b";
	}
}
