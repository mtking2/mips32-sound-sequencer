package capstone.gui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;

import capstone.gui.buttons.PitchButton;

public class NoteTable extends JTable implements MouseListener {
	
	/** A reference to the bottom JPanel of the parent window **/
	private JPanel bottom;
	
	public NoteTable(int rows, int columns, JPanel bottom){
		super(rows, columns);
		
		addMouseListener(this);
		
		this.bottom = bottom;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		bottom.removeAll();
		
		// Row clicked is instrument track, column picked is beat
		
		bottom.add(new JTextArea("Track: " + (rowAtPoint(e.getPoint()) + 1)));
		bottom.add(new JTextArea("Beat: " + (columnAtPoint(e.getPoint()) + 1)));
		
		bottom.getParent().validate();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// Unused
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// Unused
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// Unused
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// Unused
	}
}
