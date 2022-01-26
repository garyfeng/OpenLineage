package io.openlineage.client;

import java.net.URL;
import javax.annotation.Nullable;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/** HTTP client used to emit {@link OpenLineage.RunEvent}s to HTTP backend. */
@Slf4j
public final class OpenLineageClient {
  static final URL DEFAULT_BASE_URL = Utils.toUrl("http://localhost:8080");

  final OpenLineageHttp http;

  /** Creates a new {@code OpenLineageClient} object. */
  public OpenLineageClient() {
    this(
        Utils.toUrl(System.getProperty("OPENLINEAGE_URL", DEFAULT_BASE_URL.toString())),
        System.getenv("OPENLINEAGE_API_KEY"));
  }

  /** Creates a new {@code OpenLineageClient} object with the given {@code baseUrl} string. */
  public OpenLineageClient(@NonNull final String baseUrlString) {
    this(Utils.toUrl(baseUrlString), null);
  }

  /** Creates a new {@code OpenLineageClient} object with the given {@code baseUrl}. */
  public OpenLineageClient(@NonNull final URL baseUrl) {
    this(baseUrl, null);
  }

  /**
   * Creates a new {@code OpenLineageClient} object with the given {@code baseUrl} and {@code
   * apiKey}.
   */
  public OpenLineageClient(@NonNull final URL baseUrl, @Nullable final String apiKey) {
    this(OpenLineageHttp.create(baseUrl, apiKey));
  }

  OpenLineageClient(@NonNull final OpenLineageHttp http) {
    this.http = http;
  }

  /**
   * Emit the given run event to HTTP backend. The method will return successfully after the run
   * event has been emitted, regardless of any exceptions thrown by the HTTP backend.
   *
   * @param runEvent The run event to emit.
   */
  public void emit(@NonNull OpenLineage.RunEvent runEvent) {
    http.post(http.url("/lineage"), Utils.toJson(runEvent));
  }

  /**
   * Builder for {@link OpenLineageClient} instances.
   *
   * <p>Usage:
   *
   * <pre>{@code
   * OpenLineageClient client = OpenLineageClient().builder()
   *     .baseUrl("http://localhost:5000")
   *     .build()
   * }</pre>
   */
  public static final class Builder {
    private URL baseUrl;
    private @Nullable String apiKey;

    private Builder() {
      this.baseUrl = DEFAULT_BASE_URL;
    }

    public Builder baseUrl(@NonNull String baseUrlString) {
      return baseUrl(Utils.toUrl(baseUrlString));
    }

    public Builder baseUrl(@NonNull URL baseUrl) {
      this.baseUrl = baseUrl;
      return this;
    }

    public Builder apiKey(@Nullable String apiKey) {
      this.apiKey = apiKey;
      return this;
    }

    /**
     * Returns an {@link OpenLineageClient} object with the properties of this {@link
     * OpenLineageClient.Builder}.
     */
    public OpenLineageClient build() {
      return new OpenLineageClient(baseUrl, apiKey);
    }
  }

  /**
   * Returns an new {@link OpenLineageClient.Builder} object for building {@link
   * OpenLineageClient}s.
   */
  public static Builder builder() {
    return new Builder();
  }
}