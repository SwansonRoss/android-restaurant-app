package anew.resandroid.com.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.style.CharacterStyle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBufferResponse;
import com.google.android.gms.location.places.GeoDataApi;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.PlaceReport;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    final int AUTOCOMPLETE_REQUEST_CODE = 1;

    //private FusedLocationProviderClient mFusedLocationProviderClient;
    final int MY_PERMISSIONS_CODE = 1017;
    final int PLACE_PICKER_REQUEST = 1;
    public static final String PERMISSION_STRING = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private boolean bound = false;

    Location lastLocation = null;
    private LocationManager locationManager;
    private LocationListener locationListener;

    Double searchRadius = 1.0;
    final Double RADIUS_EARTH =  3960.0;

    GeoDataClient mGeoDataClient;

    ImageView loadScreen;

    Place searchPlace;

    Boolean goodToSend = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if(ContextCompat.checkSelfPermission(this, this.PERMISSION_STRING) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{this.PERMISSION_STRING}, MY_PERMISSIONS_CODE);
        }

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //GETS LOCATION. NOT REALLY NEEDED, BUT MIGHT BE USEFUL
                if(lastLocation == null){
                    lastLocation = location;
                }
                lastLocation = location;

                locationManager.removeUpdates(locationListener);

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        setContentView(R.layout.activity_search);

        loadScreen = findViewById(R.id.load_screen);



        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.v("Place complete ", "Place: " + place.getName());
                searchPlace = place;
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.v("Autocomplete ", "An error occurred: " + status);
            }
        });

        final TextView seekProgress = findViewById(R.id.seekBarProgress);

        SeekBar seekBar = findViewById(R.id.seekBar);
        SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekProgress.setText(String.valueOf((double)progress/40));
                searchRadius = (double) progress/40;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };

        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);


        final Button search = findViewById(R.id.search);
        View.OnClickListener searchClickListener;
        search.setOnClickListener(searchClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(SearchActivity.this, PERMISSION_STRING) == PackageManager.PERMISSION_GRANTED) {
                    //if(searchPlace == null)
                    loadScreen.setVisibility(View.VISIBLE);
                        locationSearch();


/*Code for getting devices Latitude and Longitude. Unnecessary with google Maps*/
//                    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//                    String provider = locationManager.getBestProvider(new Criteria(), true);
//                    Log.v("Place", "Checkpoint2");
//                    if (provider != null) {
//                        Log.v("Place", "Checkpoint3");
//                        locationManager.requestLocationUpdates(provider, 1000, 1, locationListener);
//                    }
                }
                else{
                    //Read string from above.
                }

            }
        });


        Intent intent = getIntent();
    }

    private void locationSearch(){
        final ArrayList<Restaurant> restaurantList = new ArrayList<>();

        if(ContextCompat.checkSelfPermission(this, this.PERMISSION_STRING) == PackageManager.PERMISSION_GRANTED) {

                final PlaceDetectionClient placeDetectionClient = Places.getPlaceDetectionClient(SearchActivity.this);

                Task<PlaceLikelihoodBufferResponse> placeLikelihoods = placeDetectionClient.getCurrentPlace(null);

                placeLikelihoods.addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                        List<Place> placeList = new ArrayList<Place>();
                        PlaceLikelihoodBufferResponse possiblePlaces = task.getResult();

                        for (PlaceLikelihood placeLikelihood : possiblePlaces) {
                            placeList.add(placeLikelihood.getPlace().freeze());
                        }
//
//                        possiblePlaces.release();
//
//                        for (Place place : placeList) {
//                            List<Integer> types = place.getPlaceTypes();
//                            for (int type : types) {
//                                if (type == Place.TYPE_RESTAURANT) {
//                                    Restaurant restaurant = new Restaurant(place);
//                                    restaurantList.add(restaurant);
//
//                                    break;
//                                }
//                            }
//
//                        }
                        if(searchPlace == null) {
                            searchPlace = placeList.get(0);
                        }

//                        try{

//                            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
//                                    .setBoundsBias(getBoundsBias(searchPlace))
//                                    .build(SearchActivity.this);
//                            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);

                            mGeoDataClient = Places.getGeoDataClient(SearchActivity.this);

                            AutocompleteFilter restuarantFilter = new AutocompleteFilter.Builder().
                                    setTypeFilter(AutocompleteFilter.TYPE_FILTER_ESTABLISHMENT).build();


                            char searchChar = 'A';
                            int i;

                            for(i = 0; i <  26; i++) {

                                StringBuilder searchStringBuilder = new StringBuilder();
                                searchStringBuilder.append(searchChar);
                                String searchString = searchStringBuilder.toString();
                                searchChar++;

                                if(i %5 == 0){goodToSend = true;}


                                Task<AutocompletePredictionBufferResponse> results = mGeoDataClient.getAutocompletePredictions(searchString, getBoundsBias(searchPlace), null);

                                results.addOnCompleteListener(new OnCompleteListener<AutocompletePredictionBufferResponse>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AutocompletePredictionBufferResponse> task) {
                                        AutocompletePredictionBufferResponse response = task.getResult();
                                        for (AutocompletePrediction prediction : response) {

                                            final Task retrievePlaceTask = mGeoDataClient.getPlaceById(prediction.getPlaceId());
                                            retrievePlaceTask.addOnCompleteListener(new OnCompleteListener() {
                                                @Override
                                                public void onComplete(@NonNull Task task) {
                                                    PlaceBufferResponse placeBufferResponse = (PlaceBufferResponse) task.getResult();
                                                    Place place = placeBufferResponse.get(0);
                                                    List<Integer> types = place.getPlaceTypes();
                                                    for (int type : types) {
                                                        if (type == Place.TYPE_RESTAURANT) {
                                                            Restaurant restaurant = new Restaurant(place);
                                                            restaurantList.add(restaurant);
                                                            Log.v("Place ", place.getName().toString());

                                                            break;
                                                        }
                                                    }
                                                    placeBufferResponse.release();
                                                    sendList(restaurantList);
                                                    loadScreen.setVisibility(View.INVISIBLE);
                                                }

                                            });
                                        }
                                        response.release();
                                    }



                                });
                            }

