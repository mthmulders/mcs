package it.mulders.mcs;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOut;
import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable;
import static java.util.Arrays.asList;

import it.mulders.mcs.cli.Cli;
import it.mulders.mcs.cli.SystemPropertyLoader;
import java.util.List;
import java.util.Properties;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class AppIT implements WithAssertions {
    private final Cli command = new Cli() {
        @Override
        public SearchCommand createSearchCommand() {
            return new SearchCommand() {
                public Integer call() {
                    return 0;
                }
            };
        }

        @Override
        public ClassSearchCommand createClassSearchCommand() {
            return new ClassSearchCommand() {
                public Integer call() {
                    return 0;
                }
            };
        }
    };

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
        assertThat(App.doMain(command, new SystemPropertyLoader(), "info.picocli:picocli"))
                .isEqualTo(0);
    }

    @Test
    void should_load_additional_system_properties() {
        var loader = new SystemPropertyLoader() {
            @Override
            public Properties getProperties() {
                var tmp = super.getProperties();
                tmp.put("example", "value");
                return tmp;
            }
        };

        App.doMain(command, loader, "info.picocli:picocli");

        assertThat(System.getProperty("example")).isEqualTo("value");
    }

    @Test
    void should_not_set_proxy_system_properties_when_no_env_variable_is_present() throws Exception {
        List<String> values = withEnvironmentVariable("HTTP_PROXY", null)
                .and("HTTPS_PROXY", null)
                .execute(() -> {
                    App.doMain(command, new SystemPropertyLoader(), "info.picocli:picocli");

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
                    App.doMain(command, new SystemPropertyLoader(), "info.picocli:picocli");

                    return asList(
                            System.getProperty("http.proxyHost"),
                            System.getProperty("http.proxyPort"),
                            System.getProperty("https.proxyHost"),
                            System.getProperty("https.proxyPort"));
                });

        assertThat(values).isEqualTo(asList("http.proxy.example.com", "8080", "https.proxy.example.com", "8484"));
    }
}
