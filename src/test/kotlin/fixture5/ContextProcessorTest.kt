package fixture5

import fixture5.annotations.fixtureContextOf
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext
import kotlin.test.assertNotNull
import kotlin.test.assertNull


@ExtendWith(MockKExtension::class)
class ContextProcessorTest {

    @MockK
    lateinit var store: ExtensionContext.Store

    @Test
    fun `should create context processor on annotated class`() {
        val processor = fixtureContextOf(ExampleConsumerFixture::class, store)
        assertNotNull(processor)
    }

    @Test
    fun `should return null on class without annotation`() {
        val processor = fixtureContextOf(ExampleFixture::class, store)
        assertNull(processor)
    }

    @Test
    fun `should throw on missing required dependency`() {
        every { store.get(TestData::class) } returns null

        val processor = fixtureContextOf(ExampleConsumerFixture::class, store)!!
        assertThrows<IllegalStateException> { processor.requireDependencies() }
    }

    @Test
    fun `should pass when required dependency is provided`() {
        every { store.get(TestData::class) } returns TestData()

        val processor = fixtureContextOf(ExampleConsumerFixture::class, store)!!
        assertDoesNotThrow { processor.requireDependencies() }
    }

    @Test
    fun `should throw on missing provided dependency`() {
        every { store.get(TestData::class) } returns null

        val processor = fixtureContextOf(ExampleProviderFixture::class, store)!!
        assertThrows<IllegalStateException> { processor.requireProvidedDependencies() }
    }

    @Test
    fun `should pass when provided dependency is stored`() {
        every { store.get(TestData::class) } returns TestData()

        val processor = fixtureContextOf(ExampleProviderFixture::class, store)!!
        assertDoesNotThrow { processor.requireProvidedDependencies() }
    }

    @Test
    fun `should throw on exiting provided dependency`() {
        every { store.get(TestData::class) } returns TestData()

        val processor = fixtureContextOf(ExampleProviderFixture::class, store)!!
        assertThrows<IllegalStateException> { processor.verifyProvidedDependencies() }
    }
}