package sevendoors.client;

import java.util.ArrayList;
import java.util.List;

import sevendoors.client.GameStorage.TopScore;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CustomButton;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.VerticalPanel;

public class TopScoreDialog extends DecoratedPopupPanel {

	private static final int TOP_COUNT = 10;
	private final SevenDoorsView _parent;

	private VerticalPanel _topScorePanel;
	private List<Label> _scores = new ArrayList<Label>();
	private List<Label> _names = new ArrayList<Label>();

	public TopScoreDialog(SevenDoorsView parent) {
		super(false);
		_parent = parent;
		setAnimationEnabled(true);
		setStyleName("topscorePanel");
		addScorePanel();
		addLabels();
		addBackButton();
		Window.addResizeHandler(new ResizeHandler() {

			@Override
			public void onResize(ResizeEvent event) {
				moveDialog();
			}
		});
	}

	public void addScorePanel() {
		_topScorePanel = new VerticalPanel();
		_topScorePanel.setSpacing(10);
		_topScorePanel.addStyleName("topscoreVerticalPanel");
		add(_topScorePanel);
	}

	public void addLabels() {
		Label scoreLabel = new Label("TOP SCORE");
		scoreLabel.addStyleName("topscoreLabel");
		scoreLabel.addStyleName("centerText");
		scoreLabel.setWidth(400 + "px");
		_topScorePanel.add(scoreLabel);
		for (int i = 0; i < TOP_COUNT; i++) {
			addLabel((i + 1) + ". ", 250, "-", 150);
		}
	}

	public void addBackButton() {
		String url = "images/sevendoors/menu/back_button.png";
		Image upImage = new Image(url, 0, 0, 65, 25);
		Image downImage = new Image(url, 0, 50, 65, 25);
		CustomButton backButton = new PushButton(upImage, downImage, new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();			
				_parent.showMenu();
			}
		});
		backButton.setPixelSize(65, 25);
		backButton.setStyleName("backButton");
		_topScorePanel.add(backButton);
	}

	private void addLabel(String name, int nameMaxWidth, String score, int scoreMaxWidth){
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		Label nameLabel = new Label(name);
		nameLabel.addStyleName("topscoreLabel");
		nameLabel.setWidth(nameMaxWidth + "px");
		horizontalPanel.add(nameLabel);
		_names.add(nameLabel);
		
		Label scoreLabel = new Label(score);
		scoreLabel.addStyleName("topscoreLabel");
		scoreLabel.addStyleName("rightText");
		scoreLabel.setWidth(scoreMaxWidth + "px");
		horizontalPanel.add(scoreLabel);
		_scores.add(scoreLabel);
		
		_topScorePanel.add(horizontalPanel);
	}

	public void showTopScore(List<TopScore> topScore){
		for (int i = 0; i < topScore.size(); i++) {
			 DateTimeFormat formatter = DateTimeFormat.getFormat("MM/dd/yy HH:mm");
			_names.get(i).setText((i + 1) + ". " + formatter.format(topScore.get(i).getDate()));
			_scores.get(i).setText(topScore.get(i).getScore() + "");
		}
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
