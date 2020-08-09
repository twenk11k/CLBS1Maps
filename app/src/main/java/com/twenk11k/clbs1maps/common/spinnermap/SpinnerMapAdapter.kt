package com.twenk11k.clbs1maps.common.spinnermap

class SpinnerMapAdapter(
    private var items: List<Any>,
    textColor: Int,
    backgroundSelector: Int,
    spinnerTextFormatter: SpinnerTextFormatter,
    horizontalAlignment: PopUpTextAlignment
) : SpinnerMapBaseAdapter(
    textColor,
    backgroundSelector,
    spinnerTextFormatter,
    horizontalAlignment
) {

    override fun getCount(): Int {
        return items.size - 1
    }

    override fun getItem(position: Int): Any {
        return if (position >= selectedIndex) {
            items[position + 1]
        } else {
            items[position]
        }
    }

    override fun getItemInDataset(position: Int): Any {
        return items[position]
    }

}