package honeycrm.client.view;

import honeycrm.client.CommonServiceAsync;
import honeycrm.client.LoadIndicator;
import honeycrm.client.ServiceRegistry;
import honeycrm.client.dto.AbstractDto;
import honeycrm.client.dto.ListQueryResult;
import honeycrm.client.prefetch.Prefetcher;
import honeycrm.client.prefetch.Consumer;
import honeycrm.client.prefetch.ServerCallback;

import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

public class RelateWidget extends SuggestBox {
	private long id;
	private final int marshalledClass;
	private static final CommonServiceAsync commonService = ServiceRegistry.commonService();

	public RelateWidget(final int marshalledClazz, final long id) {
		super(new MultiWordSuggestOracle());
		this.marshalledClass = marshalledClazz;
		addHandlers();

		if (0 != id) {
			// an id of 0 indicates that nothing has been selected yet. if something has been
			// selected yet load the actual name of the related account.
			this.id = id;
			setValueForId(id);
		}
	}

	private void setValueForId(final long id) {
		LoadIndicator.get().startLoading();
		
		Prefetcher.instance.get(new Consumer<AbstractDto>() {
			@Override
			public void setValueAsynch(AbstractDto result) {
				setValue(result.getQuicksearchItem());
			}
		}, new ServerCallback<AbstractDto>() {
			@Override
			public void doRpc(final Consumer<AbstractDto> internalCacheCallback) {
				commonService.get(marshalledClass, id, new AsyncCallback<AbstractDto>() {
					@Override
					public void onSuccess(final AbstractDto result) {
						LoadIndicator.get().endLoading();
						internalCacheCallback.setValueAsynch(result);
					}
					
					@Override
					public void onFailure(Throwable caught) {
						LoadIndicator.get().endLoading();
						Window.alert("Could not get item by id");
					}
				});
			}
		}, marshalledClass, id);
	}

	private void addHandlers() {
		addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				final String query = getText().trim();

				// TODO cache some results instead of doing requests over and over again
				if (!query.isEmpty()) {
					LoadIndicator.get().startLoading();

					commonService.getAllByNamePrefix(marshalledClass, query, 0, 20, new AsyncCallback<ListQueryResult<? extends AbstractDto>>() {
						@Override
						public void onSuccess(ListQueryResult<? extends AbstractDto> result) {
							LoadIndicator.get().endLoading();

							if (0 == result.getResults().length) {
								indicateNoResults();
							} else {
								final MultiWordSuggestOracle o = (MultiWordSuggestOracle) getSuggestOracle();
								o.clear();

								for (final AbstractDto a : result.getResults()) {
									o.add(a.getQuicksearchItem());
								}
							}
						}

						@Override
						public void onFailure(Throwable caught) {
							LoadIndicator.get().endLoading();
							indicateNoResults();
						}

						private void indicateNoResults() {
							MultiWordSuggestOracle o = (MultiWordSuggestOracle) getSuggestOracle();

							o.clear();
							o.add("No Results");

							// TODO indicate that no results have been returned, set background
							// color to red or something like that..
						}
					});
				}
			}
		});

		addSelectionHandler(new SelectionHandler<Suggestion>() {
			@Override
			public void onSelection(SelectionEvent<Suggestion> event) {
				// determine id of this item and store the id in the gui to make sure it is
				// available on submit
				final String selected = event.getSelectedItem().getReplacementString();

				commonService.getByName(marshalledClass, selected, new AsyncCallback<AbstractDto>() {
					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Could not get id of selected item = " + selected);
					}

					@Override
					public void onSuccess(AbstractDto result) {
						if (null == result) {
							// the related entity could not be found or the search returned more
							// than one result.
							// TODO what should be done in this case?
						} else {
							id = result.getId();
						}
					}
				});
			}
		});
	}

	/**
	 * Returns the id of the selected item.
	 */
	public long getId() {
		return id;
	}
}
