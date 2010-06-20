package honeycrm.server;

import honeycrm.client.CommonService;
import honeycrm.client.IANA;
import honeycrm.client.dto.AbstractDto;
import honeycrm.client.dto.DtoContact;
import honeycrm.client.dto.ListQueryResult;
import honeycrm.server.domain.AbstractEntity;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.jdo.Query;


/**
 * Is somewhat the business layer.
 */
public class CommonServiceImpl extends AbstractCommonService implements CommonService {
	private static final Logger log = Logger.getLogger(CommonServiceImpl.class.getName());
	private static final long serialVersionUID = -7312945910083902842L;
	private static final CommonServiceCreator creator = new CommonServiceCreator();
	private static final CommonServiceReader reader = new CommonServiceReader();
	private static final CommonServiceReaderFulltext fulltext = new CommonServiceReaderFulltext();
	private static final CommonServiceEmail email = new CommonServiceEmail();
	private static final CommonServiceReporter reporter = new CommonServiceReporter();
	
	@Override
	public long create(int dtoIndex, AbstractDto dto) {
		return creator.create(dtoIndex, dto);
	}

	@Override
	public void addDemo(int dtoIndex) {
		creator.addDemo(dtoIndex);
	}

	@Override
	public ListQueryResult<? extends AbstractDto> getAll(final int dtoIndex, int from, int to) {
		// TODO using transactions currently breaks getAll()
		// TODO do everything within the context of a transaction
		// final Transaction t = m.currentTransaction();
		// try {
		// t.begin();
		final ListQueryResult<? extends AbstractDto> result = reader.getAll(dtoIndex, from, to);
		// t.commit();
		return result;
		// } finally {
		// if (t.isActive()) {
		// t.rollback();
		// }
		// }
	}

	@Override
	public AbstractDto get(final int dtoIndex, final long id) {
		final AbstractDto dto = reader.get(dtoIndex, id);

		if (null == dto) {
			return null; // do not do anything more than that because we could not find the dto
		}

		// TODO in when the name of an relate field is determined this get() method is called
		// TODO this increases the number of views for the related entity every time it is
		// displayed..
		// TOD this should be avoided.
		// increase number of views on every get request for a specific DTO
		dto.setViews(dto.getViews() + 1);
		update(dtoIndex, dto, id);
		return dto;
	}

	@Override
	public ListQueryResult<? extends AbstractDto> search(int dtoIndex, AbstractDto searchDto, int from, int to) {
		return reader.search(dtoIndex, searchDto, from, to);
	}

	@Override
	public ListQueryResult<? extends AbstractDto> getAllByNamePrefix(int dtoIndex, String prefix, int from, int to) {
		log.fine("getAllByNamePrefix(" + dtoIndex + "," + prefix + ")");
		return reader.getAllByNamePrefix(dtoIndex, prefix, from, to);
	}

	@Override
	public AbstractDto getByName(int dtoIndex, String name) {
		return reader.getByName(dtoIndex, name);
	}

	// create classes for delete and update operations when code grows
	// TODO does not update the relate field anymore
	@Override
	public void update(int dtoIndex, AbstractDto dto, long id) {
		dto.setLastUpdatedAt(new Date(System.currentTimeMillis()));

		final AbstractEntity existingObject = (AbstractEntity) getDomainObject(dtoIndex, id);

		if (null != existingObject) {
			m.makePersistent(copy.getUpdatedInstance(dto, existingObject));
		}
	}

	@Override
	public void delete(int dtoIndex, long id) {
		final Object object = getDomainObject(dtoIndex, id);
		if (null != object) {
			m.deletePersistent(object);
		}
	}

	@Override
	public void deleteAll(int dtoIndex, Set<Long> ids) {
		for (final Long id : ids) {
			delete(dtoIndex, id);
		}
	}

	@Override
	public ListQueryResult<? extends AbstractDto> fulltextSearch(String query, int from, int to) {
		return fulltext.fulltextSearch(query, from, to);
	}

	@Override
	public void mark(int dtoIndex, long id, boolean marked) {
		AbstractDto viewable = get(dtoIndex, id);
		viewable.setFieldValue(AbstractDto.INDEX_MARKED, marked);
		update(dtoIndex, viewable, id);
	}

	@Override
	public ListQueryResult<? extends AbstractDto> getAllMarked(int dtoIndex, int from, int to) {
		return reader.getAllMarked(dtoIndex, from, to);
	}

	@Override
	public void deleteAll(int dtoIndex) {
		final Query q = m.newQuery(getDomainClass(dtoIndex));
		m.deletePersistentAll((Collection) q.execute());
	}

	@Override
	public void deleteAllItems() {
		log.info("Deleting all items..");
		for (final Class<? extends AbstractEntity> entityClass : registry.getDomainClasses()) {
			try {
				m.deletePersistentAll((Collection) m.newQuery(entityClass).execute());
				/*for (final AbstractEntity entity : ) {
					m.deletePersistent(entity);
				}*/
			} catch (NullPointerException e) {
				log.warning("A NullPointerException occured during the deletion of all entities");
				e.printStackTrace();
			}
		}
		log.info("Deleting all items done.");
	}

	/**
	 * Do nothing just call this empty method to wake up the server side. This is done to speedup
	 * the up coming requests. Do an initial slow request by the client. When this is has been
	 * answered the user interface will be rendered and the real requests will follow in quick
	 * succession.
	 */
	@Override
	public void wakeupServer() {
	}

	@Override
	public ListQueryResult<? extends AbstractDto> getAllRelated(int originatingDtoIndex, Long id, int relatedDtoIndex) {
		return reader.getAllRelated(originatingDtoIndex, id, relatedDtoIndex);
	}

	@Override
	public ListQueryResult<? extends AbstractDto> fulltextSearchForModule(int dtoIndex, String query, int from, int to) {
		return fulltext.fulltextSearchForModule(dtoIndex, query, from, to);
	}

	@Override
	public void importContacts(List<DtoContact> contacts) {
		log.info("Starting importing " + contacts.size() + " contacts");
		
		int done = 0;
		for (final DtoContact contact: contacts) {
			create(IANA.mashal(DtoContact.class), contact);
			log.fine("Created " + (++done) + " contact(s)");
		}
		
		log.fine("Finished importing " + contacts.size() + " contacts");
	}

	@Override
	public void feedback(String message) {
		email.feedback(message);
	}
	
	@Override
	public Map<Integer, Double> getAnnuallyOfferingVolumes() {
		return reporter.getAnnuallyOfferingVolumes();
	}
}
