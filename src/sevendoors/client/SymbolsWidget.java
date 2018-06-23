package sevendoors.client;

import games.client.sprites.ImageEngine;
import games.client.sprites.Rectangle;
import games.client.sprites.Sprite;
import games.client.sprites.SpriteGraphics;
import games.client.sprites.SpriteImage;
import games.client.util.DrawElement;
import games.client.util.ImageFactory;
import games.client.util.UIUtils;
import games.shared.Resources;
import sevendoors.shared.SevenDoorsProtocol;

public class SymbolsWidget extends Sprite<DrawElement> {

	private static SpriteImage[][] _symbolsImage = new SpriteImage[2][]; 
	
	private int _currentTrans;
	private int[][] _secret;

	public SymbolsWidget() {
		super(50, 40, 496, 496);
		
		final DrawElement stripe = Resources.get(ImageFactory.class).getDrawElement("bonus-secret-symbol-01");
		_symbolsImage[0] = UIUtils.cutSpriteVertically(stripe, 62, 186, 3);
		_symbolsImage[1] = UIUtils.cutSpriteVertically(UIUtils.addTransparency(stripe, 0.9), 62, 186, 3);
		
		setImageEngine(new ImageEngine<DrawElement>() {
			
			private int _ticks;

			@Override
			public boolean updateTick(final long timestamp) {
				_ticks++;
				if (_ticks % 10 == 0) {
					_currentTrans = (_currentTrans + 1) % 2;
					markDirty();
					return true;
				}
				return false;
			}
			
			@Override
			public <Z> void draw(final SpriteGraphics<Z> graphics, final SpriteImage<Z> image,
					final int x, final int y, final Rectangle dirtyRegion) {
				if (_secret != null) {
					for (int i = 0; i < SevenDoorsProtocol.BOARD_SIZE; i++) {
						for (int j = 0; j < SevenDoorsProtocol.BOARD_SIZE; j++) {
							final int cell = _secret[i][j];
							if (cell != 0 && cell < 4) {
								_symbolsImage[_currentTrans][cell - 1].draw(graphics, x + i * TileWidget.TILE_SIZE, y + j * TileWidget.TILE_SIZE, dirtyRegion);
							}
						}
					}
				}
			}
		});
		
		setVisible(false);
	}

	public void setSecret(final int[][] secret) {
		_secret = secret;
	}

}