//
//                        }catch (GooglePlayServicesRepairableException e) {
//                        }catch (GooglePlayServicesNotAvailableException e){}

                    }
                });

//            try{
//
//                Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
//                        .setBoundsBias(getBoundsBias(searchPlace))
//                        .build(this);
//                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
//
//                Task<AutocompletePredictionBufferResponse> results = mGeoDataClient.getAutocompletePredictions(searchPlace.getName().toString(), getBoundsBias(searchPlace), null);
//
//                results.addOnCompleteListener(new OnCompleteListener<AutocompletePredictionBufferResponse>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AutocompletePredictionBufferResponse> task) {
//                        AutocompletePredictionBufferResponse response = task.getResult();
//                        for(AutocompletePrediction prediction : response){
//                            Log.v("Place ",prediction.getPlaceId());
//                        }
//                    }
//                });
//
//
//
//
//            }catch (GooglePlayServicesRepairableException e) {
//            }catch (GooglePlayServicesNotAvailableException e){}

        }

    }

    private void sendList(ArrayList<Restaurant> restaurantList)
    {
        Intent toResults = new Intent(SearchActivity.this, ResultsActivity.class);
        toResults.putParcelableArrayListExtra("restaurants", restaurantList);
        startActivity(toResults);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data){
//        if(requestCode == AUTOCOMPLETE_REQUEST_CODE){
//            if (resultCode == RESULT_OK){
//                Place place = PlaceAutocomplete.getPlace(this, data);
//                Log.v("Place ", place.getName().toString() +", "+ place.getAddress().toString());
//            }
//            else if(resultCode == RESULT_CANCELED){
//                Log.v("Place ", "Canceled");
//            }
//        }
//
//    }

    public LatLngBounds getBoundsBias(Place place){
//        /*NOTE: 1 degree of latitude: ~69 mi --> so 1mi = 1/69th degree == 0.0145
//        * longitude to miles conversion is a little more complicated
//        * deltaLongMI = ((longitude)/(180/pi))*RADIUS_EARTH*Cos(Latitude.toRadians)
//        * */
        LatLng placeLatLng = place.getLatLng();
        Double placeLat = placeLatLng.latitude;
        Double placeLng = placeLatLng.longitude;
//
       Double deltaLat = getDeltaLat();
        Double deltaLng = getDeltaLng(placeLng, placeLat);

        LatLngBounds bias = new LatLngBounds(new LatLng(placeLat - deltaLat, placeLng - deltaLng), new LatLng(placeLat + deltaLat, placeLng + deltaLng));
//
      return bias;
//
    };

    private Double getDeltaLat(){
        Double dLat = searchRadius*0.0145;
        return dLat;
    }

    private Double getDeltaLng(Double lng, Double lat){
        Double r = RADIUS_EARTH * Math.cos(Math.toRadians(lat));
        Double dLng = (searchRadius/r)*(180.0/Math.PI);

        return dLng;
    }

}

