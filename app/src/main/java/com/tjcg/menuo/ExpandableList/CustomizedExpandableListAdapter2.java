package com.tjcg.menuo.ExpandableList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.tjcg.menuo.Expandablectivity;
import com.tjcg.menuo.OrderPreviewActivity;
import com.tjcg.menuo.R;
import com.tjcg.menuo.data.local.AppDatabase;
import com.tjcg.menuo.data.local.OrderDao;
import com.tjcg.menuo.data.remote.ServiceGenerator;
import com.tjcg.menuo.dialog.NewOrderDialog;
import com.tjcg.menuo.utils.Constants;
import com.tjcg.menuo.utils.Default;
import com.tjcg.menuo.utils.LottieProgressDialog;
import com.tjcg.menuo.utils.PrefManager;
import com.tjcg.menuo.utils.SharedPreferencesKeys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomizedExpandableListAdapter2 extends BaseExpandableListAdapter implements NewOrderDialog.ClickListener {

    private Context context;
    private List<String> expandableTitleList;
    private HashMap<String, List<String>> expandableDetailList;
    private OrderDao orderDao;
    PrefManager prefManager;
    String businessID;
    LottieProgressDialog lottieProgressDialog;
    Expandablectivity expandablectivity;

    // constructor
    public CustomizedExpandableListAdapter2(Expandablectivity expandablectivity, Context context, List<String> expandableListTitle,
                                            HashMap<String, List<String>> expandableListDetail) {

        this.expandablectivity = expandablectivity;
        this.context = context;
        this.expandableTitleList = expandableListTitle;
        this.expandableDetailList = expandableListDetail;
        this.orderDao= AppDatabase.getDatabase(context).orderDao();
        this.prefManager= new PrefManager(context);
        this.businessID=this.prefManager.getString("businessID");
        this.lottieProgressDialog = new LottieProgressDialog(context);
    }

    @Override
    // Gets the data associated with the given child within the given group.
    public Object getChild(int lstPosn, int expanded_ListPosition) {
        return this.expandableDetailList.get(this.expandableTitleList.get(lstPosn)).get(expanded_ListPosition);
    }

    @Override
    // Gets the ID for the given child within the given group.
    // This ID must be unique across all children within the group. Hence we can pick the child uniquely
    public long getChildId(int listPosition, int expanded_ListPosition) {
        return expanded_ListPosition;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    // Gets a View that displays the data for the given child within the given group.
    public View getChildView(int lstPosn, final int expanded_ListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final String expandedListText = (String) getChild(lstPosn, expanded_ListPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_item, null);
        }
        TextView expandedListTextView = (TextView) convertView.findViewById(R.id.txt_onum);
        TextView tvStatus = (TextView) convertView.findViewById(R.id.o_status);
        TextView tvOrderTime = (TextView) convertView.findViewById(R.id.o_time);
        TextView tvCustomer = (TextView) convertView.findViewById(R.id.o_customer);
        TextView OrderTotal = (TextView) convertView.findViewById(R.id.OrderTotal);
        TextView tvPreparedIn = (TextView) convertView.findViewById(R.id.tvPreparedIn);
        ImageView ivDot = (ImageView) convertView.findViewById(R.id.imageViewDot);
        ImageView ivNewOrder = (ImageView) convertView.findViewById(R.id.imageViewNewOrder);
        ImageView ivOrderIcon = (ImageView) convertView.findViewById(R.id.imageViewOrderIcon123);
        Button btnPickUp = (Button) convertView.findViewById(R.id.btnPickUp);
        expandedListTextView.setText(expandedListText);
        String orderID = expandedListText;
        String deliveryType = orderDao.getDeliveryType(expandedListText.toString());
        Integer orderTotal = orderDao.getOrderTotal(expandedListText.toString());
        OrderTotal.setText(orderTotal.toString()+" KR");
        btnPickUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SweetAlertDialog pDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog.setTitleText("Orders App");
                pDialog.setContentText("Vill du verkligen slutföra ordern?");
//                pDialog.setCancelText("Cancel");
//                pDialog.setConfirmText("Ok");
                pDialog.setCancelButton("Nej", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {

                        pDialog.cancel();
                    }
                });
                pDialog.setConfirmButton("Ja", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        readyForPickUp(orderID);
                    }
                });
                pDialog.show();
            }
        });
        String DeliveryText = "";
        switch (deliveryType) {
            case "1":
                DeliveryText = context.getString(R.string.Delivery);break;
            case "2":
                DeliveryText = context.getString(R.string.Pick_Up);break;
            case "3":
                DeliveryText = context.getString(R.string.Eat_In);break;
            case "4":
                DeliveryText = context.getString(R.string.Curbside);break;
            case "5":
                DeliveryText = context.getString(R.string.Driver_thru);break;
        }
        tvStatus.setText(DeliveryText);

        String statusType = orderDao.getStatus(expandedListText.toString());

        if(statusType.equals("7")) {
        btnPickUp.setVisibility(View.VISIBLE);
        }else{
            btnPickUp.setVisibility(View.GONE);
        }


        Integer time = orderDao.getPreparedIn(orderID);
        String dislayTime = "";

        if(time>0 && time!=null){
            dislayTime=String.valueOf(time)+" min";
        }else{
            dislayTime = "SET TIME";
        }
        switch (statusType) {
            case "1":
            {
                ivDot.setColorFilter(context.getResources().getColor(R.color.green));
                ivNewOrder.setColorFilter(context.getResources().getColor(R.color.green));
                ivOrderIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_item_logo));
                break;
            }
            case "2":
            {
                ivDot.setColorFilter(context.getResources().getColor(R.color.green));
                ivNewOrder.setColorFilter(context.getResources().getColor(R.color.green));
                ivOrderIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_item_logo));
                break;
            }
            case "5":
            {
                ivDot.setColorFilter(context.getResources().getColor(R.color.green));
                ivNewOrder.setColorFilter(context.getResources().getColor(R.color.green));
                ivOrderIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_item_logo));
                break;
            }
            case "6":
            {
                ivDot.setColorFilter(context.getResources().getColor(R.color.green));
                ivNewOrder.setColorFilter(context.getResources().getColor(R.color.green));
                ivOrderIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_item_logo));
                break;
            }
            case "11":
            {
                ivDot.setColorFilter(context.getResources().getColor(R.color.green));
                ivNewOrder.setColorFilter(context.getResources().getColor(R.color.green));
                ivOrderIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_item_logo));
                break;
            }
            case "12":
            {
                ivDot.setColorFilter(context.getResources().getColor(R.color.green));
                ivNewOrder.setColorFilter(context.getResources().getColor(R.color.green));
                ivOrderIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_item_logo));
                break;
            }
            case "16":
            {
                ivDot.setColorFilter(context.getResources().getColor(R.color.green));
                ivNewOrder.setColorFilter(context.getResources().getColor(R.color.green));
                ivOrderIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_item_logo));
                break;
            }
            case "17":
            {
                ivDot.setColorFilter(context.getResources().getColor(R.color.green));
                ivNewOrder.setColorFilter(context.getResources().getColor(R.color.green));
                ivOrderIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_item_logo));
                break;
            }
            case "10":
            {
                ivDot.setColorFilter(context.getResources().getColor(R.color.green));
                ivNewOrder.setColorFilter(context.getResources().getColor(R.color.green));
                ivOrderIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_item_logo));
                break;
            }
            case "3":
            {
                ivDot.setColorFilter(context.getResources().getColor(R.color.yellow));
                ivNewOrder.setColorFilter(context.getResources().getColor(R.color.yellow));
                ivOrderIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_inprogress_1));
                tvPreparedIn.setText(dislayTime);
                break;
            }
            case "21":
            {
                ivDot.setColorFilter(context.getResources().getColor(R.color.yellow));
                ivNewOrder.setColorFilter(context.getResources().getColor(R.color.yellow));
                ivOrderIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_inprogress_1));
                tvPreparedIn.setText(dislayTime);
                break;
            }
            case "20":
            {
                ivDot.setColorFilter(context.getResources().getColor(R.color.yellow));
                ivNewOrder.setColorFilter(context.getResources().getColor(R.color.yellow));
                ivOrderIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_inprogress_1));
                tvPreparedIn.setText(dislayTime);
                break;
            }
            case "19":
            {
                ivDot.setColorFilter(context.getResources().getColor(R.color.yellow));
                ivNewOrder.setColorFilter(context.getResources().getColor(R.color.yellow));
                ivOrderIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_inprogress_1));
                tvPreparedIn.setText(dislayTime);
                break;
            }
            case "18":
            {
                ivDot.setColorFilter(context.getResources().getColor(R.color.yellow));
                ivNewOrder.setColorFilter(context.getResources().getColor(R.color.yellow));
                ivOrderIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_inprogress_1));
                tvPreparedIn.setText(dislayTime);
                break;
            }
            case "15":
            {
                ivDot.setColorFilter(context.getResources().getColor(R.color.yellow));
                ivNewOrder.setColorFilter(context.getResources().getColor(R.color.yellow));
                ivOrderIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_inprogress_1));
                tvPreparedIn.setText(dislayTime);
                break;
            }
            case "14":
            {
                ivDot.setColorFilter(context.getResources().getColor(R.color.yellow));
                ivNewOrder.setColorFilter(context.getResources().getColor(R.color.yellow));
                ivOrderIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_inprogress_1));
                tvPreparedIn.setText(dislayTime);
                break;
            }
            case "9":
            {
                ivDot.setColorFilter(context.getResources().getColor(R.color.yellow));
                ivNewOrder.setColorFilter(context.getResources().getColor(R.color.yellow));
                ivOrderIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_inprogress_1));
                tvPreparedIn.setText(dislayTime);
                break;
            }
            case "8":
            {
                ivDot.setColorFilter(context.getResources().getColor(R.color.yellow));
                ivNewOrder.setColorFilter(context.getResources().getColor(R.color.yellow));
                ivOrderIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_inprogress_1));
                tvPreparedIn.setText(dislayTime);
                break;
            }
            case "7":
            {
                ivDot.setColorFilter(context.getResources().getColor(R.color.yellow));
                ivNewOrder.setColorFilter(context.getResources().getColor(R.color.yellow));
                ivOrderIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_inprogress_1));
                tvPreparedIn.setText(dislayTime);
                break;
            }
            case "4":
            {
                ivDot.setColorFilter(context.getResources().getColor(R.color.yellow));
                ivNewOrder.setColorFilter(context.getResources().getColor(R.color.yellow));
                ivOrderIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_inprogress_1));
                tvPreparedIn.setText(dislayTime);
                break;
            }
            case "0":
            {
                ivDot.setColorFilter(context.getResources().getColor(R.color.brown));
                ivNewOrder.setColorFilter(context.getResources().getColor(R.color.brown));
                ivOrderIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_pending));
                tvPreparedIn.setText(dislayTime);
                break;
            }
            case "13":
            {
                ivDot.setColorFilter(context.getResources().getColor(R.color.brown));
                ivNewOrder.setColorFilter(context.getResources().getColor(R.color.brown));
                ivOrderIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_pending));
                tvPreparedIn.setText(dislayTime);
                break;
            }
        }
        switch (deliveryType) {
            case "2":
            {
                ivOrderIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_item_logo));
                break;
            }
            case "1":
            {
                ivOrderIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_inprogress_1));
                break;
            }

        }
        tvOrderTime.setText(orderDao.getOrderDateTime(expandedListText.toString()));
        tvCustomer.setText(orderDao.getCustomerName(expandedListText.toString()));
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                context.sendBroadcast(new Intent(Default.IS_FROM_DONE).putExtra(Default.IS_ORDER_DONE_ACTIVITY, true));
//                Intent i = new Intent(context, OrderPreviewActivity.class);
//                i.putExtra("orderId",expandedListText);
//                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(i);
                NewOrderDialog newOrderDialog = new NewOrderDialog(expandedListText);
                newOrderDialog.show(expandablectivity.getSupportFragmentManager(),"");

