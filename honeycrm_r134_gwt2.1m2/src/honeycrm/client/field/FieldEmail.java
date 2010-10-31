package honeycrm.client.field;

import honeycrm.client.misc.StringAbbreviation;
import honeycrm.client.view.AbstractView.View;

import java.io.Serializable;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class FieldEmail extends AbstractField {
	private static final long serialVersionUID = -1035707556737860138L;

	public FieldEmail() {
	}

	public FieldEmail(final String index, final String label) {
		super(index, label);
	}

	@Override
	protected Serializable internalGetData(final Widget w) {
		return ((Anchor) w).getHTML();
	}

	@Override
	public String internalFormattedValue(final Object value) {
		return StringAbbreviation.shorten(stringify(value), 10);
	}

	@Override
	protected void internalSetData(final TextBox widget, final Object value, final View view) {
		widget.setValue(stringify(value)); // declare this to avoid shortening of address in edit view
	}
	
	@Override
	protected void internalSetData(final Anchor widget, final Object value, final View view) {
		// TODO do this only for value != null
		// TODO use Label when value == null
		widget.setTitle(stringify(value));
		(widget).setText(internalFormattedValue(value));
		(widget).setHref("mailto:" + String.valueOf(value));
	}

	@Override
	protected Widget detailField() {
		return new Anchor();
	}
}
