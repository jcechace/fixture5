package fixture5

import org.junit.jupiter.api.extension.ExtensionContext.Store
import kotlin.jvm.Throws
import kotlin.reflect.KClass

/**
 * Base class for fixtures
 *
 * @author Jakub Cechacek
 *
 * @param store extension store used to store and retrieve dependencies across all fixtures
 */
abstract class TestFixture(private val store: Store) {

    /**
     * Used to initialise fixture instance. Called before test's [org.junit.jupiter.api.BeforeAll] methods
     */
    @Throws(Exception::class)
    abstract fun setup()

    /**
     * Used to clean up fixture instance. Called after test's [org.junit.jupiter.api.AfterAll] methods
     */
    @Throws(Exception::class)
    abstract fun teardown()

    /**
     * Stores object in extension store shared by all fixtures.
     * The object is stored under the key corresponding to its direct class
     *
     * @param value object to be stored
     */
    fun store(value: Any) = store.put(value::class, value)


    /**
     * Stores object in extension store shared by all fixtures.
     * The object is stored under given type as the key
     *
     * @param type key reference
     * @param value object to be stored
     */
    fun <T : Any> store(type: KClass<in T>, value: T) = store.put(type, value)

    /**
     * See [store]
     */
    fun <T : Any> store(type: Class<in T>, value: T) = store(type.kotlin, value)

    /**
     * Retrieves object from extension store shared by all fixtures.
     *
     * @param type type of desired object or null if not present in the store
     */
    fun <T : Any> retrieve(type: KClass<T>): T? = store.get(type, type.java)

    /**
     * See [retrieve]
     */
    fun <T : Any> retrieve(type: Class<T>): T? = retrieve(type.kotlin)
}