//                val orderStatusDialog = CalculatorDialog(this, cartAmount.toString())
//                orderStatusDialog.show(childFragmentManager, "Order Status")
            }
        });


        return convertView;
    }

    @Override
    // Gets the number of children in a specified group.
    public int getChildrenCount(int listPosition) {
        return this.expandableDetailList.get(this.expandableTitleList.get(listPosition)).size();
    }

    @Override
    // Gets the data associated with the given group.
    public Object getGroup(int listPosition) {
        return this.expandableTitleList.get(listPosition);
    }

    @Override
    // Gets the number of groups.
    public int getGroupCount() {
        return this.expandableTitleList.size();
    }

    @Override
    // Gets the ID for the group at the given position. This group ID must be unique across groups.
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    // Gets a View that displays the given group.
    // This View is only for the group--the Views for the group's children
    // will be fetched using getChildView()
    public View getGroupView(int listPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String listTitle = (String) getGroup(listPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_group, null);
        }
        TextView listTitleTextView = (TextView) convertView.findViewById(R.id.listTitle);
        listTitleTextView.setTypeface(null, Typeface.BOLD);


        Integer orderCount=0;
        if(listTitle.equals("Pågående")) {
            orderCount=orderDao.getInProgressOrderCount();
        }
        if(listTitle.equals("Väntar")){
            orderCount=orderDao.getPendingOrderCount();
        }
        listTitleTextView.setText(listTitle+" ("+orderCount.toString()+") ");
        if(!isExpanded) listTitleTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_add_circle_24, 0, 0, 0); else listTitleTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_new_minus, 0, 0, 0);
        return convertView;
    }

    @Override
    // Indicates whether the child and group IDs are stable across changes to the underlying data.
    public boolean hasStableIds() {
        return false;
    }

    @Override
    // Whether the child at the specified position is selectable.
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }

    public void readyForPickUp(String orderId){
        lottieProgressDialog.showDialog();
        ServiceGenerator.getNentoApi().RejectORder(Constants.apiKey,orderId,"1").enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful())
                {
                    orderDao.changeOrderStatus(orderId, Constants.readyForPickUp);
                    lottieProgressDialog.cancelDialog();
                    SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context,SweetAlertDialog.SUCCESS_TYPE);
                    sweetAlertDialog.setContentText(context.getResources().getString(R.string.lbl_pickUpReady));
                    sweetAlertDialog.setConfirmText(context.getResources().getString(R.string.lbl_OK));
                    sweetAlertDialog.setCancelable(false);
                    sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.cancel();
                            Intent i = new Intent(context, Expandablectivity.class);
                            i.putExtra("businessID",businessID);
                            i.putExtra(SharedPreferencesKeys.isDBLoadRequired,false);
                            context.startActivity(i);
                        }
                    });
