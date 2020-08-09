package com.twenk11k.clbs1maps.extensions

import android.view.View

/** makes a view visible. */
fun View.visible() {
    visibility = View.VISIBLE
}

/** makes a view gone. */
fun View.gone() {
    visibility = View.GONE
}

/** makes a view invisible */
fun View.invisible() {
    visibility = View.INVISIBLE
}