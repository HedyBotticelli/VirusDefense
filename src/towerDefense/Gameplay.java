package towerDefense;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.lwjgl.input.Mouse;
import org.lwjgl.openal.AL;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

import towerDefense.towers.BombTower;
import towerDefense.towers.LongerShootingTower;
import towerDefense.towers.RocketFastTower;
import towerDefense.towers.RocketTower;
import towerDefense.towers.Tower;
import engine.Camera;
import engine.Enemy;
import engine.EnemyTypeHandler;
import engine.GameComponent;
import engine.Level;
import engine.MyVector2f;
import engine.Player;
import engine.Waypoint;
import engine.graphics.Background;
import engine.graphics.SlickRectangle;
import engine.graphics.SlickUnfilledEllipse;
import engine.graphics.SlickUnfilledRectangle;
import engine.graphics.Sprite;
import engine.gui.Clickable;
import engine.gui.GUI;
import engine.gui.InterfaceBackground;
import engine.gui.SlickHealthbar;
import engine.gui.StaticText;
import engine.gui.TowerButton;
import engine.projectiles.Projectile;

/**
 * @author Pavel
 */
public class Gameplay extends GameComponent {
	private static Camera camera;
	private float height, width;
	private ConcurrentLinkedQueue<Enemy> enemies;
	private boolean debugMode;
	private int passedMilliseconds;
	private int mode;
	private Level currentLevel;
	private int currentTileLength;
	private Tower[][] towers;
	private TowerButton towerButton1;
	private TowerButton towerButton2;
	private TowerButton towerButton3;
	private TowerButton towerButton4;
	private Tower currentTower;
	private Player player;
	private StaticText numberLives;
	private StaticText moneyAmount;
	private boolean currentTowerPlaceable;
	private int towerShadowX, towerShadowY;
	protected ConcurrentLinkedQueue<Projectile> projectiles;

	public static float CURRENT_GAME_SCALE;
	public static float MAX_GAME_SCALE;
	public static float GLOBAL_GUI_SCALE = 1f;

	private StaticText passedTime;
	private InterfaceBackground interfaceBackground;
	// Constants:
	public static float INTERFACE_START_X;
	public static int STANDARD_TEXT_SCALE;
	public static int SIZE;
	public static int DEFAULT_SIZE = 64;
	private float speed;

	// Tests:

	//
	public Gameplay(TowerDefense game) {
		super(game);

	}

