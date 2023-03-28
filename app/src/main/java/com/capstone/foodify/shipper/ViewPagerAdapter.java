package com.capstone.foodify.shipper;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.capstone.foodify.shipper.Fragment.OrderFragment;
import com.capstone.foodify.shipper.Fragment.ProfileFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position) {
            case 1: return new ProfileFragment();
            default: return new OrderFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
