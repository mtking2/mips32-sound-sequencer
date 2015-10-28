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
	
	public void setNote(int track, int beat, Note note){
		diff[track][beat] = note;
	}
	
	public void commit(){
		for(int i = 0; i < orig.length; i++)
			for(int j = 0; j < orig[i].length; j++)
				orig[i][j] = diff[i][j];
		
		modified = false;
	}
	
	public void reset(){
		for(int i = 0; i < orig.length; i++)
			for(int j = 0; j < orig[i].length; j++)
				diff[i][j] = orig[i][j];
		
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
