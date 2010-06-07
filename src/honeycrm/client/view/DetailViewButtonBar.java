package honeycrm.client.view;

import honeycrm.client.IANA;
import honeycrm.client.LoadIndicator;
import honeycrm.client.TabCenterView;
import honeycrm.client.dto.AbstractDto;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;


public class DetailViewButtonBar extends AbstractView {
	private Button demoBtn = new Button("Demo");
	private Button createBtn = new Button("Create");
	private Button saveBtn = new Button("Save");
	private Button cancelBtn = new Button("Cancel");
	private Button editBtn = new Button("Edit");
	private Button deleteBtn = new Button("Delete");

	private final DetailView detailview;

	public DetailViewButtonBar(final Class<? extends AbstractDto> clazz, final DetailView detailview) {
		super(clazz);

		this.detailview = detailview;

		final HorizontalPanel panel = new HorizontalPanel();
		panel.add(demoBtn);
		panel.add(createBtn);
		panel.add(saveBtn);
		panel.add(cancelBtn);
		panel.add(editBtn);
		panel.add(deleteBtn);

		createBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				startCreating();
			}
		});

		saveBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				detailview.saveChanges();
				// startViewing();
			}
		});

		cancelBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				startViewing();
			}
		});

		editBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				startEditing();
			}
		});

		deleteBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// TODO should close detailview after delete..
				detailview.delete();
				startViewing();
			}
		});

		demoBtn.addClickHandler(getDemoButtonClickHandler(clazz));

		stopViewing();

		initWidget(panel);
	}

	private ClickHandler getDemoButtonClickHandler(final Class<? extends AbstractDto> clazz) {
		return new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				LoadIndicator.get().startLoading();

				commonService.addDemo(IANA.mashal(clazz), new AsyncCallback<Void>() {
					@Override
					public void onSuccess(Void result) {
						TabCenterView.instance().get(clazz).refreshListView();
						LoadIndicator.get().endLoading();
					}

					@Override
					public void onFailure(Throwable caught) {
						LoadIndicator.get().endLoading();
						Window.alert("Could not create demo contact");
					}
				});
			}
		};
	}

	/**
	 * displaying detail information for an entity
	 */
	public void startViewing() {
		detailview.view();

		demoBtn.setVisible(true);
		createBtn.setVisible(true);
		// only show if detailview is displaying a valid item
		editBtn.setVisible(detailview.isShowing()); 
		deleteBtn.setVisible(detailview.isShowing());

		saveBtn.setVisible(false);
		cancelBtn.setVisible(false);
	}

	/**
	 * display all input fields for creating a new entity
	 */
	public void startCreating() {
		// if (detailview.isShowing()) {
		detailview.stopViewing();
		detailview.startCreating();

		saveBtn.setVisible(true);
		cancelBtn.setVisible(true);

		createBtn.setVisible(false);
		demoBtn.setVisible(false);
		editBtn.setVisible(false);
		deleteBtn.setVisible(false);
		// }
	}

	public void startEditing() {
		if (detailview.isShowing()) {
			detailview.startEditing();

			saveBtn.setVisible(true);
			cancelBtn.setVisible(true);

			editBtn.setVisible(false);
			deleteBtn.setVisible(false);
			demoBtn.setVisible(false);
			createBtn.setVisible(false);
		}
	}

	public void stopViewing() {
		createBtn.setVisible(true);
		demoBtn.setVisible(true);

		saveBtn.setVisible(false);
		cancelBtn.setVisible(false);
		editBtn.setVisible(false);
		deleteBtn.setVisible(false);
	}
}