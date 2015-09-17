package capstone.gui;

import javax.swing.JFrame;

/**
 * A graphical interface that represents a music sequencer.
 * 
 * @author Brad Westley and Michael King
 * @version 9/17/15
 */
public class SequencerDisplay {
	/** The window that contains the interface **/
	private JFrame window;

	/**
	 * Creates and displays a window with a certain name,
	 * height, and width.
	 * 
	 * @param title the name of the program
	 * @param width the width of the window
	 * @param height the height of the window
	 */
	public SequencerDisplay(String title, int width, int height){
		window = new JFrame();
		
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setFocusable(true);
		window.setTitle(title);
		
		window.setSize(width, height);
		
		window.setVisible(true);
	}
}