package fixture5

import fixture5.annotations.Fixture
import fixture5.annotations.FixtureContext
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext.Store

const val INT_VALUE = 42

data class TestData(val value: Int = 42)

@FixtureContext(provides = [TestData::class])
class ExampleProviderFixture(store: Store) : TestFixture(store) {
    override fun setup() {
        store(TestData())
    }

    override fun teardown() {
    }
}


@FixtureContext(provides = [Int::class], requires = [TestData::class])
class ExampleConsumerFixture(store: Store) : TestFixture(store) {
    override fun setup() {
        store(Int::class, INT_VALUE)
    }

    override fun teardown() {
    }
}

@ExtendWith(FixtureExtension::class)
@Fixture(ExampleProviderFixture::class)
@Fixture(ExampleConsumerFixture::class)
open class FixtureInjectionTest(protected val num: Int) {

    @Test
    fun `should inject through parameter`(data: TestData) {
        Assertions.assertNotNull(data)
        Assertions.assertEquals(42, data.value)
    }

    @Test
    fun `should inject through constructor`() {
        Assertions.assertEquals(INT_VALUE, num)
    }
}

class FixtureExtensionInheritanceTest(num: Int) : FixtureInjectionTest(num) {
    @Test
    fun `should inject from parent through parameter`(data: TestData) {
        Assertions.assertNotNull(data)
        Assertions.assertEquals(42, data.value)
    }

    @Test
    fun `should inject from parent through constructor`() {
        Assertions.assertEquals(INT_VALUE, num)
    }
}