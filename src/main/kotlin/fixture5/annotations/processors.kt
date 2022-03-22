@file:Suppress("MemberVisibilityCanBePrivate")
@file:OptIn(ExperimentalStdlibApi::class)

package fixture5.annotations

import fixture5.TestFixture
import fixture5.exceptions.FixtureException
import org.junit.jupiter.api.extension.ExtensionContext.Store
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.findAnnotations
import kotlin.reflect.full.superclasses


/**
 * Creates instance of [FixtureProcessor] for given [type] and its direct superclasses.
 * [FixtureProcessor.setupFixtures] is called before returning
 *
 * @param type root type to start scanning for [Fixture] annotations
 * @param store extension context store passed to fixture instances
 * @return fixture processor with setup fixtures
 */
internal fun fixturesOf(type: KClass<*>, store: Store): FixtureProcessor {
    val fixtures = (type.superclasses + type).flatMap { it.findAnnotations<Fixture>() }
    val processor = FixtureProcessor(fixtures, store)
    processor.setupFixtures()
    return processor
}

/**
 * Creates instance of [ContextProcessor] for given type
 */
internal fun fixtureContextOf(type: KClass<*>, store: Store): ContextProcessor? {
    val context = type.findAnnotation<FixtureContext>()
    return context?.let { ContextProcessor(type, it, store) }
}

internal class FixtureProcessor(fixtures: List<Fixture>, val store: Store) {

    val fixtureTypes: List<KClass<out TestFixture>> = fixtures.map { it.value }

    val fixtureObjects: List<TestFixture> by lazy { fixtureTypes.map(this::setupFixture) }

    /**
     * Instantiates all fixture type and calls [TestFixture.setup] on each of them.
     * If fixture type is annotated with [FixtureContext] the contract is checked
     *
     * @throws [FixtureException] if error occurs
     */
    fun setupFixtures() = fixtureObjects


    /**
     * Calls [TestFixture.teardown] on each fixture object
     *
     * @throws [FixtureException] if error occurs
     */
    fun teardownFixtures() {
        fixtureObjects.asReversed().forEach(this::teardownFixture)
    }

    private fun setupFixture(type: KClass<out TestFixture>): TestFixture {
        try {
            val fixture = type.constructors.first().call(store)
            val context = fixtureContextOf(type, store)

            context?.requireDependencies()
            context?.verifyProvidedDependencies()

            fixture.setup()

            context?.requireProvidedDependencies()

            return fixture
        } catch (e: Throwable) {
            throw FixtureException("Error while setting up fixture of type $type", e)
        }
    }

    private fun teardownFixture(fixture: TestFixture) {
        try {
            fixture.teardown()
        } catch (e: Throwable) {
            throw FixtureException("Error while tearing down fixture of type ${fixture::class}", e)
        }
    }
}

/**
 * [FixtureContext] processor
 *
 * @param type associated with [context]
 * @param context of [type]
 */
internal class ContextProcessor(val type: KClass<*>, val context: FixtureContext, val store: Store) {
    fun requireDependencies() {
        context.requires.find { store.get(it) == null }?.let {
            throw IllegalStateException("Missing dependency of type $it for Fixture of type $type ")
        }
    }

    fun verifyProvidedDependencies() {
        context.provides.find { store.get(it) != null }?.let {
            throw IllegalStateException("Dependency of type $it is provided by multiple fixtures")
        }
    }

    fun requireProvidedDependencies() {
        (context.provides + context.overrides).find { store.get(it) == null }?.let {
            throw IllegalStateException("Fixture of type $type didn't provide dependency of type $it")
        }
    }
}


