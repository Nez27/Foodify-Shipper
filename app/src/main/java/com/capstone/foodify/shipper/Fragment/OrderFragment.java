package com.capstone.foodify.shipper.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.capstone.foodify.shipper.Adapter.OrderAdapter;
import com.capstone.foodify.shipper.Common;
import com.capstone.foodify.shipper.Model.Order;
import com.capstone.foodify.shipper.R;

import java.util.ArrayList;
import java.util.List;

public class OrderFragment extends Fragment {

    private List<Order> listOrders = new ArrayList<>();
    RecyclerView recyclerView;
    OrderAdapter orderAdapter;
    TextView welcome_text;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order, container, false);

        //Init Component
        recyclerView = view.findViewById(R.id.rcv_list_order);
        welcome_text = view.findViewById(R.id.welcome_text);

        if(Common.CURRENT_USER != null){
            welcome_text.setText("Xin chào, " + Common.CURRENT_USER.getFullName() + "!");
        }

        orderAdapter = new OrderAdapter();

        for(int i = 0; i < 5; i++){
            int b = (int)(Math.random()*(999999-100000+1)+100000);
            listOrders.add(new Order(String.valueOf(b)));
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        orderAdapter.setData(listOrders);
        recyclerView.setAdapter(orderAdapter);

        return view;
    }
}