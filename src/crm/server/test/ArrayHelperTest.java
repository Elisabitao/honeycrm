package crm.server.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;
import crm.client.CollectionHelper;

public class ArrayHelperTest extends TestCase {
	public void testJoin() {
		final String glue = ",";
		final List<String> foo = new ArrayList<String>();
		assertEquals("", CollectionHelper.join(foo, glue));

		foo.add("a");
		assertEquals("a", CollectionHelper.join(foo, glue));

		foo.add("b");
		foo.add("c");
		assertEquals("a" + glue + "b" + glue + "c", CollectionHelper.join(foo, glue));
	}

	public void testMerge() {
		final int[][] a = new int[][] { { 1, 2 }, { 3, 4 } };
		final int[][] b = new int[][] { { 5, 6 }, { 7, 8 } };

		final int[][] c = CollectionHelper.merge(a, b);
		for (int y = 0; y < a.length; y++) {
			for (int x = 0; x < a[y].length; x++) {
				assertEquals(a[y][x], c[y][x]);
			}
		}
		for (int y = 0; y < b.length; y++) {
			for (int x = 0; x < b[y].length; x++) {
				assertEquals(b[y][x], c[y + a.length][x]);
			}
		}
	}
	
	public void testToSet() {
		assertTrue(CollectionHelper.toSet(null).isEmpty());
		assertTrue(CollectionHelper.toSet(new String[0]).isEmpty());
		assertEquals(1, CollectionHelper.toSet(new String[]{"foo"}).size());
		
		final Set<String> set = CollectionHelper.toSet(new String[]{"1","2"});
		assertEquals(2, set.size());
		assertTrue(set.contains("1"));
		assertTrue(set.contains("2"));
	}
}