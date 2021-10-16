package it.mulders.mcs.common;

import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Function;

public interface Result<T> {
    record Success<T>(T value) implements Result<T> {
        @Override
        public <U> Result<U> map(final Function<T, U> mapping) {
            try {
                return new Success<>(mapping.apply(this.value));
            } catch (final Throwable throwable) {
                return new Failure<>(throwable);
            }
        }

        @Override
        public void ifPresent(final Consumer<T> consumer) {
            consumer.accept(this.value);
        }

        @Override
        public Throwable cause() {
            throw new NoSuchElementException("success: " + this.value);
        }
    }

    record Failure<T>(Throwable cause) implements Result<T> {
        @Override
        public <U> Result<U> map(final Function<T, U> mapping) {
            return (Failure<U>) this;
        }

        @Override
        public void ifPresent(final Consumer<T> consumer) {
        }

        @Override
        public T value() {
            throw new NoSuchElementException("failure: " + this.cause.getLocalizedMessage());
        }
    }

    <U> Result<U> map(final Function<T, U> mapping);

    void ifPresent(final Consumer<T> consumer);

    T value();

    Throwable cause();
}
