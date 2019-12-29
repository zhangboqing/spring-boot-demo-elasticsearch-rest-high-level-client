package com.zbq.springbootelasticsearch.common.elasticsearch.base;

import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Author zhangboqing
 * @Date 2019/12/29
 */
public class ESSort {

    private final List<Order> orders;

    private ESSort(Direction direction, List<String> properties) {

        if (properties == null || properties.isEmpty()) {
            throw new IllegalArgumentException("You have to provide at least one property to sort by!");
        }

        this.orders = properties.stream() //
                .map(it -> new Order(direction, it)) //
                .collect(Collectors.toList());
    }


    public static class Order implements Serializable {

        private final Direction direction;
        private final String property;

        public Order(@Nullable Direction direction, String property) {
            this.direction = direction;
            this.property = property;
        }
    }

    public  enum Direction {
        /**
         * ascending
         */
        ASC,
        /**
         * descending
         */
        DESC;

        /**
         * Returns whether the direction is ascending.
         */
        public boolean isAscending() {
            return this.equals(ASC);
        }

        /**
         * Returns whether the direction is descending.
         */
        public boolean isDescending() {
            return this.equals(DESC);
        }

        /**
         * Returns the {@link Direction} enum for the given {@link String} value.
         *
         * @throws IllegalArgumentException in case the given value cannot be parsed into an enum value.
         */
        public static Direction fromString(String value) {

            try {
                return Direction.valueOf(value.toUpperCase(Locale.US));
            } catch (Exception e) {
                throw new IllegalArgumentException(String.format(
                        "Invalid value '%s' for orders given! Has to be either 'desc' or 'asc' (case insensitive).", value), e);
            }
        }

        /**
         * Returns the {@link Direction} enum for the given {@link String} or null if it cannot be parsed into an enum
         * value.
         */
        public static Optional<Direction> fromOptionalString(String value) {

            try {
                return Optional.of(fromString(value));
            } catch (IllegalArgumentException e) {
                return Optional.empty();
            }
        }
    }
}
