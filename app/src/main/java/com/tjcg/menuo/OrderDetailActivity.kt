package com.tjcg.menuo

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import androidx.appcompat.app.AppCompatActivity
import com.tjcg.menuo.databinding.ActivityOrderDetailBinding
import com.tjcg.menuo.utils.Constants
import com.tjcg.menuo.utils.PrefManager
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import woyou.aidlservice.jiuiv5.IWoyouService

import android.graphics.BitmapFactory
import android.text.Html

import android.util.Log
import com.tjcg.menuo.data.local.AppDatabase
import com.tjcg.menuo.data.local.OrderDao
import com.tjcg.menuo.data.response.newOrder.Product
import com.tjcg.menuo.data.response.newOrder.Result
import com.tjcg.menuo.utils.Default
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class OrderDetailActivity : AppCompatActivity() {
    var prefManager: PrefManager? = null
    private var woyouService: IWoyouService? = null

    var orderId: String = "0";
    var orderData = "0";

    var orderDatte: String = ""
    var PaymentMethod: String = ""
    var BusinessName: String = ""
    var BusinessMob1: String = ""
    var BusinessMob2: String = ""
    var BusinessLocation: String = ""
    var CustomerNAme: String = ""
    var CustomerEmail: String = ""
    var CustomerMobno: String = ""
    var CustomerLocation: String = ""
    var CustomerNote: String = ""
    var CustomerZip: String = ""
    var Subtotal: String = ""
    var deliveryFee: String = ""
    var Total: String = ""
    var binding: ActivityOrderDetailBinding? = null
    var arrProductList : ArrayList<Product> = arrayListOf()
    var arrProNAme = java.util.ArrayList<String>()
    var arrProQty = java.util.ArrayList<String>()
    var arrProPrice = java.util.ArrayList<String>()

    var arrResult : ArrayList<Result> = arrayListOf()
    var orderDao: OrderDao? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
//        setContentView(R.layout.activity_order_detail)

        orderDao = AppDatabase.getDatabase(this)!!.orderDao()

        var i: Intent = intent
        orderId = i.getStringExtra("orderId")!!
//        orderData = i.getStringExtra("orderData")!!


//        arrResult= orderDao!!.getOrderDataByID(orderId);

        try {
                    val jobj = JSONObject(orderData)
//                  val jarrResult: JSONArray = jobj.getJSONArray("result")
                    val order: JSONObject = jobj.getJSONObject("result")
                    orderDatte = order.getString("delivery_datetime")
                    //get paymentmenthod
                    var paymentMethod = order.getJSONObject("paymethod")
                    PaymentMethod = paymentMethod.getString("name")

//                    ??buisness detail
                    var buisnessJsonObj = order.getJSONObject("business")
                    BusinessName = buisnessJsonObj.getString("name")
                    BusinessMob1 = buisnessJsonObj.getString("cellphone")
                    BusinessMob2 = buisnessJsonObj.getString("phone")
                    BusinessLocation = buisnessJsonObj.getString("address")

//                    Customer
                    var customerJsonObj = order.getJSONObject("customer")
                    CustomerNAme = customerJsonObj.getString("name")
                    CustomerEmail = customerJsonObj.getString("email")
                    CustomerMobno = customerJsonObj.getString("cellphone")
                    CustomerLocation = customerJsonObj.getString("address")
                    CustomerNote = customerJsonObj.getString("address_notes")
                    CustomerZip = customerJsonObj.getString("zipcode")

                    //product detail

                    var productArr : JSONArray = order.getJSONArray("products")
            var proNAmeString : String = ""
                    for (i in 0..productArr.length()-1){
                        var product : JSONObject? = productArr.getJSONObject(i)
                        var name=product!!.getString("name")
                        var price=product!!.getString("price")
                        var quantity=product!!.getString("quantity")
                        arrProNAme.add(name)
                        arrProPrice.add(price)
                        arrProQty.add(quantity)
                        proNAmeString = proNAmeString + ""+quantity+" "+name +" : "+price+" </br>"

                    }
            arrProNAme
            arrProPrice
            arrProQty

                    //payment detail
                    var summeryJsonObj = order.getJSONObject("summary")
                    Subtotal = summeryJsonObj.getString("subtotal")
                    deliveryFee = summeryJsonObj.getString("delivery_price")
                    Total = summeryJsonObj.getString("total")


//                }
                binding!!.textViewOrderRecyclerPlace.text=Html.fromHtml(proNAmeString)
                binding!!.textViewDate.text = orderDatte
                binding!!.textViewPmethod.text = PaymentMethod
                binding!!.textViewHome.text = BusinessName
                binding!!.textViewCall.text = BusinessMob1
                binding!!.textViewCall2.text = BusinessMob2
                binding!!.textViewLocation.text = BusinessLocation
                binding!!.textViewCustName.text = CustomerNAme
                binding!!.textViewEmail.text = CustomerEmail
                binding!!.textViewCustomerPhone.text = if(CustomerMobno.equals("null") || CustomerMobno.equals("")) "" else CustomerMobno
                binding!!.textViewCustomerLocation.text = CustomerLocation
                binding!!.textViewDetail.text = CustomerNote
                binding!!.textViewZipCode.text = CustomerZip
//                binding!!.textViewOrderRecyclerPlace.text=orderDatte
                binding!!.textviewSubtotal.text = "Subtotal: " + Subtotal
                binding!!.textViewDeliveryFee.text = "Delivery charge: " + deliveryFee
                binding!!.total.text = "Total: " + Total
                binding!!.textView10.setOnClickListener {


                }
                binding!!.textViewPrit.setOnClickListener {
                    connectPrinter(applicationContext)
                    testSunmiPrint(applicationContext, orderId.toInt(), orderDatte, CustomerNAme, CustomerMobno, CustomerLocation, CustomerLocation,"$", Subtotal,deliveryFee, Total, arrProNAme,arrProQty,arrProPrice )
                }

//                arrOdate.add(order.getString("delivery_datetime"))
//                arrOId.add(order.getString("id"))

        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private val connService: ServiceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName) {
            woyouService = null
        }

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            woyouService = IWoyouService.Stub.asInterface(service)
        }
    }

    //
    fun testSunmiPrint(context: Context, orderNumber: Int, date: String, customerName: String, phoneNumber: String, address1: String, address2: String, currency: String, subTotal: String, deliveryFee: String, total: String, proName : ArrayList<String>, proQty : ArrayList<String>, proPrice : ArrayList<String> ) {//, orderDetail: OrderDetail?
        try {
            /*       if (woyouService != null) {
                       printerConfigure(context)
                       woyouService!!.printText("=====================================\n\n", null)
                       woyouService!!.printText("Date: "+orderDatte, null)
                       woyouService!!.printText("\n", null)
                       woyouService!!.printText("Business Detail: "+BusinessName, null)
                       woyouService!!.printText("\n", null)
                       woyouService!!.printText("Business phno: "+BusinessMob1, null)
                       woyouService!!.printText("\n", null)
                       woyouService!!.printText("Business phno 2: "+BusinessMob2, null)
                       woyouService!!.printText("\n", null)

                       woyouService!!.cutPaper(null)
            */
            if (woyouService != null) {
                setHeaderData(context, orderNumber, date, customerName, phoneNumber, address1, address2)
                woyouService!!.printText("\n", null)
                woyouService!!.printText("=======================\n", null)
                woyouService!!.setFontSize(24f, null)
                woyouService!!.sendRAWData(normalFont(), null)
                woyouService!!.printText("=======================\n", null)
                woyouService!!.printText(rightPadZeros(context.getString(R.string.lbl_ItemName).toUpperCase(), 17) + " " + rightPadZeros(context.getString(R.string.lbl_Quantity).toUpperCase(), 5) + " " + leftPadZeros(context.getString(R.string.lbl_Price).toUpperCase(), 8) + "\n", null)
                woyouService!!.printText("=======================\n", null)
                for (i in arrProNAme.indices) {
//                    val items = orderDetail.lstobjServiceOrderItem[i]
                    //woyouService!!.printText(rightPadZeros(truncateString(arrProNAme.get(i), 15, true) + truncateString(" " + currency + " " + arrProPrice.get(i), 15, true), 43) + "\n", null)
                    woyouService!!.printText(rightPadZeros(truncateString(arrProNAme.get(i), 15, true),17) + rightPadZeros(truncateString(arrProQty.get(i), 15, true),5) + leftPadZeros(truncateString(" " + currency + " " + arrProPrice.get(i), 15, true), 8) + "\n", null)

//                    woyouService!!.printText(rightPadZeros(truncateString("", 15, true), 27) + rightPadZeros(if (items.status == Default.COMPLETED) items.itemQty.toString() else (items.itemQty - (items.itemQty * 2)).toString(), 6) + " " + leftPadZeros(String.format(Locale.getDefault(), Constants.StringFormat, currency, getTwoDecimalValue(if (items.status == Default.COMPLETED) (items.pricePerUnit * items.itemQty) else (if (items.status == Default.SINGLERETURN) ((items.pricePerUnit - (items.pricePerUnit * 2)) * items.itemQty) else (items.pricePerUnit - (items.pricePerUnit * 2)) * items.itemQty))), 10) + "\n", null)
                }//example for set order Object
                woyouService!!.sendRAWData(normalFont(), null)
                woyouService!!.printText("--------------------------------\n", null)
                woyouService!!.printText(rightPadZeros(context.getString(R.string.lbl_subTotal), 17) + "" + leftPadZeros("$currency $subTotal", 11) + "\n", null)
                woyouService!!.printText(rightPadZeros(context.getString(R.string.lbl_Delivery_free), 17) + "" + leftPadZeros("$currency $deliveryFee", 11) + "\n", null)
                woyouService!!.printText("--------------------------------\n", null)
                woyouService!!.sendRAWData(boldFont(), null)
                woyouService!!.setFontSize(30f, null)
                woyouService!!.printText(rightPadZeros(context.getString(R.string.lbl_Total), 14) + "" + leftPadZeros("$total", 8) + "\n", null)
                woyouService!!.printText("\n", null)
                woyouService!!.cutPaper(null)
            }
        } catch (ex: RemoteException) {

        }
    }


    private fun setHeaderData(context: Context, orderNumber: Int, date: String, customerName: String, phoneNumber: String, address1: String, address2: String) {
        if (woyouService != null) {
            woyouService!!.setFontSize(40f, null)
            woyouService!!.sendRAWData(boldFont(), null)
            woyouService!!.sendRAWData(alignCenter(), null)
            woyouService!!.printText(truncateString(context.getString(R.string.lbl_Name), 30) + "\n", null)
            woyouService!!.setFontSize(30f, null)
            //woyouService!!.printBitmap(BitmapFactory.decodeResource(resources, R.drawable.ic_logo_name),)//callBack(Image)
            woyouService!!.sendRAWData(normalFont(), null)
            woyouService!!.printText("${context.getString(R.string.lbl_OrderNo)}" + " : $orderNumber" + "\n", null)
            woyouService!!.sendRAWData(boldFont(), null)
            woyouService!!.printText(truncateString(context.getString(R.string.lbl_delivery).toUpperCase(), 30) + "\n", null)
            woyouService!!.sendRAWData(normalFont(), null)
            woyouService!!.printText("${context.getString(R.string.lbl_DATE)} " + date.toUpperCase() + "\n", null) // createdDate
            woyouService!!.sendRAWData(boldFont(), null)
            woyouService!!.printText(truncateString(context.getString(R.string.lbl_Customer).toUpperCase(), 30) + "\n", null)
            woyouService!!.sendRAWData(normalFont(), null)
            woyouService!!.printText(truncateString("$customerName", 30) + "\n", null)
            woyouService!!.printText(truncateString("$phoneNumber", 30) + "\n", null)
            woyouService!!.sendRAWData(alignCenter(), null)
            woyouService!!.printText(truncateString("$address1", 32) + "\n", null)
            woyouService!!.printText(truncateString("$address2", 30) + "\n", null)
            woyouService!!.sendRAWData(boldFont(), null)
            woyouService!!.printText(truncateString(context.getString(R.string.lbl_notes).toUpperCase(), 30) + "\n", null)
            woyouService!!.sendRAWData(normalFont(), null)
            woyouService!!.printText(truncateString(context.getString(R.string.lbl_notes_), 30) + "\n", null)

        }
    }

    fun printPhoto() {
        try {
            val icon = BitmapFactory.decodeResource(resources, R.drawable.ic_logo_name)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("PrintTools", "the file isn't exists")
        }
    }

    fun truncateString(str: String, len: Int, isForPrint: Boolean = false): String {
        return if (str.length > len) {
            if (isForPrint) str.substring(0, len)
            else str.substring(0, len) + "..."
        } else str
    }

    fun rightPadZeros(str: String, num: Int): String {
        return String.format("%1$-" + num + "s", str).replace(' ', ' ')
    }

    fun leftPadZeros(str: String, num: Int): String {
        return String.format("%1$" + num + "s", str).replace(' ', ' ')
    }

    fun getDate(format: String): String {
        val c = Calendar.getInstance()
        val simpleDateFormat = SimpleDateFormat(format)
        return simpleDateFormat.format(c.time)
    }

    private fun boldFont(): ByteArray? {
        val result = ByteArray(3)
        result[0] = Default.ESC
        result[1] = 69
        result[2] = 0xF
        return result
    }

    fun normalFont(): ByteArray? {
        val result = ByteArray(3)
        result[0] = Default.ESC
        result[1] = 69
        result[2] = 0
        return result
    }

    private fun alignCenter(): ByteArray? {
        val result = ByteArray(3)
        result[0] = Default.ESC
        result[1] = 97
        result[2] = 1
        return result
    }

    fun alignLeft(): ByteArray? {
        val result = ByteArray(3)
        result[0] = Default.ESC
        result[1] = 97
        result[2] = 0
        return result
    }

    //
    private fun printerConfigure(context: Context) {
        if (woyouService != null) {
            woyouService!!.setFontSize(30f, null)
            woyouService!!.printText("Hello", null)
        }
    }

    fun connectPrinter(context: Context) {
        val intent = Intent()
        intent.setPackage(Constants.SERVICE_PACKAGE)
        intent.action = Constants.SERVICE_ACTION
        context.applicationContext.startService(intent)
        context.applicationContext.bindService(intent, connService, Context.BIND_AUTO_CREATE)
    }


}