package com.twenk11k.clbs1maps.common.spinnermap

import android.text.Spannable

interface SpinnerTextFormatter {

    fun format(item: Any): Spannable

}