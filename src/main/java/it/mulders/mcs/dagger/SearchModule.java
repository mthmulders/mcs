package it.mulders.mcs.dagger;

import dagger.Module;
import dagger.Provides;
import java.net.http.HttpClient;

@Module
public interface SearchModule {
    @Provides
    static HttpClient provideHttpClient() {
        return HttpClient.newHttpClient();
    }
}
