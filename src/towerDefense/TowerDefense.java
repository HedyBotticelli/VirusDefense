package towerDefense;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.lwjgl.openal.AL;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import engine.GameComponent;
import engine.Level;
import engine.SoundHandler;
import engine.TextFileToString;

public class TowerDefense extends BasicGame {

	protected SoundHandler soundHandler = new SoundHandler();
	public static final int MODE_MENU = 0;
	public static final int MODE_GAME = 1;
	public static final int MODE_MAPS = 2;
	public static final int MODE_SETTINGS = 3;
	public static boolean FULLSCREEN = false;
	private static int HEIGHT;
	private static int WIDTH;

	private Gameplay gameplay;
	private Menu menu;
	private ChooseLevel maps;
	private Settings settings;
	private GameComponent currentGameComponent;
	private boolean quitGame = false;

	private int mode;

	public TowerDefense() {
		super("Tower Defense");
	}

	@Override
	public void init(GameContainer container) {
		this.initSounds();
		TowerDefense.updateDimensions(container);
		this.gameplay = new Gameplay(this);
		this.reinitMenu(container);
		this.reinitChooseLevel(container);
		this.settings = new Settings(this, container);
		this.mode = TowerDefense.MODE_MENU;
		this.currentGameComponent = this.menu;
	}

	private void initSounds() {
		this.soundHandler.addWav("press");
		this.soundHandler.add("place", "place.wav");
		this.soundHandler.addWav("bad");
		this.soundHandler.addWav("death");
		this.soundHandler.addWav("spawn");

		this.soundHandler.addWav("explode");
		this.soundHandler.addWav("shotT1");
		this.soundHandler.addWav("shotT2");
	}

	@Override
	public void update(GameContainer container, int delta) throws SlickException {
		if (this.quitGame) {
			container.exit();
			AL.destroy();
		}
		if (this.mode == TowerDefense.MODE_GAME) {
			this.currentGameComponent = this.gameplay;
		} else if (this.mode == TowerDefense.MODE_MAPS) {
			this.currentGameComponent = this.maps;
		} else if (this.mode == TowerDefense.MODE_SETTINGS) {
			if (this.currentGameComponent != this.settings) {
				this.settings.activate(container);
			}
			this.currentGameComponent = this.settings;
		} else if (this.mode == TowerDefense.MODE_MENU) {
			if (this.currentGameComponent != this.menu) {
				// this.menu.activate(container);
			}
			this.currentGameComponent = this.menu;
		}
		this.currentGameComponent.update(container, delta);

	}

	@Override
	public void render(GameContainer container, Graphics graphics) throws SlickException {
		this.currentGameComponent.render(container, graphics);

	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public void quitGame() {
		this.quitGame = true;
	}

	public SoundHandler getSoundHandler() {
		return this.soundHandler;
	}

	public static int getHeight() {
		return TowerDefense.HEIGHT;
	}

	public static int getWidth() {
		return TowerDefense.WIDTH;
	}

	public static void updateDimensions(GameContainer container) {

		TowerDefense.HEIGHT = container.getHeight();
		TowerDefense.WIDTH = container.getWidth();
	}

	public void setLevel(Level level) {
		this.gameplay.setLevel(level);
	}

	public void initGameplay(GameContainer container) {

		try {
			this.gameplay.init(container);
		} catch (SlickException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.currentGameComponent = this.gameplay;
	}

	public Gameplay getGameplay() {
		return this.gameplay;
	}

	public void deactivateSettings() {
		this.settings.deactivate();
	}

	public static boolean isFULLSCREEN() {
		return FULLSCREEN;
	}

	public static void setFULLSCREEN(boolean fULLSCREEN) {
		FULLSCREEN = fULLSCREEN;
	}

	public void reinitMenu(GameContainer container) {

		this.menu = new Menu(this);
		try {
			this.menu.init(container);
		} catch (SlickException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void reinitChooseLevel(GameContainer container) {

		this.maps = new ChooseLevel(this);

	}

	public void deactivateMenu() {
		// this.menu.deactivate();
	}

	public static void writeSettingsToFile() {
		PrintWriter writer;
		try {
			writer = new PrintWriter("./src/data/files/settings.txt", "UTF-8");
			writer.println(TowerDefense.getWidth());
			writer.println(TowerDefense.getHeight());
			if (TowerDefense.isFULLSCREEN()) {
				writer.println(1);
			} else {
				writer.println(0);
			}
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void writeScoreToFile(String name, int score) {
		List<String> savedScores = TextFileToString.getLines("score.txt");
		String[][] scores = new String[savedScores.size() + 1][2];
		for (int i = 0; i < savedScores.size(); ++i) {
			String[] separateStrings = savedScores.get(0).split(", ");
			scores[i][0] = separateStrings[0];
			scores[i][1] = separateStrings[1];
		}

		scores[savedScores.size()][0] = name;
		scores[savedScores.size()][1] = new Integer(score).toString();

		PrintWriter writer;
		try {
			writer = new PrintWriter("./src/data/files/score.txt", "UTF-8");
			writer.println(TowerDefense.getWidth());
			writer.println(TowerDefense.getHeight());
			if (TowerDefense.isFULLSCREEN()) {
				writer.println(1);
			} else {
				writer.println(0);
			}
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
