package honeycrm.client.basiclayout;

import honeycrm.client.admin.AdminWidget;
import honeycrm.client.admin.LogConsole;
import honeycrm.client.dto.DtoModuleRegistry;
import honeycrm.client.dto.ModuleDto;
import honeycrm.client.reports.SampleReport;
import honeycrm.client.view.EmailFeedbackWidget;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

// TODO update history token when a new tab is selected
public class TabCenterView extends TabLayoutPanel  implements ValueChangeHandler<String> {
	private static TabCenterView instance = new TabCenterView();

	// use list instead of set to make sure the sequence stays the same
	private final Map<String, TabModuleView> moduleViewMap = new HashMap<String, TabModuleView>();
	/**
	 * Store the position for each module in the tab panel (e.g. Contacts -> Tab 0, Accounts -> Tab 1, ..)
	 */
	private final Map<String, Integer> tabPositionMap = new HashMap<String, Integer>();
	/**
	 * Store which module is set at which position in the tab panel (e.g. Tab 0 -> Contacts, Tab 1 -> Accounts, ..). This is almost the reverse version of tabPositionMap. However tabPositionMapReverse stores instances of Viewable instead storing classes.
	 */
	private final Map<Integer, String> tabPositionMapReverse = new HashMap<Integer, String>();
	private final Map<Integer, Widget> tabPosToCreateBtnMap = new HashMap<Integer, Widget>();


	public static TabCenterView instance() {
		return instance;
	}

	private TabCenterView() {
		super(25, Unit.PX);
		int tabPos = 0;
		
		addStyleName("with_margin");
		addStyleName("tab_layout");

		final Collection<ModuleDto> dtos = DtoModuleRegistry.instance().getDtos();

		for (final ModuleDto moduleDto : dtos) {
			if (moduleDto.isHidden()) {
				continue; // do not add this module to the tabs since it should be hidden
			}
			
			final TabModuleView view = new TabModuleView(moduleDto.getModule());

			// refresh list view only for the first tab (which is the only visible tab at the beginning)
			if (0 == tabPos)
				view.refreshListView();

			final Hyperlink createBtn = new Hyperlink("Create", moduleDto.getModule() + " create");
			createBtn.addStyleName("create_button");
			createBtn.setVisible(false);
			
			moduleViewMap.put(moduleDto.getModule(), view);
			tabPositionMap.put(moduleDto.getModule(), tabPos);
			tabPositionMapReverse.put(tabPos++, moduleDto.getModule());
			tabPosToCreateBtnMap.put(tabPos - 1, createBtn);

			final Label moduleTitle = new Label(moduleDto.getTitle() + "s");
			final HorizontalPanel titlePanel = new HorizontalPanel();
			titlePanel.add(createBtn);
			titlePanel.add(moduleTitle);
			
			add((view), titlePanel);
		}

		add(new AdminWidget(), "Admin");
		add(new EmailFeedbackWidget(), "Feedback");
		add(new SampleReport(), "Dashboard");

		addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
			@Override
			public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
				// hide all create buttons
				for (final Integer pos : tabPosToCreateBtnMap.keySet()) {
					tabPosToCreateBtnMap.get(pos).setVisible(false);
				}
				if (tabPosToCreateBtnMap.containsKey(event.getItem())) {
					// show create button for currently selected tab
					tabPosToCreateBtnMap.get(event.getItem()).setVisible(true);
				}

				if (tabPositionMapReverse.containsKey(event.getItem())) {
					// add the history token for the module stored in this tab
					History.newItem(DtoModuleRegistry.instance().get(tabPositionMapReverse.get(event.getItem())).getHistoryToken());
				} else {
					// TODO add history for special tabs like admin panel
				}
			}
		});

		LogConsole.log("created center view");

		History.addValueChangeHandler(this);
        // History.fireCurrentHistoryState();
		
		selectTab(0);
	}

	public TabModuleView get(String moduleName) {
		return moduleViewMap.get(moduleName);
	}

	/**
	 * Shows the module tab for the module described by the given class.
	 */
	public void showModuleTabWithId(final String clazz, final long id) {
		showModuleTab(clazz);
		moduleViewMap.get(clazz).showDetailView(id);
	}

	public void showModuleTab(String clazz) {
		if (!tabPositionMap.containsKey(clazz) || !moduleViewMap.containsKey(clazz)) {
			Window.alert("Cannot switch to module: '" + clazz + "'");
			return;
		}
		
		if (!moduleViewMap.get(clazz).isListViewInitialized()) {
			moduleViewMap.get(clazz).refreshListView();
		}
		selectTab(tabPositionMap.get(clazz));
	}
	
	public void showCreateViewForModule(final String clazz) {
		showModuleTab(clazz);
		moduleViewMap.get(clazz).showCreateView();
	}

	public void showCreateViewForModulePrefilled(String module, String fieldId, Serializable value) {
		showModuleTab(module);
		moduleViewMap.get(module).showCreateViewPrefilled(fieldId, value);
	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		final String[] token = event.getValue().split("\\s+");

		if (2 == token.length) {
			if ("create".equals(token[1])) {
				showCreateViewForModule(token[0]);
			} else {
				showModuleTabWithId(token[0], Long.valueOf(token[1]));
			}
		} else if (1 == token.length) {
			showModuleTab(token[0]);
		}
	}
}