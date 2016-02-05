package capstone.gui.utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import capstone.gui.Note;
import capstone.gui.NoteButton;
import capstone.gui.Scale;
import capstone.gui.SequencerDisplay;
import capstone.gui.containers.NoteCollection;
import capstone.gui.enums.Pitch;
import capstone.gui.enums.ScaleType;
import capstone.gui.enums.TimeSignature;

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
	
	/** The file name to save MIPS code to  **/
	private static final String MIPS_FILENAME = "mips.asm";
	
	/** The size of one word in bytes **/
	private static final int WORD_SIZE = 4;
	
	/** The pitch value that designates that the note is a rest **/
	private static final String REST_PITCH = "1000";
		
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
	
	public static String currentFileName = null;
	
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
		
		String answer = builder.toString();

		// Windows uses backslash as separator, need to escape for MIPS
		if(System.getProperty("os.name").contains("Windows")){
			int index = 0;
			
			for(char c : builder.toString().toCharArray()){
				if(c == '\\')					
					builder.insert(index, c);	// If char is backslash, add another
				
				index++;
			}
		}
		
		return answer;
	}
	
	public static void loadFile(NoteCollection notes, String filename, 
			SequencerDisplay display) throws IOException {
		// Reset track and beat number
		track = 0;
		beat = 0;
		
		Path p = Paths.get(getPathToDataStorage() + filename);
		
		List<String> contents = Files.readAllLines(p);
		
		// Parse instrument data (first line)
		String first = contents.get(0);	// 0 -> first index
		contents.remove(0);
		
		int[] instruments = new int[NUMBER_OF_TRACKS];
		
		// Use length % 4 (size of word) as boundary
		// i == trackNumber
		for(int i = 0; i < (first.length() % 4); i++){
			int index = i * 4;	// Multiply by 4 so we move word-by-word
			
			int instrument = fromWord(first.substring(index, index + 3));
			
			instruments[i] = instrument;
		}
		
		// Use first line to calculate number of beats
		// (Instrument data has been removed)
		int beats = contents.get(0).length() / 8;
		
		notes.reset(NUMBER_OF_TRACKS, beats * 2);
		
		int count = 0;
		StringBuilder currentWord = new StringBuilder();
		
		// Parse lines
		for(int i = 0; i < contents.size(); i++){
			String line = contents.get(i);
			int track = i / 3;
			
			for(char c : line.toCharArray()){
				if(count % 4 == 0){
					switch(i % NUMBER_OF_TRACKS){
					case 0:	notes.editNotePitch(track, count / 4, fromWord(currentWord.toString()));
					case 1: notes.editNoteDuration(track, count / 4, fromWord(currentWord.toString()));
					case 2: notes.editNoteVolume(track, count / 4, fromWord(currentWord.toString()));
					}
					
					count++;
					currentWord = new StringBuilder(c);
				} else {
					currentWord.append(c);
				}
			}
		}
		
		// Commit the parsed notes
		notes.commit();
		
		for(TimeSignature tSig : TimeSignature.values()){
			if(beats == tSig.getBeats())
				SequencerUtils.tSig = tSig;
		}
		
		display.setupButtons(true);
		display.createTrackSelectionArea();
		
		scale = null;
		tempo = 120;	// TODO Add tempo to data file
		
		display.resetLabels();
		display.validate();
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
	 * Get the file path to where data files are stored.
	 * 
	 * @return
	 */
	public static String getPathToDataStorage(){
		// In windows, escape character is the same as file separator, 
		// so we need to escape the escape character.
		if(System.getProperty("os.name").contains("Windows")){
			return getPathToMIPS() + File.separator + File.separator
					+ "data" + File.separator + File.separator;
		} else {
			return getPathToMIPS() + File.separator
					+ "data" + File.separator;
		}
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
			if (button.isSelected())
				button.setBackground(Color.WHITE);
			else
				button.setBackground(Color.GRAY);
		} else {
			if (button.isSelected())
				button.setBackground(Color.WHITE);
			else
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
            Image img = ImageIO.read(new File(getPathToUtils()+"eighth_rest.png"));
			button.setIcon(new ImageIcon(img));
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
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
	
	/**
	 * Creates a data file as well as a MIPS file for playing the sequence.
	 * 
	 * @param notes the stored note data
	 * @throws IOException if something goes wrong during writing to file
	 */
	public static void toFile(NoteCollection notes, String filename) throws IOException {
		// Add file extension to filename if it isn't there already
		if(!filename.endsWith(".mss"))
			filename += ".mss";
		
		saveDataFile(notes, filename);
		saveMipsFile(filename);
	}

	/**
	 * Creates the MIPS code file that plays the sequence.
	 * 
	 * @throws IOException if something goes wrong during writing to file
	 */
	private static void saveMipsFile(String filename) throws IOException {
		StringBuilder builder = new StringBuilder();

		builder.append(".data\n\n");
		builder.append("timeToWait:\t.word\t" + timeToWait() + '\n');
		builder.append("beats:\t.word\t" + tSig.getBeats() + '\n');
		builder.append("tracks:\t.word\t" + 4 + '\n');		// Number of tracks hardcoded
		builder.append("filename:\t.asciiz\t\"" 
				+ getPathToDataStorage() + filename + "\"\n\n");

		Path p = Paths.get(getPathToMIPS() + "SequencerStem.asm");

		byte[] stemData = Files.readAllBytes(p);

		File mipsFile = new File(getPathToMIPS() + MIPS_FILENAME);

		OutputStream stream = new FileOutputStream(mipsFile);

		stream.write(builder.toString().getBytes());
		stream.write(stemData);
		
		stream.close();
	}
	
	/**
	 * Saves MIDI information to a file.
	 * 
	 * @param notes the stored note information
	 * @throws IOException if something goes wrong during writing to file
	 */
	private static void saveDataFile(NoteCollection notes, String filename) throws IOException {
		File file = new File(getPathToDataStorage() + filename);
		
		OutputStream stream = new FileOutputStream(file);
		
		// Write instruments
		for(int i = 0; i < NUMBER_OF_TRACKS; i++)
			stream.write(toWord(notes.getNote(i, 0).getInstrument()).getBytes());
		
		stream.write('\n');

		for(int i = 0; i < NUMBER_OF_TRACKS; i++){
			stream.write(getFormattedTrackContent(notes, i));
			if(i != NUMBER_OF_TRACKS - 1) stream.write('\n');
		}

		stream.close();
	}
	
	/**
	 * Get the MIDI data for a specific track.
	 * 
	 * @param notes the stored note information
	 * @param track the track to store
	 * @return the formatted track data as a series of bytes
	 */
	private static byte[] getFormattedTrackContent(NoteCollection notes, int track){
		Note[] row = notes.getRow(track);
		
		StringBuilder pitches = new StringBuilder(getNoteValueByteLength());
		StringBuilder volumes = new StringBuilder(getNoteValueByteLength());
		StringBuilder durations = new StringBuilder(getNoteValueByteLength());
		
		for(Note n : row){
			// If rest, denote by having pitch be 1000 in data file
			if(n.isRest()){
				pitches.append(REST_PITCH);
			} else {
				pitches.append(toWord(n.getPitch()));
			}
			
			durations.append(toWord(n.getDuration()));
			volumes.append(toWord(getVolumeValueFromPercentage(n.getVolume())));
		}
		
		String contentString = 
				pitches.toString() + '\n' 
				+ durations.toString() + '\n'
				+ volumes.toString();

		byte[] content = new byte[contentString.length()];
		
		for(int i = 0; i < contentString.length(); i++)
			content[i] = contentString.getBytes()[i];
		
		return content;
	}
	
	/**
	 * Calculate how much space for each note value (pitch, volume,
	 * duration).
	 * 
	 * @return the amount of space to store all note values
	 */
	private static int getNoteValueByteLength(){
		return 4 * tSig.getBeats();
	}
	
	/**
	 * Convert a number to a four byte word, preceding it with 
	 * 0's if necessary.
	 * 
	 * @param num the number to convert
	 * @return the number as a four-byte word
	 */
	private static String toWord(int num){
		StringBuilder builder = new StringBuilder(WORD_SIZE);
		
		if(num >= 1000 || num < 0){
			// This should not happen, so make this note a rest
			// MIPS parses rests as the first byte of word = 1
			return REST_PITCH;
		} else {
			builder.append('0');
			
			if(num >= 100){
				builder.append(num);
			} else {
				builder.append('0');
				
				if(num >= 10){
					builder.append(num);
				} else {
					builder.append('0');
					
					builder.append(num);
				}
			}
		}
		
		return builder.toString();
	}
	
	/**
	 * Converts a four-character word into an integer.
	 * 
	 * @param word the word to convert to an integer
	 * @return word as an integer, -1 if error
	 */
	private static int fromWord(String word){
		if(word.length() != 4) return -1;	// Word is 4 bytes
		
		int result = 0;
		
		// Parse word front-to-back
		for(int i = 0; i < word.length(); i++){
			int factor = 10 * (4 - i);	// Which power of 10 we are currently parsing
			
			int current = word.charAt(i);
			
			result += (factor * current);
		}
		
		return result;
	}
	
	public static String filenamePrompt(){
		return (String) JOptionPane.showInputDialog("Please enter a file name:");
	}
	
	/**
	 * Converts volume as a percentage to a value between 0 and 127.
	 * 
	 * @param volumePercentage the note volume as a percentage
	 * @return the note volume's MIDI value
	 */
	private static int getVolumeValueFromPercentage(int volumePercentage){
		return (int) (volumePercentage / 0.7874);
	}
	
	/**
	 * Convert a tempo in beats per minute to milliseconds to wait between each 
	 * beat, for MIPS purposes.
	 * 
	 * @param tempo the tempo in beats per minute
	 * @return time to wait in between each beat
	 */
	private static int timeToWait(){
		return (int) 60000 / tempo;
	}
}
