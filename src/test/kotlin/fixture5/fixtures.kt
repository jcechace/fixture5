package fixture5

import fixture5.annotations.FixtureContext
import org.junit.jupiter.api.extension.ExtensionContext.Store
import kotlin.properties.Delegates

data class TestData(val value: Int = 42)

class ExampleFixture(store: Store) : TestFixture(store) {
    var initialised by Delegates.notNull<Boolean>()

    override fun setup() {
        initialised = true
    }

    override fun teardown() {
        initialised = false
    }
}

@FixtureContext(provides = [TestData::class])
class ExampleProviderFixture(store: Store) : TestFixture(store) {
    override fun setup() {
        store(TestData())
    }

    override fun teardown() {}
}


@FixtureContext(requires = [TestData::class])
class ExampleConsumerFixture(store: Store) : TestFixture(store) {
    override fun setup() {}

    override fun teardown() {}
}

class ExampleSetupThrowingFixture(store: Store) : TestFixture(store) {
    override fun setup() {
        throw RuntimeException("Oopsie Daisy!")
    }

    override fun teardown() {}
}

class ExampleTeardownThrowingFixture(store: Store) : TestFixture(store) {
    override fun setup() {}

    override fun teardown() {
        throw RuntimeException("Oopsie Daisy!")
    }
}

class CountingSetupFixture(store: Store) : TestFixture(store) {
    companion object {
        val list = mutableListOf<CountingSetupFixture>()
        private var counter = 0
    }

    val num = counter++

    override fun setup() {
        list.add(this)
    }

    override fun teardown() {}
}

class CountingTeardownFixture(store: Store) : TestFixture(store) {
    companion object {
        val list = mutableListOf<CountingTeardownFixture>()
        private var counter = 0
    }

    val num = counter++

    override fun setup() {}

    override fun teardown() {
        list.add(this)
    }
}
