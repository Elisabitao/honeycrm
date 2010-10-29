package honeycrm.client.view;

import honeycrm.client.dto.Dto;
import honeycrm.client.dto.DtoModuleRegistry;
import honeycrm.client.dto.ListQueryResult;
import honeycrm.client.dto.ModuleDto;
import honeycrm.client.prefetch.Consumer;
import honeycrm.client.prefetch.Prefetcher;
import honeycrm.client.prefetch.ServerCallback;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ModuleFulltextWidget extends FulltextSearchWidget {
	private final ModuleDto dtoClazz;

	public ModuleFulltextWidget(final String clazz) {
		super();
		this.dtoClazz = DtoModuleRegistry.instance().get(clazz);
		addStyleName("module_search");
	}

	@Override
	protected void startFulltextSearch(final String queryString) {
		Prefetcher.instance.get(new Consumer<ListQueryResult>() {
			@Override
			public void setValueAsynch(final ListQueryResult result) {
				if (null != result && result.getItemCount() > 0) {
					final FulltextSuggestOracle o = emptySuggestOracle();
					final Map<String, Integer> quicksearchLabels = new HashMap<String, Integer>();

					for (final Dto a : result.getResults()) {
						String label = a.getQuicksearch();

						if (quicksearchLabels.containsKey(label)) {
							quicksearchLabels.put(label, quicksearchLabels.get(label) + 1);
							// add a counter for this label since it is not unique
							label = label + " (" + quicksearchLabels.get(label) + ")";
						} else {
							quicksearchLabels.put(label, 1);
						}

						// insert this label into the name to id translation map to make sure that
						// later the id can be determined by looking up the name in this map.
						nameToDto.put(label, a);

						o.add(label);
					}

					showSuggestionList();
				}
			}
		}, new ServerCallback<ListQueryResult>() {
			@Override
			public void doRpc(final Consumer<ListQueryResult> internalCacheCallback) {
				readService.fulltextSearchForModule(dtoClazz.getModule(), queryString, 0, 10, new AsyncCallback<ListQueryResult>() {
				// commonService.fulltextSearchForModule(dtoClazz.getModule(), queryString, 0, 10, new AsyncCallback<ListQueryResult>() {
					@Override
					public void onSuccess(ListQueryResult result) {
						internalCacheCallback.setValueAsynch(result);
					}

					@Override
					public void onFailure(Throwable caught) {
						Window.alert("fulltext search failed");
					}
				});
			}
		}, 60 * 1000, dtoClazz.getModule(), queryString, 0, 10);
	}
}