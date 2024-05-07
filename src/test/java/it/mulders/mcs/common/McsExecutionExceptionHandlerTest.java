package it.mulders.mcs.common;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemErr;

import javax.net.ssl.SSLHandshakeException;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class McsExecutionExceptionHandlerTest implements WithAssertions {
  private final CommandLine.IExecutionExceptionHandler handler = new McsExecutionExceptionHandler();

  @Test
  void should_unwrap_mcs_runtime_exception() throws Exception {
    var message = "PKIX path building failed";
    var exception = new McsRuntimeException(new SSLHandshakeException(message));
    var result = tapSystemErr(() -> handler.handleExecutionException(exception, null, null));
    assertThat(result).contains(message);
  }
}
