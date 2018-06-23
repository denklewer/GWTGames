package games.client.util;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;

public class CorrectStateDialog extends DialogBox {
	
	public CorrectStateDialog(final ScheduledCommand command) {
		setText("There were some problems, so our server corrected the game state!");
		setAnimationEnabled(true);
		setGlassEnabled(true);
		final Button ok = new Button("OK");
		ok.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				command.execute();
				CorrectStateDialog.this.hide();
			}
		});
		setWidget(ok);
	}
}
