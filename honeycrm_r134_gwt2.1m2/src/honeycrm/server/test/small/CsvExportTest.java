package honeycrm.server.test.small;

import honeycrm.client.csv.AbstractCsv;
import honeycrm.client.csv.CsvExporter;
import honeycrm.client.dto.Configuration;
import honeycrm.client.dto.Dto;
import honeycrm.client.dto.DtoModuleRegistry;
import honeycrm.client.misc.CollectionHelper;
import honeycrm.server.RelationshipFieldTable;
import honeycrm.server.transfer.DtoWizard;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

public class CsvExportTest extends TestCase {
	@Override
	protected void setUp() throws Exception {
		DtoModuleRegistry.create(new Configuration(DtoWizard.instance.getDtoConfiguration(), RelationshipFieldTable.instance.getMap()));
	}

	public void testExport() {
		final List<Dto> list = new LinkedList<Dto>();
		final Dto foo = new Dto();
		foo.setModule("contact");
		foo.set("name", "Foo");
		final Dto bar = new Dto();
		bar.setModule("contact");
		bar.set("name", "Bar");
		list.add(foo);
		list.add(bar);

		final CsvExporter exporter = new CsvExporter();
		final String csv = exporter.export(list);

		assertNotNull(csv);

		final String[] lines = csv.split(AbstractCsv.LINE_SEP);
		assertEquals(list.size(), lines.length);

		for (int i = 0; i < lines.length; i++) {
			final String[] cols = lines[i].split(AbstractCsv.COL_SEP);
			assertTrue(cols.length > 1);

			final Set<String> colSet = CollectionHelper.toSet(cols);
			assertTrue(colSet.contains(AbstractCsv.FIELD_DEL + "Foo" + AbstractCsv.FIELD_DEL) || colSet.contains(AbstractCsv.FIELD_DEL + "Bar" + AbstractCsv.FIELD_DEL));
		}
	}
}
