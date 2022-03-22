Fixture5: Declarative Test Fixtures for JUnit5
===

This extension allows for creation of encapsulated test fixtures and their declarative use with JUnit5 Jupiter. Fixture5 helps in situation when test lasses need to
perform complex setup in ``@BeforeAll`` methods.

Fixtures
---
A fixture is simply a class extending ``TestFixture``

```kotlin
class HttpClientFixture(store: ExtensionContext.Store) : TestFixture(store) {
    override fun setup() {
        // setup code here
    }

    override fun teardown() {
        // teardown code here
    }
}
```

Fixture classes must adhere to the following

- The class has a single constructor matching that of ``TestFixture`` class
- The class implements ``setup`` method
- The class implements ``teardown`` method

## Using Fixtures
Fixtures can be employed in test by registering the ``FixtureExtension`` and using ``@Fixture`` annotation

```kotlin
@Fixture(HttpClientFixture::class)
@ExtendWith(FixtureExtension::class)
class HttpClientTest {
    // test code here
}
```
Fixtures are processed in order of declaration and for each fixture its ``setup`` method is called before all ``@BeforeAll`` methods.
Similarly, the ``teardown`` method for each fixture is called after all ``@AfterAll`` methods in reverse order (meaning fixture set up first is torn down last )

Providers and Consumers
---
Fixtures can utilise a common store to either provide or consume objects of specified type

```kotlin
override fun setup() {
    val engine = retrieve(Engine::class)
    val plainClient = PlainHttpClient(engine)
    val secureClient = AuthenticatedHttpClient(engine)
    
    store(plainClient)  // will be stored as PlainHttpClient::class
    store(HttpClient::class, secureClient) // or declare provided type explicitly
}
```

Objects provided by fixtures can also be injected into tests either via constructor or method parameters

```kotlin
@Fixture(BlockingEngine::class)
@Fixture(HttpClientFixture::class)
@ExtendWith(FixtureExtension::class)
class HttpClientTest(val client: HttpClient) {
    @Test
    fun `http client should use provided engine`(engine: Engine) {
        assertTrue(client.engine === egine)
    }
}
```
Fixture Context
---
Fixtures can be annotated with ``FixtureContext`` in order to declare (and enforce) their dependency requirements

```kotlin
@FixtureContext(requires = [ Engine::class ])
class HttpClientFixture(store: ExtensionContext.Store) : TestFixture(store) {
    override fun setup() {
        val engine = retrieve(Engine::class)
        val secureClient = AuthenticatedHttpClient(engine)
        
        store(HttpClient::class, secureClient) 
    }

    override fun teardown() {
        // teardown code here
    }
}
```

The following code will result in ``FixtureException`` due to missing dependency on ``Engine::class``

```kotlin
@Fixture(HttpClientFixture::class)
@ExtendWith(FixtureExtension::class)
class HttpClientTest 
```

In similar fashion the extension checks that fixtures provide what they promise

```kotlin
@FixtureContext(
    requires = [ Engine::class ],
    provides = [ HttpClient::class ]
)
class HttpClientFixture(store: ExtensionContext.Store) : TestFixture(store) {
    override fun setup() {
        // nothing here
        
    }

    override fun teardown() {
        // teardown code here
    }
}
```

Using such fixture will also lead to ``FixtureException``
