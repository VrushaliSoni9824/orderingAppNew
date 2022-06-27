package com.tjcg.menuo

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import androidx.appcompat.app.AppCompatActivity
import woyou.aidlservice.jiuiv5.IWoyouService
import android.graphics.Color
import android.os.Build

import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.tjcg.menuo.adapter.OrderPreviewProductAdapter
import com.tjcg.menuo.data.local.AppDatabase
import com.tjcg.menuo.data.local.OrderDao
import com.tjcg.menuo.data.remote.ServiceGenerator
import com.tjcg.menuo.data.response.EntitiesModel.*
import com.tjcg.menuo.data.response.newOrder.Result
import com.tjcg.menuo.databinding.OrderPreviewLayoutBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

import android.media.MediaPlayer
import android.view.Window
import android.widget.TextView
import com.tjcg.menuo.utils.*
import org.json.JSONArray
import java.lang.StringBuilder


class OrderPreviewActivity : AppCompatActivity() {
    lateinit var custom_dialog : Dialog
    var mIntentFilter: IntentFilter? = null
    var prefManager: PrefManager? = null
    private var woyouService: IWoyouService? = null
    var orderId: String = "0";
    var orderDatte: String = ""
    var deliveryFee: String = ""
    var binding: OrderPreviewLayoutBinding? = null
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
    lateinit var mplayer: MediaPlayer
    lateinit var note : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = OrderPreviewLayoutBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        custom_dialog = Dialog(this@OrderPreviewActivity)
        mIntentFilter = IntentFilter()
        mIntentFilter!!.addAction("new_order_pre")
        registerReceiver(reMyreceive, mIntentFilter)
        mplayer = MediaPlayer.create(applicationContext, R.raw.alarmtone);
        lottieProgressDialog = LottieProgressDialog(this)
        orderDao = AppDatabase.getDatabase(this)!!.orderDao()
        prefManager = PrefManager(applicationContext)
        businessID=prefManager!!.getString("businessID")
        connectPrinter(applicationContext)
        init()
        var i: Intent = intent
        orderId = i.getStringExtra("orderId")!!

