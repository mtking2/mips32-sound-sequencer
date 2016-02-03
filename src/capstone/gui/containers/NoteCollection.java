package capstone.gui.containers;

import capstone.gui.Note;

/**
 * Contains the information for each note in the sequencer.
 * 
 * @author Brad Westley
 * @author Michael King
 * @version 12.11.15
 */
public class NoteCollection {
	/** The notes that were previously committed **/
	private Note[][] orig;
	/** The notes that are waiting to be committed **/
	private Note[][] diff;
	
	/**
	 * Makes a new collection of notes.
	 * 
	 * @param tracks the number of tracks
	 * @param beats the number of beats
	 */
	public NoteCollection(int tracks, int beats){
		reset(tracks, beats);
	}
	
	/**
	 * Re-create this collection with a new amount of tracks 
	 * and beats.
	 * 
	 * @param tracks the number of tracks
	 * @param beats the number of beats
	 */
	public void reset(int tracks, int beats){
		orig = new Note[tracks][beats];
		diff = new Note[tracks][beats];
		
		for(int i = 0; i < tracks; i++){
			for(int j = 0; j < beats; j++){
				orig[i][j] = new Note(i, j);
			}
		}
	}
	
	/**
	 * Get a note from a specific location.
	 * 
	 * @param track the track the note is on
	 * @param beat the beat the note is on
	 * @return the note at the given track and beat
	 */
	public Note getNote(int track, int beat){
		return diff[track][beat];
	}
	
	/**
	 * Get the last committed note from a specific location.
	 * 
	 * @param track the track the note is on
	 * @param beat the beat the note is on
	 * @return the last committed note at the given track and beat
	 */
	public Note getOriginalNote(int track, int beat){
		return orig[track][beat];
	}
	
	/**
	 * Set the note at a specific location.
	 * 
	 * @param track the track the note is on
	 * @param beat the beat the note is on
	 * @param note the note to insert
	 */
	public void setNote(int track, int beat, Note note){
		diff[track][beat] = new Note(note.getTrack(), note.getBeat(), 
									 note.getPitch(), note.getVolume(), 
									 note.getDuration(), note.getInstrument());
	}
	
	/**
	 * Commit all changes to contained notes.
	 */
	public void commit(){
		for(int i = 0; i < orig.length; i++){
			for(int j = 0; j < orig[i].length; j++){
				// orig[i][j] = diff[i][j];
				// Copy values over
				orig[i][j].setPitch(diff[i][j].getPitch());
				orig[i][j].setVolume(diff[i][j].getVolume());
				orig[i][j].setDuration(diff[i][j].getDuration());
				orig[i][j].setInstrument(diff[i][j].getInstrument());
				orig[i][j].setIfRest(diff[i][j].isRest());
			}
		}
	}
	
	/**
	 * Reset all changes to contained notes.
	 */
	public void reset(){
		for(int i = 0; i < orig.length; i++){
			for(int j = 0; j < orig[i].length; j++){
				// diff[i][j] = orig[i][j];
				// Copy values over
				diff[i][j].setPitch(orig[i][j].getPitch());
				diff[i][j].setVolume(orig[i][j].getVolume());
				diff[i][j].setDuration(orig[i][j].getDuration());
				diff[i][j].setInstrument(orig[i][j].getInstrument());
				diff[i][j].setIfRest((orig[i][j].isRest()));
			}
		}
	}
	
	/**
	 * Get if any notes in the collection have been modified
	 * 
	 * @return true if any note in the collection has been modified
	 */
	public boolean isModified(){	
		for(int i = 0; i < diff.length; i++){
			for(int j = 0; j < diff[i].length; j++){
				if(hasNoteBeenModified(i, j)) return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Sets the instrument for every note in a particular track.
	 * 
	 * @param track which track to modify
	 * @param instrument the instrument value to assign to each note
	 */
	public void setTrackInstrument(int track, int instrument){
		for(Note n : diff[track])
			n.setInstrument(instrument);
	}
	
	/**
	 * Get all modified versions of the contained notes.
	 * 
	 * @return
	 */
	public Note[][] getAll(){	return diff;	}
	
	/**
	 * Get a row of notes from a track.
	 * 
	 * @param track the track to retrieve the notes from
	 * @return the notes from the given track
	 */
	public Note[] getRow(int track){
		return diff[track];
	}
	
	/**
	 * Get if a note in the collection has been modified.
	 * 
	 * @param track the track the note is on
	 * @param beat the beat the note is on
	 * @return true if the note has been modified
	 */
	public boolean hasNoteBeenModified(int track, int beat){		
		return !((orig[track][beat].getPitch() == diff[track][beat].getPitch()) &&
			   (orig[track][beat].getVolume() == diff[track][beat].getVolume()) &&
			   (orig[track][beat].getDuration() == diff[track][beat].getDuration()) &&
			   (orig[track][beat].getInstrument() == diff[track][beat].getInstrument()) &&
			   (orig[track][beat].isRest() == diff[track][beat].isRest()));
	}
	
	/**
	 * Get if every note in the collection is a rest.
	 *  
	 * @return true if all notes are rests
	 */
	public boolean allRests(){
		boolean ans = true;
		
		for(int i = 0; i < diff.length; i++)
			for(int j = 0; j < diff[i].length; j++)
				if(!diff[i][j].isRest()) ans = false;
		
		return ans;
	}
}
