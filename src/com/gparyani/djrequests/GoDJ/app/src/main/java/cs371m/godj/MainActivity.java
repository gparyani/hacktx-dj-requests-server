package cs371m.godj;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.SettableFuture;
import com.wrapper.spotify.Api;
import com.wrapper.spotify.models.Artist;
import com.wrapper.spotify.models.Page;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final String clientId = "6e8cc80c1cd6419484b88a02348aa7e4";
        final String clientSecret = "a06668cfb70643c8b497a3cfd1dd90d9";

        final Api api = Api.builder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .build();
        SettableFuture<Page<Artist>> artistSettableFuture = api.searchArtists("usher").market("US").limit(10).build().getAsync();
        Futures.addCallback(artistSettableFuture, new FutureCallback<Page<Artist>>() {
            @Override
            public void onSuccess(Page<Artist> artistPage) {
                List<Artist> artists = artistPage.getItems();
                //display artists in list
                Log.d("artists:", artists.toString());
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.d("artists:", "ERROR:" + throwable.getMessage());

            }
        });
    }
}
