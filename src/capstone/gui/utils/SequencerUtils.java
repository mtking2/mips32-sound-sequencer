package capstone.gui.utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import capstone.gui.Note;
import capstone.gui.NoteButton;
import capstone.gui.Scale;
import capstone.gui.TimeSignature;
import capstone.gui.containers.NoteCollection;
import capstone.gui.enums.Pitch;
import capstone.gui.enums.ScaleType;

/**
 * Class that keeps general information about the sequence, as well 
 * as static utility or convenience methods.
 * 
 * @author Brad Westley
 * @author Michael King
 * @version 12.11.15
 */
public class SequencerUtils {
	// Static final fields //
	
	/** Notes contained in the Aeolian scale **/
	public static final Pitch[] AEOLIAN = { Pitch.C, Pitch.D, Pitch.D_SHARP, 
		Pitch.F, Pitch.G, Pitch.G_SHARP, Pitch.A_SHARP };
	
	/** Notes contained in the Ionian scale **/
	public static final Pitch[] IONIAN = { Pitch.C, Pitch.D, Pitch.E, 
		Pitch.F, Pitch.G, Pitch.A, Pitch.B };
	
	/** Notes contained in the Phrygian scale **/
	public static final Pitch[] PHRYGIAN = { Pitch.C, Pitch.C_SHARP, Pitch.D_SHARP, 
		Pitch.F, Pitch.G, Pitch.G_SHARP, Pitch.A_SHARP };
	
	/** Notes contained in the Dorian scale **/
	public static final Pitch[] DORIAN = { Pitch.C, Pitch.D, Pitch.D_SHARP, 
		Pitch.F, Pitch.G, Pitch.A, Pitch.A_SHARP };
	
	/** Notes contained in the Mixolydian scale **/
	public static final Pitch[] MIXOLYDIAN = { Pitch.C, Pitch.D, Pitch.E, 
		Pitch.F, Pitch.G, Pitch.A, Pitch.A_SHARP };
	
	/** Notes contained in the Lydian scale **/
	public static final Pitch[] LYDIAN = { Pitch.C, Pitch.D, Pitch.E, 
		Pitch.F_SHARP, Pitch.G, Pitch.A, Pitch.B };
	
	/** Notes contained in the Locrian scale **/
	public static final Pitch[] LOCRIAN = { Pitch.C, Pitch.C_SHARP, Pitch.D_SHARP, 
		Pitch.F, Pitch.F_SHARP, Pitch.G_SHARP, Pitch.A_SHARP };
	
	/** The number of tracks in the sequencer **/
	public static final int NUMBER_OF_TRACKS = 4;
		
	////////////
	
	/** The map of instruments to integer values **/
	public static InstrumentMap instrumentMap = new InstrumentMap();
	
	/** The currently selected instrument **/
	public static int selectedInstrument;
	
	/** The current scale **/
	public static Scale scale = null;
	
	/** The current time signature **/
	public static TimeSignature tSig = TimeSignature.FOUR_FOUR;
	
	/** How many beats per minutes **/
	public static int tempo = 120;
	
	/** The current track **/
	public static int track = 0;
	
	/** The current beat **/
	public static int beat = 0;
	
	/** If slider change needs to be ignored **/
	public static boolean ignoreStateChange = false;
	
	/** If the sequencer is currently playing **/
	public static boolean playing = false;
	
	/** If the sequencer is displaying flats **/
	public static boolean flats = false;
	
	/**
	 * Display an error message to the user for invalid tempo value.
	 * 
	 * @param parent the container to display the message in
	 */
	public static void showTempoError(Component parent){
		JOptionPane.showMessageDialog(parent, 
				"Please enter a valid tempo value from 1 - 300.");
	}
	