	@Override
	public void init(GameContainer container) throws SlickException {
		super.init(container);
		this.initDefaults();
		Gameplay.camera = new Camera(0, 0);
		// this.currentMapLayout = new MapLayout("maps/map.png", "veins/bg.png", DEFAULT_SIZE);
		this.currentTileLength = Gameplay.DEFAULT_SIZE;
		this.height = Gameplay.DEFAULT_SIZE * this.getVerticalTiles();
		this.width = Gameplay.DEFAULT_SIZE * this.getHorizontalTiles();

		// Set Constants:

		Gameplay.INTERFACE_START_X = TowerDefense.getWidth() - 3 * 64 * Gameplay.GLOBAL_GUI_SCALE;
		float scale1 = Gameplay.INTERFACE_START_X / this.width;
		float scale2 = TowerDefense.getHeight() / this.height;
		Gameplay.CURRENT_GAME_SCALE = Math.max(scale1, scale2);
		Gameplay.MAX_GAME_SCALE = Gameplay.CURRENT_GAME_SCALE;
		Gameplay.SIZE = (int) (64 * Gameplay.CURRENT_GAME_SCALE);

		//
		this.interfaceBackground = new InterfaceBackground("Interface1.png");

		this.towers = new Tower[this.getVerticalTiles()][this.getHorizontalTiles()];
		this.initWaves();

		// add all objects that need to be drawn to the respectable arrays
		// entities

		this.projectiles = new ConcurrentLinkedQueue<Projectile>();

		// Buttons; this has nothing to do with the draw sequence
		this.towerButton1 = new TowerButton(Gameplay.INTERFACE_START_X, 4 * 64 * Gameplay.GLOBAL_GUI_SCALE, "buttons/PSButton1.png",
				"buttons/PSButton1_click.png", new LongerShootingTower(0, 0, new Sprite("tower/Tower2.png", 0.5f), this, 400, 0.08f,
						400), this);
		this.towerButton2 = new TowerButton(Gameplay.INTERFACE_START_X, 5 * 64 * Gameplay.GLOBAL_GUI_SCALE, "buttons/PSButton1.png",
				"buttons/PSButton1_click.png", new BombTower(0, 0, new Sprite("tower/t1n.png", 0.5f), this, 1500, 15f, 50), this);
		this.towerButton3 = new TowerButton(Gameplay.INTERFACE_START_X, 6 * 64 * Gameplay.GLOBAL_GUI_SCALE, "buttons/PSButton1.png",
				"buttons/PSButton1_click.png", new RocketTower(0, 0, new Sprite("tower/t1.png", 0.5f), this, 200, 15f, 50), this);
		this.towerButton4 = new TowerButton(Gameplay.INTERFACE_START_X + 64 + 32, 4 * 64 * Gameplay.GLOBAL_GUI_SCALE,
				"buttons/PSButton1.png", "buttons/PSButton1_click.png", new RocketFastTower(0, 0, new Sprite(
						"tower/roteBlutk_klein.png", 1f), this, 1000, 20f), this);
		this.clickables.add(this.towerButton1);
		this.clickables.add(this.towerButton2);
		this.clickables.add(this.towerButton3);
		this.clickables.add(this.towerButton4);

		//
		this.initGUI();
		container.setShowFPS(this.debugMode);

	}

	private void initDefaults() {
		this.enemies = new ConcurrentLinkedQueue<Enemy>();
		this.debugMode = false;
		this.passedMilliseconds = 0;
		this.mode = 0;
		this.player = new Player(10, 200);
		STANDARD_TEXT_SCALE = 15;
		this.speed = 1f;
		this.currentTowerPlaceable = true;

	}

	private void initWaves() {

	}

	private void initGUI() {
		float guiTileSize = 64 * Gameplay.GLOBAL_GUI_SCALE;
		float textHeight = 20 * Gameplay.GLOBAL_GUI_SCALE;
		float guiX = 3 * Gameplay.GLOBAL_GUI_SCALE;
		float livesY = 3 * 64 * Gameplay.GLOBAL_GUI_SCALE;
		float moneyY = livesY + textHeight;
		this.numberLives = new StaticText(Gameplay.INTERFACE_START_X + guiTileSize, livesY, Color.white, "" + this.player.getLives());
		this.passedTime = new StaticText(Gameplay.INTERFACE_START_X + guiX, TowerDefense.getHeight() - textHeight, Color.white,
				this.passedTimeToString());
		this.moneyAmount = new StaticText(Gameplay.INTERFACE_START_X + guiTileSize + textHeight, moneyY, Color.white, ""
				+ this.player.getMoney());

		this.guiElements.add(this.interfaceBackground);
		this.guiElements.add(this.numberLives);
		this.guiElements.add(this.towerButton1);
		this.guiElements.add(this.towerButton2);
		this.guiElements.add(this.towerButton3);
		this.guiElements.add(this.towerButton4);
		this.guiElements.add(new StaticText(Gameplay.INTERFACE_START_X + guiX, livesY, Color.white, "Lives:"));
		this.guiElements.add(this.passedTime);
		this.guiElements.add(new StaticText(Gameplay.INTERFACE_START_X + guiX, moneyY, Color.white, "Money:"));
		this.guiElements.add(this.moneyAmount);
	}

	@Override
	public void render(GameContainer container, Graphics graphics) throws SlickException {
		this.drawBackground();
		this.currentLevel.renderPath();
		this.renderEnemies();
		this.renderTowers();

		this.renderTowerShadow(container, graphics);
		this.renderGUI(container, graphics);

		for (Projectile projectiles : this.projectiles) {
			projectiles.draw();
		}

	}

