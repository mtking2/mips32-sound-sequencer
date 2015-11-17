package capstone.gui;

public enum TimeSignature {
	TWO_FOUR("2/4", 4),
	THREE_FOUR("3/4", 6),
	FOUR_FOUR("4/4", 8),
	FOUR_EIGHT("4/8", 4),
	FIVE_EIGHT("5/8", 5),
	SIX_EIGHT("6/8", 6),
	SEVEN_EIGHT("7/8", 7);
	
	private String name;
	private int beats;
	
	private TimeSignature(String name, int beats){
		this.name = name;
		this.beats = beats;
	}
	
	public String getName(){
		return name;
	}
	
	public int getBeats(){
		return beats;
	}
}