	/**
	 * Convert an integer pitch to a pitch enumerator objects
	 * 
	 * @param pitch pitch as an integer
	 * @return the pitch as an enumerator
	 */
	public static Pitch intToPitch(int pitch){
		switch(pitch % 12){
		case 0:	return Pitch.C;
		case 1:	return Pitch.C_SHARP;
		case 2:	return Pitch.D;
		case 3:	return Pitch.D_SHARP;
		case 4:	return Pitch.E;
		case 5:	return Pitch.F;
		case 6:	return Pitch.F_SHARP;
		case 7:	return Pitch.G;
		case 8:	return Pitch.G_SHARP;
		case 9:	return Pitch.A;
		case 10:	return Pitch.A_SHARP;
		case 11:	return Pitch.B;
		default:	return Pitch.C;
		}
	}
	
	/**
	 * Convert an integer pitch to a string.
	 * 
	 * @param pitch the pitch as an integer
	 * @return the pitch as a string
	 */
	public static String intPitchToString(int pitch){
		return intToPitch(pitch).toString() 
				+ ((pitch - (pitch % 12)) / 12);
	}
	
	/**
	 * Get the path to the MIPS data folder.
	 * 
	 * @return the path to the MIPS data folder
	 */
	public static String getPathToMIPS(){
		StringBuilder builder = new StringBuilder();

		builder.append(System.getProperty("user.dir"));

		builder.append(File.separator + "src");
		builder.append(File.separator + "capstone");
		builder.append(File.separator + "mips" + File.separator);

		return builder.toString();
	}

	/**
	 * Get the path to the sequencer utils folder
	 * 
	 * @return the path to the sequencer utils folder
	 */
	public static String getPathToUtils(){
		StringBuilder builder = new StringBuilder();

		builder.append(System.getProperty("user.dir"));

		builder.append(File.separator + "src");
		builder.append(File.separator + "capstone");
		builder.append(File.separator + "gui");
		builder.append(File.separator + "utils" + File.separator);

		return builder.toString();
	}

	/**
	 * Reset the backgrounds of all notes.
	 * 
	 * @param components the note button components to set
	 * @param notes the note information to match with buttons
	 */
	public static void resetNoteBackgrounds(Component[] components, 
			NoteCollection notes){
		for(Component c : components){
			if(c instanceof NoteButton)
				resetNoteBackground((NoteButton) c, notes);
		}
	}

	/**
	 * Reset the background of a note based on its stored information.
	 * 
	 * @param button the button to reset
	 * @param notes the stored note information
	 */
	private static void resetNoteBackground(NoteButton button, 
			NoteCollection notes){
		Note n = notes.getNote(button.getTrack(), 
				button.getBeat());

		if(n.isRest()) {
			button.setBackground(Color.GRAY);
            button.setText(null);
			setRestIcon(button);
		} else {
            button.setBackground(null);
            button.setIcon(null);
            button.setText(SequencerUtils.intPitchToString(n.getPitch()));
        }
	}

