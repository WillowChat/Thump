package engineer.carrot.warren.thump.util.helper;

import com.google.common.base.Predicate;

public class PredicateHelper {
    public static class StartsWithPredicate implements Predicate<String> {
        private String matcher;

        public StartsWithPredicate(String matcher) {
            this.matcher = matcher;
        }

        @Override
        public boolean apply(String input) {
            return input.startsWith(matcher);
        }

        @Override
        public boolean equals(Object object) {
            return this.equals(object);
        }
    }

    public static class DoesNotContainPredicate implements Predicate<String> {
        private String matcher;

        public DoesNotContainPredicate(String matcher) {
            this.matcher = matcher;
        }

        @Override
        public boolean apply(String input) {
            return !input.contains(matcher);
        }

        @Override
        public boolean equals(Object object) {
            return this.equals(object);
        }
    }
}
