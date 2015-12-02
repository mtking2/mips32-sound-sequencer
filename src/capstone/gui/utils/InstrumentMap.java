package capstone.gui.utils;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

/**
 * A version of a {@link HashMap} that is specific to the General MIDI Level 1 Sound Set.
 *
 * @author Brad Westley
 * @author Michael King
 * @version 11.4.2015
 */
public class InstrumentMap extends HashMap {

    /** Integer value corresponding to the size of each instrument family. */
    public static final int FAMILY_SIZE = 8;

    /** A {@link HashMap} object to store this instrument map with keys and values reversed. */
    private HashMap reverseMap;

    /** Constructs and initializes the <code>InstrumentMap</code> with initial capacity of 144. */
    public InstrumentMap() {
        super(144);
        reverseMap = new HashMap<>(128);
        init();
    }

    /**
     * Helper method responsible for reading data from the instrument table and adding entries
     * into the <code>InstrumentMap</code>.
     */
    private void init() {
        Scanner scanIn = new Scanner(System.in);
        try {
            scanIn = new Scanner(new File(SequencerUtils.getPathToUtils()+"instrument_table.txt"));
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
        int i = 0;
        while (scanIn.hasNextLine()) {
            String line = scanIn.nextLine();

            if (!line.startsWith("#") && !line.isEmpty()) {
                Scanner scanLine = new Scanner(line).useDelimiter("[.]");
                int key = scanLine.nextInt()-1;
                String value = scanLine.next();
                this.put(key,value);
                reverseMap.put(value, key);
            } else if (line.startsWith("#")) {
                this.put("type"+i++, line.substring(1));
            }
        }

    }

    /**
     * @return the "reverse" of this <code>InstrumentMap</code>. Meaning the key and value
     * of each key-value pair are switched.
     */
    public HashMap getReverseMap() {
        return reverseMap;
    }

}
