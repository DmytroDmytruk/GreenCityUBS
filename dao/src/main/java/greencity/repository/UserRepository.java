package greencity.repository;

import greencity.entity.user.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import greencity.entity.user.ubs.UBSuser;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    /**
     * Method returns user by user uuid.
     *
     * @param uuid {@link String} - id to connect 2 db.
     * @return {@link User} - current user.
     */
    User findByUuid(String uuid);

    /**
     * Method returns user by user uuid.
     *
     * @param uuid {@link String} - id to connect 2 db.
     * @return optional of {@link User} - current user.
     */
    Optional<User> findUserByUuid(String uuid);

    /**
     * Method returns total user violations.
     *
     * @param userId {@link Long} - id to connect 2 db.
     * @return number of {@link User} violations.
     */
    @Query(nativeQuery = true,
        value = "SELECT COUNT(user_id) FROM violations_description_mapping as v where v.user_id = :userId")
    int countTotalUsersViolations(Long userId);

    /**
     * Method returns 1 if user has violations for the current order made by user or
     * 0 if there are no violations.
     *
     * @param userId  {@link Long} - id to connect 2 db.
     * @param orderId {@link Long}
     * @return number of {@link User} violations.
     */
    @Query(nativeQuery = true, value = "SELECT CAST(CASE WHEN EXISTS "
        + "(SELECT TRUE FROM violations_description_mapping "
        + " AS v WHERE v.user_id = :userId and v.order_id = :orderId)\n"
        + " THEN 1 ELSE 0 END AS INT);")
    int checkIfUserHasViolationForCurrentOrder(Long userId, Long orderId);

    /**
     * Method that count orders.
     *
     * @author Struk Nazariy
     */
    @Query(nativeQuery = true, value = "select count(*) from orders")
    int orderCounter();

    /**
     * Method that count orders.
     *
     * @author Struk Nazariy
     */
    @Query(nativeQuery = true, value = "select count(*) from orders")
    int orderCounterForSorting();

    /**
     * Method that count orders.
     *
     * @author Struk Nazariy
     */
    @Query(nativeQuery = true, value = "SELECT users.* "
        + "FROM orders as o "
        + "JOIN users ON o.users_id = users.id "
        + "WHERE o.id = :orderId")
    Optional<User> findUserByOrderId(Long orderId);

    /**
     * Finds list of User who have not paid of the order within three days.
     *
     * @param localDate - date when the user made an order.
     * @return a {@link List} of {@link User} - which need to send a message.
     */
    @Query(nativeQuery = true,
        value = "SELECT * FROM users u INNER JOIN orders o ON u.id = o.users_id "
            + "WHERE CAST(o.order_date AS DATE) < :localDate AND o.order_status LIKE 'FORMED'")
    List<User> getAllUsersWhoHaveNotPaid(LocalDate localDate);

    /**
     * Finds list of User who have no orders after {@param localDate}.
     *
     * @param toDate - date after which user have no orders.
     * @return a {@link List} of {@link User} - which have no orders after
     *         {@param localDate}.
     */
    @Query(nativeQuery = true,
        value = "SELECT * FROM users as u INNER JOIN orders as o ON u.id = o.users_id "
            + "WHERE (SELECT COUNT(id) FROM orders WHERE CAST(o.order_date AS DATE) < :toDate "
            + "AND CAST(o.order_date AS DATE) > :fromDate)!=0")
    List<User> getAllInactiveUsers(LocalDate fromDate, LocalDate toDate);
}
