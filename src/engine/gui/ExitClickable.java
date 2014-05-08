package engine.gui;

import towerDefense.TowerDefense;

public class ExitClickable extends ClickableText {
	private TowerDefense towerDefense;

	public ExitClickable(float x, float y, TowerDefense towerDefense) {
		super(x, y, "Exit Game");
		this.towerDefense = towerDefense;
	}

	@Override
	public void onRelease() {
		super.onRelease();
		this.towerDefense.quitGame();
		System.out.println("act2");
	}
}
