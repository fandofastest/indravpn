package com.apps.xprovpnapps.Fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.anchorfree.hydrasdk.HydraSdk;
import com.anchorfree.hydrasdk.api.data.Country;
import com.anchorfree.hydrasdk.callbacks.Callback;
import com.anchorfree.hydrasdk.exceptions.HydraException;
import com.apps.xprovpnapps.xpro.R;
import com.apps.xprovpnapps.adapter.FreeServerListAdapter;

import java.util.ArrayList;
import java.util.List;

public class FreeFragment extends Fragment implements FreeServerListAdapter.RegionListAdapterInterface {
    private RecyclerView recyclerView;
    private FreeServerListAdapter adapter;
    private ArrayList<Country> countryArrayList;
    private VipFragment.RegionChooserInterface regionChooserInterface;
    private RelativeLayout animationHolder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_fragment_two, container, false);
        recyclerView = view.findViewById(R.id.region_recycler_view);
        animationHolder = view.findViewById(R.id.animation_layout);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        countryArrayList = new ArrayList<>();
        adapter = new FreeServerListAdapter(countryArrayList, getActivity());
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadServers();
    }

    private void loadServers() {
        HydraSdk.countries(new Callback<List<Country>>() {
            @Override
            public void success(List<Country> countries) {
                for (int i = 0; i < countries.size(); i++) {
                    if (i % 2 == 0) {
                        countryArrayList.add(countries.get(i));
                    }
                }
                adapter.notifyDataSetChanged();
                animationHolder.setVisibility(View.GONE);

            }

            @Override
            public void failure(HydraException e) {

            }
        });
    }

    @Override
    public void onCountrySelected(Country item) {
        regionChooserInterface.onRegionSelected(item);
    }

    @Override
    public void onAttach(Context ctx) {
        super.onAttach(ctx);
        if (ctx instanceof VipFragment.RegionChooserInterface) {
            regionChooserInterface = (VipFragment.RegionChooserInterface) ctx;
        }
    }
}
