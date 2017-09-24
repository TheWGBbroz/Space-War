package com.thewgb.spacewar.music;

import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.SourceDataLine;

public class Music implements Runnable {
	private AudioInputStream sound;
	private SourceDataLine sdl;
	private FloatControl gainControl;
	private float customVolume; // -1.0f - 1.0f
	private boolean running = false;
	
	public Music(String path, float customVolume) {
		this.customVolume = invert(customVolume);
		
		try {
			sound = AudioSystem.getAudioInputStream(getClass().getResource(path));
			
			DataLine.Info info = new DataLine.Info(SourceDataLine.class, sound.getFormat());
			sdl = (SourceDataLine) AudioSystem.getLine(info);
			sdl.open(sound.getFormat());
			
			gainControl = (FloatControl) sdl.getControl(FloatControl.Type.MASTER_GAIN);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Music(String path) {
		this(path, 1.0f);
	}
	
	public void play() {
		if(gainControl.getValue() < -78.0f)
			return;
		
		sdl.start();
		
		running = true;
		new Thread(this, "Music-Stream").start();
	}
	
	public void stop() {
		running = false;
		
		if(sdl.isRunning()) {
			sdl.stop();
			
			sdl.drain();
			sdl.close();
			try {
				sound.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void setVolume(float val) { // 0.0f - 1.0f
		gainControl.setValue(capVolume(-80 * (invert(val) + customVolume)));
		
		if(gainControl.getValue() < -78.0f)
			stop();
	}
	
	public boolean isRunning() {
		return running;
	}
	
	public void run() {
		int BUFFER_SIZE = 4096;
		
		byte[] bytesBuffer = new byte[BUFFER_SIZE];
		int bytesRead = -1;
		
		try {
			while((bytesRead = sound.read(bytesBuffer)) != -1) {
				sdl.write(bytesBuffer, 0, bytesRead);
			}
			running = false;
		} catch (IOException e) {
		}
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
