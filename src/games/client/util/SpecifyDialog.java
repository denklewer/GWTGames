package games.client.util;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SpecifyDialog extends DialogBox {
	
	private Runnable _cancelAction;
	
	public SpecifyDialog(String message, int width, int height, final Runnable action) {
		this("Are you sure?", message, width, height, action);
	}
	
	public SpecifyDialog(String title, String message, int width, int height, final Runnable action) {
		super(false);
		final VerticalPanel boxContents = new VerticalPanel();
		setText(title);
		setAnimationEnabled(true);
		setGlassEnabled(true);
		setWidget(boxContents);
		Label text = new Label(message);
		boxContents.add(text);
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		Button okButton = addOkButton(action, horizontalPanel);
		okButton.setWidth(width/2 + "px");
		Button cancelButton = addCancelButton(horizontalPanel);
		cancelButton.setWidth(width/2 + "px");
		boxContents.add(horizontalPanel);
		boxContents.setSize(width+"px", height+"px");
		center();
	}

	private Button addCancelButton(HorizontalPanel horizontalPanel) {
		Button cancel = new Button("cancel");
		cancel.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(_cancelAction != null){
					_cancelAction.run();
				}
				SpecifyDialog.this.hide();
			}
		});
		cancel.setStyleName("button-center");
		horizontalPanel.add(cancel);
		return cancel;
	}

	private Button addOkButton(final Runnable action,
			HorizontalPanel horizontalPanel) {
		Button ok = new Button("ok");
		ok.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(action != null){
					action.run();
				}
				SpecifyDialog.this.hide();
			}
		});
		ok.setStyleName("button-center");
		horizontalPanel.add(ok);
		return ok;
	}

	public void setCancelAction(Runnable cancelAction) {
		_cancelAction = cancelAction;
	}

}
