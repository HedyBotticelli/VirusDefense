package engine.gui;

import java.util.HashMap;
import java.util.Map;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

public class SoundHandler {
	private Map<String, Sound> sounds = new HashMap<String, Sound>();

	public void add(String name, String location) {
		try {
			this.sounds.put(name, new Sound(location));
		} catch (SlickException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void play(String name) {
		if (this.sounds.containsKey(name)) {
			this.sounds.get(name).play();
		}
	}

}
