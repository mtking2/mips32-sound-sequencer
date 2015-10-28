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
				Note note = diff[i][j];
				
				orig[i][j] = new Note(note.getTrack(), note.getBeat(), 
									  note.getPitch(), note.getVolume(),
									  note.getDuration(), note.getInstrument());
			}
		}
		
		modified = false;
	}
	
	public void reset(){
		for(int i = 0; i < orig.length; i++){
			for(int j = 0; j < orig[i].length; j++){
				Note note = orig[i][i];

				diff[i][j] = new Note(note.getTrack(), note.getBeat(), 
									  note.getPitch(), note.getVolume(),
									  note.getDuration(), note.getInstrument());
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
}
