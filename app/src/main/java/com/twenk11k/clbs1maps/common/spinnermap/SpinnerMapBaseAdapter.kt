package com.twenk11k.clbs1maps.common.spinnermap

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.twenk11k.clbs1maps.R

abstract class SpinnerMapBaseAdapter(
    private var textColor: Int,
    private var backgroundSelector: Int,
    spinnerTextFormatter: SpinnerTextFormatter,
    horizontalAlignment: PopUpTextAlignment
) : BaseAdapter() {

    private var horizontalAlignment: PopUpTextAlignment? = horizontalAlignment
    private var spinnerTextFormatter: SpinnerTextFormatter? = spinnerTextFormatter

    var selectedIndex = 0

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val context = parent.context
        val textView: TextView
        var convertView = convertView
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.spinner_map_list_item, null)
            textView = convertView.findViewById(R.id.text_view_spinner)
            textView.background = ContextCompat.getDrawable(context, backgroundSelector)
            convertView.tag = ViewHolder(textView)
        } else {
            textView = (convertView.tag as ViewHolder).textView
        }

        textView.text = spinnerTextFormatter?.format(getItem(position))
        textView.setTextColor(textColor)

        setTextHorizontalAlignment(textView)

        return convertView!!
    }

    open fun setTextHorizontalAlignment(textView: TextView) {
        when (horizontalAlignment) {
            PopUpTextAlignment.START -> textView.gravity = Gravity.START
            PopUpTextAlignment.END -> textView.gravity = Gravity.END
            PopUpTextAlignment.CENTER -> textView.gravity = Gravity.CENTER_HORIZONTAL
        }
    }

    abstract fun getItemInDataset(position: Int): Any

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    abstract override fun getCount(): Int

    inner class ViewHolder(var textView: TextView)

}