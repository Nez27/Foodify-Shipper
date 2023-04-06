package com.capstone.foodify.shipper.Fragment.Order;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.capstone.foodify.shipper.Fragment.OrderFragment;
import com.capstone.foodify.shipper.Fragment.ProfileFragment;

public class OrderViewPagerAdapter extends FragmentStateAdapter {

    public OrderViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position) {
            case 1: return new CompletedOrder();
            default: return new ShippingOrder();
        }
    }
    @Override
    public int getItemCount() {
        return 2;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }
}
