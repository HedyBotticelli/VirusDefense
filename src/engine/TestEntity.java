package engine;

import engine.graphics.Sprite;

public class TestEntity extends RotatableEntity implements Drawable {

	private Sprite sprite;

	public TestEntity(float x, float y, float rotation, String spritePath) {
		super(x, y, rotation);

		this.sprite = new Sprite(spritePath);
	}

	@Override
	public void draw() {
		sprite.draw(this.x, this.y, this.rotation, 1);

	}

	@Override
	public void rotate(float degrees) {
		this.rotation += rotation;

	}

}
