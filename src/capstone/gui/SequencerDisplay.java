package capstone.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * A graphical interface that represents a music sequencer.
 *
 * @author Brad Westley and Michael King
 * @version 9/17/15
 */
public class SequencerDisplay extends JFrame implements ActionListener, ChangeListener {
    private static final String PITCH_NAME = "Pitch";
    private static final String VOLUME_NAME = "Volume";
    private static final String DURATION_NAME = "Duration";
    private static final String INSTRUMENT_NAME = "Instrument";

    private NoteButton[][] notes;
    
    private int tempo;
    
    private JOptionPane tempoSelect;

    private JPanel panel, north, west, center;
    private JMenuBar menuBar;
    private JMenu fileMenu, editMenu;
    private JMenuItem newMenuItem, exitMenuItem, saveMenuItem, tempoMenuItem;
    private JButton play, stop, confBtn, pitchBtn, volumeBtn, durationBtn, instrumentBtn, previous;
    
    private JLabel pitchValue, volumeValue, durationValue, instrumentValue,tempoLabel;
    private JSlider volume, pitch, duration, instrument;
    private Note currentNote;
    private ValueType type;

    public SequencerDisplay(String title, int width, int height){
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle(title);
        this.setResizable(false);
        menuInit();
        
        tempoSelect = null;
        
        tempo = 120;
        tempoLabel = new JLabel("Tempo: " + tempo);
        tempoLabel.setAlignmentX(SwingConstants.RIGHT);

        type = ValueType.PITCH;	// Set to pitch as default

        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        north = new JPanel(new FlowLayout(FlowLayout.LEFT));
        west = new JPanel(new BorderLayout());
        north.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        west.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        west.setPreferredSize(new Dimension(315, 200));

        // 4 tracks, 16 beats
        int tracks = 4;
        int beats = 16;

        notes = new NoteButton[tracks][beats];

        center = new JPanel(new GridLayout(tracks, beats));
        center.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

        for(int i = 0; i < tracks; i++)
            for(int j = 0; j < beats; j++){
                NoteButton button = new NoteButton(i, j);
                button.addActionListener(this);

                if(i == 0 && j == 0){
                    // Default note is first note
                    currentNote = button.getNote();
                    button.setEnabled(false);
                    previous = button;
                }

                notes[i][j] = button;
                notes[i][j].getNote().setVolume(127);
            }

        for(NoteButton[] noteRow : notes)
            for(NoteButton note : noteRow)	center.add(note);

        play = new JButton("Play");
        play.setContentAreaFilled(false);
        stop = new JButton("Stop");
        stop.setContentAreaFilled(false);

        north.add(play);
        north.add(stop);
        
        north.add(tempoLabel);

        createTrackSelectionArea(west);

        panel.add(north, BorderLayout.NORTH);
        panel.add(center, BorderLayout.CENTER);
        panel.add(west, BorderLayout.WEST);

        this.setContentPane(panel);
        this.setJMenuBar(menuBar);
        this.setSize(width, height);
        this.setVisible(true);

    }

    private void menuInit() {
        menuBar = new JMenuBar();

        // File Menu, F - Mnemonic
        fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(fileMenu);
        
        // Edit Menu, E - Mnemonic
        editMenu = new JMenu("Edit");
        editMenu.setMnemonic(KeyEvent.VK_E);
        menuBar.add(editMenu);

        // File->New, N - Mnemonic
        newMenuItem = new JMenuItem("New", KeyEvent.VK_N);
        fileMenu.add(newMenuItem);

        // File -> Save, S - Mnemonic
        saveMenuItem = new JMenuItem("Save", KeyEvent.VK_S);
        saveMenuItem.addActionListener(this);
        fileMenu.add(saveMenuItem);

        // File->Exit, X - Mnemonic
        exitMenuItem = new JMenuItem("Exit", KeyEvent.VK_X);
        exitMenuItem.addActionListener(this);
        fileMenu.add(exitMenuItem);
        
        // Edit -> Tempo..., T - Mnemonic
        tempoMenuItem = new JMenuItem("Tempo...", KeyEvent.VK_T);
        tempoMenuItem.addActionListener(this);
        editMenu.add(tempoMenuItem);
    }

