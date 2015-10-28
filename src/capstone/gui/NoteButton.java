package capstone.gui;

import javax.swing.JButton;

public class NoteButton extends JButton {
	private Note note;
	
	public NoteButton(Note note){
		super();
		
		this.note = note;
	}
	
	public Note getNote(){
		return note;
	}
}
