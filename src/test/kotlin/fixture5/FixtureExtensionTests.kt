package fixture5

import fixture5.annotations.Fixture
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith


@ExtendWith(FixtureExtension::class)
@Fixture(ExampleProviderFixture::class)
@Fixture(ExampleConsumerFixture::class)
open class FixtureInjectionTest(protected val constructorData: TestData) {

    @Test
    fun `should inject through parameter`(paramData: TestData) {
        Assertions.assertNotNull(paramData)
        Assertions.assertEquals(42, paramData.value)
    }

    @Test
    fun `should inject through constructor`() {
        Assertions.assertEquals(42, constructorData.value)
    }
}

class FixtureExtensionInheritanceTest(constructorData: TestData) : FixtureInjectionTest(constructorData) {
    @Test
    fun `should inject from parent through parameter`(data: TestData) {
        Assertions.assertNotNull(data)
        Assertions.assertEquals(42, data.value)
    }

    @Test
    fun `should inject from parent through constructor`() {
        Assertions.assertEquals(42, constructorData.value)
    }
}
