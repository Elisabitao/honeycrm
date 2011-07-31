package honeycrm.client.field;

import honeycrm.client.dto.Dto;
import honeycrm.client.dto.DtoModuleRegistry;
import honeycrm.client.dto.ModuleDto;
import honeycrm.client.misc.Callback;
import honeycrm.client.misc.CollectionHelper;
import honeycrm.client.misc.QuicksearchHelper;
import honeycrm.client.misc.QuicksearchValue;
import honeycrm.client.misc.View;
import honeycrm.client.view.ModuleAction;
import honeycrm.client.view.RelateWidget;

import java.io.Serializable;

import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class FieldRelate extends AbstractField<SafeHtml, QuicksearchValue> {
	private static final long serialVersionUID = -1518485985368479493L;
	private String relatedModule;

	public FieldRelate() {
	}

	public FieldRelate(final String id, final String relatedModule, final String label) {
		super(id, label);
		this.relatedModule = relatedModule;
	}

	public String getRelatedModule() {
		return relatedModule;
	}

	@Override
	protected Widget internalGetCreateWidget(final Dto dto, final String fieldId) {
		final Serializable value = dto.get(fieldId);
		final long id = (null != value && value instanceof Long && (Long) value > 0) ? (Long) value : 0;
		return new RelateWidget(getRelatedModule(), id);
	}

	@Override
	protected Widget internalGetDetailWidget(final Dto dto, final String fieldId) {
		final Serializable value = dto.get(fieldId);
		if (null == value || 0 == (Integer) value || 0 == (Long) value) {
			// return an empty label because no account has been selected yet
			return new Label();
		} else {
			final Dto related = ((Dto) dto.get(fieldId + "_resolved"));

			if (null == related) {
				return new Label("[unresolved]");
			} else {
				final ModuleDto moduleDtoRelated = DtoModuleRegistry.instance().get(relatedModule);
				final String token = CollectionHelper.join(" ", moduleDtoRelated.getHistoryToken(), ModuleAction.DETAIL.toString().toLowerCase(), String.valueOf(value));
				final Hyperlink link = new Hyperlink(related.getQuicksearch(), token);

				if (related.getAllPreviewableFieldsSorted().isEmpty()) {
					/**
					 * there are no details that can be displayed. only display the link to the related item.
					 */
					return link;
				} else {
					/**
					 * there are details to this related dto. attach a [details] label and attach a popup that can be displayed as an onMouseOver effect.
					 */
					return getDetailsPanel(related, link, fieldId, moduleDtoRelated);
				}
			}
		}
	}

	private Panel getDetailsPanel(final Dto related, final Hyperlink link, final String fieldId, final ModuleDto moduleDtoRelated) {
		final PopupPanel popup = getDetailsPopup(related, fieldId, moduleDtoRelated);
		final Label details = new Label(" [details]");

		details.addMouseOverHandler(new MouseOverHandler() {
			@Override
			public void onMouseOver(MouseOverEvent event) {
				int left = link.getAbsoluteLeft();
				int top = link.getAbsoluteTop() + 16;
				popup.setPopupPosition(left, top);
				popup.show();
			}
		});

		details.addMouseOutHandler(new MouseOutHandler() {
			@Override
			public void onMouseOut(MouseOutEvent event) {
				popup.hide();
			}
		});

		final Panel panel = new HorizontalPanel();
		panel.add(link);
		panel.add(details);

		return panel;
	}

	private PopupPanel getDetailsPopup(final Dto related, final String fieldId, final ModuleDto moduleDtoRelated) {
		final PopupPanel popup = new PopupPanel(true);

		String html = "";

		for (final String key : related.getAllPreviewableFieldsSorted()) {
			final Serializable value = related.get(key);
			final String label = moduleDtoRelated.getFieldById(key).getLabel();
			html += "<li>" + label + ": " + value.toString() + "</li>";
		}

		popup.add(new HTML("<ul>" + html + "</ul>"));

		return popup;
	}

	@Override
	protected Widget internalGetEditWidget(final Dto dto, final String fieldId) {
		final Serializable value = dto.get(fieldId);
		return new RelateWidget(getRelatedModule(), (null == value) ? 0 : (Long) value);
	}

	@Override
	protected Widget internalGetListWidget(final Dto dto, final String fieldId) {
		return internalGetDetailWidget(dto, fieldId);
	}

	@Override
	protected Serializable internalGetData(Widget w) {
		if (w instanceof RelateWidget) {
			return ((RelateWidget) w).getId();
		} else if (w instanceof Hyperlink) {
			return ((Hyperlink) w).getHTML();
		} else {
			throw new RuntimeException("Unexpected type " + w.getClass());
		}
	}

	@Override
	public Column<Dto, SafeHtml> getColumn(final String fieldName, final View viewMode, final Callback<QuicksearchValue> fieldUpdatedCallback) {
		return new Column<Dto, SafeHtml>(new SafeHtmlCell()) {
			@Override
			public SafeHtml getValue(final Dto object) {
				if (View.isReadOnly(viewMode)) {
					return new SafeHtml() {
						private static final long serialVersionUID = -8017901422992491763L;
						@Override
						public String asString() {
							final String name = getInitialName(fieldName, object);
							return null == name ? "[unresolved]" : name;
						}
					};
				} else {
					return QuicksearchHelper.getQuickSearchMarkup(object, fieldName, getInitialName(fieldName, object), fieldUpdatedCallback);
				}
			}
		};
	}
	
	private String getInitialName(final String fieldName, final Dto object) {
		if (null == object.get(fieldName + "_resolved")) {
			return null;
		} else {
			return String.valueOf(((Dto) object.get(fieldName + "_resolved")).get("name"));
		}
	}
	
	/*
	 * TODO how to implement this when we cannot access the whole dto object from here?
	 * 
	 * @Override public String internalFormattedValue(Serializable value) { if (null == value || 0 == (Long) value) { return ""; } else { final Serializable resolved = object.get(id + "_resolved"); if (null == resolved || null == ((Dto) resolved).get("name")) { return "fail!"; } else { return (String) ((Dto) resolved).get("name"); } } }
	 */
}
