package cs371m.godj;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final String clientId = "6e8cc80c1cd6419484b88a02348aa7e4";
        final String clientSecret = "a06668cfb70643c8b497a3cfd1dd90d9";

        SpotifyApi api = new SpotifyApi();

// Most (but not all) of the Spotify Web API endpoints require authorisation.
// If you know you'll only use the ones that don't require authorisation you can skip this step
        //api.setAccessToken("myAccessToken");

        SpotifyService spotify = api.getService();

        spotify.searchArtists("usher", new Callback<ArtistsPager>() {
            @Override
            public void success(ArtistsPager artistsPager, Response response) {
                //display artists in list
                List<Artist> artists = artistsPager.artists.items;




                
                Log.d("artists:", artists.toString());
            }

            @Override
            public void failure(RetrofitError error) {
               Log.d("artists:", "ERROR:" + error.getMessage());

            }
        });

//        final Api api = Api.builder()
//                .clientId(clientId)
//                .clientSecret(clientSecret)
//                .build();
//        SettableFuture<Page<Artist>> artistSettableFuture = api.searchArtists("usher").market("US").limit(10).build().getAsync();
//        Futures.addCallback(artistSettableFuture, new FutureCallback<Page<Artist>>() {
//            @Override
//            public void onSuccess(Page<Artist> artistPage) {
//                List<Artist> artists = artistPage.getItems();
//                //display artists in list
//                Log.d("artists:", artists.toString());
//            }
//
//            @Override
//            public void onFailure(Throwable throwable) {
//                Log.d("artists:", "ERROR:" + throwable.getMessage());
//
//            }
//        });
    }
}
