package com.capstone.foodify.shipper.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.foodify.shipper.API.FoodApiToken;
import com.capstone.foodify.shipper.Activity.MainActivity;
import com.capstone.foodify.shipper.Adapter.OrderAdapter;
import com.capstone.foodify.shipper.Common;
import com.capstone.foodify.shipper.Fragment.Order.OrderViewPagerAdapter;
import com.capstone.foodify.shipper.Model.Order;
import com.capstone.foodify.shipper.Model.Response.Orders;
import com.capstone.foodify.shipper.R;
import com.capstone.foodify.shipper.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderFragment extends Fragment {
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    OrderViewPagerAdapter orderViewPagerAdapter;
    TextView welcome_text;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order, container, false);

        //Init Component
        welcome_text = view.findViewById(R.id.welcome_text);

        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager2 = view.findViewById(R.id.view_pager);

        orderViewPagerAdapter = new OrderViewPagerAdapter(getActivity());
        viewPager2.setAdapter(orderViewPagerAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition(), false);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });

        if(Common.CURRENT_USER != null){
            welcome_text.setText("Xin chào, " + Common.CURRENT_USER.getFullName() + "!");
        }

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();

        //Update user name on textview
        welcome_text.setText("Xin chào, " + Common.CURRENT_USER.getFullName() + "!");
    }

}