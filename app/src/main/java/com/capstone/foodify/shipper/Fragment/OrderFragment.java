package com.capstone.foodify.shipper.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.foodify.shipper.API.FoodApiToken;
import com.capstone.foodify.shipper.Activity.MainActivity;
import com.capstone.foodify.shipper.Activity.OrderDetailActivity;
import com.capstone.foodify.shipper.Adapter.OrderAdapter;
import com.capstone.foodify.shipper.Common;
import com.capstone.foodify.shipper.Fragment.Order.OrderViewPagerAdapter;
import com.capstone.foodify.shipper.Model.Order;
import com.capstone.foodify.shipper.Model.Response.Orders;
import com.capstone.foodify.shipper.R;
import com.capstone.foodify.shipper.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;
import com.thecode.aestheticdialogs.AestheticDialog;
import com.thecode.aestheticdialogs.DialogStyle;
import com.thecode.aestheticdialogs.DialogType;
import com.thecode.aestheticdialogs.OnDialogClickListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderFragment extends Fragment {
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    OrderViewPagerAdapter orderViewPagerAdapter;
    TextView welcome_text, order_tracking_number, user_name, phone, address, orderTime, total;
    CardView card_view_order_shipping;
    Order orderShipping;
    LinearLayout order_shipping_layout;
    private static final int PAGE_SIZE = 8;
    private static final String SORT_BY = "id";
    private static final String SORT_DIR = "asc";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order, container, false);

        //Init Component
        initComponent(view);

        welcome_text = view.findViewById(R.id.welcome_text);
        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager2 = view.findViewById(R.id.view_pager);

        getOrderShipping();

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
            setNameUser();
        }

        return view;
    }

    private void initComponent(View view) {
        welcome_text = view.findViewById(R.id.welcome_text);
        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager2 = view.findViewById(R.id.view_pager);
        card_view_order_shipping = view.findViewById(R.id.card_view_order_shipping);
        order_shipping_layout = view.findViewById(R.id.order_shipping_layout);

        order_tracking_number = view.findViewById(R.id.order_tracking_number);
        user_name = view.findViewById(R.id.user_name);
        phone = view.findViewById(R.id.phone);
        address = view.findViewById(R.id.address);
        orderTime = view.findViewById(R.id.order_time);
        total = view.findViewById(R.id.total);
    }

    public void getOrderShipping(){
        if(Common.CURRENT_SHIPPER != null){
            FoodApiToken.apiService.getListOrder(Common.CURRENT_SHIPPER.getId(), "SHIPPING", 0, PAGE_SIZE, SORT_BY, SORT_DIR).enqueue(new Callback<Orders>() {
                @Override
                public void onResponse(Call<Orders> call, Response<Orders> response) {
                    if(response.code() == 200){

                        assert response.body() != null;
                        if(response.body().getOrders().size() == 1){
                            //Get order shipping
                            orderShipping = response.body().getOrders().get(0);

                            initData();

                            order_shipping_layout.setVisibility(View.VISIBLE);
                        } else {
                            order_shipping_layout.setVisibility(View.GONE);
                        }
                    } else {
                        Toast.makeText(getContext(), "Đã có lỗi hệ thống! Mã lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Orders> call, Throwable t) {
                    Toast.makeText(getContext(), Common.ERROR_CONNECT_SERVER, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void initData() {
        order_tracking_number.setText("Order Id: #" + orderShipping.getOrderTrackingNumber());
        user_name.setText("Tên: " + orderShipping.getUser().getFullName());
        phone.setText("Số điện thoại: " + orderShipping.getUser().getPhoneNumber());
        address.setText("Địa chỉ: " + orderShipping.getAddress());
        orderTime.setText("Thời gian đặt: " + orderShipping.getOrderTime());
        total.setText("Tổng: " + Common.changeCurrencyUnit(orderShipping.getTotal()));

        Activity activity = getActivity();

        card_view_order_shipping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(activity instanceof MainActivity){
                    if(((MainActivity)activity).checkPermission()){
                        Intent intent = new Intent(activity, OrderDetailActivity.class);
                        intent.putExtra("order", orderShipping);
                        activity.startActivity(intent);
                    } else {
                        new AestheticDialog.Builder(activity, DialogStyle.TOASTER, DialogType.WARNING)
                                .setTitle("THÔNG BÁO!")
                                .setMessage("Bạn cần cấp quyền vị trí cho ứng dụng để có thể tiếp tục!")
                                .setCancelable(false)
                                .setOnClickListener(new OnDialogClickListener() {
                                    @Override
                                    public void onClick(@NonNull AestheticDialog.Builder builder) {
                                        builder.dismiss();
                                    }
                                }).show();
                    }
                }
            }
        });
    }

    private void setNameUser(){
        String[] namePart = Common.CURRENT_USER.getFullName().split(" ");

        welcome_text.setText("Xin chào, " + namePart[namePart.length - 1] + "!");

    }


    @Override
    public void onResume() {
        super.onResume();

        //Update user name on textview
        setNameUser();
    }

}