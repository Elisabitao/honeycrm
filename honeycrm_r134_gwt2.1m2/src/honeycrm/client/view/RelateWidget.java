package honeycrm.client.view;

import honeycrm.client.dto.Dto;
import honeycrm.client.dto.ListQueryResult;
import honeycrm.client.misc.Observer;
import honeycrm.client.misc.ServiceRegistry;
import honeycrm.client.misc.Subscriber;
import honeycrm.client.prefetch.Consumer;
import honeycrm.client.prefetch.Prefetcher;
import honeycrm.client.prefetch.ServerCallback;
import java.util.HashSet;
import java.util.Set;

import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

public class RelateWidget extends SuggestBox implements Subscriber<Dto> {
	private long id = 0; // start with id == 0L to indicate that nothing has been selected.
	private final String kind;
	private boolean timerRunning;
	private final Set<Observer<Dto>> observers = new HashSet<Observer<Dto>>();
	
	public RelateWidget(final String kind, final long id) {
		super(new MultiWordSuggestOracle());
		this.kind = kind;
		addHandlers();

		
		if (0 != id) {
			// an id of 0 indicates that nothing has been selected yet. if something has been
			// selected yet load the actual name of the related account.
			this.id = id;
			setValueForId(id);
		}
	}

	private void setValueForId(final long id) {
		Prefetcher.instance.get(new Consumer<Dto>() {
			@Override
			public void setValueAsynch(final Dto result) {
				if (null != result) { 
					setValue(result.getQuicksearch());
				}
			}
		}, new ServerCallback<Dto>() {
			@Override
			public void doRpc(final Consumer<Dto> internalCacheCallback) {
				ServiceRegistry.readService().get(kind, id, new AsyncCallback<Dto>() {
//				commonService.get(marshalledClass, id, new AsyncCallback<Dto>() {
					@Override
					public void onSuccess(final Dto result) {
						internalCacheCallback.setValueAsynch(result);
					}

					@Override
					public void onFailure(final Throwable caught) {
						Window.alert("Could not get item by id");
					}
				});
			}
		}, 60 * 1000, kind, id);
	}

	private void addHandlers() {
		addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(final KeyPressEvent event) {
				final String lastQuery = getText().trim() + event.getCharCode();
				
				if (!timerRunning) {
					new Timer(){
						@Override
						public void run() {
							startDeferredSearch(lastQuery);
						}
					}.schedule(300);
				}
			}
		});

		addSelectionHandler(new SelectionHandler<Suggestion>() {
			@Override
			public void onSelection(final SelectionEvent<Suggestion> event) {
				// determine id of this item and store the id in the gui to make sure it is
				// available on submit
				final String selected = event.getSelectedItem().getReplacementString();

				Prefetcher.instance.get(new Consumer<Dto>() {
					@Override
					public void setValueAsynch(final Dto value) {
						if (null == value) {
							// the related entity could not be found or the search returned more
							// than one result.
							// TODO what should be done in this case?
						} else {
							id = value.getId();
							
							// notify all observers of that new value
							for (final Observer<Dto> observer: observers) {
								observer.notify(value);
							}
						}						
					}
				}, new ServerCallback<Dto>() {
					@Override
					public void doRpc(final Consumer<Dto> internalCacheCallback) {
						ServiceRegistry.readService().getByName(kind, selected, new AsyncCallback<Dto>() {
//						commonService.getByName(marshalledClass, selected, new AsyncCallback<Dto>() {
							@Override
							public void onFailure(final Throwable caught) {
								Window.alert("Could not get id of selected item = " + selected);
							}

							@Override
							public void onSuccess(final Dto result) {
								internalCacheCallback.setValueAsynch(result);
							}
						});						
					}
				}, 60*1000, kind, selected);
			}
		});
	}
	
	private void startDeferredSearch(final String query) {
		if (!query.isEmpty()) {
			Prefetcher.instance.get(new Consumer<ListQueryResult>() {
				@Override
				public void setValueAsynch(final ListQueryResult result) {
	//				LoadIndicator.get().endLoading();

					if (0 == result.getResults().length) {
						indicateNoResults();
					} else {
						final MultiWordSuggestOracle o = (MultiWordSuggestOracle) getSuggestOracle();
						o.clear();

						for (final Dto a : result.getResults()) {
							o.add(a.getQuicksearch());
						}
					}
				}
			}, new ServerCallback<ListQueryResult>() {
				@Override
				public void doRpc(final Consumer<ListQueryResult> internalCacheCallback) {
		//			LoadIndicator.get().startLoading();
					ServiceRegistry.readService().getAllByNamePrefix(kind, query, 0, 20, new AsyncCallback<ListQueryResult>() {
//					commonService.getAllByNamePrefix(marshalledClass, query, 0, 20, new AsyncCallback<ListQueryResult>() {
						@Override
						public void onSuccess(final ListQueryResult result) {
							internalCacheCallback.setValueAsynch(result);
						}

						@Override
						public void onFailure(final Throwable caught) {
	//						LoadIndicator.get().endLoading();
							indicateNoResults();
						}
					});
					
					// send request to server. allow user doing some more requests asap.
					timerRunning = false;
				}
			}, 60 * 1000, kind, query, 0, 20);
		}
	}
	
	private void indicateNoResults() {
		final MultiWordSuggestOracle o = (MultiWordSuggestOracle) getSuggestOracle();

		o.clear();
		o.add("No Results");

		// TODO indicate that no results have been returned, set background
		// color to red or something like that..
	}

	/**
	 * Returns the id of the selected item.
	 */
	public long getId() {
		return id;
	}

	@Override
	public void subscribe(final Observer<Dto> observer) {
		observers.add(observer);
	}
}
