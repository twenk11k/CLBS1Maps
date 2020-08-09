package com.twenk11k.clbs1maps.common.spinnermap

import android.text.Spannable
import android.text.SpannableString

class SimpleSpinnerTextFormatter: SpinnerTextFormatter {

    override fun format(item: Any): Spannable {
        return SpannableString(item.toString())
    }

}