	/**
	 * Set the icon of a button to a rest icon.
	 * 
	 * @param button the button to modify
	 */
	public static void setRestIcon(JButton button) {
		try {
            Image img = ImageIO.read(new File(getPathToUtils()+"quarter-rest.png"));
			button.setIcon(new ImageIcon(img));
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * Export note information to a MIPS file.
	 * 
	 * @param notes the stored note information
	 * @return a string containing exported note information
	 */
	public static String exportNotesToMIPS(NoteCollection notes){
		StringBuilder builder = new StringBuilder();

		// Begin with data section
		builder.append(".data\n\n");

		builder.append("instruments:    .word ");

		for(int i = 0; i < notes.getAll().length; i++){
			if(i > 0) builder.append(", ");

			builder.append(notes.getNote(i, 0).getInstrument());
		}

		builder.append("\n\n");

		for(int i = 0; i < notes.getAll().length; i++){
			builder.append("# TRACK " + (i + 1) + " DATA #\n\n");

			builder.append(notesDataToMIPS(notes, i));

			builder.append("\n");
		}

		builder.append("\n");

        // converts the tempo (in BPMs) to delay time (in milliseconds).
		int bpmToMs = 60000/SequencerUtils.tempo;

		builder.append("tempo:        .word    "
				+ bpmToMs);

		builder.append("\n\n");

		return builder.toString();
	}
	
	/**
	 * Convert note data for a track to MIPS arrays.
	 * 
	 * @param notes the stored note information
	 * @param track the track to convert
	 * @return a string representing the track's note values as MIPS arrays
	 */
	public static String notesDataToMIPS(NoteCollection notes, int track){
		StringBuilder builder = new StringBuilder();

		// Add pitch array
		builder.append("pitchArray" + (track + 1) + ":\t.word ");

		for(int i = 0; i < notes.getRow(track).length; i++){
			if(i > 0) builder.append(", ");

			if(!notes.getNote(track, i).isRest()){
				builder.append(notes.getNote(track, i).getPitch());
			} else {
				// -1 is value designating a rest
				builder.append(-1);
			}
		}

		builder.append("\n");

		// Add volume array
		builder.append("volumeArray" + (track + 1) + ":\t.word ");

		for(int i = 0; i < notes.getRow(track).length; i++){
			if(i > 0) builder.append(", ");

            // convert the volume percentage into an integer value between 1 and 127
			int d = (int)(notes.getNote(track, i).getVolume()/0.7874);
			builder.append(d);
		}

		builder.append('\n');

		// Add duration array
		builder.append("durationArray" + (track + 1) + ":\t.word ");

		for(int i = 0; i < notes.getRow(track).length; i++){
			if(i > 0) builder.append(", ");

			builder.append(notes.getNote(track, i).getDuration());
		}

		builder.append('\n');
		
		int beats = SequencerUtils.tSig.getBeats() * 2;
		
		builder.append("beats:\t.word\t" + beats + '\n');
		
		builder.append('\n');

		return builder.toString();
	}
	
	/**
	 * Create MIPS file for playing the current sequence.
	 * 
	 * @param parent the container to display messages to
	 * @param notes the stored note information
	 */
	public static void toFile(Component parent, NoteCollection notes) {
		File file = new File(SequencerUtils.getPathToMIPS() + "mips.asm");
		if (file.exists())
            file.delete();

		byte[] data = exportNotesToMIPS(notes).getBytes();
		byte[] code = null;

		try {
			code = Files.readAllBytes(
					Paths.get(SequencerUtils.getPathToMIPS() + 
							"SequencerStem.asm"));
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(parent,
					"Error reading from file SequencerStem.asm:\n"
							+ ex.getMessage());
			return;
		}

		// Combine track data and template into one array

		byte[] contents = new byte[data.length + code.length];

		for (int i = 0; i < data.length; i++)
			contents[i] = data[i];

		for (int i = data.length; i < data.length + code.length; i++)
			contents[i] = code[i - data.length];

		try {
			Files.write(Paths.get(file.getPath()),
					contents,
					StandardOpenOption.CREATE,
					StandardOpenOption.WRITE);
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(parent,
					"Error saving to file:\n" + ex.getMessage());
		}

		JOptionPane.showMessageDialog(parent, "File successfully saved.");
	}
	
	/**
	 * Brings up a prompt for selecting a new scale, or removing the 
	 * current scale if needed.
	 * 
	 * @param parent the container to display messages to
	 * @param scaleLabel the label that shows the current scale
	 */
	public static void promptUserForNewScale(Component parent, JLabel scaleLabel){
		if(scale != null){
			int option = JOptionPane.showConfirmDialog(parent, "Would you like to disable the scale?");
			
			if(option == JOptionPane.YES_OPTION){
				scale = null;
				scaleLabel.setText("Scale: None");
				return;
			} else if(option == JOptionPane.CANCEL_OPTION) {
				return;
			}
		}
		
		Object pitch = JOptionPane.showInputDialog(parent, "Select the pitch of the scale:"
				, "Scale Pitch Select", JOptionPane.QUESTION_MESSAGE, null
				, Pitch.values(), "C");

		if(pitch instanceof Pitch){
			Object scaleType = JOptionPane.showInputDialog(parent, "Select the type of the scale:"
					, "Scale Type Select", JOptionPane.QUESTION_MESSAGE, null
					, ScaleType.values(), "C");

			if(scaleType instanceof ScaleType){
				scale = new Scale((Pitch) pitch, (ScaleType) scaleType);
				scaleLabel.setText("Scale: " + scale);
			}
		}
	}
}