	private void renderGUI(GameContainer container, Graphics graphics) {
		for (GUI guiElement : this.guiElements) {
			guiElement.draw();
		}
		this.renderHealthBars(container, graphics);
		this.renderDebug(container, graphics);

		if (this.mode == 1) {
			new Sprite("You Win.png").draw(0, 0, Gameplay.CURRENT_GAME_SCALE);
		} else if (this.mode == -1) {
			new Sprite("Game Over.png").draw(0, 0, Gameplay.CURRENT_GAME_SCALE);
		}
	}

	private void renderHealthBars(GameContainer container, Graphics graphics) {
		for (Enemy enemy : this.enemies) {
			int barLength = 30;
			int barHeight = 7;
			SlickHealthbar h = new SlickHealthbar(graphics, (enemy.getX() - barLength / 2) * Gameplay.CURRENT_GAME_SCALE
					- Gameplay.getCameraX(), (enemy.getY() - Gameplay.DEFAULT_SIZE / 2) * Gameplay.CURRENT_GAME_SCALE
					- Gameplay.getCameraY(), enemy.getMaxHealth(), barLength, barHeight);
			h.setHealth(enemy.getHealth());
			h.setBordered(true);
			h.draw();
		}
	}

	private void renderTowers() {
		for (Tower[] towerArray : this.towers) {
			for (Tower tower : towerArray) {
				if (tower != null) {
					tower.draw();
				}
			}
		}
	}

	private void renderEnemies() {
		for (Enemy enemy : this.enemies) {
			if (enemy != null)
				enemy.draw();
		}
	}

	/**
	 * renders the transparent version of the tower's sprite when choosing a place
	 * 
	 * @param container
	 * @param graphics
	 */
	private void renderTowerShadow(GameContainer container, Graphics graphics) {

		if (this.currentTower != null && this.getMode() == 0) {
			Sprite sprite = this.currentTower.getSprite().clone();

			if (this.currentTowerPlaceable) {
				new SlickUnfilledRectangle(graphics, SIZE / Gameplay.CURRENT_GAME_SCALE, SIZE / Gameplay.CURRENT_GAME_SCALE,
						Color.green).draw(this.towerShadowX, this.towerShadowY, Gameplay.CURRENT_GAME_SCALE);
			} else {
				new SlickUnfilledRectangle(graphics, SIZE / Gameplay.CURRENT_GAME_SCALE, SIZE / Gameplay.CURRENT_GAME_SCALE, Color.red)
						.draw(this.towerShadowX, this.towerShadowY, Gameplay.CURRENT_GAME_SCALE);
				sprite.setAlpha(0.1f);
				sprite.setColor(1f, 0, 0);

			}

			sprite.draw(this.towerShadowX, this.towerShadowY, Gameplay.CURRENT_GAME_SCALE);
		}
	}

	/**
	 * renders circles for tower radius and enemy radius and black box for FPS
	 * 
	 * @param container
	 * @param graphics
	 */
	private void renderDebug(GameContainer container, Graphics graphics) {
		if (this.debugMode) {
			for (Enemy enemy : this.enemies) {
				new SlickUnfilledEllipse(graphics, enemy.getRadius() * 2, enemy.getRadius() * 2, Color.blue).draw((enemy.getX())
						* Gameplay.CURRENT_GAME_SCALE - Gameplay.getCameraX(),
						(enemy.getY()) * Gameplay.CURRENT_GAME_SCALE - Gameplay.getCameraY(), Gameplay.CURRENT_GAME_SCALE);
			}
			for (int i = 0; i < this.towers.length; ++i) {
				for (int j = 0; j < this.towers[0].length; ++j) {
					if (this.towers[i][j] != null) {
						Tower currentTower = this.towers[i][j];
						new SlickUnfilledEllipse(graphics, currentTower.getRadius() * 2, currentTower.getRadius() * 2, Color.red)
								.draw((currentTower.getX() * this.currentTileLength + Gameplay.DEFAULT_SIZE / 2)
										* Gameplay.CURRENT_GAME_SCALE - Gameplay.getCameraX(), (currentTower.getY()
										* this.currentTileLength + DEFAULT_SIZE / 2)
										* Gameplay.CURRENT_GAME_SCALE - Gameplay.getCameraY(), Gameplay.CURRENT_GAME_SCALE);
					}
				}
			}
			// create a black box that the FPS are visible
			new SlickRectangle(graphics, 100, 20, Color.black).draw(5, 10, 1f);
		}
	}

