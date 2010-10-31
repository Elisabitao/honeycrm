package honeycrm.client.view;

import honeycrm.client.basiclayout.TabCenterView;
import honeycrm.client.dto.Dto;
import honeycrm.client.mvp.views.LoadView;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;

/**
 * Checkbox that is responsible for marking special dtos.
 */
public class MarkWidget extends AbstractView {
	public MarkWidget(final Dto viewable) {
		super(viewable.getModule());

		final long id = viewable.getId();
		final CheckBox markBox = new CheckBox();

		markBox.setValue(viewable.getMarked());

		markBox.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				LoadView.get().startLoading();

				// mark dto on client side and do a usual update call on the server side.
				// this assumes the data in the dto is still up to date.
				viewable.set("marked", markBox.getValue());

				updateService.update(viewable, new AsyncCallback<Void>() {
//				commonService.mark(viewable.getModule(), id, markBox.getValue(), new AsyncCallback<Void>() {
					@Override
					public void onFailure(final Throwable caught) {
						displayError(caught);
						LoadView.get().endLoading();
					}

					@Override
					public void onSuccess(final Void result) {
						// tell container that mark has been completed
						TabCenterView.instance().get(moduleDto.getModule()).refreshListView();
						LoadView.get().endLoading();
					}
				});
			}
		});

		initWidget(markBox);
	}
}
