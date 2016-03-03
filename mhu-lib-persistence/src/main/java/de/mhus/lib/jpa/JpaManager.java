package de.mhus.lib.jpa;

import java.util.Map;
import java.util.UUID;

import javax.persistence.Cache;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.metamodel.Metamodel;

import de.mhus.lib.core.lang.MObject;


/**
 * <p>JpaManager class.</p>
 *
 * @author mikehummel
 * @version $Id: $Id
 */
public class JpaManager extends MObject implements EntityManagerFactory {

	private JpaSchema schema;
	private JpaProperties properties;
	private EntityManagerFactory entityManagerFactory;
	private UUID unitId;

	/**
	 * <p>Constructor for JpaManager.</p>
	 *
	 * @param properties a {@link de.mhus.lib.jpa.JpaProperties} object.
	 */
	public JpaManager(JpaProperties properties) {
		this(properties, null);
	}

	/**
	 * <p>Constructor for JpaManager.</p>
	 *
	 * @param properties a {@link de.mhus.lib.jpa.JpaProperties} object.
	 * @param schema a {@link de.mhus.lib.jpa.JpaSchema} object.
	 */
	public JpaManager(JpaProperties properties, JpaSchema schema) {
		if (schema != null) properties.setSchema(schema);
		this.schema = properties.getSchema();
		this.properties = properties;
		initJpa();
	}

	/**
	 * <p>initJpa.</p>
	 */
	protected void initJpa() {
		log().t("init");
		unitId = UUID.randomUUID();
		schema.doInit(properties.getConfig());
		properties.configureTypes();
		entityManagerFactory = Persistence.createEntityManagerFactory(schema.getSchemaName() + "-" + unitId.toString(), properties);
		schema.doPostInit(this);
	}

	/**
	 * <p>Getter for the field <code>schema</code>.</p>
	 *
	 * @return a {@link de.mhus.lib.jpa.JpaSchema} object.
	 */
	public JpaSchema getSchema() {
		return schema;
	}

	/** {@inheritDoc} */
	@Override
	public JpaEntityManager createEntityManager() {
		return new JpaEntityManager(this, entityManagerFactory, null);
	}

	/** {@inheritDoc} */
	@Override
	public void close() {
		if (entityManagerFactory == null) return;
		log().t("close");
		entityManagerFactory.close();
		entityManagerFactory = null;
	}

	/** {@inheritDoc} */
	@Override
	public EntityManager createEntityManager(@SuppressWarnings("rawtypes") Map map) {
		log().t("create entity manager",map);
		return new JpaEntityManager(this, entityManagerFactory, map);
	}

	/** {@inheritDoc} */
	@Override
	public CriteriaBuilder getCriteriaBuilder() {
		return entityManagerFactory.getCriteriaBuilder();
	}

	/** {@inheritDoc} */
	@Override
	public Metamodel getMetamodel() {
		return entityManagerFactory.getMetamodel();
	}

	/** {@inheritDoc} */
	@Override
	public boolean isOpen() {
		return entityManagerFactory != null && entityManagerFactory.isOpen();
	}

	/** {@inheritDoc} */
	@Override
	public Map<String, Object> getProperties() {
		return entityManagerFactory.getProperties();
	}

	/** {@inheritDoc} */
	@Override
	public Cache getCache() {
		return entityManagerFactory.getCache();
	}

	/** {@inheritDoc} */
	@Override
	public PersistenceUnitUtil getPersistenceUnitUtil() {
		return entityManagerFactory.getPersistenceUnitUtil();
	}

}
