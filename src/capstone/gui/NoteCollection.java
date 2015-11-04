package capstone.gui;

public class NoteCollection {
	/** The notes that were previously committed **/
	private Note[][] orig;
	/** The notes that are waiting to be committed **/
	private Note[][] diff;
	
	public NoteCollection(int tracks, int beats){
		orig = new Note[tracks][beats];
		diff = new Note[tracks][beats];
		
		for(int i = 0; i < tracks; i++){
			for(int j = 0; j < beats; j++){
				orig[i][j] = new Note(i, j);
			}
		}
	}
	
	public Note getNote(int track, int beat){
		return diff[track][beat];
	}
	
	public Note getOriginalNote(int track, int beat){
		return orig[track][beat];
	}
	
	public void setNote(int track, int beat, Note note){
		diff[track][beat] = new Note(note.getTrack(), note.getBeat(), 
									 note.getPitch(), note.getVolume(), 
									 note.getDuration(), note.getInstrument());
	}
	
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
	
	public boolean isModified(){	
		for(int i = 0; i < diff.length; i++){
			for(int j = 0; j < diff[i].length; j++){
				if(hasNoteBeenModified(i, j)) return true;
			}
		}
		
		return false;
	}
	
	public Note[][] getAll(){
		return diff;
	}
	
	public Note[] getRow(int track){
		return diff[track];
	}
	
	public boolean hasNoteBeenModified(int track, int beat){		
		return !((orig[track][beat].getPitch() == diff[track][beat].getPitch()) &&
			   (orig[track][beat].getVolume() == diff[track][beat].getVolume()) &&
			   (orig[track][beat].getDuration() == diff[track][beat].getDuration()) &&
			   (orig[track][beat].getInstrument() == diff[track][beat].getInstrument()) &&
			   (orig[track][beat].isRest() == diff[track][beat].isRest()));
	}
	
	public boolean allRests(){
		boolean ans = true;
		
		for(int i = 0; i < diff.length; i++)
			for(int j = 0; j < diff[i].length; j++)
				if(!diff[i][j].isRest()) ans = false;
		
		return ans;
	}
}
