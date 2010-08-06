package honeycrm.client.field;

import honeycrm.client.dto.Dto;
import honeycrm.client.misc.CollectionHelper;
import honeycrm.client.view.RelateWidget;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class FieldRelate extends AbstractField {
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
	protected Widget internalGetCreateWidget(Object value) {
		final long id = (null != value && value instanceof Long && (Long) value > 0) ? (Long) value : 0;
		return new RelateWidget(getRelatedModule(), id);
	}

	@Override
	protected Widget internalGetDetailWidget(final Dto dto, final String fieldId) {
		final Serializable value = dto.get(fieldId);
		if (null == value || 0 == (Long) value) {
			// return an empty label because no account has been selected yet
			return new Label();
		} else {
			final Dto related = ((Dto) dto.get(fieldId + "_resolved"));
			
			if (null == related) {
				return new Label("[unresolved]");
			} else {
				final PopupPanel popup = getDetailsPopup(related, fieldId);
				final Hyperlink link = new Hyperlink(related.getQuicksearch(), related.getHistoryToken() + " " + value);
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
		}
	}

	private PopupPanel getDetailsPopup(final Dto related, final String fieldId) {
		final PopupPanel popup = new PopupPanel(true);
	
		String html = "";
		
		final List<String> sortedFieldNames = CollectionHelper.toList(related.getAllData().keySet());
		Collections.sort(sortedFieldNames);
		
		for (final String key: sortedFieldNames) {
			final Serializable value = related.get(key);

			// TODO shouldn't this be encapsulated in the dto class in a method like getAll detail preview
			if (!"name".equals(key) && null != value && value instanceof String && !value.toString().isEmpty()) {
				final String label = related.getFieldById(key).getLabel();
				html += "<li>" + label + ": " + value.toString() + "</li>";
			}
		}
		
		popup.add(new HTML("<ul>" + html + "</ul>"));

		return popup;
	}

	@Override
	protected Widget internalGetEditWidget(Object value) {
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
}