	@Override
	public void update(GameContainer container, int originalDelta) throws SlickException {

		if (originalDelta < 100) {
			this.passedMilliseconds += originalDelta;
			this.passedTime.setText(this.passedTimeToString());
			this.moneyAmount.setText("" + this.player.getMoney());
			int delta = (int) (originalDelta * this.speed);
			if (this.mode == 0) {
				for (Enemy enemy : this.enemies) {

					if (enemy != null)
						enemy.update(delta);
				}
				for (int i = 0; i < this.towers.length; ++i) {
					for (int j = 0; j < this.towers[0].length; ++j) {
						if (this.towers[i][j] != null) {
							this.towers[i][j].update(delta);
						}
					}
				}

				this.currentLevel.getWaveHandler().update(delta);
			}
			this.mouseEvents(container, delta);
			this.keyboardEvents(container, delta);

			if (this.player.getLives() <= 0) {
				this.mode = -1;

			}
			this.updateTowerShadow(container);

			for (Projectile projectiles : this.projectiles) {
				projectiles.update(delta);
			}

		}
	}

	private void updateTowerShadow(GameContainer container) {
		if (this.currentTower != null && this.getMode() == 0) {
			Input input = container.getInput();
			// old version of shadow Coordinates, with pixel accurate coordinates
			// this.towerShadowX = (int) (input.getMouseX() - this.currentTower.getSprite().getWidth() / 2);
			// this.towerShadowY = (int) (input.getMouseY() - this.currentTower.getSprite().getHeight() / 2);
			int x = input.getMouseX() + Gameplay.getCameraX();
			int y = input.getMouseY() + Gameplay.getCameraY();
			int newX = (x) / Gameplay.SIZE;
			int newY = (y) / Gameplay.SIZE;
			this.towerShadowX = (int) (newX * Gameplay.SIZE - Gameplay.getCameraX());
			this.towerShadowY = (int) (newY * Gameplay.SIZE - Gameplay.getCameraY());
			int[][] path = this.currentLevel.getPath();
			if (this.player.getMoney() < this.currentTower.getCost()) {
				this.currentTowerPlaceable = false;
			} else if (newX >= 0 && newY >= 0 && newY < this.getVerticalTiles() && newX < this.getHorizontalTiles()
					&& path[newY][newX] == 1 && this.towers[newY][newX] == null) {
				this.currentTowerPlaceable = true;
			} else {
				this.currentTowerPlaceable = false;
			}
		}
	}

