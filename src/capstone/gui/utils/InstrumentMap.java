package capstone.gui.utils;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by michael on 11/4/15.
 */
public class InstrumentMap extends HashMap {

    public static final int NUM_INST = 127;
    public static final int FAMILY_SIZE = 8;
    private HashMap reverseMap;

    public InstrumentMap() {
        super(144);
        reverseMap = new HashMap<>(128);
        init();
    }

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

        //System.out.println(this.size());
        //System.out.println(this);
        //System.out.println(reverseMap);
    }

    public HashMap getReverseMap() {
        return reverseMap;
    }

    public static void main(String[] args) {
        new InstrumentMap();
    }
}
