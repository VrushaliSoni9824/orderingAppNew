package com.tjcg.menuo

import android.annotation.SuppressLint
import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import androidx.appcompat.app.AppCompatActivity
import com.tjcg.menuo.utils.Constants
import com.tjcg.menuo.utils.PrefManager
import woyou.aidlservice.jiuiv5.IWoyouService

import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.tjcg.menuo.adapter.OrderPreviewProductAdapter
import com.tjcg.menuo.data.local.AppDatabase
import com.tjcg.menuo.data.local.OrderDao
import com.tjcg.menuo.data.remote.ServiceGenerator
import com.tjcg.menuo.data.response.EntitiesModel.*
import com.tjcg.menuo.data.response.newOrder.Product
import com.tjcg.menuo.data.response.newOrder.Result
import com.tjcg.menuo.databinding.OrderPreviewLayoutBinding
import com.tjcg.menuo.utils.Default
import com.tjcg.menuo.utils.LottieProgressDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class OrderPreviewActivity : AppCompatActivity() {
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
    var CustomerEmail: String = ""
    var CustomerLocation: String = ""
    var CustomerNote: String = ""
    var CustomerZip: String = ""
    var deliveryFee: String = ""
    var binding: OrderPreviewLayoutBinding? = null
    var arrProductList : ArrayList<Product> = arrayListOf()
    var arrProNAme = java.util.ArrayList<String>()
    var arrProQty = java.util.ArrayList<String>()
    var arrProPrice = java.util.ArrayList<String>()
    private var intentBroadcast: BroadcastReceiver? = null
    var isFromDoneActivity: Boolean = false
    var orderDate : String = ""
    lateinit var arrResult : Result
    lateinit var arrBusiness : BisinessEntity
    lateinit var arrCity : CityEntity
    lateinit var arrCustomer : CustomerEntity
    lateinit var arrHistory : HistoryEntity
    var arrProduct : List<ProductEntity> = arrayListOf()
    lateinit var arrSummery : SummaryEntity
    var orderDao: OrderDao? = null
    var productAdapter: OrderPreviewProductAdapter? = null
    var preparedIn : String = "30"
    var businessID: String? = null
    var lottieProgressDialog: LottieProgressDialog? = null
    var imageViewBack : ImageView? = null
    var status = ""
    var customerMobno: String = ""
    var customerNAme : String = ""
    var customerAddress : String = ""
    var total : String = ""
    var subtotal : String = ""
//     lateinit var sheetBehavior: BottomSheetBehavior<LinearLayout>
     lateinit var bottomSheet: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = OrderPreviewLayoutBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
//        setContentView(R.layout.activity_order_detail)
//        bottomSheet = findViewById(R.id.bottomSheet)
//        sheetBehavior = BottomSheetBehavior.from(bottomSheet)

        lottieProgressDialog = LottieProgressDialog(this)
        orderDao = AppDatabase.getDatabase(this)!!.orderDao()
        prefManager = PrefManager(applicationContext)
        businessID=prefManager!!.getString("businessID")

        init()


//        binding!!.bottomSheetOrderMinutes.

        binding!!.imageViewBack.setOnClickListener {
            if (!isFromDoneActivity){
                val i = Intent(this, Expandablectivity::class.java)
                i.putExtra("businessID", businessID)
                startActivity(i)
                finish()
            }else{
                val i = Intent(this, OrderCompleteActivity::class.java)
                i.putExtra("businessID", businessID)
                startActivity(i)
                finish()
            }
        }

        var i: Intent = intent
        orderId = i.getStringExtra("orderId")!!
//        orderData = i.getStringExtra("orderData")!!


        arrResult= orderDao!!.getOrderDataByID(orderId);
        arrBusiness= orderDao!!.getBusinessById(orderId);
        arrCity= orderDao!!.getCityById(orderId);
        arrCustomer= orderDao!!.getCustomerById(orderId);
        arrHistory= orderDao!!.getHistoryById(orderId);
        arrProduct= orderDao!!.getProductById(orderId);
        arrSummery= orderDao!!.getSummaryById(orderId);

        var orderId : String = arrResult.id.toString()
        orderDate = arrResult.created_at.toString()
        customerNAme = arrCustomer.name
        customerMobno = arrCustomer.phone
        customerAddress =arrCustomer.address
        var note : String = arrCustomer.address_notes
        subtotal = arrSummery.subtotal.toString()
        var deliveryFee : String =arrSummery.delivery_price.toString()
        total= arrSummery.total.toString()
        var preparedIn = arrResult.prepared_in.toString()
        status = arrResult.status.toString()
        binding!!.textOrderID.text=orderId.toString()
        binding!!.textviewOrderDate.text=orderDate
        binding!!.textviewCustomerName.text=customerNAme
        binding!!.customerMobno.text=if(customerMobno.equals("null") || customerMobno.equals("")) "" else customerMobno
        binding!!.textviewCustomerAddress.text=customerAddress
        binding!!.textviewNotes.text=if(note.equals("null") || note.equals("")) "" else note
        binding!!.textViewSubtotal.text=subtotal
        binding!!.textviewdeliveryfee.text=deliveryFee
        binding!!.textviewTotal.text=total
        binding!!.textViewPreparedIn.text=if(preparedIn.toString().equals("null") || preparedIn.toString().equals(""))
            ""
        else
                preparedIn.toString()
        productAdapter = OrderPreviewProductAdapter(arrProduct,applicationContext)
        binding!!.rvProduct.layoutManager = LinearLayoutManager(applicationContext)
        binding!!.rvProduct.adapter = productAdapter
        for(i in 0..arrProduct.size-1){
            arrProNAme.add(arrProduct.get(i).name)
            arrProQty.add(arrProduct.get(i).quantity.toString())
            arrProPrice.add(arrProduct.get(i).price.toString())
        }

        if(status.equals("13") || status.equals("0")){
            if(status.equals("13")) {
                binding!!.bottomSheetOrderMinutes.linearMainView.visibility=View.GONE
                binding!!.bottomSheetOrderMinutes.linearLayout4.visibility=View.GONE
            }
            else{
//            binding!!.bottomSheetOrderMinutes.linearMainView.visibility=View.VISIBLE
                binding!!.bottomSheetOrderMinutes.linearLayout4.visibility=View.VISIBLE
                binding!!.bottomSheetOrderMinutes.textViewAccept.visibility=View.VISIBLE
                binding!!.bottomSheetOrderMinutes.imageArrowUp.visibility=View.VISIBLE
            }
        }else{
            binding!!.bottomSheetOrderMinutes.linearMainView.visibility=View.GONE
            binding!!.bottomSheetOrderMinutes.linearLayout4.visibility=View.GONE
            binding!!.bottomSheetOrderMinutes.textViewAccept.visibility=View.GONE
            binding!!.bottomSheetOrderMinutes.imageArrowUp.visibility=View.GONE
        }

        binding!!.bottomSheetOrderMinutes.tv11.setOnClickListener { preparedIn = "10"; setButtonColor(tv11 = true)}
        binding!!.bottomSheetOrderMinutes.tv15.setOnClickListener { preparedIn = "15";setButtonColor(tv15 = true)}
        binding!!.bottomSheetOrderMinutes.tv20.setOnClickListener { preparedIn = "20";setButtonColor(tv20 = true)}
        binding!!.bottomSheetOrderMinutes.tv25.setOnClickListener { preparedIn = "25";setButtonColor(tv25 = true)}
        binding!!.bottomSheetOrderMinutes.tv30.setOnClickListener { preparedIn = "30";setButtonColor(tv30 = true)}
        binding!!.bottomSheetOrderMinutes.tv40.setOnClickListener { preparedIn = "45";setButtonColor(tv40 = true)}
        binding!!.bottomSheetOrderMinutes.tv50.setOnClickListener { preparedIn = "50";setButtonColor(tv50 = true)}
        binding!!.bottomSheetOrderMinutes.tv60.setOnClickListener { preparedIn = "60";setButtonColor(tv60 = true)}
        binding!!.bottomSheetOrderMinutes.tv70.setOnClickListener { preparedIn = "70";setButtonColor(tv70 = true)}
        binding!!.bottomSheetOrderMinutes.tv80.setOnClickListener { preparedIn = "80";setButtonColor(tv80 = true)}
        binding!!.bottomSheetOrderMinutes.tv90.setOnClickListener { preparedIn = "90";setButtonColor(tv90 = true)}


        binding!!.bottomSheetOrderMinutes.imageArrowUp.setOnClickListener {
            if(!status.equals("13")) binding!!.bottomSheetOrderMinutes.linearMainView.visibility=if(binding!!.bottomSheetOrderMinutes.linearMainView.visibility.equals(View.VISIBLE)) View.GONE else View.VISIBLE
//            if(sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED){
//                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
//                linearLayout1.visibility = View.GONE
//                linearLayout2.visibility = View.GONE
//                linearLayout3.visibility = View.GONE
//                linearMainView.visibility=View.GONE
//
//            } else {
//                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
//                linearLayout1.visibility = View.VISIBLE
//                linearLayout2.visibility = View.VISIBLE
//                linearLayout3.visibility = View.VISIBLE
//                linearMainView.visibility=View.VISIBLE
//            }
        }

        binding!!.bottomSheetOrderMinutes.textViewAccept.visibility= if(status.equals("0") || status.equals("13")) View.VISIBLE else View.GONE

        binding!!.bottomSheetOrderMinutes.textViewReject.setOnClickListener {
            var myIntent =  Intent(this@OrderPreviewActivity, OrderRejectActivity::class.java)
            myIntent.putExtra("phoneNumber", customerMobno)
            myIntent.putExtra("orderId", orderId)
            startActivity(myIntent)
        }

        binding!!.bottomSheetOrderMinutes.textViewAccept.setOnClickListener {
            if(status.equals("13")) preparedIn= "0"
            setAcceptOrder(preparedIn)
            //showBottomSheetDialog()
        }

        binding!!.bottomSheetOrderMinutes.textViewPrit.setOnClickListener {
            connectPrinter(applicationContext)
            testSunmiPrint(applicationContext, orderId.toInt(), orderDate, customerNAme, customerMobno, customerAddress, customerAddress,"$", subtotal,deliveryFee, total, arrProNAme,arrProQty,arrProPrice )
        }

        /*
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
                binding!!.textViewCustomerPhone.text = CustomerMobno
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
         */



    }

    override fun onResume() {
        super.onResume()
        registerReceiver(intentBroadcast, IntentFilter(Default.IS_FROM_DONE))
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(intentBroadcast)
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
                woyouService!!.setFontSize(24f, null)
                woyouService!!.sendRAWData(normalFont(), null)
                woyouService!!.printText("================================\n", null)
                woyouService!!.printText(rightPadZeros(context.getString(R.string.lbl_ItemName).toUpperCase(), 17) + " " + rightPadZeros(context.getString(R.string.lbl_Quantity).toUpperCase(), 5) + " " + leftPadZeros(context.getString(R.string.lbl_Price).toUpperCase(), 8) + "\n", null)
                woyouService!!.printText("================================\n", null)
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
            woyouService!!.setFontSize(26f, null)
            //woyouService!!.printBitmap(BitmapFactory.decodeResource(resources, R.drawable.ic_logo_name),)//callBack(Image)
            woyouService!!.sendRAWData(normalFont(), null)
            woyouService!!.printText("------------------------------\n", null)
            woyouService!!.sendRAWData(normalFont(), null)
            woyouService!!.printText("${context.getString(R.string.lbl_OrderNo)}" + " : $orderNumber" + "\n", null)
            woyouService!!.sendRAWData(boldFont(), null)
            woyouService!!.printText(truncateString(context.getString(R.string.lbl_delivery).toUpperCase(), 30) + "\n", null)
            woyouService!!.setFontSize(24f, null)
            woyouService!!.sendRAWData(normalFont(), null)
            woyouService!!.printText("${context.getString(R.string.lbl_DATE)} " + date.toUpperCase() + "\n", null) // createdDate
            woyouService!!.setFontSize(26f, null)
            woyouService!!.sendRAWData(boldFont(), null)
            woyouService!!.printText(truncateString(context.getString(R.string.lbl_Customer).toUpperCase(), 30) + "\n", null)
            woyouService!!.sendRAWData(normalFont(), null)
            woyouService!!.setFontSize(24f, null)
            woyouService!!.printText(truncateString("$customerName", 30) + "\n", null)
            woyouService!!.printText(truncateString("$phoneNumber", 30) + "\n", null)
            woyouService!!.sendRAWData(alignCenter(), null)
            woyouService!!.printText(truncateString("$address1", 32) + "\n", null)
            woyouService!!.printText(truncateString("$address2", 30) + "\n", null)
            woyouService!!.setFontSize(26f, null)
            woyouService!!.sendRAWData(boldFont(), null)
            woyouService!!.printText(truncateString(context.getString(R.string.lbl_notes).toUpperCase(), 30) + "\n", null)
            woyouService!!.setFontSize(24f, null)
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


    fun showBottomSheetDialog(){
//        var  bottomSheetDialog: BottomSheetDialog =  BottomSheetDialog(this)
//        bottomSheetDialog.setContentView(R.layout.bottom_sheet_order_minutes)
//        bottomSheetDialog.window!!.setBackgroundDrawable( ColorDrawable(Color.TRANSPARENT))
//
//        var tv11 : TextView = bottomSheetDialog.findViewById<TextView>(R.id.tv11) as TextView
//        var tv15 : TextView = bottomSheetDialog.findViewById<TextView>(R.id.tv15) as TextView
//        var tv20 : TextView = bottomSheetDialog.findViewById<TextView>(R.id.tv20) as TextView
//        var tv25 : TextView = bottomSheetDialog.findViewById<TextView>(R.id.tv25) as TextView
//        var tv30 : TextView = bottomSheetDialog.findViewById<TextView>(R.id.tv30) as TextView
//        var tv40 : TextView = bottomSheetDialog.findViewById<TextView>(R.id.tv40) as TextView
//        var tv45 : TextView = bottomSheetDialog.findViewById<TextView>(R.id.tv45) as TextView
//        var tv50 : TextView = bottomSheetDialog.findViewById<TextView>(R.id.tv50) as TextView
//        var tv60 : TextView = bottomSheetDialog.findViewById<TextView>(R.id.tv60) as TextView
//        var tv70 : TextView = bottomSheetDialog.findViewById<TextView>(R.id.tv70) as TextView
//        var tv80 : TextView = bottomSheetDialog.findViewById<TextView>(R.id.tv80) as TextView
//        var tv90 : TextView = bottomSheetDialog.findViewById<TextView>(R.id.tv90) as TextView
//        var imageArrowUp : ImageView = bottomSheetDialog.findViewById<ImageView>(R.id.imageArrowUp) as ImageView
//        var linearMainView : LinearLayout = bottomSheetDialog.findViewById<LinearLayout>(R.id.linearMainView) as LinearLayout
//        var linearLayout1 : LinearLayout = bottomSheetDialog.findViewById<LinearLayout>(R.id.linearLayout1) as LinearLayout
//        var linearLayout2 : LinearLayout = bottomSheetDialog.findViewById<LinearLayout>(R.id.linearLayout2) as LinearLayout
//        var linearLayout3 : LinearLayout = bottomSheetDialog.findViewById<LinearLayout>(R.id.linearLayout3) as LinearLayout
//        var linearLayout4 : LinearLayout = bottomSheetDialog.findViewById<LinearLayout>(R.id.linearLayout4) as LinearLayout
//        var textViewAccept : TextView = bottomSheetDialog.findViewById<TextView>(R.id.textViewAccept) as TextView
//        var textViewReject : TextView = bottomSheetDialog.findViewById<TextView>(R.id.textViewReject) as TextView
//        var textViewPrit : TextView = bottomSheetDialog.findViewById<TextView>(R.id.textViewPrit) as TextView
        binding!!.bottomSheetOrderMinutes.tv11.setOnClickListener { preparedIn = "10"; }
        binding!!.bottomSheetOrderMinutes.tv15.setOnClickListener { preparedIn = "15"; }
        binding!!.bottomSheetOrderMinutes.tv20.setOnClickListener { preparedIn = "20"; }
        binding!!.bottomSheetOrderMinutes.tv25.setOnClickListener { preparedIn = "25"; }
        binding!!.bottomSheetOrderMinutes.tv30.setOnClickListener { preparedIn = "30"; }
        binding!!.bottomSheetOrderMinutes.tv40.setOnClickListener { preparedIn = "40"; }
        binding!!.bottomSheetOrderMinutes.tv50.setOnClickListener { preparedIn = "50"; }
        binding!!.bottomSheetOrderMinutes.tv60.setOnClickListener { preparedIn = "60"; }
        binding!!.bottomSheetOrderMinutes.tv70.setOnClickListener { preparedIn = "70"; }
        binding!!.bottomSheetOrderMinutes.tv80.setOnClickListener { preparedIn = "80"; }
        binding!!.bottomSheetOrderMinutes.tv90.setOnClickListener { preparedIn = "90"; }


        binding!!.bottomSheetOrderMinutes.imageArrowUp.setOnClickListener {
            binding!!.bottomSheetOrderMinutes.linearMainView.visibility=View.VISIBLE
//            if(sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED){
//                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
//                linearLayout1.visibility = View.GONE
//                linearLayout2.visibility = View.GONE
//                linearLayout3.visibility = View.GONE
//                linearMainView.visibility=View.GONE
//
//            } else {
//                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
//                linearLayout1.visibility = View.VISIBLE
//                linearLayout2.visibility = View.VISIBLE
//                linearLayout3.visibility = View.VISIBLE
//                linearMainView.visibility=View.VISIBLE
//            }
        }

        binding!!.bottomSheetOrderMinutes.textViewAccept.visibility= if(status.equals("0") || status.equals("13")) View.VISIBLE else View.GONE

        binding!!.bottomSheetOrderMinutes.textViewReject.setOnClickListener {
            var myIntent =  Intent(this@OrderPreviewActivity, OrderRejectActivity::class.java)
            myIntent.putExtra("phoneNumber", customerMobno)
            myIntent.putExtra("orderId", orderId)
            startActivity(myIntent)
        }

        binding!!.bottomSheetOrderMinutes.textViewAccept.setOnClickListener {
            //showBottomSheetDialog()
            setAcceptOrder(preparedIn)
        }

        binding!!.bottomSheetOrderMinutes.textViewPrit.setOnClickListener {
            connectPrinter(applicationContext)
            testSunmiPrint(applicationContext, orderId.toInt(), orderDate, customerNAme, customerMobno, customerAddress, customerAddress,"$", subtotal,deliveryFee, total, arrProNAme,arrProQty,arrProPrice )
        }

//        bottomSheetDialog.show()
    }

    fun setAcceptOrder(preparedTime : String){
        lottieProgressDialog!!.showDialog()
        ServiceGenerator.nentoApi.RejectORder(Constants.apiKey,orderId,"7")!!.enqueue(object :
            Callback<String?> {
            @SuppressLint("NewApi", "ResourceAsColor")
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if (response.isSuccessful) {
                    orderDao!!.changeOrderStatus(orderId,Constants.acceptStatus)
                    orderDao!!.setPreparedTime(orderId,preparedTime)
                    lottieProgressDialog!!.cancelDialog()
                    val sweetAlertDialog = SweetAlertDialog(this@OrderPreviewActivity, SweetAlertDialog.WARNING_TYPE)
                    sweetAlertDialog.setCanceledOnTouchOutside(false)
                    sweetAlertDialog.setCancelable(false)
                    sweetAlertDialog.contentText = resources.getString(R.string.lbl_accept_done) //sweetAlertDialog.contentTextSize = resources.getDimension(R.dimen._7ssp).roundToInt(
                    sweetAlertDialog.confirmText = resources.getString(R.string.lbl_OK)
                    sweetAlertDialog.confirmButtonBackgroundColor = resources.getColor(R.color.green)
                    sweetAlertDialog.showCancelButton(true)
                    sweetAlertDialog.setCancelClickListener { sDialog ->
                        sDialog.cancel()
                    }
                    sweetAlertDialog.setConfirmClickListener { sDialog ->
                        startActivity(Intent(applicationContext, Expandablectivity::class.java).putExtra("businessID",businessID))

                        finish()
                    }.show()

//                    Toast.makeText(applicationContext,"order accepted", Toast.LENGTH_SHORT).show()

                } else {
                    lottieProgressDialog!!.cancelDialog()
                    val sweetAlertDialog = SweetAlertDialog(applicationContext, SweetAlertDialog.WARNING_TYPE)
                    sweetAlertDialog.setCanceledOnTouchOutside(false)
                    sweetAlertDialog.setCancelable(false)
                    sweetAlertDialog.contentText = resources.getString(R.string.lbl_error) //sweetAlertDialog.contentTextSize = resources.getDimension(R.dimen._7ssp).roundToInt(
                    sweetAlertDialog.confirmText = resources.getString(R.string.lbl_OK)
                    sweetAlertDialog.confirmButtonBackgroundColor = resources.getColor(R.color.green)
                    sweetAlertDialog.showCancelButton(true)
                    sweetAlertDialog.setCancelClickListener { sDialog ->

                    }
                    sweetAlertDialog.setConfirmClickListener { sDialog ->
                        sDialog.cancel()
                    }.show()
                    Log.e("tag", " =  = = =error = ==  " + response.message())
                }
            }

            override fun onFailure(call: Call<String?>, t: Throwable) {
                lottieProgressDialog!!.cancelDialog()
                val sweetAlertDialog = SweetAlertDialog(applicationContext, SweetAlertDialog.WARNING_TYPE)
                sweetAlertDialog.setCanceledOnTouchOutside(false)
                sweetAlertDialog.setCancelable(false)
                sweetAlertDialog.contentText = resources.getString(R.string.lbl_error) //sweetAlertDialog.contentTextSize = resources.getDimension(R.dimen._7ssp).roundToInt(
                sweetAlertDialog.confirmText = resources.getString(R.string.lbl_OK)
                sweetAlertDialog.confirmButtonBackgroundColor = resources.getColor(R.color.green)
                sweetAlertDialog.showCancelButton(true)
                sweetAlertDialog.setCancelClickListener { sDialog ->
                    sDialog.cancel()
                }
                sweetAlertDialog.setConfirmClickListener { sDialog ->
                    sDialog.cancel()
                }.show()
                Log.e("tag", " =  = = =error = ==  " + t.message)
            }
        })
    }


    fun init() {
        intentBroadcast = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent != null) {
                    if (intent.action != null && intent.action.equals(Default.IS_FROM_DONE)) {
                        isFromDoneActivity = intent.getBooleanExtra(Default.IS_ORDER_DONE_ACTIVITY, false)
                    }
                }

            }
        }
    }
    fun setButtonColor(tv11: Boolean=false,tv15: Boolean=false,tv20: Boolean=false,tv25: Boolean=false,tv30: Boolean=false,tv40: Boolean=false,tv45: Boolean = false,tv50: Boolean=false,tv60: Boolean=false,tv70: Boolean=false,tv80: Boolean=false,tv90: Boolean=false){
        binding!!.bottomSheetOrderMinutes.tv11.setBackgroundColor(if(tv11) Color.RED else Color.WHITE)
        binding!!.bottomSheetOrderMinutes.tv15.setBackgroundColor(if(tv15) Color.RED else Color.WHITE)
        binding!!.bottomSheetOrderMinutes.tv20.setBackgroundColor(if(tv20) Color.RED else Color.WHITE)
        binding!!.bottomSheetOrderMinutes.tv25.setBackgroundColor(if(tv25) Color.RED else Color.WHITE)
        binding!!.bottomSheetOrderMinutes.tv30.setBackgroundColor(if(tv30) Color.RED else Color.WHITE)
        binding!!.bottomSheetOrderMinutes.tv40.setBackgroundColor(if(tv40) Color.RED else Color.WHITE)
        binding!!.bottomSheetOrderMinutes.tv45.setBackgroundColor(if(tv45) Color.RED else Color.WHITE)
        binding!!.bottomSheetOrderMinutes.tv50.setBackgroundColor(if(tv50) Color.RED else Color.WHITE)
        binding!!.bottomSheetOrderMinutes.tv60.setBackgroundColor(if(tv60) Color.RED else Color.WHITE)
        binding!!.bottomSheetOrderMinutes.tv70.setBackgroundColor(if(tv70) Color.RED else Color.WHITE)
        binding!!.bottomSheetOrderMinutes.tv80.setBackgroundColor(if(tv80) Color.RED else Color.WHITE)
        binding!!.bottomSheetOrderMinutes.tv90.setBackgroundColor(if(tv90) Color.RED else Color.WHITE)
//        binding!!.bottomSheetOrderMinutes.tv11.setBackgroundColor(if(tv11) Color.RED else Color.WHITE)

//        binding!!.bottomSheetOrderMinutes.tv11.setBackgroundColor(if(tv11) R.drawable.dark_red else R.drawable.dark_grey_border)
//        binding!!.bottomSheetOrderMinutes.tv15.setBackgroundColor(if(tv15) R.drawable.dark_red else R.drawable.dark_grey_border)
//        binding!!.bottomSheetOrderMinutes.tv20.setBackgroundColor(if(tv20) R.drawable.dark_red else R.drawable.dark_grey_border)
//        binding!!.bottomSheetOrderMinutes.tv25.setBackgroundColor(if(tv25) R.drawable.dark_red else R.drawable.dark_grey_border)
//        binding!!.bottomSheetOrderMinutes.tv30.setBackgroundColor(if(tv30) R.drawable.dark_red else R.drawable.dark_grey_border)
//        binding!!.bottomSheetOrderMinutes.tv40.setBackgroundColor(if(tv40) R.drawable.dark_red else R.drawable.dark_grey_border)
//        binding!!.bottomSheetOrderMinutes.tv45.setBackgroundColor(if(tv45) R.drawable.dark_red else R.drawable.dark_grey_border)
//        binding!!.bottomSheetOrderMinutes.tv50.setBackgroundColor(if(tv50) R.drawable.dark_red else R.drawable.dark_grey_border)
//        binding!!.bottomSheetOrderMinutes.tv60.setBackgroundColor(if(tv60) R.drawable.dark_red else R.drawable.dark_grey_border)
//        binding!!.bottomSheetOrderMinutes.tv70.setBackgroundColor(if(tv70) R.drawable.dark_red else R.drawable.dark_grey_border)
//        binding!!.bottomSheetOrderMinutes.tv80.setBackgroundColor(if(tv80) R.drawable.dark_red else R.drawable.dark_grey_border)
//        binding!!.bottomSheetOrderMinutes.tv90.setBackgroundColor(if(tv90) R.drawable.dark_red else R.drawable.dark_grey_border)

    }

//    fun setColor()

}