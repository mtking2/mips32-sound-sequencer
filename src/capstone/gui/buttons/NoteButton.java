package capstone.gui.buttons;

import javax.swing.Icon;
import javax.swing.JButton;

/**
 * A button containing either a note or a rest, that 
 * selects the note upon clicking.
 * 
 * @author Brad Westley and Michael King
 * @version 9/17/15
 */
public class NoteButton extends JButton {
	/** True if this note is a rest (no note played) **/
	private boolean empty;
	
	public NoteButton(Icon icon){
		super(icon);
		
		empty = true;
	}
	
	public boolean isEmpty(){
		return empty;
	}
}
