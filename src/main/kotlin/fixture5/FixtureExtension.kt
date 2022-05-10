package fixture5

import fixture5.annotations.FixtureProcessor
import fixture5.annotations.fixturesOf
import org.junit.jupiter.api.extension.*
import org.junit.jupiter.api.extension.ExtensionContext.Namespace

/**
 * Fixture Extension provides the ability to declare Test Fixtures in declarative way.
 * Test fixtures are objects which are initialised and torn down as part of test life-cycle.
 * Fixtures can act as dependency producers,
 * these dependencies can then be injected either via constructor or method parameters into test instance
 *
 * [fixture5.annotations.Fixture] annotation is used to declare fixture types on top of test class
 */
class FixtureExtension : BeforeAllCallback, AfterAllCallback, ParameterResolver {

    private lateinit var fixtures: FixtureProcessor

    companion object {
        val STORE: Namespace = Namespace.create(FixtureExtension::class)
    }

    private fun ExtensionContext.fixtureObjectStore() = getStore(STORE)

    /**
     * Instantiates and sets up all declared fixtures of current test class
     */
    override fun beforeAll(context: ExtensionContext) {
        fixtures = fixturesOf(context.requiredTestClass.kotlin, store =  context.fixtureObjectStore())
    }

    override fun afterAll(context: ExtensionContext) {
        fixtures.teardownFixtures()
    }

    override fun supportsParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Boolean {
        val store = extensionContext.fixtureObjectStore()
        val type = parameterContext.parameter.type.kotlin
        return store.get(type) != null
    }

    override fun resolveParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Any {
        val store = extensionContext.fixtureObjectStore()
        val type = parameterContext.parameter.type.kotlin
        return store.get(type, type.java)
    }
}