package capstone.gui;

public class Note {
	private final MidiInfo REST = new MidiInfo(0, 0, 0, 1, true);
	
	private class MidiInfo {
		private int pitch;
		private int volume;
		private int duration;
		private int instrument;
		
		private boolean rest;
		
		public MidiInfo(int pitch, int volume, int duration, int instrument, boolean rest) {
			this.pitch = pitch;
			this.volume = volume;
			this.duration = duration;
			this.instrument = instrument;
			this.rest = rest;
		}
		
		public int getPitch(){ return pitch; }
		
		public int getVolume(){ return volume; }
		
		public int getDuration() { return duration; }
		
		public int getInstrument() { return instrument; }
		
		public boolean isRest(){ return rest; }
	}
	
	private int track;
	private int beat;
	
	private MidiInfo noteInfo;
	
	public Note(int track, int beat){
		this.track = track;
		this.beat = beat;
		
		// New notes are rests
		
		noteInfo = REST;
	}
	
	public int getTrack(){
		return track;
	}
	
	public int getBeat(){
		return beat;
	}
	
	public int getPitch(){ return noteInfo.getPitch(); }
	
	public int getVolume(){ return noteInfo.getVolume(); }
	
	public int getDuration(){ return noteInfo.getDuration(); }
	
	public int getInstrument(){ return noteInfo.getInstrument(); }
	
	public void makeRest(){
		noteInfo = REST;
	}
}
