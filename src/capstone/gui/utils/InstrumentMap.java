package capstone.gui.utils;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by michael on 11/4/15.
 */
public class InstrumentMap extends HashMap<Object, Object> {
    /** Generated serialization UID **/
	private static final long serialVersionUID = 2508140311509124210L;
	
	public static final int NUM_INST = 127;
    public static final int FAMILY_SIZE = 8;
    private HashMap<Object, Object> reverseMap;

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
                Scanner scanLine = new Scanner(line);
                scanLine.useDelimiter("[.]");
                int key = scanLine.nextInt()-1;
                String value = scanLine.next();
                this.put(key,value);
                reverseMap.put(value, key);
                scanLine.close();
            } else if (line.startsWith("#")) {
                this.put("type"+i++, line.substring(1));
            }
        }
        
        scanIn.close();

        //System.out.println(this.size());
        //System.out.println(this);
        //System.out.println(reverseMap);
    }

    public HashMap<Object, Object> getReverseMap() {
        return reverseMap;
    }

}
