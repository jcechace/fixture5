package fixture5.annotations

import fixture5.TestFixture
import kotlin.reflect.KClass

/**
 * Fixture declaration
 *
 * @author Jakub Cechacek
 *
 * @param value fixture type
 */
@Retention
@Repeatable
@Target(AnnotationTarget.CLASS)
annotation class Fixture(val value: KClass<out TestFixture>)