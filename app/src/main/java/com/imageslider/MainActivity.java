package com.imageslider;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.imageslider.model.BannerModel;
import com.shivam.library.imageslider.ImageSlider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    ArrayList<BannerModel> bannerModelsArray;
    ImageSlider slider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        slider = (ImageSlider) findViewById(R.id.pager);

        try {
            getMenus();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getMenus() throws IOException {

        String url = "https://cakeapi.trinitytuts.com/api/slider";

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                String mMessage = e.getMessage().toString();
                Log.w("failure Response", mMessage);
                //call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String mMessage = response.body().string();

                Log.e("Response", String.valueOf(response.code()));


                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        bannerModelsArray = new ArrayList<>();

                        JSONObject parent = null;
                        try {
                            parent = new JSONObject(mMessage);

                            // Banners
                            JSONArray banners = parent.getJSONArray("banners");
                            for (int i = 0; i < banners.length(); i++){
                                JSONObject data = banners.getJSONObject(i);
                                BannerModel bannerModel = new BannerModel();
                                bannerModel.setUrl(data.getString("url"));
                                bannerModelsArray.add(bannerModel);
                            }

                            // Set banner
                            SectionsPagerAdapter  mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), bannerModelsArray.size());
                            slider.setAdapter(mSectionsPagerAdapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {   // adapter to set in ImageSlider

        int imageCount = 0;

        public SectionsPagerAdapter(FragmentManager fm, int count) {
            super(fm);

            imageCount = count;
        }

        @Override
        public Fragment getItem(int position) {

            BannerModel banner = (BannerModel)bannerModelsArray.get(position);
            return PlaceholderFragment.newInstance(banner.getUrl());
        }

        @Override
        public int getCount() {

            return imageCount;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            return null;
        }
    }

    // Image fragment
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {

        }

        public static PlaceholderFragment newInstance(String pic) {

            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putString("index", pic);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_main3, container, false);
            Bundle args = getArguments();
            String image = args.getString("index", "https://prfashionweb.files.wordpress.com/2018/11/new-arri-bannr.jpg");
            ImageView imageView=(ImageView)rootView.findViewById(R.id.image);

            Glide
                    .with(getActivity())
                    .load(image)
                    .into(imageView);
            return rootView;
        }
    }
}
