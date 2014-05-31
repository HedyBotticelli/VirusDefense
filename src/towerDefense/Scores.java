package towerDefense;

import engine.GameComponent;
import engine.gui.GoToMenuButton;

public class Scores extends GameComponent {

	public Scores(TowerDefense game) {
		super(game);

		GoToMenuButton back = new GoToMenuButton(0, TowerDefense.getHeight() - 20, "Back", this.game);
		this.clickables.add(back);
		this.guiElements.add(back);

	}

}