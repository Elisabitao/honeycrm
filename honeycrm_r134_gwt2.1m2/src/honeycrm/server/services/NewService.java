package honeycrm.server.services;

import honeycrm.client.dto.Dto;
import honeycrm.client.dto.ListQueryResult;
import honeycrm.client.dto.ModuleDto;
import honeycrm.server.NewDtoWizard;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public abstract class NewService extends RemoteServiceServlet {
	private final Logger log = Logger.getLogger(NewService.class.toString());
	private static final long serialVersionUID = -4102236506193658058L;
	protected static final DatastoreService db = DatastoreServiceFactory.getDatastoreService();
	protected static final HashMap<String, ModuleDto> configuration = NewDtoWizard.getConfiguration().getModuleDtos();
	protected final CopyMachine copy = new CopyMachine();

	class CopyMachine {
		public ListQueryResult entitiesToDtoArray(String kind, final int count, final Iterable<Entity> entities, boolean isDetailView) {
			int i = 0;
			final Dto[] dtos = new Dto[count];

			for (final Entity entity : entities) {
				dtos[i++] = entityToDto(kind, entity, isDetailView);
			}

			return new ListQueryResult(dtos, count);
		}

		/**
		 * Converts a server side entity into a client side DTO instance. Referenced entities are resolved and stored in special *_resolved fields of the DTO. Key lists are resolved, too.
		 * 
		 * @param kind
		 *            Kind of the entity i.e. the name of the related domain class (e.g. "Product").
		 * @param entity
		 *            Instance of the entity that should be converted to a DTO.
		 * @param isDetailView
		 *            True if the DTO should contain all data necessary for displaying it in the detail view.
		 * @param resolvDepth
		 *            Specifies in how many layers a resolution of referenced entities occurred. This has to be specified to be able to limit the number of resolve-steps. This value is incremented before each new resolve step.
		 * @return A DTO object based on the given entity.
		 */
		private Dto entityToDto(final String kind, final Entity entity, final boolean isDetailView, final int resolvDepth) {
			if (null == entity) {
				return null;
			}

			final Dto dto = new Dto();
			dto.setModule(kind);
			dto.setId(entity.getKey().getId());

			for (final Map.Entry<String, Object> entry : entity.getProperties().entrySet()) {
				if ("id".equals(entry.getKey())) {
					continue; // skip this field to avoid overriding a field that has already been set
				}

				final String fieldName = entry.getKey();

				if (!isDetailView && !configuration.get(kind).isListViewField(fieldName)) {
					// skip this field because we are in the list view mode but the current field is not visible in the list view.
					// this is used to save bandwidth.
					continue;
				}

				dto.set(fieldName, (Serializable) entry.getValue());
			}

			if (resolvDepth <= 2) {
				// resolve at most 3 times e.g. Contract -> Unique Services -> Product
				resolveRelatedEntities(dto, entity, 1 + resolvDepth);
			}
			if (isDetailView) {
				resolveKeyLists(dto, entity);
			}

			return dto;
		}

		public Dto entityToDto(final String kind, final Entity entity, final boolean isDetailView) {
			return entityToDto(kind, entity, isDetailView, 0);
		}

		private void resolveKeyLists(Dto dto, Entity entity) {
			try {
				for (final Map.Entry<String, String> oneToManyEntry : configuration.get(dto.getModule()).getOneToManyMappings().entrySet()) {
					final String field = oneToManyEntry.getKey();

					final ArrayList<Dto> children = new ArrayList<Dto>();
					final Iterable<Key> keys = (Iterable<Key>) entity.getProperty(field);

					if (null != keys) {
						for (final Map.Entry<Key, Entity> entry : db.get(keys).entrySet()) {
							final String kindOfChild = entry.getValue().getKind();
							final Dto child = entityToDto(kindOfChild, entry.getValue(), false);
							children.add(child);
						}
					}

					dto.set(field, children);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void resolveRelatedEntities(Dto dto, Entity entity, final int resolvDepth) {
			for (final Map.Entry<String, String> entry : configuration.get(dto.getModule()).getRelateFieldMappings().entrySet()) {
				final String fieldName = entry.getKey();
				final String relatedEntityName = entry.getValue();

				if (null == entity.getProperty(fieldName)) {
					// No Key has been stored yet. Set the relate field to 0 to express that on the client side.
					dto.set(fieldName, 0L);
				} else if (!(entity.getProperty(fieldName) instanceof Key)) {
					// check if Entity has been stored properly!
					log.warning("Expected instance of " + Key.class + ". Found " + entity.getProperty(fieldName).getClass() + ". Skipping field.");
				} else {
					final long id = ((Key) entity.getProperty(fieldName)).getId();

					if (id > 0) {
						/**
						 * retrieve the referenced entity and copy its dto representation as an additional field into the originating dto object.
						 */
						final Dto relatedEntity = get(relatedEntityName, id, 1 + resolvDepth);

						if (null == relatedEntity) {
							log.warning(dto.getModule() + "/" + dto.getId() + " references a no-longer existing entity " + relatedEntityName + "/" + id);
							dto.set(fieldName, 0);
							// TODO should we persist this change of the Dto object?
						} else {
							dto.set(fieldName, relatedEntity.getId());
							dto.set(fieldName + "_resolved", relatedEntity);
						}
					}
				}
			}
		}

		/**
		 * Converts a given Dto to an Entity. This is usually the case during create/update operations.
		 */
		public Entity dtoToEntity(final Dto dto) {
			final boolean entityExists = dto.getId() > 0;
			final Entity entity = entityExists ? new Entity(KeyFactory.createKey(dto.getModule(), dto.getId())) : new Entity(dto.getModule());

			final HashMap<String, String> relationMap = configuration.get(dto.getModule()).getRelateFieldMappings();
			final HashMap<String, String> oneToManyMap = configuration.get(dto.getModule()).getOneToManyMappings();

			for (final Map.Entry<String, Serializable> entry : dto.getAllData().entrySet()) {
				final String fieldName = entry.getKey();

				if (null == entry.getValue()) {
					entity.setProperty(fieldName, null);
				} else if ("id".equals(fieldName)) {
					// Has already been set.
				} else if (fieldName.endsWith("_resolved")) {
					// Skip already resolved fields. We only want to store their id.
				} else if (relationMap.containsKey(fieldName)) {
					// This is a relate field.
					// Want to store value as Key value. Create a new Key for the referenced entity.
					if (entry.getValue() instanceof Long && (Long) entry.getValue() > 0) {
						final String relatedEntity = relationMap.get(fieldName);
						final Key key = KeyFactory.createKey(relatedEntity, (Long) entry.getValue());

						entity.setProperty(fieldName, key);
					} else {
						// No valid id has been set. Null the property.
						entity.setProperty(fieldName, null);
					}
				} else if (oneToManyMap.containsKey(fieldName)) {
					// This is a one to many field.
					// Create all referenced items and store a list containing their keys in the entity we wanted to create in the first place.
					final ArrayList<Key> keys = new ArrayList<Key>();

					for (final Dto item : (ArrayList<Dto>) entry.getValue()) {
						keys.add(db.put(dtoToEntity(item)));
					}

					entity.setProperty(fieldName, keys);
				} else {
					entity.setProperty(fieldName, entry.getValue());
				}
			}

			return entity;
		}
	}

	private Dto get(String kind, long id, final int resolvDepth) {
		try {
			return copy.entityToDto(kind, db.get(KeyFactory.createKey(kind, id)), true, resolvDepth);
		} catch (EntityNotFoundException e) {
			log.warning(kind + "/" + id + " could not be found - An EntityNotFoundException occured.");
			// e.printStackTrace();
			return null;
		}
	}

	public Dto get(String kind, long id) {
		return get(kind, id, 0);
	}
}
