package it.mulders.mcs;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOut;
import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static it.mulders.mcs.TestUtils.getResourceAsString;
import static java.util.Arrays.asList;

import com.github.tomakehurst.wiremock.core.Options;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import it.mulders.mcs.dagger.Application;
import it.mulders.mcs.dagger.DaggerApplication;
import java.io.IOException;
import java.util.List;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junitpioneer.jupiter.StdErr;
import org.junitpioneer.jupiter.StdIo;
import org.junitpioneer.jupiter.StdOut;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class AppIT implements WithAssertions {
    @RegisterExtension
    static WireMockExtension wiremock = WireMockExtension.newInstance()
            .options(wireMockConfig()
                    .bindAddress("localhost")
                    .dynamicPort()
                    .useChunkedTransferEncoding(Options.ChunkedEncodingPolicy.NEVER))
            .configureStaticDsl(true)
            .build();

    private final Application components = DaggerApplication.builder()
            .centralHostname(wiremock.getRuntimeInfo().getHttpBaseUrl())
            .build();

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
            var output = tapSystemOut(() -> App.doMain(components, "-V"));
            assertThat(output).contains("mcs v");
        }

        @Test
        void should_exit_cleanly() {
            assertThat(App.doMain(components, "-V")).isEqualTo(0);
        }

        @Test
        void should_exit_nonzero_on_wrong_invocation() {
            assertThat(App.doMain(components, "--does-not-exist")).isNotEqualTo(0);
        }

        @Test
        void runs_without_search_command_specified() throws IOException {
            stubFor(get(urlPathMatching("/solrsearch/select*"))
                    .willReturn(ok(getResourceAsString("/it/runs_without_search_command_specified.json"))));
            assertThat(App.doMain(components, "info.picocli:picocli:4.7.7")).isEqualTo(0);
        }

        @Test
        void should_not_set_proxy_system_properties_when_no_env_variable_is_present() throws Exception {
            List<String> values = withEnvironmentVariable("HTTP_PROXY", null)
                    .and("HTTPS_PROXY", null)
                    .execute(() -> {
                        App.doMain(components, "info.picocli:picocli");

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
                        App.doMain(components, "info.picocli:picocli");

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
        void should_find_plexus_utils_341(StdOut out, StdErr err) throws IOException {
            stubFor(get(urlPathMatching("/solrsearch/select*"))
                    .willReturn(ok(getResourceAsString("/it/should_find_plexus_utils_341.json"))));

            App.doMain(components, "search", "org.codehaus.plexus:plexus-utils:3.4.1");

            assertThat(err.capturedLines()).isEmpty();

            var output = out.capturedLines();

            assertThat(output).anySatisfy(line -> assertThat(line).contains("<groupId>org.codehaus.plexus</groupId>"));
            assertThat(output).anySatisfy(line -> assertThat(line).contains("<artifactId>plexus-utils</artifactId>"));
            assertThat(output).anySatisfy(line -> assertThat(line).contains("<version>3.4.1</version>"));
        }

        @StdIo
        @Test
        void should_find_plexus_utils_341_without_search(StdOut out, StdErr err) throws IOException {
            stubFor(get(urlPathMatching("/solrsearch/select*"))
                    .willReturn(ok(getResourceAsString("/it/should_find_plexus_utils_341.json"))));

            App.doMain(components, "org.codehaus.plexus:plexus-utils:3.4.1");

            assertThat(err.capturedLines()).isEmpty();

            var output = out.capturedLines();

            assertThat(output).anySatisfy(line -> assertThat(line).contains("<groupId>org.codehaus.plexus</groupId>"));
            assertThat(output).anySatisfy(line -> assertThat(line).contains("<artifactId>plexus-utils</artifactId>"));
            assertThat(output).anySatisfy(line -> assertThat(line).contains("<version>3.4.1</version>"));
        }

        @StdIo
        @Test
        void should_find_multiple_jreleaser_maven_plugin(StdOut out, StdErr err) throws IOException {
            stubFor(get(urlPathMatching("/solrsearch/select*"))
                    .willReturn(ok(getResourceAsString("/it/should_find_multiple_jreleaser_maven_plugin.json"))));

            App.doMain(components, "search", "org.jreleaser:jreleaser-maven-plugin");

            assertThat(err.capturedLines()).isEmpty();

            var output = out.capturedLines();

            assertThat(output).anySatisfy(line -> assertThat(line).matches("Found (\\d*) results \\(showing 20\\)"));
            assertThat(output).anySatisfy(line -> assertThat(line).contains("org.jreleaser:jreleaser-maven-plugin:1"));
        }

        @StdIo
        @Test
        void should_find_many_artifacts_for_JAX_WS_Handler(StdOut out, StdErr err) throws IOException {
            stubFor(get(urlPathMatching("/solrsearch/select*"))
                    .willReturn(ok(getResourceAsString("/it/should_find_many_artifacts_for_JAX_WS_Handler.json"))));

            App.doMain(components, "class-search", "-f", "javax.xml.ws.handler.Handler");

            assertThat(err.capturedLines()).isEmpty();

            var output = out.capturedLines();

            assertThat(output).hasSizeGreaterThan(20);
            assertThat(output)
                    .anySatisfy(line -> assertThat(line).contains("containing javax.xml.ws.handler.Handler..."));
            // Cannot do assertions on which artifact would be found since the ordering is not guaranteed.
        }

        @StdIo
        @Test
        void should_find_artifacts_for_Clocky_class(StdOut out, StdErr err) throws IOException {
            stubFor(get(urlPathMatching("/solrsearch/select*"))
                    .willReturn(ok(getResourceAsString("/it/should_find_artifacts_for_Clocky_class.json"))));

            App.doMain(components, "class-search", "AdvanceableTime");

            assertThat(err.capturedLines()).isEmpty();

            var output = out.capturedLines();

            assertThat(output).anySatisfy(line -> assertThat(line).contains("it.mulders.clocky:clocky:0.4"));
            assertThat(output).anySatisfy(line -> assertThat(line).contains("it.mulders.clocky:clocky:0.4.1"));
        }

        @StdIo
        @Test
        void should_find_artifacts_for_Clocky_full_class_name(StdOut out, StdErr err) throws IOException {
            stubFor(get(urlPathMatching("/solrsearch/select*"))
                    .willReturn(ok(getResourceAsString("/it/should_find_artifacts_for_Clocky_full_class_name.json"))));
            App.doMain(components, "class-search", "-f", "it.mulders.clocky.AdvanceableTime");

            assertThat(err.capturedLines()).isEmpty();

            var output = out.capturedLines();

            assertThat(output).anySatisfy(line -> assertThat(line).contains("it.mulders.clocky:clocky:0.4"));
            assertThat(output).anySatisfy(line -> assertThat(line).contains("it.mulders.clocky:clocky:0.4.1"));
        }
    }
}
