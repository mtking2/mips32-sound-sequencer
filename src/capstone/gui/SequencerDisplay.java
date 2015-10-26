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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
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
public class SequencerDisplay extends JFrame implements ActionListener {
    private static final String PITCH_NAME = "Pitch";
    private static final String VOLUME_NAME = "Volume";
    private static final String DURATION_NAME = "Duration";
    private static final String INSTRUMENT_NAME = "Instrument";

    private NoteButton[][] notes;

    private JPanel panel, north, west, center;
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenuItem newMenuItem, exitMenuItem, saveMenuItem;
    private JButton play, stop, confBtn, pitchBtn, volumeBtn, durationBtn, instrumentBtn, previous;

    private JLabel pitchValue;
    private JLabel volumeValue;
    private JLabel durationValue;
    private JLabel instrumentValue;

    private JSlider slider;
    private JLabel sliderValue;

    private Note currentNote;
    private ValueType type;

    public SequencerDisplay(String title, int width, int height){
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle(title);
        this.setResizable(false);
        menuInit();

        type = ValueType.PITCH;	// Set to pitch as default

        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        north = new JPanel(new FlowLayout(FlowLayout.LEFT));
        west = new JPanel();
        north.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        west.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        west.setPreferredSize(new Dimension(310,200));

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
            }

        for(NoteButton[] noteRow : notes)
            for(NoteButton note : noteRow)	center.add(note);

        play = new JButton("Play");
        play.setContentAreaFilled(false);
        stop = new JButton("Stop");
        stop.setContentAreaFilled(false);

        north.add(play);
        north.add(stop);

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
        GridBagConstraints c = new GridBagConstraints();

        cont.setLayout(new GridBagLayout());

        pitchBtn = new JButton(PITCH_NAME);
        volumeBtn = new JButton(VOLUME_NAME);
        durationBtn = new JButton(DURATION_NAME);
        instrumentBtn = new JButton(INSTRUMENT_NAME);

        pitchValue = new JLabel();
        volumeValue = new JLabel();
        durationValue = new JLabel();
        instrumentValue = new JLabel();

        slider = new JSlider(0, 127);
        sliderValue = new JLabel();

        c.anchor = GridBagConstraints.EAST;
        c.insets = new Insets(5,0,5,0);
        c.ipady = 10;
        c.fill = GridBagConstraints.HORIZONTAL;
        //c.ipadx = 20;
        //c.weightx = 0.5;

        // Add pitch button to panel
        c.gridx = 0;
        c.gridy = 0;
        cont.add(pitchBtn, c);

        pitchBtn.addActionListener(this);

        String spaceBuf = "     ";

        // Add pitch label to panel
        c.gridx++;
        pitchValue.setText(spaceBuf + currentNote.getPitch());
        cont.add(pitchValue, c);

        // Add volume button to panel
        c.gridx = 0;
        c.gridy++;
        cont.add(volumeBtn, c);

        volumeBtn.addActionListener(this);

        // Add volume label to panel
        c.gridx++;
        volumeValue.setText(spaceBuf + currentNote.getVolume());
        cont.add(volumeValue, c);

        // Add duration button to panel
        c.gridx = 0;
        c.gridy++;
        cont.add(durationBtn, c);

        durationBtn.addActionListener(this);

        // Add duration label to panel
        c.gridx++;
        durationValue.setText(spaceBuf + currentNote.getDuration());
        cont.add(durationValue, c);

        // Add instrument button to panel
        c.gridx = 0;
        c.gridy++;
        cont.add(instrumentBtn, c);

        instrumentBtn.addActionListener(this);

        // Add instrument label to panel
        c.gridx++;
        instrumentValue.setText(spaceBuf + currentNote.getInstrument());
        cont.add(instrumentValue, c);

        // Add listener to slider so that when it scrolls, the value in the 
        // JLabel is updated.

