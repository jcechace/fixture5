package fixture5

import fixture5.annotations.FixtureContext
import org.junit.jupiter.api.extension.ExtensionContext
import kotlin.properties.Delegates

data class TestData(val value: Int = 42)

class ExampleFixture(store: ExtensionContext.Store): TestFixture(store) {
    var initialised by Delegates.notNull<Boolean>()

    override fun setup() {
        initialised = true
    }

    override fun teardown() {
        initialised = false
    }
}

@FixtureContext(provides = [TestData::class])
class ExampleProviderFixture(store: ExtensionContext.Store) : TestFixture(store) {
    override fun setup() {
        store(TestData())
    }

    override fun teardown() {
        // no-op
    }
}


@FixtureContext(requires = [TestData::class])
class ExampleConsumerFixture(store: ExtensionContext.Store) : TestFixture(store) {
    override fun setup() {
        // no-op
    }

    override fun teardown() {
        // no-op
    }
}

class ExampleSetupThrowingFixture(store: ExtensionContext.Store) : TestFixture(store) {
    override fun setup() {
        throw RuntimeException("Oopsie Daisy!")
    }

    override fun teardown() {
        // no-op
    }
}

class ExampleTeardownThrowingFixture(store: ExtensionContext.Store) : TestFixture(store) {
    override fun setup() {
        // no-op
    }

    override fun teardown() {
        throw RuntimeException("Oopsie Daisy!")
    }
}