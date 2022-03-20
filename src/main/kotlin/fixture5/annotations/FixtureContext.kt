package fixture5.annotations

import kotlin.reflect.KClass

/**
 * Fixture metadata
 *
 * @author Jakub Cechacek
 *
 * @param requires array of dependency types required by annotated fixture
 * @param provides array of dependency types provided by annotated fixture
 * @param overrides same as [provides] with ability to override already provided dependencies
 */
@Retention
@Target(AnnotationTarget.CLASS)
annotation class FixtureContext(
    val requires: Array<KClass<*>> = [],
    val provides: Array<KClass<*>> = [],
    val overrides: Array<KClass<*>> = [],
)
