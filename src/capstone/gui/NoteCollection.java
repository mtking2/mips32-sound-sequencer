package capstone.gui;

public class NoteCollection {
	/** The notes that were previously committed **/
	private Note[][] orig;
	/** The notes that are waiting to be committed **/
	private Note[][] diff;
	
	private boolean modified;
	
	public NoteCollection(int tracks, int beats){
		orig = new Note[tracks][beats];
		diff = new Note[tracks][beats];
		modified = false;
		
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
				if(diff[i][j].isRest())
					orig[i][j].makeRest();
				else
					orig[i][j].unmakeRest();
			}
		}
		
		modified = false;
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
				if(orig[i][j].isRest())
					diff[i][j].makeRest();
				else
					diff[i][j].unmakeRest();
			}
		}
			
		modified = false;
	}
	
	public void setModified(){	modified = true;	}
	
	public boolean isModified(){	return modified;	}
	
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
}