        ChangeListener sliderListener = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if(slider.getValueIsAdjusting())
                    sliderValue.setText("" + slider.getValue());
            }
        };

        slider.addChangeListener(sliderListener);

        // Add slider to track
        c.gridx = 0;
        c.gridy++;
        cont.add(slider,c);

        // Add slider label
        c.gridx++;
        cont.add(sliderValue, c);


        confBtn = new JButton("Confirm Changes");
        c.gridwidth = 2;
        c.gridx = 0;
        c.gridy++;
        confBtn.addActionListener(this);
        cont.add(confBtn, c);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object c = e.getSource();
        if (c.equals(exitMenuItem)) {
            System.exit(0);
        } else if(e.getSource().equals(saveMenuItem)){
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

            for(int i = 0; i < data.length; i++)
                contents[i] = data[i];

            for(int i = data.length; i < data.length + code.length; i++)
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
        } else if (c.equals(pitchBtn)||c.equals(volumeBtn)||c.equals(durationBtn)||c.equals(instrumentBtn)) {

            pitchBtn.setEnabled(true);
            volumeBtn.setEnabled(true);
            durationBtn.setEnabled(true);
            instrumentBtn.setEnabled(true);

            if (c.equals(pitchBtn)) {
                pitchBtn.setEnabled(false);
                sliderValue.setText("" + currentNote.getPitch());
                slider.setValue(currentNote.getPitch());
                type = ValueType.PITCH;
            } else if (c.equals(volumeBtn)) {
                volumeBtn.setEnabled(false);
                sliderValue.setText("" + currentNote.getVolume());
                slider.setValue(currentNote.getVolume());
                type = ValueType.VOLUME;
            } else if (c.equals(durationBtn)) {
                durationBtn.setEnabled(false);
                sliderValue.setText("" + currentNote.getDuration());
                slider.setValue(currentNote.getDuration());
                type = ValueType.DURATION;
            } else if (c.equals(instrumentBtn)) {
                instrumentBtn.setEnabled(false);
                sliderValue.setText("" + currentNote.getInstrument());
                slider.setValue(currentNote.getInstrument());
                type = ValueType.INSTRUMENT;
            }

        } else if (c instanceof NoteButton) {
            NoteButton button = (NoteButton) e.getSource();

            currentNote = button.getNote();

            // Disable this note button, enable the last one selected
            button.setEnabled(false);
            previous.setEnabled(true);

            pitchValue.setText("     " + currentNote.getPitch());
            volumeValue.setText("     " + currentNote.getVolume());
            durationValue.setText("     " + currentNote.getDuration());
            instrumentValue.setText("     " + currentNote.getInstrument());

            // Move slider to new note's value
            switch(type){
                case DURATION:
                    slider.setValue(currentNote.getDuration());
                    sliderValue.setText("" + currentNote.getDuration());
                    break;
                case INSTRUMENT:
                    slider.setValue(currentNote.getInstrument());
                    sliderValue.setText("" + currentNote.getInstrument());
                    break;
                case PITCH:
                    slider.setValue(currentNote.getPitch());
                    sliderValue.setText("" + currentNote.getPitch());
                    break;
                case VOLUME:
                    slider.setValue(currentNote.getVolume());
                    sliderValue.setText("" + currentNote.getVolume());
            }
            previous = button;
        } else if (c.equals(confBtn)) {
            switch(type){
                case PITCH:
                    currentNote.setPitch(slider.getValue());
                    sliderValue.setText("" + currentNote.getPitch());
                    pitchValue.setText("     " + currentNote.getPitch());
                    break;
                case VOLUME:
                    currentNote.setVolume(slider.getValue());
                    sliderValue.setText("" + currentNote.getVolume());
                    volumeValue.setText("     " + currentNote.getVolume());
                    break;
                case DURATION:
                    currentNote.setDuration(slider.getValue());
                    sliderValue.setText("" + currentNote.getDuration());
                    durationValue.setText("     " + currentNote.getDuration());
                    break;
                case INSTRUMENT:
                    currentNote.setInstrument(slider.getValue());
                    sliderValue.setText("" + currentNote.getInstrument());
                    instrumentValue.setText("     " + currentNote.getInstrument());
                    break;
            }
        }
    }

    private String getPathToGUI(){
        StringBuilder builder = new StringBuilder();

        builder.append(System.getProperty("user.dir"));

        builder.append(File.separator + "src");
        builder.append(File.separator + "capstone");
        builder.append(File.separator + "gui" + File.separator);

        return builder.toString();
    }

    private Path getMIPSStemPath(){
        File f = new File(System.getProperty("user.dir") + File.separator + "SequencerStem.asm");

        return Paths.get(f.getPath());
    }
}
