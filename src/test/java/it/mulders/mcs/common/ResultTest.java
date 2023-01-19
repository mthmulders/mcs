package it.mulders.mcs.common;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ResultTest implements WithAssertions {
    @Nested
    class Success {
        @Test
        void ifPresent() {
            // Arrange
            var x = new AtomicReference<>(false);
            var result = new Result.Success<>("foo");

            // Act
            result.ifPresent(r -> {
                assertThat(r).isEqualTo("foo");
                x.set(true);
            });

            // Assert
            assertThat(x).withFailMessage("expect consumer to be called").hasValue(true);
        }

        @Test
        void map() {
            // Arrange
            var input = new Result.Success<>("foo");

            // Act
            var result = input.map(String::toUpperCase);

            // Assert
            assertThat(result.value()).isEqualTo("FOO");
        }

        @Test
        void map_consumer_throws_exception() {
            // Arrange
            var input = new Result.Success<>("foo");

            // Act
            var result = input.map(a -> {
                throw new NullPointerException();
            });

            // Assert
            assertThatThrownBy(result::value).isInstanceOf(NoRequestedElementException.class);
            assertThat(result.cause()).isInstanceOf(NullPointerException.class);
        }

        @Test
        void cause() {
            // Arrange
            var input = new Result.Success<>("foo");

            // Act

            // Assert
            assertThatThrownBy(input::cause).isInstanceOf(NoRequestedElementException.class);
        }
    }

    @Nested
    class Failure {
        @Test
        void ifPresent() {
            // Arrange
            var x = new AtomicReference<>(false);
            var result = new Result.Failure<>(new Exception());

            // Act
            result.ifPresent(r -> {
                assertThat(r).isEqualTo("foo");
                x.set(true);
            });

            // Assert
            assertThat(x).withFailMessage("expect consumer not to be called").hasValue(false);
        }

        @Test
        void map() {
            // Arrange
            var input = new Result.Failure<String>(new Exception());

            // Act
            var result = input.map(String::toUpperCase);

            // Assert
            assertThatThrownBy(result::value).isInstanceOf(NoRequestedElementException.class);
        }

        @Test
        void cause() {
            // Arrange
            var input = new Result.Failure<>(new Exception() {
            });

            // Act

            // Assert
            assertThat(input.cause()).isInstanceOf(Exception.class);
        }
    }
}