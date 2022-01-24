package com.tjcg.menuo.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.tjcg.menuo.R
import com.tjcg.menuo.databinding.DialogCalculatorBinding
import com.tjcg.menuo.utils.Constants
import java.math.RoundingMode
import java.text.DecimalFormat

class CalculatorDialog(var clickListener: ClickListener, var totalCartAmount: String) : DialogFragment(), View.OnClickListener {
    var binding: DialogCalculatorBinding? = null
    var customerPaid: String = "0"
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogCalculatorBinding.inflate(layoutInflater)
        binding!!.closeButton.setOnClickListener { dismiss() }
        binding!!.totalAmount.text = Constants.CUREENCY + totalCartAmount

        binding!!.next.setOnClickListener {
            clickListener.onCalculatorNext("")
        }

        binding!!.root.findViewById<TextView>(R.id.btn0).setOnClickListener(this)
        binding!!.root.findViewById<TextView>(R.id.btn1).setOnClickListener(this)
        binding!!.root.findViewById<TextView>(R.id.btn2).setOnClickListener(this)
        binding!!.root.findViewById<TextView>(R.id.btn3).setOnClickListener(this)
        binding!!.root.findViewById<TextView>(R.id.btn4).setOnClickListener(this)
        binding!!.root.findViewById<TextView>(R.id.btn5).setOnClickListener(this)
        binding!!.root.findViewById<TextView>(R.id.btn6).setOnClickListener(this)
        binding!!.root.findViewById<TextView>(R.id.btn7).setOnClickListener(this)
        binding!!.root.findViewById<TextView>(R.id.btn8).setOnClickListener(this)
        binding!!.root.findViewById<TextView>(R.id.btn9).setOnClickListener(this)
        binding!!.root.findViewById<TextView>(R.id.btn1010).setOnClickListener(this)
        binding!!.root.findViewById<TextView>(R.id.btn00).setOnClickListener(this)
        binding!!.root.findViewById<TextView>(R.id.btn100).setOnClickListener(this)
        binding!!.root.findViewById<TextView>(R.id.btn50).setOnClickListener(this)
        binding!!.root.findViewById<TextView>(R.id.btn20).setOnClickListener(this)
        binding!!.root.findViewById<TextView>(R.id.btn55).setOnClickListener(this)
        binding!!.root.findViewById<TextView>(R.id.btn1000).setOnClickListener(this)
        binding!!.root.findViewById<TextView>(R.id._clear).setOnClickListener(this)
        binding!!.root.findViewById<TextView>(R.id._point).setOnClickListener(this)

        return binding!!.root
    }

    interface ClickListener {
        fun onCalculatorNext(customerPaidAmount: String)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn0 -> calculate("0")
            R.id.btn00 -> calculate("00")
            R.id.btn1 -> calculate("1")
            R.id.btn2 -> calculate("2")
            R.id.btn3 -> calculate("3")
            R.id.btn4 -> calculate("4")
            R.id.btn5 -> calculate("5")
            R.id.btn6 -> calculate("6")
            R.id.btn7 -> calculate("7")
            R.id.btn8 -> calculate("8")
            R.id.btn9 -> calculate("9")
            R.id.btn1010 -> calculate("10")
            R.id.btn100-> calculate("100")
            R.id.btn1000 -> calculate("1000")
            R.id.btn55 -> calculate("55")
            R.id.btn20 -> calculate("20")
            R.id.btn50 -> calculate("50")
            R.id._clear -> calculate("C")
            R.id._point -> calculate(".")

        }
    }

    @SuppressLint("SetTextI18n")
    private fun calculate(amount: String) {
        if(amount.equals("C"))
        {
            customerPaid="0"
        }else if (amount.equals(".")){
            if(!customerPaid.contains("."))
            {
                customerPaid=customerPaid+".";
            }
        }
        else
        {
            if(amount.equals("0"))
            {
                concateZeros("0")
            }
            else if(amount.equals("00"))
            {
                concateZeros("00")
            }
            else{

                if(amount.toInt()>0 && amount.toInt()<=9)
                {

                    var totalAmount = customerPaid + amount
                    if (customerPaid == "0") {
                        totalAmount = if (amount.toInt() == 0) {
                            "0"
                        } else {
                            amount
                        }
                    }
                    if(customerPaid.contains(".")){
                        customerPaid = totalAmount.toDouble().toString()
                    }else{
                        customerPaid = totalAmount.toInt().toString()
                    }

                }
                else
                {
                    var totalAmount:Int =0;
                    var totalAmountDouble:Double =0.0;
                    if(amount.equals("55")){
                        if(customerPaid.contains("."))
                        {
                            totalAmountDouble = roundOffDecimal(customerPaid.toDouble()+ 5.0)!!
                        }else{
                            totalAmount = customerPaid.toInt() + 5
                        }

                    }
                    else if(amount.equals(".")){
                        if(!customerPaid.contains(".")){
                          customerPaid=  customerPaid+"."
                        }
                    }
                    else{

                        if(customerPaid.contains("."))
                        {
                            totalAmountDouble = roundOffDecimal(customerPaid.toDouble() + amount.toDouble())!!
                        }else{
                            totalAmount = customerPaid.toInt() + amount.toInt()
                        }

                    }
//                    var totalAmount = customerPaid.toInt() + amount.toInt()
//                    var totalAmountDouble = customerPaid.toDouble() + amount.toDouble()
                    if (customerPaid == "0") {

                        if(customerPaid.contains(".")) {
                            totalAmountDouble = if (amount.toDouble() == 0.0) {
                                0.0
                            } else {
                                if(amount.equals("55")) { 5.0 } else {amount.toDouble()}
                            }
                        }else {
                            totalAmount = if (amount.toInt() == 0) {
                                0
                            } else {
                                if(amount.equals("55")) { 5 } else {amount.toInt()}
                            }
                        }

                    }
                if(customerPaid.contains(".")) {
                    customerPaid = totalAmountDouble.toDouble().toString()
                }
                else {
                    customerPaid = totalAmount.toInt().toString()

                }
                }


            }



        }


        binding!!.customerPaid.text = Constants.CUREENCY + customerPaid
        binding!!.balanceDue.text = Constants.CUREENCY + (roundOffDecimal(customerPaid.toDouble() - totalCartAmount.toDouble())).toString()
    }

    fun roundOffDecimal(number: Double): Double? {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.CEILING
        return df.format(number).toDouble()
    }


    fun concateZeros(amount: String)
    {
        var totalAmount = customerPaid + amount
        if (customerPaid == "0") {
            totalAmount = if (amount == "0") {
                "0"
            } else {
                amount
            }
        }
        customerPaid = totalAmount.toInt().toString()
    }

    fun concate1to9(amount: String)
    {
        var totalAmount = customerPaid + amount
        customerPaid = totalAmount.toInt().toString()
    }

}