        arrResult= orderDao!!.getOrderDataByID(orderId);
        arrBusiness= orderDao!!.getBusinessById(orderId);
        arrCity= orderDao!!.getCityById(orderId);
        arrCustomer= orderDao!!.getCustomerById(orderId);
        arrHistory= orderDao!!.getHistoryById(orderId);
        arrProduct= orderDao!!.getProductById(orderId);
        arrSummery= orderDao!!.getSummaryById(orderId);
        var orderId : String = arrResult.id.toString()
        orderDate = arrResult.delivery_datetime.toString()
        customerNAme = arrCustomer.name
        customerMobno = arrCustomer.phone
        customerAddress =arrCustomer.address
        note = arrCustomer.address_notes
        subtotal= orderDao!!.getSubTotal(orderId.toString()).toString();
        var deliveryFee : String =arrSummery.delivery_price.toString()
        total= arrSummery.total.toString()
        val pendingStatus = java.util.ArrayList<String>()
        pendingStatus.add("1")
        pendingStatus.add("2")
        pendingStatus.add("5")
        pendingStatus.add("6")
        pendingStatus.add("11")
        pendingStatus.add("12")
        pendingStatus.add("16")
        pendingStatus.add("17")
        pendingStatus.add("10")
        preparedIn = orderDao!!.getPreparedIn(orderId.toString()).toString()
        status = arrResult.status.toString()
        if(preparedIn.equals("0") || preparedIn.equals("null")){
            binding!!.textViewPreparedIn.visibility=View.GONE
        }else{
            binding!!.textViewPreparedIn.visibility=View.VISIBLE
            binding!!.textViewPreparedIn.text = preparedIn+" min"
        }
        binding!!.textOrderID.text="#"+orderId.toString()
        binding!!.textviewOrderDate.text=orderDate.dropLast(3).toString()
        binding!!.textviewCustomerName.text=customerNAme
        binding!!.customerMobno.text=if(customerMobno.equals("null") || customerMobno.equals("")) "" else customerMobno
        binding!!.textviewCustomerAddress.text=customerAddress
        if(note.equals("null") || note.equals("")){
            binding!!.tvNotesHeading.visibility=View.GONE
        }else{
            binding!!.tvNotesHeading.visibility=View.VISIBLE
        }
        binding!!.textviewNotes.text=if(note.equals("null") || note.equals("")) { "" } else { note }
        binding!!.textViewSubtotal.text=subtotal
        binding!!.textviewdeliveryfee.text=deliveryFee
        binding!!.textviewTotal.text=total
        val deliveryType = orderDao!!.getDeliveryType(orderId)
        var DeliveryText = ""
        when (deliveryType) {
            "1" -> DeliveryText = getString(R.string.Delivery)
            "2" -> DeliveryText = getString(R.string.Pick_Up)
            "3" -> DeliveryText = getString(R.string.Eat_In)
            "4" -> DeliveryText = getString(R.string.Curbside)
            "5" -> DeliveryText = getString(R.string.Driver_thru)
        }
        binding!!.tvStatus.setText(DeliveryText)
        productAdapter = OrderPreviewProductAdapter(arrProduct,applicationContext)
        binding!!.rvProduct.layoutManager = LinearLayoutManager(applicationContext)
        binding!!.rvProduct.adapter = productAdapter
        binding!!.rvProduct.isNestedScrollingEnabled=false
        for(i in 0..arrProduct.size-1){
            arrProNAme.add(arrProduct.get(i).name)
            arrProQty.add(arrProduct.get(i).quantity.toString())
            arrProPrice.add(arrProduct.get(i).price.toString())
        }
        if(status.equals("13") || status.equals("0")){
            binding!!.bottomSheetOrderMinutes.textViewPrit.visibility=View.GONE
            if(status.equals("13")) {
                binding!!.bottomSheetOrderMinutes.linearMainView.visibility=View.GONE
                binding!!.bottomSheetOrderMinutes.linearLayout4.visibility=View.GONE
            }
            else{
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
        binding!!.bottomSheetOrderMinutes.tv40.setOnClickListener { preparedIn = "40";setButtonColor(tv40 = true)}
        binding!!.bottomSheetOrderMinutes.tv45.setOnClickListener { preparedIn = "45";setButtonColor(tv45 = true)}
        binding!!.bottomSheetOrderMinutes.tv50.setOnClickListener { preparedIn = "50";setButtonColor(tv50 = true)}
        binding!!.bottomSheetOrderMinutes.tv60.setOnClickListener { preparedIn = "60";setButtonColor(tv60 = true)}
        binding!!.bottomSheetOrderMinutes.tv70.setOnClickListener { preparedIn = "70";setButtonColor(tv70 = true)}
        binding!!.bottomSheetOrderMinutes.tv80.setOnClickListener { preparedIn = "80";setButtonColor(tv80 = true)}
        binding!!.bottomSheetOrderMinutes.tv90.setOnClickListener { preparedIn = "90";setButtonColor(tv90 = true)}
        binding!!.imageViewBack.setOnClickListener {
            if (!isFromDoneActivity){
                startActivity(Intent(applicationContext, Expandablectivity::class.java).putExtra("businessID",businessID).putExtra(SharedPreferencesKeys.isDBLoadRequired,false))
                finish()
            }else{
                val i = Intent(this, OrderCompleteActivity::class.java)
                i.putExtra("businessID", businessID)
                startActivity(i)
                finish()
            }
        }
        binding!!.bottomSheetOrderMinutes.textViewPrit.setOnClickListener {
            testSunmiPrintAcceptOrder(applicationContext, orderId.toInt(), orderDate, customerNAme, customerMobno, customerAddress, customerAddress,"", subtotal,deliveryFee, total, arrProNAme,arrProQty,arrProPrice ,arrProduct,note)
        }

        binding!!.bottomSheetOrderMinutes.imageArrowUp.setOnClickListener {
            if(!status.equals("13")) binding!!.bottomSheetOrderMinutes.linearMainView.visibility=if(binding!!.bottomSheetOrderMinutes.linearMainView.visibility.equals(View.VISIBLE)) View.GONE else View.VISIBLE
        }

        binding!!.bottomSheetOrderMinutes.textViewAccept.visibility= if(status.equals("0") || status.equals("13")) View.VISIBLE else View.GONE

        binding!!.bottomSheetOrderMinutes.textViewReject.setOnClickListener {
            var myIntent =  Intent(this@OrderPreviewActivity, OrderRejectActivity::class.java)
            myIntent.putExtra("phoneNumber", customerMobno)
            myIntent.putExtra("orderId", orderId)
            startActivity(myIntent)
        }
        binding!!.bottomSheetOrderMinutes.textViewAccept.setOnClickListener {
            if(preparedIn.equals("0") || preparedIn.equals("null") || preparedIn.equals(" ")){
                preparedIn="30"
            }
            if(status.equals("13")) preparedIn= "0"
            setAcceptOrder(preparedIn)
        }
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

    fun testSunmiPrintAcceptOrder(context: Context, orderNumber: Int, date: String, customerName: String, phoneNumber: String, address1: String, address2: String, currency: String, subTotal: String, deliveryFee: String, total: String, proName : ArrayList<String>, proQty : ArrayList<String>, proPrice : ArrayList<String> , productEntity: List<ProductEntity>, address_note : String) {//, orderDetail: OrderDetail?
        try {
            if (woyouService != null) {
                setHeaderData(context, orderNumber, date, customerName, phoneNumber, address1, address2, address_note)
                woyouService!!.printText("\n", null)
                woyouService!!.setFontSize(24f, null)
                woyouService!!.sendRAWData(normalFont(), null)
                woyouService!!.printText("-----------------------------\n", null)
                for (product in arrProduct){
                    val addonsArray = JSONArray(product.options)
                    val arrIngridiants = JSONArray(product.ingredients)
                    val fmt1 = Formatter()
                    var splittedString : List<String?>? = splitIntoLine(product.name,18)
                    var linecount =0
                    for(item in splittedString!!){
                        if(linecount==0){
                            fmt1.format("%-2s X %-18s %5s\n", product.quantity.toString(),item.toString(), product.price)
                        }else{
                            fmt1.format("%-2s   %-18s %5s\n", "",item.toString(),"")
                        }
                        linecount++
                    }
                    woyouService!!.printText(fmt1.toString()+"\n", null)

                    for (i in 0..addonsArray.length() - 1) {
                        val addon = addonsArray.getJSONObject(i)
                        val suboptionsArray = addon.getJSONArray("suboptions")
                        for(j in 0..suboptionsArray.length()-1){
                            val suboption = suboptionsArray.getJSONObject(j)
                            var name = suboption.getString("name");
                            var qty =  suboption.getInt("quantity").toString()+" x "
                            var price = suboption.getString("price");
                            val fmtSubPoints = Formatter()
                            if(price.toInt()>0){
                                var splittedString : List<String?>? = splitIntoLine(name,18)
                                var linecount =0
                                for(item in splittedString!!){
                                    if(linecount==0){
                                        fmtSubPoints.format("%-2s X %-18s %5s\n", qty.toString(),item.toString(), price)
                                    }else{
                                        fmtSubPoints.format("%-2s   %-18s %5s\n", "",item.toString(),"")
                                    }
                                    linecount++
                                }
                                woyouService!!.printText( fmtSubPoints.toString(), null)
                            }
                            else{
                                var splittedString : List<String?>? = splitIntoLine(name,18)
                                var linecount =0
                                for(item in splittedString!!){
                                    if(linecount==0){
                                        fmtSubPoints.format("%-2s X %-18s %5s\n", qty.toString(),item.toString(), "")
                                    }else{
                                        fmtSubPoints.format("%-2s   %-18s %5s\n", "",item.toString(),"")
                                    }
                                    linecount++
                                }
                                woyouService!!.printText( fmtSubPoints.toString(), null)

                            }
                        }

                    }

                    for(i in 0..arrIngridiants.length()-1){
                        var ingreItem = arrIngridiants.getJSONObject(i)
                        var IngridiantsName = ingreItem.getString("name")
                        val fmtIngridiants = Formatter()
                        var splittedString : List<String?>? = splitIntoLine(IngridiantsName,18)
                        var linecount =0
                        for(item in splittedString!!){
                            if(linecount==0){
                                fmtIngridiants.format("%-8s %-18s\n","Ta bort:",item.toString())
                            }else{
                                fmtIngridiants.format("%-8s %-18s\n","",item.toString())
                            }
                            linecount++
                        }
                        woyouService!!.printText(fmtIngridiants.toString(), null)

                    }

                    woyouService!!.printText("-----------------------------\n", null)
                }
                woyouService!!.sendRAWData(normalFont(), null)
                woyouService!!.printText(rightPadZeros(context.getString(R.string.lbl_subTotal), 17) + "" + leftPadZeros("$currency $subTotal", 11) + "\n", null)
                woyouService!!.printText(rightPadZeros(context.getString(R.string.lbl_Delivery_free), 17) + "" + leftPadZeros("$currency "+if(deliveryFee.equals("0") || deliveryFee.equals("null") || deliveryFee.equals("") || deliveryFee.equals(" ")) "0" else deliveryFee, 11) + "\n", null)
                woyouService!!.printText("--------------------------------\n", null)
                woyouService!!.sendRAWData(boldFont(), null)
                woyouService!!.setFontSize(30f, null)
                woyouService!!.printText(rightPadZeros(context.getString(R.string.lbl_Total), 14) + "" + leftPadZeros("$total", 8) + "\n", null)
                woyouService!!.printText("\n", null)
                woyouService!!.printText("\n", null)
                woyouService!!.printText("\n", null)
                woyouService!!.printText("\n", null)
                woyouService!!.printText("\n", null)
                woyouService!!.printText("\n", null)
                woyouService!!.printText("\n", null)
                woyouService!!.cutPaper(null)
                startActivity(Intent(applicationContext, Expandablectivity::class.java).putExtra("businessID",businessID).putExtra(SharedPreferencesKeys.isDBLoadRequired,false))
                finish()
            }
        } catch (ex: RemoteException) {

        }
    }

    private fun setHeaderData(context: Context, orderNumber: Int, date: String, customerName: String, phoneNumber: String, address1: String, address2: String, address_note: String) {
        val onlytime: String? = date.substringAfterLast(" ")
        val onlydate: String? = date.substringBefore(" ")

        if (woyouService != null) {
            var deliveryType: String = orderDao!!.getDeliveryType(orderNumber.toString())
            var DeliveryText: String = ""
            when (deliveryType) {
                "1" -> {
                    DeliveryText = context.getString(R.string.Delivery)
                }
                "2" -> {
                    DeliveryText = context.getString(R.string.Pick_Up)
                }
                "3" -> {
                    DeliveryText = context.getString(R.string.Eat_In)
                }
                "4" -> {
                    DeliveryText = context.getString(R.string.Curbside)
                }
                "5" -> {
                    DeliveryText = context.getString(R.string.Driver_thru)
                }
            }


            woyouService!!.setFontSize(40f, null)
            woyouService!!.sendRAWData(boldFont(), null)
            woyouService!!.sendRAWData(alignCenter(), null)
            woyouService!!.printText(truncateString(context.getString(R.string.lbl_Name), 30) + "\n", null)
            woyouService!!.setFontSize(26f, null)
            woyouService!!.sendRAWData(normalFont(), null)
            woyouService!!.printText("# " + "$orderNumber" + "\n", null)
            //woyouService!!.printBitmap(BitmapFactory.decodeResource(resources, R.drawable.ic_logo_name),)//callBack(Image)
            woyouService!!.sendRAWData(normalFont(), null)
            woyouService!!.printText("-----------------------------\n", null)
            woyouService!!.sendRAWData(boldFont(), null)
            woyouService!!.printText(truncateString(DeliveryText.toUpperCase(), 30) + "\n", null)
            woyouService!!.setFontSize(24f, null)
            woyouService!!.sendRAWData(normalFont(), null)
            woyouService!!.printText( preparedIn.toString()+" min" + "\n", null)
            woyouService!!.sendRAWData(normalFont(), null)
            woyouService!!.printText( onlydate!!.toUpperCase() + "\n", null) // createdDate
            woyouService!!.sendRAWData(normalFont(), null)
            woyouService!!.printText( onlytime!!.dropLast(3)!!.toUpperCase() + "\n\n", null) // createdDate
            woyouService!!.setFontSize(26f, null)
            woyouService!!.sendRAWData(boldFont(), null)
            woyouService!!.printText(truncateString(context.getString(R.string.lbl_Customer).toUpperCase(), 30) + "\n", null)
            woyouService!!.sendRAWData(normalFont(), null)
            woyouService!!.setFontSize(24f, null)
            woyouService!!.printText(truncateString("$customerName", 30) + "\n", null)
            if(phoneNumber!=null) {
                woyouService!!.printText(truncateString("$phoneNumber", 30) + "\n\n", null)
            }
            woyouService!!.sendRAWData(alignCenter(), null)
            var a1 = address1.substringBefore(",")
            var a2 = address1.substringAfter(",")
            woyouService!!.printText(truncateString(a1, 32) + "\n", null)
            woyouService!!.sendRAWData(alignCenter(), null)
            woyouService!!.printText(truncateString(a2, 32) + "\n\n", null)
//            woyouService!!.printText(truncateString("$address2", 30) + "\n\n", null)
            woyouService!!.setFontSize(26f, null)
            woyouService!!.sendRAWData(boldFont(), null)
            if(address_note.equals("null") || address_note.equals("")) {

            } else {
                woyouService!!.printText(truncateString(context.getString(R.string.lbl_notes).toUpperCase(), 30) + "\n\n", null)
                woyouService!!.setFontSize(24f, null)
                woyouService!!.sendRAWData(normalFont(), null)
                woyouService!!.printText(truncateString(address_note, 30) + "\n", null)
            }



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

    fun connectPrinter(context: Context) {
        val intent = Intent()
        intent.setPackage(Constants.SERVICE_PACKAGE)
        intent.action = Constants.SERVICE_ACTION
        context.applicationContext.startService(intent)
        context.applicationContext.bindService(intent, connService, Context.BIND_AUTO_CREATE)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(applicationContext, Expandablectivity::class.java).putExtra("businessID",businessID).putExtra(SharedPreferencesKeys.isDBLoadRequired,false))
        finish()

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

        /*binding!!.bottomSheetOrderMinutes.textViewAccept.setOnClickListener {
            //showBottomSheetDialog()
            setAcceptOrder(preparedIn)
        }

         */

        /*binding!!.bottomSheetOrderMinutes.textViewPrit.setOnClickListener {
            Toast.makeText(applicationContext,"btn1",Toast.LENGTH_LONG).show();
            connectPrinter(applicationContext)
            var bt : Bitmap = getBitmapFromView(binding!!.lvReciept)!!
            onClick(bt)
//            connectPrinter(applicationContext)
//            testSunmiPrint(applicationContext, orderId.toInt(), orderDate, customerNAme, customerMobno, customerAddress, customerAddress,"$", subtotal,deliveryFee, total, arrProNAme,arrProQty,arrProPrice )
        }

         */

//        bottomSheetDialog.show()
    }

    fun setAcceptOrder(preparedTime : String){
        lottieProgressDialog!!.showDialog()
        ServiceGenerator.nentoApi.AcceptORder(Constants.apiKey,orderId,"7",preparedTime)!!.enqueue(object :
            Callback<String?> {
            @SuppressLint("NewApi", "ResourceAsColor")
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if (response.isSuccessful) {
                    orderDao!!.changeOrderStatus(orderId,Constants.acceptStatus)
                    orderDao!!.setPreparedTime(orderId,preparedTime)
                    lottieProgressDialog!!.cancelDialog()
                    prefManager!!.setString(SharedPreferencesKeys.lastAcceptedOrder,orderId);
                    testSunmiPrintAcceptOrder(applicationContext, orderId.toInt(), orderDate, customerNAme, customerMobno, customerAddress, customerAddress,"", subtotal,deliveryFee, total, arrProNAme,arrProQty,arrProPrice,arrProduct,note )
                } else {
                    lottieProgressDialog!!.cancelDialog()
                    val sweetAlertDialog = SweetAlertDialog(this@OrderPreviewActivity, SweetAlertDialog.WARNING_TYPE)
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
        binding!!.bottomSheetOrderMinutes.tv11.setBackgroundResource(if(tv11) R.color.appOrange else R.color.white)
        binding!!.bottomSheetOrderMinutes.tv11.setTextColor(if(tv11) Color.WHITE else Color.BLACK)
        binding!!.bottomSheetOrderMinutes.tv15.setBackgroundResource(if(tv15) R.color.appOrange  else R.color.white)
        binding!!.bottomSheetOrderMinutes.tv15.setTextColor(if(tv15) Color.WHITE else Color.BLACK)
        binding!!.bottomSheetOrderMinutes.tv20.setBackgroundResource(if(tv20) R.color.appOrange  else R.color.white)
        binding!!.bottomSheetOrderMinutes.tv20.setTextColor(if(tv20) Color.WHITE else Color.BLACK)
        binding!!.bottomSheetOrderMinutes.tv25.setBackgroundResource(if(tv25) R.color.appOrange  else R.color.white)
        binding!!.bottomSheetOrderMinutes.tv25.setTextColor(if(tv25) Color.WHITE else Color.BLACK)
        binding!!.bottomSheetOrderMinutes.tv30.setBackgroundResource(if(tv30) R.color.appOrange  else R.color.white)
        binding!!.bottomSheetOrderMinutes.tv30.setTextColor(if(tv30) Color.WHITE else Color.BLACK)
        binding!!.bottomSheetOrderMinutes.tv40.setBackgroundResource(if(tv40) R.color.appOrange  else R.color.white)
        binding!!.bottomSheetOrderMinutes.tv40.setTextColor(if(tv40) Color.WHITE else Color.BLACK)
        binding!!.bottomSheetOrderMinutes.tv45.setBackgroundResource(if(tv45) R.color.appOrange  else R.color.white)
        binding!!.bottomSheetOrderMinutes.tv45.setTextColor(if(tv45) Color.WHITE else Color.BLACK)
        binding!!.bottomSheetOrderMinutes.tv50.setBackgroundResource(if(tv50) R.color.appOrange  else R.color.white)
        binding!!.bottomSheetOrderMinutes.tv50.setTextColor(if(tv50) Color.WHITE else Color.BLACK)
        binding!!.bottomSheetOrderMinutes.tv60.setBackgroundResource(if(tv60) R.color.appOrange  else R.color.white)
        binding!!.bottomSheetOrderMinutes.tv60.setTextColor(if(tv60) Color.WHITE else Color.BLACK)
        binding!!.bottomSheetOrderMinutes.tv70.setBackgroundResource(if(tv70) R.color.appOrange  else R.color.white)
        binding!!.bottomSheetOrderMinutes.tv70.setTextColor(if(tv70) Color.WHITE else Color.BLACK)
        binding!!.bottomSheetOrderMinutes.tv80.setBackgroundResource(if(tv80) R.color.appOrange  else R.color.white)
        binding!!.bottomSheetOrderMinutes.tv80.setTextColor(if(tv80) Color.WHITE else Color.BLACK)
        binding!!.bottomSheetOrderMinutes.tv90.setBackgroundResource(if(tv90) R.color.appOrange  else R.color.white)
        binding!!.bottomSheetOrderMinutes.tv90.setTextColor(if(tv90) Color.WHITE else Color.BLACK)
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

    private fun showDialog( orderId : String, date: String, amt : String, deliveryType : String) {
        runOnUiThread {
            custom_dialog = Dialog(this@OrderPreviewActivity)

            if(!mplayer.isPlaying)
            {
                mplayer.isLooping=true
                mplayer.start()
            }

            custom_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            custom_dialog.setCancelable(false)
            custom_dialog.setContentView(R.layout.dialog_new_order)
            val textView9 = custom_dialog.findViewById(R.id.textView9) as TextView
            val textView6 = custom_dialog.findViewById(R.id.textView6) as TextView
            val textView8 = custom_dialog.findViewById(R.id.textView8) as TextView
            val tvDate = custom_dialog.findViewById(R.id.tvDate) as TextView
            val tvTime = custom_dialog.findViewById(R.id.tvTime) as TextView


            var deliveryType1 : String = ""
            when (deliveryType) {
                "1" -> deliveryType1 = applicationContext.getString(R.string.Delivery)
                "2" -> deliveryType1 = applicationContext.getString(R.string.Pick_Up)
                "3" -> deliveryType1 = applicationContext.getString(R.string.Eat_In)
                "4" -> deliveryType1 = applicationContext.getString(R.string.Curbside)
                "5" -> deliveryType1 = applicationContext.getString(R.string.Driver_thru)
            }
            Log.e("logdel",deliveryType1.toString())
            Log.e("logdel",deliveryType.toString())
            textView9.setText("#"+orderId.toString())
            textView6.setText(amt.toString()+" Kr")
            textView8.setText(deliveryType1.toString())
            val dateWithMinute = date.dropLast(3)
            val time: String? = dateWithMinute.substringAfterLast(" ")
            val date: String? = dateWithMinute.substringBefore(" ")
            tvDate.setText("    "+date.toString()+"    ")
            tvTime.setText("   "+time.toString()+"   ")

            val btnclose = custom_dialog.findViewById(R.id.btnclose) as ImageView
            val confirmOrderBtn = custom_dialog.findViewById(R.id.confirm_order_btn) as TextView
            btnclose.setOnClickListener {
                orderDao!!.deleteFromQueue(orderId)
                if(mplayer.isPlaying)
                {
                    mplayer.stop()
                }
                custom_dialog.dismiss()
            }
            confirmOrderBtn.setOnClickListener {
                orderDao!!.deleteFromQueue(orderId)
                if(mplayer.isPlaying)
                {
                    mplayer.stop()
                }
                custom_dialog.dismiss()
                val i = Intent(applicationContext, OrderPreviewActivity::class.java)
                i.putExtra("orderId", orderId)
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(i)
//            finish()

            }

            custom_dialog.show()
        }
    }

    private val reMyreceive: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                if (intent.action == "new_order_pre") {
                    val orderId = intent.getStringExtra("id");
                    var total=orderDao!!.getOrderTotal(orderId!!)
                    var deliveryDateTime=intent.getStringExtra("deliveryDateTime");
                    var deliverytype=intent.getStringExtra("deliverytype");
                    Log.e("msggg", "inside broadcast rec")
//                    Toast.makeText(applicationContext,"newOrder from broad",Toast.LENGTH_LONG).show()
                    showDialog(orderId,deliveryDateTime!!,
                        total.toString()!!,deliverytype!!)
                }
            } catch (e: Exception) {
                Log.e("Error BR", e.message.toString())
            }
        }
    }

    fun splitIntoLine(input: String, maxCharInLine: Int): List<String?>? {
        val tok = StringTokenizer(input, " ")
        val output = StringBuilder(input.length)
        var lineLen = 0
        var lineNumber =0
        while (tok.hasMoreTokens()) {

            var word = tok.nextToken()
            while (word.length > maxCharInLine) {
                output.append(
                    """
                    ${word.substring(0, maxCharInLine - lineLen)}
                    
                    """.trimIndent()
                )
                word = word.substring(maxCharInLine - lineLen)
                lineLen = 0
            }
            if (lineLen + word.length > maxCharInLine) {
                output.append("\n")

                lineLen = 0
                lineNumber=lineNumber+1

            }
            output.append("$word ")

            lineLen += word.length + 1

        }
        return output.toString().split("\n")
    }
}