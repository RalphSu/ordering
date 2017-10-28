package net.samhouse.db.dao;

/**
 * base entity dao interface
 */
public interface EntityDao<E> {
    /**
     * @param entity
     */
    void insert(E entity);

    /**
     * @param entityID
     * @return
     */
    E findById(String entityID);

    /**
     * @param entityID
     */
    void delete(String entityID);
}
