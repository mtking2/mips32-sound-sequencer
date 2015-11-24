package capstone.gui;

import javax.swing.tree.TreePath;

/**
 * Class representing a note in music.
 * 
 * @author Brad Westley
 * @author Michael King
 * @version 12.11.15
 */
public class Note {
	/**
     * Private class containing note information for MIDI service.
     * 
     * @author Brad Westley
     * @author Michael King
     * @version 12.11.15
     */
	private class MidiInfo {
		/** The note's pitch value **/
		private int pitch;
		/** The volume of the note **/
		private int volume;
		/** The duration of the note **/
		private int duration;
		/** The instrument that the note uses **/
		private int instrument;
		
		/** If this note is a rest **/
		private boolean rest;
		
		/**
		 * Creates a new MIDI information object with the given pitch, 
		 * volume, duration, instrument, and rest values.
		 * 
		 * @param pitch this note's pitch value
		 * @param volume the volume of the note
		 * @param duration the duration of the note
		 * @param instrument the instrument that the note uses
		 * @param rest if this note is a rest
		 */
		public MidiInfo(int pitch, int volume, int duration, int instrument, boolean rest) {
			this.pitch = pitch;
			this.volume = volume;
			this.duration = duration;
			this.instrument = instrument;
			this.rest = rest;
		}
		
		/**
		 * Set if this note is a rest or not.
		 * 
		 * @param rest if this note is a rest
		 */
		public void setIfRest(boolean rest){	this.rest = rest; }
		
		/**
		 * @return the note's pitch value
		 */
		public int getPitch(){ return pitch; }
		
		/**
		 * @return the volume of the note
		 */
		public int getVolume(){ return volume; }
		
		/**
		 * @return the duration of the note
		 */
		public int getDuration() { return duration; }
		
		/**
		 * @return the instrument that the note uses
		 */
		public int getInstrument() { return instrument; }
		
		/**
		 * Set the pitch of the note.
		 * 
		 * @param pitch the note's new pitch value
		 */
		public void setPitch(int pitch){	this.pitch = pitch;	}
		
		/**
		 * Set the volume of the note.
		 * 
		 * @param volume the new volume of the note
		 */
		public void setVolume(int volume){ this.volume = volume; }
		
		/**
		 * Set the duration of the note.
		 * 
		 * @param duration the new duration of the note
		 */
		public void setDuration(int duration){ this.duration = duration; }
		
		/**
		 * Set the note's instrument value.
		 * 
		 * @param instrument the note's new instrument value
		 */
		public void setInstrument(int instrument){ this.instrument = instrument; }
		
		/**
		 * @return if this note is a rest
		 */
		public boolean isRest(){ return rest; }
	}
	
	/** Default note value of rest **/
	private final MidiInfo REST = new MidiInfo(0, 100, 0, 0, true);
	
	/** The tree path for this note's instrument **/
    private TreePath path;
	
	/** This note's MIDI information **/
	private MidiInfo noteInfo;
	
	/** The track this note is on **/
	private int track;
	
	/** The beat this note is on **/
	private int beat;
	
	/**
	 * Create a new, default note at the specified track and beat.
	 * 
	 * @param track the track the note is on
	 * @param beat the beat the note is on
	 */
	public Note(int track, int beat){
		this.track = track;
		this.beat = beat;
		
		// New notes are rests
		noteInfo = REST;
	}
	
	/**
	 * Creates a new note with the specified MIDI information, track, 
	 * and beat.
	 * 
	 * @param track the track the note is on
	 * @param beat the beat the note is on
	 * @param pitch the note's pitch value
	 * @param volume the volume of the note
	 * @param duration the duration of the note
	 * @param instrument the MIDI instrument the note uses
	 */
	public Note(int track, int beat, int pitch, 
				int volume, int duration, int instrument){
		this.track = track;
		this.beat = beat;
		
		noteInfo = new MidiInfo(pitch, volume, duration, instrument, true);
	}
	
	/**
	 * @return the track the note is on
	 */
	public int getTrack(){
		return track;
	}
	
	/**
	 * @return the beat the note is on
	 */
	public int getBeat(){	return beat;	}
	
	/**
	 * @return the note's pitch value
	 */
	public int getPitch(){ return noteInfo.getPitch(); }
	
	/**
	 * @return the volume of the note
	 */
	public int getVolume(){ return noteInfo.getVolume(); }
	
	/**
	 * @return the duration of the note
	 */
	public int getDuration(){ return noteInfo.getDuration(); }
	
	/**
	 * @return the MIDI instrument the note uses
	 */
	public int getInstrument(){ return noteInfo.getInstrument(); }

	/**
	 * @return the tree path to this note's instrument
	 */
    public TreePath getTreePath() { return path; }

    /**
     * Set if this note is a rest or not.
     * 
     * @param rest if this note is a rest
     */
	public void setIfRest(boolean rest) {	noteInfo.setIfRest(rest); }

	/**
	 * @return if this note is a rest
	 */
	public boolean isRest() { return noteInfo.isRest();}
	
	/**
	 * Set the pitch of this note.
	 * 
	 * @param pitch the note's new pitch value
	 */
	public void setPitch(int pitch){	noteInfo.setPitch(pitch);	}
	
	/**
	 * Set the volume of this note.
	 * 
	 * @param volume the new volume of this note
	 */
	public void setVolume(int volume){ 	noteInfo.setVolume(volume);	}
	
	/**
	 * Set the duration of this note.
	 * 
	 * @param duration the new duration of this note
	 */
	public void setDuration(int duration){ 	noteInfo.setDuration(duration);	}
	
	/**
	 * Set the MIDI instrument this note uses.
	 * 
	 * @param instrument the MIDI note this note uses
	 */
	public void setInstrument(int instrument){ 	noteInfo.setInstrument(instrument);	}

	/**
	 * Set the path to this note's MIDI instrument
	 * 
	 * @param path the path to this note's MIDI instrument
	 */
    public void setTreePath(TreePath path){ this.path = path; }
}