//                    sweetAlertDialog.show();
//                    sweetAlertDialog.setContentText(context.getResources().getString(R.string.lbl_pickUpReady));
                    Intent i = new Intent(context, Expandablectivity.class);
                    i.putExtra(SharedPreferencesKeys.isDBLoadRequired,false);
                    i.putExtra("businessID",businessID);
                    context.startActivity(i);
                }else{
                    lottieProgressDialog.cancelDialog();
                    SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context,SweetAlertDialog.ERROR_TYPE);
                    sweetAlertDialog.setContentText(context.getResources().getString(R.string.lbl_error));
                    sweetAlertDialog.setConfirmText(context.getResources().getString(R.string.lbl_OK));
                    sweetAlertDialog.setCancelable(false);
                    sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                           sweetAlertDialog.cancel();
                        }
                    });
                    sweetAlertDialog.show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                lottieProgressDialog.cancelDialog();
                SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context,SweetAlertDialog.ERROR_TYPE);
                sweetAlertDialog.setContentText(context.getResources().getString(R.string.lbl_error));
                sweetAlertDialog.setConfirmText(context.getResources().getString(R.string.lbl_OK));
                sweetAlertDialog.setCancelable(false);
                sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.cancel();
                    }
                });
                sweetAlertDialog.show();
            }
        });
    }

    @Override
    public void onClose() {

    }

//    @Override
//    public void onOKClick(@NonNull String orderId) {
//z
//    }
}

