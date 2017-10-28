package net.samhouse.db.service;

/**
 * @param <E>
 */
public interface EntityService<E> {
    /**
     * @param entity
     */
    void insert(E entity);

    /**
     * @param orderID
     * @return
     */
    E getById(String orderID);

    /**
     * @param orderID
     */
    void delete(String orderID);
}
