package com.apps.xprovpnapps.Fragments;

import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;

import com.anchorfree.hydrasdk.HydraSdk;
import com.anchorfree.hydrasdk.api.data.Country;
import com.anchorfree.hydrasdk.callbacks.Callback;
import com.anchorfree.hydrasdk.exceptions.HydraException;
import com.apps.xprovpnapps.Apppurchaseconfig;
import com.apps.xprovpnapps.xpro.R;
import com.apps.xprovpnapps.activity.SubscriptionActivity;
import com.apps.xprovpnapps.adapter.VipServerListAdapter;

import java.util.ArrayList;
import java.util.List;

public class VipFragment extends Fragment implements VipServerListAdapter.RegionListAdapterInterface {

  private RecyclerView recyclerView;
    private VipServerListAdapter adapter;
    private ArrayList<Country> countryArrayList;
    private RegionChooserInterface regionChooserInterface;

    private RelativeLayout animationHolder;

    private RelativeLayout mPurchaseLayout;
    private TextView mUnblockButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.activity_fragment_one, container, false);

        recyclerView = view.findViewById(R.id.region_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        animationHolder = view.findViewById(R.id.animation_layout);
        countryArrayList = new ArrayList<>();

        mPurchaseLayout = view.findViewById(R.id.purchase_layout);
        mUnblockButton = view.findViewById(R.id.vip_unblock);

        mUnblockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), SubscriptionActivity.class));
            }
        });

        if (Apppurchaseconfig.top_subscription_main || Apppurchaseconfig.both_subscription_main) {
            mPurchaseLayout.setVisibility(View.GONE);
        }

        adapter = new VipServerListAdapter(countryArrayList, getActivity());
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
                    if (countries.get(i).getServers() > 0) {
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
        if (ctx instanceof RegionChooserInterface) {
            regionChooserInterface = (RegionChooserInterface) ctx;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        regionChooserInterface = null;
    }

    public interface RegionChooserInterface {
        void onRegionSelected(Country item);
    }
}