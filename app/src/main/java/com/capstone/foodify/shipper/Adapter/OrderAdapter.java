package com.capstone.foodify.shipper.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.foodify.shipper.Activity.MainActivity;
import com.capstone.foodify.shipper.Activity.OrderDetailActivity;
import com.capstone.foodify.shipper.Common;
import com.capstone.foodify.shipper.Model.Order;
import com.capstone.foodify.shipper.R;
import com.thecode.aestheticdialogs.AestheticDialog;
import com.thecode.aestheticdialogs.DialogStyle;
import com.thecode.aestheticdialogs.DialogType;
import com.thecode.aestheticdialogs.OnDialogClickListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder>{

    public List<Order> listOrders;
    private Activity activity;
    public OrderAdapter(Activity activity) {
        this.activity = activity;
    }

    public void setData(List<Order> listOrders){
        this.listOrders = listOrders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = listOrders.get(position);

        if(order == null)
            return;

        holder.order_tracking_number.setText("Order Id: #" + order.getOrderTrackingNumber());
        holder.user_name.setText("Tên: " + order.getUser().getFullName());
        holder.phone.setText("Số điện thoại: " + order.getUser().getPhoneNumber());
        holder.address.setText("Địa chỉ: " + order.getAddress());
        holder.orderTime.setText("Thời gian đặt: " + order.getOrderTime());
        holder.total.setText("Tổng: " + Common.changeCurrencyUnit(order.getTotal()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(activity instanceof MainActivity){
                    if(((MainActivity)activity).checkPermission()){
                        Intent intent = new Intent(activity, OrderDetailActivity.class);
                        intent.putExtra("order", order);
                        activity.startActivity(intent);
                        activity.finish();
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

    @Override
    public int getItemCount() {
        if(listOrders != null)
            return listOrders.size();
        return 0;
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder{

        TextView order_tracking_number, user_name, phone, address, orderTime, total;
        Context context;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();

            order_tracking_number = itemView.findViewById(R.id.order_tracking_number);
            user_name = itemView.findViewById(R.id.user_name);
            phone = itemView.findViewById(R.id.phone);
            address = itemView.findViewById(R.id.address);
            orderTime = itemView.findViewById(R.id.order_time);
            total = itemView.findViewById(R.id.total);
        }
    }
}
