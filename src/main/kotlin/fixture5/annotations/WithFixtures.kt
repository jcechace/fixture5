package fixture5.annotations

import fixture5.TestFixture
import kotlin.reflect.KClass

/**
 * Fixture declaration
 *
 * @author Jakub Cechacek
 *
 * @param value fixture types used by annotated test class
 */
@Retention
@Target(AnnotationTarget.CLASS)
annotation class WithFixtures(vararg val value: KClass<out TestFixture>)
