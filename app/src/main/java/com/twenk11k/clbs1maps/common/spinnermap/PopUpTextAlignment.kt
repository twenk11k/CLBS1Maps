package com.twenk11k.clbs1maps.common.spinnermap

enum class PopUpTextAlignment(id: Int) {

    START(0), END(1), CENTER(2);

    private val id: Int = id

    companion object {
        fun fromId(id: Int): PopUpTextAlignment {
        for(value in values()) {
            if(value.id == id)
                return value
        }
        return CENTER
        }
    }

}