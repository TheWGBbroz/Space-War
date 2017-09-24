package com.thewgb.spacewar.music;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;

public class SFX {
	private Clip clip;
	private FloatControl gainControl;
	private float vol;
	
	public SFX(String path) {
		try {
			AudioInputStream sound = AudioSystem.getAudioInputStream(getClass().getResource(path));
			
			DataLine.Info info = new DataLine.Info(Clip.class, sound.getFormat());
			clip = (Clip) AudioSystem.getLine(info);
			clip.open(sound);
			
			gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
			
			sound.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void play() {
		gainControl.setValue(capVolume(-80 * vol));
		
		stop();
		
		clip.setFramePosition(0);
		clip.setMicrosecondPosition(0);
		clip.start();
	}
	
	public void play(float customVol) {
		gainControl.setValue(capVolume(-80 * (vol + customVol)));
	}
	
	public void stop() {
		if(clip.isRunning()) {
			clip.stop();
			clip.setFramePosition(0);
			clip.setMicrosecondPosition(0);
		}
	}
	
	public void setVolume(float vol) { // 0.0f - 1.0f
		this.vol = invert(vol);
	}
	
	public void loop(int count) {
		clip.setFramePosition(0);
		clip.loop(count);
	}
	
	
	private static float capVolume(float vol) {
		if(vol > 0.0f)
			vol = 0.0f;
		if(vol < -80.0f)
			vol = -80.0f;
		
		return vol;
	}
	
	private static float invert(float f) {
		return 1.0f - f;
	}
}
