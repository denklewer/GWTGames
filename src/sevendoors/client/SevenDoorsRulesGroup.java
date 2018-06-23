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

public class SevenDoorsRulesGroup extends DecoratedPopupPanel {
	
	private final SevenDoorsView _parent;
	private VerticalPanel _panel;

	public SevenDoorsRulesGroup(SevenDoorsView parent) {
		super(false);
		_parent = parent;
		setAnimationEnabled(true);
		setStyleName("rulesPanel");
		addPanel();
		addBackButton();
		
		Window.addResizeHandler(new ResizeHandler() {
			
			@Override
			public void onResize(ResizeEvent event) {
				moveDialog();
			}
		});
	}
	
	private void addPanel() {
		_panel = new VerticalPanel();
		_panel.setSpacing(10);
		_panel.addStyleName("rulesVerticalPanel");
		add(_panel);
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
		_panel.add(backButton);
		
	}
	
	public void showRules(){
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
