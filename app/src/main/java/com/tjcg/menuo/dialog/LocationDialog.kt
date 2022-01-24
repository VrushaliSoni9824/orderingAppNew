package com.tjcg.menuo.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.tjcg.menuo.R
import com.tjcg.menuo.data.response.Login.OutletsRS
import com.tjcg.menuo.databinding.DialogLocationBinding
import android.view.Gravity
import android.widget.*
import java.util.*
import kotlin.collections.ArrayList


class LocationDialog(var clickListener: ClickListener, var outlets: List<OutletsRS>) : DialogFragment() {
    var binding: DialogLocationBinding? = null
    var adapter: LocationAdapter? = null
    var outletIds : ArrayList<String>? = null
    var outletNames : ArrayList<String>? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val window = dialog!!.window

        // set "origin" to top left corner, so to speak
        window!!.setGravity(Gravity.BOTTOM or Gravity.RIGHT)
        binding = DialogLocationBinding.inflate(layoutInflater)
        val outletName= outlets
//        val outletId = outletIds
        outletIds= ArrayList()
        outletNames=ArrayList()
        for(outletItem in outlets)
        {
            outletIds!!.add(outletItem.outlet_id.toString())
            outletNames!!.add(outletItem.name.toString())
        }


        val arrayAdapter = ArrayAdapter<String>(requireContext(),
            R.layout.layout_spinner,
            R.id.textView1, outletNames!!
        )

//        adapter = LocationAdapter(requireContext(), outlets)
        binding!!.rvLocation.visibility=View.VISIBLE
        binding!!.rvLocation.setSelection(1)
        binding!!.rvLocation.adapter=arrayAdapter

        binding!!.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if(outletNames!!.contains(query)){
                    arrayAdapter!!.filter.filter(query!!)
                }else{
                    Toast.makeText(context,"No Match Found", Toast.LENGTH_SHORT).show()
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                arrayAdapter!!.filter.filter(newText!!)
                return false
            }

        })



        binding!!.rvLocation.setOnItemClickListener { parent, view, position, id ->
            dismiss()
            clickListener.onLocationChanged(outlets.get(position).outlet_id,
                outlets.get(position).name!!, outlets.get(position).unique_id!!
            )

            for (j in 0 until parent.getChildCount()) parent.getChildAt(j)
                .setBackgroundColor(
                    Color.TRANSPARENT
                )

            // change the background color of the selected element

            // change the background color of the selected element
            view.setBackgroundColor(Color.LTGRAY)
        }
        return binding!!.root
    }

    interface ClickListener {
        fun onLocationChanged(outletId:String,outletname : String,uniqueId : String)
    }

    class LocationAdapter(private val context: Context, private val arrayList: List<OutletsRS>) : BaseAdapter() {
        private lateinit var location_name: TextView
        var outlets : ArrayList<OutletsRS>? = null
        override fun getCount(): Int {
            return arrayList.size
        }
        override fun getItem(position: Int): Any {
            return position
        }
        override fun getItemId(position: Int): Long {
            return position.toLong()
        }
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
            var convertView = convertView
            convertView = LayoutInflater.from(context).inflate(R.layout.item_location, parent, false)
            location_name = convertView.findViewById(R.id.location_name)

            location_name.text = arrayList[position].name
            return convertView
        }

//        fun filter(charText: String) {
//            var charText = charText
//            charText = charText.toLowerCase(Locale.getDefault())
//
//            if (charText.length == 0) {
////                animalNamesList.addAll(arraylist)
//            } else {
//                for (wp in outlets!!) {
//                    if (wp.name!!.toLowerCase(Locale.getDefault()).contains(charText)) {
//                        outlets!!.add(wp)
//                    }
//                }
//            }
//            notifyDataSetChanged()
//        }

    }

}