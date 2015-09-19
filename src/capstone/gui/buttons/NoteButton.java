package capstone.gui.buttons;

import javax.swing.JButton;

/**
 * A button containing either a note or a rest, that 
 * selects the note upon clicking.
 * 
 * @author Brad Westley and Michael King
 * @version 9/17/15
 */
public class NoteButton extends JButton {
	private static final int EMPTY = -1;
	
	private int note;
	
	public NoteButton(){
		super();
		setBorderPainted(false);
		
		note = EMPTY;
		
		this.setText("poo");
	}
	
	public boolean isEmpty(){
		return note == EMPTY;
	}
	
	public int getNote(){
		return note;
	}
}
