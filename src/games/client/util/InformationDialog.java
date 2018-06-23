package games.client.util;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

public class InformationDialog  extends DialogBox {

	public InformationDialog(final String title, HTML message) {
		this(title, message, null);
	}
	
	public InformationDialog(final String title, HTML message, final Runnable action) {
		super(false);
		final VerticalPanel boxContents = new VerticalPanel();
		setText(title);
		// Enable animation.
		setAnimationEnabled(true);
		// Enable glass background.
		setGlassEnabled(true);
		final Button ok = new Button("OK");
		ok.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				if(action != null){
					action.run();
				}
				InformationDialog.this.hide();
			}
		});
	    boxContents.add(message);
	    ok.setStyleName("button-center");
	    addExtraComponents(boxContents);
	    boxContents.add(ok);
	    
	    setWidget(boxContents);

	    center();
	}

	protected void addExtraComponents(VerticalPanel boxContents) {
	}
	
}