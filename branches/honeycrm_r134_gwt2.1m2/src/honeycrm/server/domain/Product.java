package honeycrm.server.domain;

import honeycrm.server.domain.decoration.DetailViewable;
import honeycrm.server.domain.decoration.Label;
import honeycrm.server.domain.decoration.ListViewable;
import honeycrm.server.domain.decoration.Quicksearchable;
import honeycrm.server.domain.decoration.fields.FieldBooleanAnnotation;
import honeycrm.server.domain.decoration.fields.FieldCurrencyAnnotation;
import honeycrm.server.domain.decoration.fields.FieldStringAnnotation;
import honeycrm.server.domain.decoration.fields.FieldTextAnnotation;

import javax.jdo.annotations.PersistenceCapable;

import org.compass.annotations.Searchable;

@PersistenceCapable
@Searchable
@ListViewable( { "name", "price" })
@DetailViewable( { "name", "price", "published", "description" })
@Quicksearchable( { "name" })
public class Product extends AbstractEntity {
	@FieldStringAnnotation
	@Label("Name")
	public String name;
	
	@FieldCurrencyAnnotation("0")
	@Label("Price")
	public double price;

	@FieldTextAnnotation(width=400)
	@Label("Description")
	public String description;
	
	@FieldBooleanAnnotation
	@Label("Published")
	public boolean published;
}
