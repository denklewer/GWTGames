package sevendoors.client;

import games.client.sprites.Sprite;
import games.client.sprites.SpriteImage;
import games.client.util.DrawElement;
import games.client.util.ImageFactory;
import games.client.util.UIUtils;
import games.shared.Resources;

public class KeysWidget extends Sprite<DrawElement> {

	private static SpriteImage[][] _keys = {
		UIUtils.cutSpriteVertically(Resources.get(ImageFactory.class).getDrawElement("id-key-01"), 72, 584, 8),
		UIUtils.cutSpriteVertically(Resources.get(ImageFactory.class).getDrawElement("id-key-02"), 72, 584, 8),
		UIUtils.cutSpriteVertically(Resources.get(ImageFactory.class).getDrawElement("id-key-03"), 72, 584, 8),
		UIUtils.cutSpriteVertically(Resources.get(ImageFactory.class).getDrawElement("id-key-04"), 72, 584, 8),
		UIUtils.cutSpriteVertically(Resources.get(ImageFactory.class).getDrawElement("id-key-05"), 72, 584, 8),
		UIUtils.cutSpriteVertically(Resources.get(ImageFactory.class).getDrawElement("id-key-06"), 72, 584, 8),
		UIUtils.cutSpriteVertically(Resources.get(ImageFactory.class).getDrawElement("id-key-07"), 72, 584, 8)
	};
	
	private int _num;
	
	public KeysWidget(final int num, final int x, final int y) {
		super(_keys[num][0], x, y, _keys[0][0].width(), _keys[0][0].height());
		_num = num;
	}

	public SpriteImage getImage(final int keyNum) {
		return _keys[_num][keyNum+1];
	}

	public int getNum() {
		return _num;
	}

}
