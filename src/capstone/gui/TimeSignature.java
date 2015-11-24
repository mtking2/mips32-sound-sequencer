package capstone.gui;

/**
 * A class enumerating several musical time signatures.
 * 
 * @author Brad Westley
 * @author Michael King
 * @version 12.11.15
 */
public enum TimeSignature {
	TWO_FOUR("2/4", 4),
	THREE_FOUR("3/4", 6),
	FOUR_FOUR("4/4", 8),
	FOUR_EIGHT("4/8", 4),
	FIVE_EIGHT("5/8", 5),
	SIX_EIGHT("6/8", 6),
	SEVEN_EIGHT("7/8", 7);
	
	/** The name of the time signature **/
	private String name;
	
	/** How many eighth-note beats are in this time signature **/
	private int beats;
	
	/**
	 * Make a new time signature object with specified name and 
	 * amount of eighth-notes.
	 * 
	 * @param name the name of the time signature
	 * @param beats how many beats are in the time signature
	 */
	private TimeSignature(String name, int beats){
		this.name = name;
		this.beats = beats;
	}
	
	/**
	 * @return the name of the time signature
	 */
	public String getName(){	return name;	}
	
	/**
	 * @return get the number of eighth-note beats in the time signature
	 */
	public int getBeats(){	return beats;	}
}