    public String exportNotesToMIPS(){
        StringBuilder builder = new StringBuilder();

        // Begin with data section
        builder.append(".data\n\n");

        builder.append("instruments:    .word ");

        for(int i = 0; i < notes.length; i++){
            if(i > 0) builder.append(", ");

            builder.append(notes[i][0].getNote().getInstrument());
        }

        builder.append("\n\n");

        for(int i = 0; i < notes.length; i++){
            builder.append("# TRACK " + (i + 1) + " DATA #\n\n");

            builder.append(notesDataToMIPS(i));

            builder.append("\n");
        }
        
        builder.append("\n");
        
        builder.append("tempo:        .word    "
        			   + tempo);

        return builder.toString();
    }

    private String notesDataToMIPS(int track){
        StringBuilder builder = new StringBuilder();

        // Add pitch array
        builder.append("pitchArray" + (track + 1) + ":    .word ");

        for(int i = 0; i < notes[track].length; i++){
            if(i > 0) builder.append(", ");

            if(!notes[track][i].getNote().isRest()){
                builder.append(notes[track][i].getNote().getPitch());
            } else {
                // -1 is value designating a rest
                builder.append(-1);
            }
        }

        builder.append("\n");

        // Add volume array
        builder.append("volumeArray" + (track + 1) + ":    .word ");

        for(int i = 0; i < notes[track].length; i++){
            if(i > 0) builder.append(", ");

            builder.append(notes[track][i].getNote().getVolume());
        }

        builder.append("\n");

        // Add duration array
        builder.append("durationArray" + (track + 1) + ":    .word ");

        for(int i = 0; i < notes[track].length; i++){
            if(i > 0) builder.append(", ");

            builder.append(notes[track][i].getNote().getDuration());
        }

        builder.append("\n");

        return builder.toString();
    }

