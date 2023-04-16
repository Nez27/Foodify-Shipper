package com.capstone.foodify.shipper.Fragment.Order;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class OrderViewPagerAdapter extends FragmentStateAdapter {

    public OrderViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position) {
            case 1: return new CompletedOrder();
            case 2: return new CancelOrder();
            default: return new ConfirmedOrder();
        }
    }
    @Override
    public int getItemCount() {
        return 3;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }
}
