package com.capstone.foodify.shipper.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.foodify.shipper.Common;
import com.capstone.foodify.shipper.Model.OrderDetail;
import com.capstone.foodify.shipper.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.OrderDetailViewHolder>{

    public List<OrderDetail> listOrder;

    public OrderDetailAdapter(){}

    public void setData(List<OrderDetail> listOrder){
        this.listOrder = listOrder;
    }


    @NonNull
    @Override
    public OrderDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_detail, parent, false);
        return new OrderDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailViewHolder holder, int position) {
        OrderDetail order = listOrder.get(position);

        if(order == null)
            return;

        if(order.getFood().getImages().size() == 0){
            Picasso.get().load(R.drawable.default_image_food).into(holder.image_view);
        } else {
            Picasso.get().load(order.getFood().getImages().get(0).getImageUrl()).into(holder.image_view);
        }

        holder.shop_name.setText(order.getFood().getShop().getName());
        holder.quantity.setText("Số lượng: " + order.getQuantity());

        holder.food_name.setText(order.getFood().getName());

        holder.shop_name.setText(order.getFood().getShop().getName());

        //Check value discountPercent
        float cost = 0;
        if(order.getFood().getDiscountPercent() > 0){

            //Calculate final cost when apply discountPercent
            cost = order.getFood().getCost() - (order.getFood().getCost() * order.getFood().getDiscountPercent()/100);

            //Show discountPercent value on screen
            holder.discount.setVisibility(View.VISIBLE);
            holder.discount.setText("-" + order.getFood().getDiscountPercent()+ "%");
        } else {
            cost = order.getFood().getCost();
        }

        holder.price.setText(Common.changeCurrencyUnit(cost));
    }

    @Override
    public int getItemCount() {
        if(listOrder != null)
            return listOrder.size();
        return 0;
    }

    public class OrderDetailViewHolder extends RecyclerView.ViewHolder{
        ImageView image_view;
        TextView food_name, shop_name, price, quantity, discount;

        public OrderDetailViewHolder(@NonNull View itemView) {
            super(itemView);

            //Init Component
            image_view = itemView.findViewById(R.id.image_view);
            food_name = itemView.findViewById(R.id.food_name_text_view);
            shop_name = itemView.findViewById(R.id.shop_name_text_view);
            price = itemView.findViewById(R.id.price_text_view);
            quantity = itemView.findViewById(R.id.quantity_text_view);
            discount = itemView.findViewById(R.id.discount);
        }
    }
}
