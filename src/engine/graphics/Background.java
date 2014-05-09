package engine.graphics;

import towerDefense.TowerDefense;
import engine.Drawable;

public class Background implements Drawable {
	protected Sprite picture;

	public Background(float scale, String backgroundPath) {
		this.picture = new Sprite(backgroundPath, scale);
	}

	@Override
	public void draw() {
		this.picture.draw(0, 0, TowerDefense.GLOBAL_GAME_SCALE);

	}

}
