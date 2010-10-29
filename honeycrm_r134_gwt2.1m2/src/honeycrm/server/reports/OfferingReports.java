package honeycrm.server.reports;

import honeycrm.server.domain.Offering;

import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class OfferingReports {
	public Map<Integer, Double> getAnnuallyOfferingVolumes(final Collection<Offering> offerings) {
		final Map<Integer, Double> volumes = new HashMap<Integer, Double>();

		for (final Offering offering : offerings) {
			if (null == offering.deadline) {
				continue; // skip this offering since we cannot analyze it anyway
			}
			
			final Calendar c = Calendar.getInstance();
			c.setTime(offering.deadline);

			final int year = c.get(Calendar.YEAR);

			if (volumes.containsKey(year)) {
				volumes.put(year, offering.getCosts() + volumes.get(year));
			} else {
				volumes.put(year, offering.getCosts());
			}
		}

		return volumes;
	}
}