package scrabble.client;

import games.client.sprites.Sprite;
import games.client.sprites.SpriteGroup;
import games.client.sprites.SpriteImage;
import games.client.util.DrawElement;
import games.client.util.ImageFactory;
import games.shared.Resources;

import java.util.HashMap;
import java.util.Map;

import scrabble.shared.ScrabbleProtocol;

public class PlayersWidget extends SpriteGroup<DrawElement> {
	
	private static SpriteImage _playersImage;
	private Map<Byte, PlayerWidget> _playersWidgets = new HashMap<Byte, PlayerWidget>();
	
	private static final int SHIFT_X = 517;
	private static final int SHIFT_Y = 55;

	public PlayersWidget() {
	}

	public void init() {
		if(_playersImage == null){
			_playersImage = new SpriteImage(Resources.get(ImageFactory.class).getDrawElement("players-01"), 82, 24);
		}
		final Sprite<DrawElement> playersImage = new Sprite<DrawElement>(_playersImage, SHIFT_X, SHIFT_Y, _playersImage.width(), _playersImage.height());
		addSprite(playersImage);
	}
	
	private void doLayout() {
		for (byte i = 0; i < ScrabbleProtocol._MAX_OF_GAMERS; i++) {
			final PlayerWidget playerWidget = _playersWidgets.get(i);
			if(playerWidget != null){
				playerWidget.moveTo(SHIFT_X, SHIFT_Y + _playersImage.height() + i * PlayerWidget.getPlayerWidgetHeight());
			}
		}
	}
	
	public void addPlayer(final byte side, final String name, final int score) {
		final PlayerWidget playerWidget = new PlayerWidget(0, 0, name);
		playerWidget.addScore(score);
		_playersWidgets.put(new Byte(side), playerWidget);
		addSprite(playerWidget);
		doLayout();
	}

	public void switchTurn(final byte side) {
		for (final PlayerWidget widget : _playersWidgets.values()) {
			widget.setLight(false);
		}
		_playersWidgets.get(new Byte(side)).setLight(true);		
	}

	public void scoreChanged(final int score, final byte side) {
		final PlayerWidget playerWidget = _playersWidgets.get(new Byte(side));
		if (playerWidget != null) {
			playerWidget.addScore(score);
		}
	}
}
