package honeycrm.client.dto;

import honeycrm.client.field.FieldDate;
import honeycrm.client.field.FieldRelate;
import honeycrm.client.field.FieldTable;
import honeycrm.client.view.ITableWidget;
import honeycrm.client.view.ServiceTableWidget;
import honeycrm.client.view.AbstractView.View;

import java.util.Date;
import java.util.List;

public class DtoOffering extends AbstractDto {
	private static final long serialVersionUID = -5569259575976686950L;

	@RelatesTo(DtoContact.class)
	private Long contactId;
	private Date deadline;
	private List<DtoService> services;

	public static final int INDEX_SERVICES = 1;
	public static final int INDEX_DEADLINE = 2;
	public static final int INDEX_CONTACTID = 3;

	public DtoOffering() {
		fields.add(new FieldTable(INDEX_SERVICES, "Services"));
		fields.add(new FieldRelate(INDEX_CONTACTID, DtoContact.class, "Contact"));
		fields.add(new FieldDate(INDEX_DEADLINE, "Deadline"));
	}

	public ITableWidget getWidget(View view) {
		return new ServiceTableWidget(view);
	}

	@Override
	public int[] getListViewColumnIds() {
		return new int[] { INDEX_CONTACTID, INDEX_DEADLINE };
	}

	@Override
	public String getQuicksearchItem() {
		return getTitle() + " for " + contactId;
	}

	@Override
	public int[][] getSearchFields() {
		return new int[][] { new int[] { INDEX_CONTACTID } };
	}

	@Override
	protected int[][] interalGetFormFieldIds() {
		return new int[][] { new int[] { INDEX_CONTACTID }, new int[] { INDEX_DEADLINE }, new int[] { INDEX_SERVICES } };
	}

	@Override
	protected Object internalGetFieldValue(int index) {
		switch (index) {
		case INDEX_SERVICES:
			return services;
		case INDEX_DEADLINE:
			return deadline;
		case INDEX_CONTACTID:
			return contactId;
		default:
			throw new RuntimeException("Unexpected index: " + index);
		}
	}

	@Override
	protected void internalSetFieldValue(int index, Object value) {
		switch (index) {
		case INDEX_SERVICES:
			if (null == value) {
				setServices(null);
			} else if (value instanceof List<?>) {
				setServices((List<DtoService>) value);
			} else {
				throw new RuntimeException("Received value of invalid type. Expected list received " + value.getClass());
			}
			break;
		case INDEX_DEADLINE:
			setDeadline((Date) value);
			break;
		case INDEX_CONTACTID:
			setContactId(Long.parseLong(value.toString()));
			break;
		default:
			throw new RuntimeException("Unexpected index: " + index);
		}
	}

	public List<DtoService> getServices() {
		return services;
	}

	public void setServices(List<DtoService> services) {
		this.services = services;
	}

	public Long getContactId() {
		return contactId;
	}

	public void setContactId(Long contactId) {
		this.contactId = contactId;
	}

	public Date getDeadline() {
		return deadline;
	}

	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}
}
