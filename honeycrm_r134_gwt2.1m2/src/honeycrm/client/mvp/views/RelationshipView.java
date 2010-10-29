package honeycrm.client.mvp.views;

import java.util.HashMap;
import java.util.HashSet;

import honeycrm.client.dto.Dto;
import honeycrm.client.dto.DtoModuleRegistry;
import honeycrm.client.dto.ListQueryResult;
import honeycrm.client.mvp.presenters.SingleRelationshipPresenter.Display;
import honeycrm.client.view.list.ListViewDataProvider;
import honeycrm.client.view.relationship.RelationshipListViewDataProvider;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.view.client.HasData;

public class RelationshipView extends ListView implements Display {
	private String relatedDtoClass;
	private final RelationshipListViewDataProvider provider;

	public RelationshipView(final String originatingDto, final String relatedDto) {
		super(originatingDto);

		setDisclose(true);
		setShowTitle(true);
		setPageSize(5);

		this.relatedDtoClass = relatedDto;
		this.provider = new RelationshipListViewDataProvider(moduleDto.getModule(), relatedDtoClass);

		/*
		 * only add the create button if this relationship has is represented by a single id field e.g. no create button should be displayed in the contact <-> contact relationship because there are three different fields in each contact referencing other contacts. just clicking the create button it is not clear which of the fields should be pre-filled.
		 */
		if (hasRelationshipUniqueFieldName()) {
			setAdditionalButtons(getInitialCreateBtn());
		}
	}

	private boolean hasRelationshipUniqueFieldName() {
		final HashMap<String, HashMap<String, HashSet<String>>> r = DtoModuleRegistry.instance().getRelationships();
		// assume the relationship exists.
		return 1 == r.get(moduleDto.getModule()).get(relatedDtoClass).size();
	}

	@Override
	protected ListViewDataProvider getListDataProvider() {
		return provider; // = new RelationshipListViewDataProvider(moduleDto.getModule(), relatedDtoClass);
	}

	private Button getInitialCreateBtn() {
		final Button btn = new Button("Create");
		btn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				final HashMap<String, HashMap<String, HashSet<String>>> relationships = DtoModuleRegistry.instance().getRelationships();

				/**
				 * name of the field that should be pre-filled e.g. contactId
				 */
				/*
				 * final String field = relationships.get(moduleDto.getModule()).get(relatedDtoClass).iterator().next(); History.newItem(HistoryTokenFactory.get(moduleDto.getModule(), ModuleAction.CREATE, field, id));
				 * 
				 * if (null != presenter) { presenter.onCreate(); }
				 */
			}
		});
		return btn;
	}

	@Override
	public void setId(Long relatedId) {
		if (null != provider) {
			provider.setOriginatingId(relatedId);
		}
	}

	@Override
	public void insertRefreshedData(ListQueryResult relationshipData) {
		if (null != provider) {
			initialize();
			for (final HasData<Dto> display: provider.getDataDisplays()) {
				provider.insertRefreshedData(display, relationshipData);
			}
		}
	}
}