package com.capstone.foodify.shipper.Fragment.Order;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.foodify.shipper.API.FoodApiToken;
import com.capstone.foodify.shipper.Adapter.OrderAdapter;
import com.capstone.foodify.shipper.Common;
import com.capstone.foodify.shipper.Model.Order;
import com.capstone.foodify.shipper.Model.Response.Orders;
import com.capstone.foodify.shipper.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompletedOrder extends Fragment {
    private static int CURRENT_PAGE = 0;
    private static int PAGE_SIZE = 8;
    private static String SORT_BY = "id";
    private static String SORT_DIR = "asc";
    private static boolean LAST_PAGE = false;
    private List<Order> listOrders = new ArrayList<>();
    RecyclerView recyclerView;
    OrderAdapter orderAdapter;
    TextView end_of_list_text_view;
    NestedScrollView list_order_layout;
    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_completed_order, container, false);

        //Init component
        recyclerView = view.findViewById(R.id.rcv_list_order);
        list_order_layout = view.findViewById(R.id.list_order_layout);
        progressBar = view.findViewById(R.id.progress_bar);
        end_of_list_text_view = view.findViewById(R.id.end_of_list_text);

        orderAdapter = new OrderAdapter(getActivity());

        CURRENT_PAGE = 0;
        getListOrder();


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setAdapter(orderAdapter);

        if (list_order_layout != null) {
            list_order_layout.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(@NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                        //When user scroll to bottom, load more data
                        dataLoadMore();
                    }
                }
            });
        }
        return view;
    }

    private void getListOrder(){
        if(Common.CURRENT_SHIPPER != null){
            FoodApiToken.apiService.getListOrder(Common.CURRENT_SHIPPER.getId(), "COMPLETED", CURRENT_PAGE++, PAGE_SIZE, SORT_BY, SORT_DIR).enqueue(new Callback<Orders>() {
                @Override
                public void onResponse(Call<Orders> call, Response<Orders> response) {
                    if(response.code() == 200){

                        listOrders.addAll(response.body().getOrders());

                        orderAdapter.setData(listOrders);

                        LAST_PAGE = response.body().getPage().isLast();

                        if(LAST_PAGE)
                            hideProgressBarAndShowEndOfListText();
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

    private void hideProgressBarAndShowEndOfListText(){
        progressBar.setVisibility(View.GONE);
        end_of_list_text_view.setVisibility(View.VISIBLE);
    }

    private void dataLoadMore() {
        if(!LAST_PAGE){
            getListOrder();
        } else {
            hideProgressBarAndShowEndOfListText();
        }
    }
}