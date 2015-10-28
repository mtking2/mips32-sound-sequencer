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
		
		public void setPitch(int pitch){
			this.pitch = pitch;
			this.rest = false;
		}
		
		public void setVolume(int volume){ this.volume = volume; }
		
		public void setDuration(int duration){ this.duration = duration; }
		
		public void setInstrument(int instrument){ this.instrument = instrument; }
		
		public boolean isRest(){ return rest; }
	}
	
	private MidiInfo noteInfo;
	private int track;
	
	public Note(int track){
		this.track = track;
		
		// New notes are rests
		noteInfo = REST;
	}
	
	public int getTrack(){
		return track;
	}
	
	public int getPitch(){ return noteInfo.getPitch(); }
	
	public int getVolume(){ return noteInfo.getVolume(); }
	
	public int getDuration(){ return noteInfo.getDuration(); }
	
	public int getInstrument(){ return noteInfo.getInstrument(); }
	
	public void makeRest(){
		noteInfo = REST;
	}

	public boolean isRest() { return noteInfo.isRest();}
	
	public void setPitch(int pitch){
		noteInfo.setPitch(pitch);
		noteInfo.rest = false;
	}
	
	public void setVolume(int volume){ 	noteInfo.setVolume(volume);	}
	
	public void setDuration(int duration){ 	noteInfo.setDuration(duration);	}
	
	public void setInstrument(int instrument){ 	noteInfo.setInstrument(instrument);	}
}
