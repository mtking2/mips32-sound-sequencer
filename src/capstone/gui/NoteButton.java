package capstone.gui;

import javax.swing.JButton;

public class NoteButton extends JButton {
	private Note note;
	
	public NoteButton(int track, int beat){
		super();
		
		this.note = new Note(track, beat);
		
		setActionCommand(note.getTrack() + "/" + note.getBeat());
	}
	
	public Note getNote(){
		return note;
	}
}
