package capstone.gui.utils;

import java.awt.Color;
import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;

import capstone.gui.Note;
import capstone.gui.NoteButton;
import capstone.gui.Scale;
import capstone.gui.containers.NoteCollection;
import capstone.gui.enums.Pitch;
import capstone.gui.enums.ScaleType;

public class SequencerUtils {
	// Static final fields //
	
	public static final Pitch[] AEOLIAN = { Pitch.C, Pitch.D, Pitch.D_SHARP, 
		Pitch.F, Pitch.G, Pitch.G_SHARP, Pitch.A_SHARP };
	
	public static final Pitch[] IONIAN = { Pitch.C, Pitch.D, Pitch.E, 
		Pitch.F, Pitch.G, Pitch.A, Pitch.B };
	
	public static final Pitch[] PHRYGIAN = { Pitch.C, Pitch.C_SHARP, Pitch.D_SHARP, 
		Pitch.F, Pitch.G, Pitch.G_SHARP, Pitch.A_SHARP };
	
	public static final Pitch[] DORIAN = { Pitch.C, Pitch.D, Pitch.D_SHARP, 
		Pitch.F, Pitch.G, Pitch.A, Pitch.A_SHARP };
	
	public static final Pitch[] MIXOLYDIAN = { Pitch.C, Pitch.D, Pitch.E, 
		Pitch.F, Pitch.G, Pitch.A, Pitch.A_SHARP };
	
	public static final Pitch[] LYDIAN = { Pitch.C, Pitch.D, Pitch.E, 
		Pitch.F_SHARP, Pitch.G, Pitch.A, Pitch.B };
	
	public static final Pitch[] LOCRIAN = { Pitch.C, Pitch.C_SHARP, Pitch.D_SHARP, 
		Pitch.F, Pitch.F_SHARP, Pitch.G_SHARP, Pitch.A_SHARP };
	
	public static final Border BUTTON_SELECTED_BORDER = 
			new MatteBorder(2, 2, 2, 2, Color.YELLOW);
	
	public static final Border BUTTON_DEFAULT_BORDER = 
			new MatteBorder(1, 1, 1, 1, Color.BLACK);
	
	// Static fields //
	
	public static Scale scale = null;
	public static int tempo = 120;
	public static int track = 0;
	public static int beat = 0;
	
	public static boolean ignoreStateChange = false;
	public static boolean playing = false;
	
	public static void showTempoError(Component parent){
		JOptionPane.showMessageDialog(parent, 
				"Please enter a valid tempo value from 1 - 300.");
	}
	
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
	
	public static String intPitchToString(int pitch){
		return intToPitch(pitch).toString() 
				+ ((pitch - (pitch % 12)) / 12);
	}
	
	public static String getPathToMIPS(){
		StringBuilder builder = new StringBuilder();

		builder.append(System.getProperty("user.dir"));

		builder.append(File.separator + "src");
		builder.append(File.separator + "capstone");
		builder.append(File.separator + "mips" + File.separator);

		return builder.toString();
	}
	
	public static void resetNoteBackgrounds(Component[] components, 
			NoteCollection notes){
		for(Component c : components){
			if(c instanceof NoteButton)
				resetNoteBackground((NoteButton) c, notes);
		}
	}

	private static void resetNoteBackground(NoteButton button, 
			NoteCollection notes){
		Note n = notes.getNote(button.getTrack(), 
				button.getBeat());

		if(n.isRest())
			button.setBackground(Color.RED);
		else
			button.setBackground(null);

		button.setText(
				SequencerUtils.intPitchToString(n.getPitch()).toString());
	}
	
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

		builder.append("tempo:        .word    "
				+ SequencerUtils.tempo);

		builder.append("\n\n");

		return builder.toString();
	}
	
	public static String notesDataToMIPS(NoteCollection notes, int track){
		StringBuilder builder = new StringBuilder();

		// Add pitch array
		builder.append("pitchArray" + (track + 1) + ":    .word ");

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
		builder.append("volumeArray" + (track + 1) + ":    .word ");

		for(int i = 0; i < notes.getRow(track).length; i++){
			if(i > 0) builder.append(", ");

			builder.append(notes.getNote(track, i).getVolume());
		}

		builder.append("\n");

		// Add duration array
		builder.append("durationArray" + (track + 1) + ":    .word ");

		for(int i = 0; i < notes.getRow(track).length; i++){
			if(i > 0) builder.append(", ");

			builder.append(notes.getNote(track, i).getDuration());
		}

		builder.append("\n");

		return builder.toString();
	}
	
	public static void toFile(Component parent, NoteCollection notes) {
		File file = new File(SequencerUtils.getPathToMIPS() + "mips.asm");

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
		
		Object scaleType = JOptionPane.showInputDialog(parent, "Select the type of the scale:"
				, "Scale Type Select", JOptionPane.QUESTION_MESSAGE, null
				, ScaleType.values(), "C");
		
		if(pitch instanceof Pitch && scaleType instanceof ScaleType){
			scale = new Scale((Pitch) pitch, (ScaleType) scaleType);
			scaleLabel.setText("Scale: " + scale);
		}
	}
}