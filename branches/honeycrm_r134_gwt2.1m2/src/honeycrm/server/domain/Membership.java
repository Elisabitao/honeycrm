package honeycrm.server.domain;

import honeycrm.server.domain.decoration.DetailViewable;
import honeycrm.server.domain.decoration.Label;
import honeycrm.server.domain.decoration.ListViewable;
import honeycrm.server.domain.decoration.Quicksearchable;
import honeycrm.server.domain.decoration.fields.FieldCurrencyAnnotation;
import honeycrm.server.domain.decoration.fields.FieldDateAnnotation;
import honeycrm.server.domain.decoration.fields.FieldEnumAnnotation;
import honeycrm.server.domain.decoration.fields.FieldRelateAnnotation;
import honeycrm.server.domain.decoration.fields.FieldTextAnnotation;

import java.util.Date;

import javax.jdo.annotations.PersistenceCapable;

import org.compass.annotations.Searchable;
import org.compass.annotations.SearchableProperty;

@PersistenceCapable
@Searchable
@ListViewable( { "marked", "memberId", "employeeId", "startDate" })
@DetailViewable( { "memberId,employeeId", "tiedToPurpose,purpose", "payment,paymentMethod", "startDate,endDate" })
@Quicksearchable( { "memberId", "employeeId" })
public class Membership extends AbstractEntity {
	@FieldRelateAnnotation(Contact.class)
	@Label("Member")
	private long memberId; // relation to contacts

	@Label("Employee")
	@FieldRelateAnnotation(Employee.class)
	private long employeeId; // relation to employees

	@FieldCurrencyAnnotation("0")
	@Label("Contribution")
	private double payment;

	@SearchableProperty
	@FieldEnumAnnotation( { "Yes", "No", "Soon" })
	@Label("Tied to purpose")
	private String tiedToPurpose; // yes, no, 'soon'

	@SearchableProperty
	@FieldTextAnnotation
	@Label("Purpose")
	private String purpose;

	@SearchableProperty
	@Label("Payment method")
	@FieldEnumAnnotation( { "Direct Debit authorisation", "transaction" })
	private String paymentMethod; // Direct Debit authorisation / transaction

	@Label("Start date")
	@FieldDateAnnotation
	private Date startDate;

	@Label("End date")
	@FieldDateAnnotation
	private Date endDate;

	public long getMemberId() {
		return memberId;
	}

	public void setMemberId(final long memberId) {
		this.memberId = memberId;
	}

	public long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(final long employeeId) {
		this.employeeId = employeeId;
	}

	public double getPayment() {
		return payment;
	}

	public void setPayment(final double payment) {
		this.payment = payment;
	}

	public String getTiedToPurpose() {
		return tiedToPurpose;
	}

	public void setTiedToPurpose(final String tiedToPurpose) {
		this.tiedToPurpose = tiedToPurpose;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(final String purpose) {
		this.purpose = purpose;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(final String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(final Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(final Date endDate) {
		this.endDate = endDate;
	}
}
