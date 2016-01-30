package engineer.carrot.warren.thump.helper

import com.google.common.base.Predicate

class PredicateHelper {
    class StartsWithPredicate(private val matcher: String) : Predicate<String> {

        override fun apply(input: String?): Boolean {
            return input!!.startsWith(matcher)
        }

        override fun equals(other: Any?): Boolean {
            return this == other
        }

        override fun hashCode(): Int{
            return matcher.hashCode()
        }
    }

    class DoesNotContainPredicate(private val matcher: String) : Predicate<String> {

        override fun apply(input: String?): Boolean {
            return !input!!.contains(matcher)
        }

        override fun equals(other: Any?): Boolean {
            return this == other
        }

        override fun hashCode(): Int{
            return matcher.hashCode()
        }
    }
}
