package com.example.weatherapp2;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class FragmentFavourite extends Fragment {

    private MainActivity mainActivity;
    Settings settings;

    List<FragmentContainerView> fragmentContainerViews = new ArrayList<>();

    public FragmentFavourite() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favourite, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainActivity = (MainActivity) getActivity();

        getNewestSettings();
        createNewFavs();
    }

    @Override
    public void onResume(){
        ImageButton imageSearch = mainActivity.findViewById(R.id.imageButtonSearch);
        ImageButton imageFav = mainActivity.findViewById(R.id.imageButtonFav);
        ImageButton imageMenu = mainActivity.findViewById(R.id.imageButtonMenu);
        imageSearch.setBackgroundColor(0x00FFFFFF);
        imageFav.setBackgroundColor(0x20FFFFFF);
        imageMenu.setBackgroundColor(0x00FFFFFF);
        super.onResume();
        getNewestSettings();
        createNewFavs();
    }

    public void createNewFavs(){
        int numContainers = mainActivity.getFavourites().size();
        LinearLayout linearLayout = getView().findViewById(R.id.linLayout);

        for (int i = 0; i < numContainers; i++) {
            FrameLayout frameLayout = new FrameLayout(getContext());
            frameLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            ));
            frameLayout.setId(i);

            linearLayout.addView(frameLayout);
            FragmentOneFavourite myFragment = new FragmentOneFavourite();
            Bundle args = new Bundle();
            args.putString("ARG_PARAM1", mainActivity.getFavourites().get(i));
            FragmentManager fragmentManager = getParentFragmentManager(); // For support library
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(frameLayout.getId(), myFragment);
            fragmentTransaction.commit();
        }
    }

    public void getNewestSettings(){
        SharedPreferences mPrefs = mainActivity.getPreferences(MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("settings", "");
        Settings settings1 = gson.fromJson(json, Settings.class);

        if(settings1 != null){
            settings = new Settings(settings1.isUseImperial(), settings1.getRefreshTime());
        }
        else{
            settings = new Settings(false, 5);
        }
    }
}