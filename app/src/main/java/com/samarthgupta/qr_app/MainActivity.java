package com.samarthgupta.qr_app;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.util.LogWriter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.samarthgupta.qr_app.Interfaces.MapInterface;
import com.samarthgupta.qr_app.POJO.Distance;
import com.samarthgupta.qr_app.POJO.Element;
import com.samarthgupta.qr_app.POJO.MapData;
import com.samarthgupta.qr_app.POJO.Row;

import java.util.LinkedHashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements ViewPagerEx.OnPageChangeListener{
    Double lat,lon;
    SliderLayout mDemoSlider;
    MapInterface client;
    LinkedHashMap<String, Integer> file_maps;
    LinkedHashMap<String,String> stops_latlong;
    String BASE__URL= "https://maps.googleapis.com/";
    String API_KEY = "AIzaSyAHUlSqXGSjCaBsh7MGyPtHylUwHp9HVc8";
    Integer stopCode;
    int stopDistance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

          stopCode=0;
          mDemoSlider=(SliderLayout)findViewById(R.id.slider);


        Retrofit.Builder builder = new Retrofit.Builder().baseUrl(BASE__URL).addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();
        client = retrofit.create(MapInterface.class);

        stops_latlong= new LinkedHashMap<>();
        stops_latlong.put("0","28.6098,77.1002");
        stops_latlong.put("1","28.6304,77.0798");
        stops_latlong.put("2","28.675,77.0949");
        stops_latlong.put("3","28.7246,77.128");
        stops_latlong.put("4","28.7499,77.1183");

          file_maps = new LinkedHashMap<>();
          file_maps.put("Janakpuri",R.drawable.jpdblk);
          file_maps.put("District Centre",R.drawable.district_center_jp);
          file_maps.put("Peera Garhi",R.drawable.peera_garhi);
          file_maps.put("Sachdeva", R.drawable.sachdeva_school);
          file_maps.put("DTU", R.drawable.dtu);

        for (String name : file_maps.keySet()) {
            TextSliderView textSliderView = new TextSliderView(this);
            // initialize a SliderLayout
            textSliderView.description(name).image(file_maps.get(name)).setScaleType(BaseSliderView.ScaleType.Fit);

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle().putString("extra", name);

            mDemoSlider.addSlider(textSliderView); }

            mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
            mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
            mDemoSlider.setCustomAnimation(new DescriptionAnimation());
            mDemoSlider.addOnPageChangeListener(this);
            mDemoSlider.stopAutoCycle();


            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            LocationListener listener = new OurListener();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 45000, 0, listener);


        }




    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }





    private class OurListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            location.setAccuracy(Criteria.ACCURACY_HIGH);
            lat= location.getLatitude();
            lon=location.getLongitude();

            Log.i("TAG",lat+" "+lon+" ");

            String origin = lat+","+lon; //CURRENT LOCATION OF THE BUS
            String destination;
            switch(stopCode) {

                case 0: destination = stops_latlong.get("1");
                        calculateDistance(origin,destination);
                    break;

                case 1: destination = stops_latlong.get("2");
                    calculateDistance(origin,destination);
                    break;

                case 2: destination = stops_latlong.get("3");
                    calculateDistance(origin,destination);
                    break;

                case 3: destination = stops_latlong.get("4");
                    calculateDistance(origin,destination);
                    break;

            }




        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }

       void calculateDistance(String origin,String destination){
            //  String dest ="28.7499,77.1183";

            Call<MapData> data = client.getMapData(origin,destination,"driving",API_KEY);
            data.enqueue(new Callback<MapData>() {
                @Override
                public void onResponse(Call<MapData> call, Response<MapData> response) {
                    MapData mapData = response.body();
                    Log.i("TAG",mapData.getStatus());
                    List<Row> rowData = mapData.getRows();
                    Row row = rowData.get(0);
                    List<Element> elementList = row.getElements();
                    Distance distance = elementList.get(0).getDistance();
                      stopDistance = distance.getValue();
                    Log.i("TAG","Distance - "+stopDistance);

                    if(stopDistance<800) {
                        mDemoSlider.moveNextPosition();
                        stopCode++;
                    }
                }

                @Override
                public void onFailure(Call<MapData> call, Throwable t) {

                }
            });



        }
    }
}
