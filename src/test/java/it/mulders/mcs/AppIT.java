package it.mulders.mcs;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOut;
import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable;
import static java.util.Arrays.asList;

import java.util.List;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.*;
import org.junitpioneer.jupiter.StdIo;
import org.junitpioneer.jupiter.StdOut;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class AppIT implements WithAssertions {
    @Nested
    class TechnicalIT {
        @BeforeEach
        void clearProxyProperties() {
            System.clearProperty("http.proxyHost");
            System.clearProperty("http.proxyPort");
            System.clearProperty("https.proxyHost");
            System.clearProperty("https.proxyPort");
        }

        @Test
        void should_show_version() throws Exception {
            var output = tapSystemOut(() -> App.doMain("-V"));
            assertThat(output).contains("mcs v");
        }

        @Test
        void should_exit_cleanly() {
            assertThat(App.doMain("-V")).isEqualTo(0);
        }

        @Test
        void should_exit_nonzero_on_wrong_invocation() {
            assertThat(App.doMain("--does-not-exist")).isNotEqualTo(0);
        }

        @Test
        void runs_without_search_command_specified() {
            assertThat(App.doMain("info.picocli:picocli")).isEqualTo(0);
        }

        @Test
        void should_not_set_proxy_system_properties_when_no_env_variable_is_present() throws Exception {
            List<String> values = withEnvironmentVariable("HTTP_PROXY", null)
                    .and("HTTPS_PROXY", null)
                    .execute(() -> {
                        App.doMain("info.picocli:picocli");

                        return asList(
                                System.getProperty("http.proxyHost"),
                                System.getProperty("http.proxyPort"),
                                System.getProperty("https.proxyHost"),
                                System.getProperty("https.proxyPort"));
                    });

            assertThat(values).isEqualTo(asList(null, null, null, null));
        }

        @Test
        void should_set_proxy_system_properties_when_env_variables_are_present() throws Exception {
            List<String> values = withEnvironmentVariable("HTTP_PROXY", "http://http.proxy.example.com:8080")
                    .and("HTTPS_PROXY", "http://https.proxy.example.com:8484")
                    .execute(() -> {
                        App.doMain("info.picocli:picocli");

                        return asList(
                                System.getProperty("http.proxyHost"),
                                System.getProperty("http.proxyPort"),
                                System.getProperty("https.proxyHost"),
                                System.getProperty("https.proxyPort"));
                    });

            assertThat(values).isEqualTo(asList("http.proxy.example.com", "8080", "https.proxy.example.com", "8484"));
        }
    }

    @Nested
    class FunctionalIT {
        @StdIo
        @Test
        void should_find_plexus_utils_341(StdOut out) {
            App.doMain("search", "org.codehaus.plexus:plexus-utils:3.4.1");

            var output = out.capturedLines();

            assertThat(output).anySatisfy(line -> assertThat(line).contains("<groupId>org.codehaus.plexus</groupId>"));
            assertThat(output).anySatisfy(line -> assertThat(line).contains("<artifactId>plexus-utils</artifactId>"));
            assertThat(output).anySatisfy(line -> assertThat(line).contains("<version>3.4.1</version>"));
        }

        @StdIo
        @Test
        void should_find_plexus_utils_341_without_search(StdOut out) {
            App.doMain("org.codehaus.plexus:plexus-utils:3.4.1");

            var output = out.capturedLines();

            assertThat(output).anySatisfy(line -> assertThat(line).contains("<groupId>org.codehaus.plexus</groupId>"));
            assertThat(output).anySatisfy(line -> assertThat(line).contains("<artifactId>plexus-utils</artifactId>"));
            assertThat(output).anySatisfy(line -> assertThat(line).contains("<version>3.4.1</version>"));
        }

        @StdIo
        @Test
        void should_find_multiple_jreleaser_maven_plugin(StdOut out) {
            App.doMain("search", "org.jreleaser:jreleaser-maven-plugin");

            var output = out.capturedLines();

            assertThat(output).anySatisfy(line -> assertThat(line).matches("Found (\\d*) results \\(showing 20\\)"));
            assertThat(output)
                    .anySatisfy(line -> assertThat(line).contains("org.jreleaser:jreleaser-maven-plugin:1.16.0"));}

        @StdIo
        @Test
        void should_find_many_artifacts_for_JAX_WS_Handler(StdOut out) {
            App.doMain("class-search", "-f", "javax.xml.ws.handler.Handler", "-l", "250");

            var output = out.capturedLines();

            assertThat(output).hasSizeGreaterThan(250);
            assertThat(output).anySatisfy(line -> assertThat(line).contains("jakarta.xml.ws:jakarta.xml.ws-api:2.3.3"));
        }

        @StdIo
        @Test
        void should_find_artifacts_for_Clocky_class(StdOut out) {
            App.doMain("class-search", "AdvanceableTime");

            var output = out.capturedLines();

            assertThat(output).anySatisfy(line -> assertThat(line).contains("it.mulders.clocky:clocky:0.4"));
            assertThat(output).anySatisfy(line -> assertThat(line).contains("it.mulders.clocky:clocky:0.4.1"));
        }

        @StdIo
        @Test
        void should_find_artifacts_for_Clocky_full_class_name(StdOut out) {
            App.doMain("class-search", "-f", "it.mulders.clocky.AdvanceableTime");

            var output = out.capturedLines();

            assertThat(output).anySatisfy(line -> assertThat(line).contains("it.mulders.clocky:clocky:0.4"));
            assertThat(output).anySatisfy(line -> assertThat(line).contains("it.mulders.clocky:clocky:0.4.1"));
        }
    }
}
