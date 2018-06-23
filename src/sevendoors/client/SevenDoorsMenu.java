package sevendoors.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CustomButton;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SevenDoorsMenu extends DecoratedPopupPanel {

	private static final int GAP = 20;
	private final SevenDoorsView _parent;
	private CustomButton _resume;
	private boolean _haveSavedGame;
	private TopScoreDialog _topScoreDialog;

	private VerticalPanel _panel;

	public SevenDoorsMenu(SevenDoorsView parent) {
		super(false);
		_parent = parent;
		setAnimationEnabled(true);
		setStyleName("menuPanel");
		addPanel();

		Window.addResizeHandler(new ResizeHandler() {

			@Override
			public void onResize(ResizeEvent event) {
				moveDialog();
			}
		});
	}

	private void addPanel() {
		_panel = new VerticalPanel();
		_panel.setSpacing(GAP);
		_panel.addStyleName("menuMainPanel");
		add(_panel);
	}

	public void initMenu(boolean haveSavedGame){
		_haveSavedGame = haveSavedGame;
		addResumeButton();
		addNewGameButton();
		addTopScoreButton();
		addRulesButton();
		addTopScoreWidget();
		_resume.setVisible(_haveSavedGame);
	}

	private void addTopScoreWidget() {
		_topScoreDialog = new TopScoreDialog(_parent);
	}

	private void addResumeButton() {
		String url = "images/sevendoors/menu/resume_button.png";
		Image upImage = new Image(url, 0, 0, 155, 25);
		Image downImage = new Image(url, 0, 50, 155, 25);
		_resume = new PushButton(upImage, downImage, new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				_parent.resumeGame();
				_parent.showGame();
				hide();				
			}
		});
		_resume.setPixelSize(155, 25);
		_resume.setStyleName("backButton");
		_resume.setVisible(false);
		_panel.add(_resume);
	}

	private void addTopScoreButton() {
		String url = "images/sevendoors/menu/topscore.png";
		Image upImage = new Image(url, 0, 0, 120, 25);
		Image downImage = new Image(url, 0, 50, 120, 25);
		CustomButton button = new PushButton(upImage, downImage, new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				_topScoreDialog.showTopScore(_parent.getTopScore());
				hide();				
			}
		});
		button.setPixelSize(120, 25);
		button.setStyleName("backButton");
		_panel.add(button);
	}

	private void addRulesButton() {
		String url = "images/sevendoors/menu/rules_button.png";
		Image upImage = new Image(url, 0, 0, 73, 25);
		Image downImage = new Image(url, 0, 50, 73, 25);
		CustomButton button = new PushButton(upImage, downImage, new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();				
				_parent.showRules();
			}
		});
		button.setPixelSize(73, 25);
		button.setStyleName("backButton");
		_panel.add(button);
	}

	private void addNewGameButton() {
		
		String url = "images/sevendoors/menu/newgame.png";
		Image upImage = new Image(url, 0, 0, 120, 25);
		Image downImage = new Image(url, 0, 50, 120, 25);
		CustomButton button = new PushButton(upImage, downImage, new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				_parent.startNewGame();
				_parent.showGame();
				hide();				
			}
		});
		button.setPixelSize(120, 25);
		button.setStyleName("backButton");
		_panel.add(button);
	}

	public void showMenu() {
		_resume.setVisible(_parent.hasSavedGame());
		show();
		setPopupPositionAndShow(new PositionCallback() {
			
			@Override
			public void setPosition(int offsetWidth, int offsetHeight) {
				moveDialog();
			}
		});
	}
	
	private void moveDialog(){
		if(isShowing()){
			int x = _parent.getGameX();
			int y = _parent.getGameY();
			setPopupPosition(x, y);
		}
	}

}