	/**
	 * updates keyboard events i.e. button presses
	 * 
	 * @param container
	 * @param delta
	 */
	private void keyboardEvents(GameContainer container, int delta) {
		Input input = container.getInput();

		if (input.isKeyPressed(Input.KEY_I)) {
			this.debugMode = !this.debugMode;
			container.setShowFPS(this.debugMode);
			if (this.debugMode) {
				System.out.println("debug");
				this.player.setMoney(100000);
			} else {
				System.out.println("not debug");
				this.speed = 1f;
			}
		}
		if (input.isKeyPressed(Input.KEY_ESCAPE)) {
			container.exit();
		}
		if (input.isKeyPressed(Input.KEY_S)) {
			try {
				new Sound("data/sound/Blip_Select.wav").play();
			} catch (SlickException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (input.isKeyPressed(Input.KEY_R)) {

			AL.destroy();
			try {
				container.reinit();
			} catch (SlickException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		int mouseWheel = Mouse.getDWheel();
		if (mouseWheel > 0) { // mouse wheel up
			Gameplay.CURRENT_GAME_SCALE += .1f;
			if (Gameplay.CURRENT_GAME_SCALE > 6) {
				Gameplay.CURRENT_GAME_SCALE = 6f;
			}
			Gameplay.SIZE = (int) (Gameplay.DEFAULT_SIZE * Gameplay.CURRENT_GAME_SCALE);
		} else if (mouseWheel < 0) {// mouse wheel down
			Gameplay.CURRENT_GAME_SCALE -= .1f;
			if (Gameplay.CURRENT_GAME_SCALE < Gameplay.MAX_GAME_SCALE) {
				Gameplay.CURRENT_GAME_SCALE = Gameplay.MAX_GAME_SCALE;
			}
			Gameplay.SIZE = (int) (Gameplay.DEFAULT_SIZE * Gameplay.CURRENT_GAME_SCALE);
		}
		float cameraWidth = Gameplay.INTERFACE_START_X;
		float cameraHeight = TowerDefense.getHeight();
		float scrollSpeed = 0.5f;
		float scrollDistance = scrollSpeed * delta;
		if (input.isKeyDown(Input.KEY_LEFT)) {
			Gameplay.camera.addX(-scrollDistance);
			if (Gameplay.getCameraX() - scrollDistance < 0) {
				Gameplay.camera.setX(0);
			}
		}

		if (input.isKeyDown(Input.KEY_RIGHT)) {
			Gameplay.camera.addX(+scrollDistance);
			if ((Gameplay.getCameraX() + cameraWidth) / Gameplay.CURRENT_GAME_SCALE > this.getHorizontalTiles()
					* Gameplay.DEFAULT_SIZE) {
				Gameplay.camera.setX((this.getHorizontalTiles() * Gameplay.DEFAULT_SIZE)* Gameplay.CURRENT_GAME_SCALE - cameraWidth);

			}
		}
		if (input.isKeyDown(Input.KEY_UP)) {
			Gameplay.camera.addY(-scrollDistance);
			if (Gameplay.getCameraY() - scrollDistance < 0) {
				Gameplay.camera.setY(0);
			}
		}
		if (input.isKeyDown(Input.KEY_DOWN)) {
			Gameplay.camera.addY(+scrollDistance);
			if ((Gameplay.getCameraY() + cameraHeight) / Gameplay.CURRENT_GAME_SCALE > this.getVerticalTiles()
					* Gameplay.DEFAULT_SIZE) {
				Gameplay.camera.setY((this.getVerticalTiles() * Gameplay.DEFAULT_SIZE)* Gameplay.CURRENT_GAME_SCALE - cameraHeight);

			}
		}
		if (this.debugMode) {
			this.debugKeyboardEvents(container, delta);
		}

	}

	/**
	 * updates the keyboard events(button presse) only occuring in debug mode
	 * 
	 * @param container
	 * @param delta
	 */
	private void debugKeyboardEvents(GameContainer container, int delta) {
		Input input = container.getInput();
		if (input.isKeyPressed(Input.KEY_ADD)) {
			this.speed *= 2f;
		}
		if (input.isKeyPressed(Input.KEY_SUBTRACT)) {
			this.speed /= 2f;
		}
		if (input.isKeyPressed(Input.KEY_L)) {
			this.enemies.add(this.getEnemyTypes().createEnemy(0));
		}
	}

	/**
	 * handles mouse events like clicks
	 * 
	 * @param container
	 * @param delta
	 */
	private void mouseEvents(GameContainer container, int delta) {
		if (this.mode == 0) {
			Input input = container.getInput();

			float x = input.getMouseX();
			float y = input.getMouseY();
			if (input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
				this.placeTower(input);
				for (Clickable clickable : this.clickables) {
					clickable.update(x, y, container);
				}

			} else if (input.isMousePressed(Input.MOUSE_RIGHT_BUTTON)) {
				this.releaseAllClickables();

			}
		}
	}

	private void placeTower(Input input) {
		float x = input.getMouseX() + Gameplay.getCameraX();
		float y = input.getMouseY() + Gameplay.getCameraY();
		int newX = (int) x / Gameplay.SIZE;
		int newY = (int) y / Gameplay.SIZE;

		if (this.currentTower != null) {
			int[][] path = this.currentLevel.getPath();
			int cost = this.currentTower.getCost();
			if (this.currentTowerPlaceable) {
				Tower bufferTower = this.currentTower.clone();
				bufferTower.setX(newX);
				bufferTower.setY(newY);
				bufferTower.getSprite().setAlpha(1f);
				this.towers[newY][newX] = bufferTower;
				this.player.reduceMoney(cost);
				this.game.getSoundHandler().play("place");

			} else {
				boolean mouseCollidesButton = false;
				for (Clickable clickable : this.clickables) {
					if (clickable.collides((int) x, (int) y, Gameplay.GLOBAL_GUI_SCALE)) {
						mouseCollidesButton = true;
					}
				}
				if (!mouseCollidesButton) {
					this.game.getSoundHandler().play("bad");
				}
			}

		}
	}

	private void releaseAllClickables() {
		for (Clickable clickable : this.clickables) {
			clickable.onRelease();
		}
	}

	public ConcurrentLinkedQueue<Enemy> getEnemies() {
		return this.enemies;
	}

	public Waypoint getWaypoints() {
		return this.currentLevel.getWaypoints();
	}

	/**
	 * @return returns a list of coordinates belonging to the waypoints
	 */
	public List<MyVector2f> getWaypointsGrid() {
		List<MyVector2f> coordinates = new ArrayList<MyVector2f>();
		for (Waypoint waypoint = this.getWaypoints(); waypoint != null; waypoint = waypoint.getNextWaypoint()) {
			coordinates.add(new MyVector2f(waypoint.getX(), waypoint.getY()));
		}
		return coordinates;

	}

	public void reduceLives() {
		this.player.reduceLives();
		this.numberLives.setText("" + this.player.getLives());
	}

	public EnemyTypeHandler getEnemyTypes() {
		return this.currentLevel.getEnemyTypes();
	}

	private String millisecondsToTimeString(int milliseconds) {
		int seconds = milliseconds / 1000;
		int minutes = seconds / 60;
		int hours = minutes / 60;
		seconds %= 60;
		minutes %= 60;
		String secondsString = this.makeTwoDecimals(seconds);
		String minutesString = this.makeTwoDecimals(minutes);
		String hoursString = this.makeTwoDecimals(hours);

		if (hours > 0) {
			return hoursString + ":" + minutesString + ":" + secondsString;
		} else {
			return minutesString + ":" + secondsString;
		}
	}

	private String makeTwoDecimals(int number) {
		if (number >= 10) {
			return "" + number;
		} else {
			return "0" + number;
		}
	}

	private String passedTimeToString() {
		return this.millisecondsToTimeString(this.passedMilliseconds);
	}

	public Player getPlayer() {
		return this.player;
	}

	public int getMode() {
		return this.mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public Tower getCurrentTower() {
		return this.currentTower;
	}

	public int getHorizontalTiles() {
		return this.currentLevel.getNumberTilesWidth();
	}

	public void drawBackground() {
		this.getMapBackground().draw();
	}

	public Background getMapBackground() {
		return this.currentLevel.getMapBackground();
	}

	public int getVerticalTiles() {
		return this.currentLevel.getNumberTilesHeight();
	}

	public void setCurrentTower(Tower currentTower) {
		this.currentTower = currentTower;
	}

	public ConcurrentLinkedQueue<Projectile> getProjectiles() {
		return this.projectiles;
	}

	public void setLevel(Level level) {
		this.currentLevel = level;
	}

	public boolean currentTowerPlaceable() {
		return this.currentTowerPlaceable;
	}

	public static int getCameraX() {
		return (int) Gameplay.camera.getX();
	}

	public static int getCameraY() {
		return (int) Gameplay.camera.getY();
	}
}