    public void createTrackSelectionArea(Container cont){

        JPanel subNorth = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JPanel subCenter = new JPanel(new GridBagLayout());
        JPanel subSouth = new JPanel(new FlowLayout());
        clearNote = new JButton("Clear Note");
        clearNote.addActionListener(this);
        subNorth.add(clearNote);

        GridBagConstraints c = new GridBagConstraints();

        pitch = new JSlider(0, 127, 0);
        volume = new JSlider(0, 127, 127);
        duration = new JSlider(0, 2000);
        instrument = new JSlider(0, 127, 0);

        pitchValue = new JLabel();
        volumeValue = new JLabel();
        durationValue = new JLabel();
        instrumentValue = new JLabel();

        pitchValue.setText("" + pitch.getValue());
        volumeValue.setText("" + volume.getValue());
        durationValue.setText("" + duration.getValue());
        instrumentValue.setText("" + instrument.getValue());

        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(5,2,5,0);
        c.ipady = 10;
        c.fill = GridBagConstraints.HORIZONTAL;
        //c.ipadx = 2;
        c.weightx = 0.5;

        // Add pitch slider to panel
        c.gridx = 0;
        c.gridy = 0;
        subCenter.add(new JLabel("Pitch "), c);
        c.gridx++;
        subCenter.add(pitch, c);


        // Add pitch label to panel
        c.gridx++;
        subCenter.add(pitchValue, c);

        // Add volume slider to panel
        c.gridx = 0;
        c.gridy++;
        subCenter.add(new JLabel("Volume "), c);
        c.gridx++;
        subCenter.add(volume, c);

        // Add volume label to panel
        c.gridx++;
        subCenter.add(volumeValue, c);

        // Add duration slider to panel
        c.gridx = 0;
        c.gridy++;
        subCenter.add(new JLabel("Duration "), c);
        c.gridx++;
        subCenter.add(duration, c);

        // Add duration label to panel
        c.gridx++;
        subCenter.add(durationValue, c);

        // Add instrument slider to panel
        c.gridx = 0;
        c.gridy++;
        subCenter.add(new JLabel("Instrument "), c);
        c.gridx++;
        subCenter.add(instrument, c);

        // Add instrument label to panel
        c.gridx++;
        subCenter.add(instrumentValue, c);

        // Add listener to slider so that when it scrolls, the value in the 
        // JLabel is updated.

        pitch.addChangeListener(this);
        volume.addChangeListener(this);
        duration.addChangeListener(this);
        instrument.addChangeListener(this);

        confBtn = new JButton("Confirm Changes");
        c.gridwidth = 2;
        c.gridx = 0;
        c.gridy++;
        confBtn.addActionListener(this);
        subCenter.add(confBtn, c);


        cont.add(subNorth, BorderLayout.NORTH);
        cont.add(subCenter, BorderLayout.CENTER);
        cont.add(subSouth, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object c = e.getSource();
        if (c.equals(exitMenuItem)) {
            System.exit(0);
        } else if(e.getSource().equals(saveMenuItem)) {
            File file = new File(getPathToGUI() + "mips.asm");

            byte[] data = exportNotesToMIPS().getBytes();
            byte[] code = null;

            try {
                code = Files.readAllBytes(
                        Paths.get(getPathToGUI() + "SequencerStem.asm"));
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
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
                JOptionPane.showMessageDialog(this,
                        "Error saving to file:\n" + ex.getMessage());
            }
        } else if (c.equals(tempoMenuItem)) {
            String answer = JOptionPane.showInputDialog(this,
                    "Enter a new tempo:",
                    tempo);

            try {
                tempo = Integer.parseInt(answer);
            } catch (NumberFormatException ex){
                tempoError();
            }

            if(tempo > 0 && tempo <= 300){
                tempo = Integer.parseInt(answer);
                tempoLabel.setText("Tempo: " + tempo);
            } else {
                tempoError();
            }
        } else if (c instanceof NoteButton) {
            NoteButton button = (NoteButton) e.getSource();

            currentNote = button.getNote();

            // Disable this note button, enable the last one selected
            button.setEnabled(false);
            previous.setEnabled(true);

            pitch.setValue(currentNote.getPitch());
            volume.setValue(currentNote.getVolume());
            duration.setValue(currentNote.getDuration());
            instrument.setValue(currentNote.getInstrument());

            pitchValue.setText("" + currentNote.getPitch());
            volumeValue.setText("" + currentNote.getVolume());
            durationValue.setText("" + currentNote.getDuration());
            instrumentValue.setText("" + currentNote.getInstrument());

            previous = button;

        } else if (c.equals(confBtn)) {
            currentNote.setPitch(pitch.getValue());
            currentNote.setVolume(volume.getValue());
            currentNote.setDuration(duration.getValue());
            currentNote.setInstrument(instrument.getValue());
            previous.setBackground(Color.green);
        } else if (c.equals(clearNote)) {
            currentNote.setPitch(0);
            currentNote.setVolume(127);
            currentNote.setDuration(0);
            currentNote.setInstrument(0);
            pitch.setValue(currentNote.getPitch());
            volume.setValue(currentNote.getVolume());
            duration.setValue(currentNote.getDuration());
            instrument.setValue(currentNote.getInstrument());
            pitchValue.setText("" + currentNote.getPitch());
            volumeValue.setText("" + currentNote.getVolume());
            durationValue.setText("" + currentNote.getDuration());
            instrumentValue.setText("" + currentNote.getInstrument());
            previous.setBackground(null);
        } else if (c.equals(tempoSelect)){
            try{
                tempo = Integer.parseInt(e.getActionCommand());
            } catch(NumberFormatException ex){
                if(e.getActionCommand().equals("")){

                }
            }
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (pitch.getValueIsAdjusting())
            pitchValue.setText("" + pitch.getValue());
        else if (volume.getValueIsAdjusting())
            volumeValue.setText("" + volume.getValue());
        else if (duration.getValueIsAdjusting())
            durationValue.setText("" + duration.getValue());
        else if (instrument.getValueIsAdjusting())
            instrumentValue.setText("" + instrument.getValue());
    }

    private String getPathToGUI(){
        StringBuilder builder = new StringBuilder();

        builder.append(System.getProperty("user.dir"));

        builder.append(File.separator + "src");
        builder.append(File.separator + "capstone");
        builder.append(File.separator + "gui" + File.separator);

        return builder.toString();
    }
    
    private void tempoError(){
    	JOptionPane.showMessageDialog(this, "Please enter a valid tempo value from 1 - 300.");
    }
}
