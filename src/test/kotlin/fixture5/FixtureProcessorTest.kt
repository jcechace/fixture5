package fixture5

import fixture5.annotations.Fixture
import fixture5.annotations.FixtureProcessor
import fixture5.exceptions.FixtureException
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext
import kotlin.test.*

@ExtendWith(MockKExtension::class)
class FixtureProcessorTest {

    @MockK
    lateinit var store: ExtensionContext.Store

    @Test
    fun `should resolve fixture types`() {
        val fixtures = listOf(
            Fixture(ExampleConsumerFixture::class), Fixture(ExampleProviderFixture::class)
        )
        val processor = FixtureProcessor(fixtures, store)

        assertContains(processor.fixtureTypes, ExampleConsumerFixture::class)
        assertContains(processor.fixtureTypes, ExampleProviderFixture::class)
    }

    @Test
    fun `should throw on setup error`() {
        val fixtures = listOf(Fixture(ExampleSetupThrowingFixture::class))
        val processor = FixtureProcessor(fixtures, store)

        assertThrows<FixtureException> { processor.setupFixtures() }
    }

    @Test
    fun `should throw on teardown error`() {
        val fixtures = listOf(Fixture(ExampleTeardownThrowingFixture::class))
        val processor = FixtureProcessor(fixtures, store)

        processor.setupFixtures()
        assertThrows<FixtureException> { processor.teardownFixtures() }
    }

    @Test
    fun `should create fixture object`() {
        val fixtures = listOf(Fixture(ExampleFixture::class))
        val processor = FixtureProcessor(fixtures, store)

        processor.setupFixtures()

        val fixtureObject = processor.fixtureObjects.firstOrNull()
        assertNotNull(fixtureObject, "Expected fixture object")
        assertTrue(fixtureObject is ExampleFixture, "Fixture object should be ${ExampleFixture::class}")
        assertTrue(fixtureObject.initialised, "Fixture object should be initialised")
    }

    @Test
    fun `should create fixture object implicitly`() {
        val fixtures = listOf(Fixture(ExampleFixture::class))
        val processor = FixtureProcessor(fixtures, store)
        val fixtureObject = processor.fixtureObjects.firstOrNull()

        assertNotNull(fixtureObject, "Expected fixture object")
        assertTrue(fixtureObject is ExampleFixture, "Fixture object should be ${ExampleFixture::class}")
        assertTrue(fixtureObject.initialised, "Fixture object should be set up")
    }

    @Test
    fun `should teardown fixture object`() {
        val fixtures = listOf(Fixture(ExampleFixture::class))
        val processor = FixtureProcessor(fixtures, store)
        val fixtureObject = processor.fixtureObjects.first() as ExampleFixture

        processor.teardownFixtures()
        assertFalse("Fixture object should be torn down") { fixtureObject.initialised }
    }

    @Test
    fun `should setup fixtures in order`() {
        val fixtures = listOf(Fixture(CountingSetupFixture::class), Fixture(CountingSetupFixture::class))
        val processor = FixtureProcessor(fixtures, store)
        processor.setupFixtures()

        assertContentEquals(listOf(0, 1), CountingSetupFixture.list.map { it.num })
    }

    @Test
    fun `should tear down fixtures in reverse order`() {
        val fixtures = listOf(Fixture(CountingTeardownFixture::class), Fixture(CountingTeardownFixture::class))
        val processor = FixtureProcessor(fixtures, store)
        processor.setupFixtures()
        processor.teardownFixtures()

        assertContentEquals(listOf(1, 0), CountingTeardownFixture.list.map { it.num })
    }
}