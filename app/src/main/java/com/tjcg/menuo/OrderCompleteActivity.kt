package com.tjcg.menuo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.tjcg.menuo.adapter.OrderComepleteAdapter
import com.tjcg.menuo.data.local.AppDatabase
import com.tjcg.menuo.data.local.OrderDao
import com.tjcg.menuo.dialog.NewOrderDialog
import com.tjcg.menuo.utils.Default
import com.tjcg.menuo.utils.SharedPreferencesKeys
import java.util.ArrayList

class OrderCompleteActivity : AppCompatActivity(), OrderComepleteAdapter.orderClickListener {

    lateinit var itemManagementDetailsLayoutManager: LinearLayoutManager
    lateinit var orderComepleteAdapter: OrderComepleteAdapter
    lateinit var recyclerViewOrderComplete: RecyclerView
    lateinit var swipeRefreshCategory: SwipeRefreshLayout
    lateinit var imageViewBack: ImageView

    var orderDao: OrderDao? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complete_order)

        recyclerViewOrderComplete = findViewById(R.id.recyclerViewOrderComplete)
        swipeRefreshCategory = findViewById(R.id.swipeRefreshCategory)
        imageViewBack = findViewById(R.id.imageViewBack)
        orderDao = AppDatabase.getDatabase(this)!!.orderDao()

        var orderListDone : List<Int> = orderDao!!.getDoneOrder()
        var orderListDoneString : List<String> = convertIntToString(orderListDone)

        itemManagementDetailsLayoutManager = LinearLayoutManager(this)
        recyclerViewOrderComplete.layoutManager = itemManagementDetailsLayoutManager
        orderComepleteAdapter = OrderComepleteAdapter(this, this, orderListDoneString)
        recyclerViewOrderComplete.adapter = orderComepleteAdapter

        imageViewBack.setOnClickListener {
            startActivity(Intent(this, Expandablectivity::class.java).putExtra(SharedPreferencesKeys.isDBLoadRequired,false))
            finish()
        }

    }

    override fun onItemClick(orderId: String, position: Int) {
        this.sendBroadcast(Intent(Default.IS_FROM_DONE).putExtra(Default.IS_ORDER_DONE_ACTIVITY, true))
//        val i = Intent(this@OrderCompleteActivity, OrderPreviewActivity::class.java)
//        i.putExtra("orderId", orderId)
//        i.putExtra("OrderDone", true)
//        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//        startActivity(i)

        val newOrderDialog = NewOrderDialog(orderId)
        newOrderDialog.show(getSupportFragmentManager(), "")
    }

    fun convertIntToString(objInt: List<Int>) : List<String>{
//        var stringObj : List<String> = arrayListOf()
        val stringObj: MutableList<String> = ArrayList()
        for(obj in objInt){
            stringObj.add(obj.toString())
        }
        return stringObj
    }
}