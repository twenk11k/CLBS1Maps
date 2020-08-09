package com.twenk11k.clbs1maps.common.spinnermap

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ListPopupWindow
import android.widget.PopupWindow
import androidx.annotation.DrawableRes
import androidx.annotation.NonNull
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import com.twenk11k.clbs1maps.R

class SpinnerMap: AppCompatTextView {

    private val MAX_LEVEL = 10000
    private val INSTANCE_STATE = "instance_state"
    private val SELECTED_INDEX = "selected_index"
    private val IS_POPUP_SHOWING = "is_popup_showing"
    private val IS_ARROW_HIDDEN = "is_arrow_hidden"
    private val ARROW_DRAWABLE_RES_ID = "arrow_drawable_res_id"

    private var selectedIndex = 0
    private var arrowDrawable: Drawable? = null
    private var popupWindow: ListPopupWindow? = null
    private var adapter: SpinnerMapBaseAdapter? = null

    private val onItemClickListener: AdapterView.OnItemClickListener? = null
    private val onItemSelectedListener: OnItemSelectedListener? = null
    private var onSpinnerItemSelectedListener: OnSpinnerItemSelectedListener? = null

    private var isArrowHidden = false
    private var textColor1 = 0
    private var backgroundSelector = 0
    private var arrowDrawableTint = 0
    private var displayHeight = 0
    private var dropDownListPaddingBottom = 0

    @DrawableRes
    private var arrowDrawableResId = 0
    private val spinnerTextFormatter: SpinnerTextFormatter = SimpleSpinnerTextFormatter()
    private var selectedTextFormatter: SpinnerTextFormatter = SimpleSpinnerTextFormatter()
    private var horizontalAlignment: PopUpTextAlignment? = null

    private var arrowAnimator: ObjectAnimator? = null

    var isNight = false

    constructor(context: Context): super(context) {
        init(context,null)
    }

    constructor(context: Context, attrs: AttributeSet): super(context,attrs) {
        init(context,attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context,attrs,defStyleAttr) {
        init(context,attrs)
    }

    override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState())
        bundle.putInt(SELECTED_INDEX, selectedIndex)
        bundle.putBoolean(IS_ARROW_HIDDEN, isArrowHidden)
        bundle.putInt(ARROW_DRAWABLE_RES_ID, arrowDrawableResId)
        if (popupWindow != null) {
            bundle.putBoolean(IS_POPUP_SHOWING, popupWindow!!.isShowing)
        }
        return bundle
    }

    override fun onRestoreInstanceState(savedState: Parcelable?) {
        var savedState = savedState
        if (savedState is Bundle) {
            val bundle = savedState as Bundle
            selectedIndex = bundle.getInt(SELECTED_INDEX)
            if (adapter != null) {
                setTextInternal(selectedTextFormatter.format(adapter!!.getItemInDataset(selectedIndex)).toString())
                adapter?.selectedIndex = selectedIndex
            }
            if (bundle.getBoolean(IS_POPUP_SHOWING)) {
                if (popupWindow != null) {
                    // Post the show request into the looper to avoid bad token exception
                    post { this.showDropDown() }
                }
            }
            isArrowHidden = bundle.getBoolean(IS_ARROW_HIDDEN, false)
            arrowDrawableResId = bundle.getInt(ARROW_DRAWABLE_RES_ID)
            savedState = bundle.getParcelable(INSTANCE_STATE)
        }
        super.onRestoreInstanceState(savedState)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        val resources = resources
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SpinnerMap)
        val defaultPadding = resources.getDimensionPixelSize(R.dimen.one_and_a_half_grid_unit)

        gravity = Gravity.CENTER_VERTICAL or Gravity.START
        setPadding(resources.getDimensionPixelSize(R.dimen.spinner_scan_list_item_padding), defaultPadding, defaultPadding, defaultPadding)
        isClickable = true
        backgroundSelector = typedArray.getResourceId(R.styleable.SpinnerMap_backgroundSelector, R.drawable.rect_selector_spinner_scan)
        setTextSize(TypedValue.COMPLEX_UNIT_PX,resources.getDimension(R.dimen.text_spinner_scan_size))
        setBackgroundResource(backgroundSelector)
        textColor1 = typedArray.getColor(R.styleable.SpinnerMap_textTint, getDefaultTextColor(context))
        setTextColor(textColor1)
        popupWindow = ListPopupWindow(context)
        popupWindow?.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id -> // The selected item is not displayed within the list, so when the selected position is equal to
            // the one of the currently selected item it gets shifted to the next item.
            var position = position
            if (position >= selectedIndex && position < adapter!!.count) {
                position++
            }
            selectedIndex = position
            onSpinnerItemSelectedListener?.onItemSelected(this@SpinnerMap, view, position, id)
            onItemClickListener?.onItemClick(parent, view, position, id)
            onItemSelectedListener?.onItemSelected(parent, view, position, id)
            adapter?.selectedIndex = position
            setTextInternal(adapter!!.getItemInDataset(position))
            dismissDropDown()
        })
        popupWindow?.isModal = true

        popupWindow?.setOnDismissListener(PopupWindow.OnDismissListener {
            if (!isArrowHidden) {
                animateArrow(false)
            }
        })

        isArrowHidden = typedArray.getBoolean(R.styleable.SpinnerMap_hideArrow, false)
        arrowDrawableTint = typedArray.getColor(R.styleable.SpinnerMap_arrowTint, Color.BLACK)
        arrowDrawableResId = typedArray.getResourceId(R.styleable.SpinnerMap_arrowDrawable, R.drawable.arrow)
        dropDownListPaddingBottom = typedArray.getDimensionPixelSize(R.styleable.SpinnerMap_dropDownListPaddingBottom, 0)
        horizontalAlignment = PopUpTextAlignment.fromId(
            typedArray.getInt(R.styleable.SpinnerMap_popupTextAlignment, PopUpTextAlignment.CENTER.ordinal)
        )

        val entries = typedArray.getTextArray(R.styleable.SpinnerMap_entries)
        if (entries != null) {
            attachDataSource(listOf(*entries))
        }

        typedArray.recycle()

        measureDisplayHeight()
    }

    private fun measureDisplayHeight() {
        displayHeight = context.resources.displayMetrics.heightPixels
    }

    override fun onDetachedFromWindow() {
        arrowAnimator?.cancel()
        super.onDetachedFromWindow()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            onVisibilityChanged(this, visibility)
        }
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        arrowDrawable = initArrowDrawable(arrowDrawableTint)
        setArrowDrawableOrHide(arrowDrawable)
    }

    private fun initArrowDrawable(drawableTint: Int): Drawable? {
        if (arrowDrawableResId == 0)
            return null
        var drawable: Drawable? = ContextCompat.getDrawable(context, arrowDrawableResId)
        if (drawable != null) {
            // Gets a copy of this drawable as this is going to be mutated by the animator
            drawable = DrawableCompat.wrap(drawable).mutate()
            if (drawableTint != Int.MAX_VALUE && drawableTint != 0) {
                if(isNight)
                    DrawableCompat.setTint(drawable, Color.GRAY)
                else
                    DrawableCompat.setTint(drawable, drawableTint)
            }
        }
        return drawable
    }

    private fun setArrowDrawableOrHide(drawable: Drawable?) {
        if (!isArrowHidden && drawable != null) {
            setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
        } else {
            setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        }
    }

    private fun getDefaultTextColor(context: Context): Int {
        val typedValue = TypedValue()
        context.theme
            .resolveAttribute(android.R.attr.textColorPrimary, typedValue, true)
        val typedArray = context.obtainStyledAttributes(typedValue.data, intArrayOf(android.R.attr.textColorPrimary))
        val defaultTextColor = typedArray.getColor(0, Color.BLACK)
        typedArray.recycle()
        return defaultTextColor
    }

    private fun setTextInternal(item: Any) {
        text = if (selectedTextFormatter != null) {
            selectedTextFormatter.format(item)
        } else {
            item.toString()
        }
    }

    /**
     * Set the default spinner item using its index
     *
     * @param position the item's position
     */
    fun setSelectedIndex(position: Int) {
        if (adapter != null) {
            if (position >= 0 && position <= adapter!!.count) {
                adapter?.selectedIndex = position
                selectedIndex = position
                setTextInternal(selectedTextFormatter.format(adapter!!.getItemInDataset(position)).toString())
            } else {
                throw IllegalArgumentException("Position must be lower than adapter count!")
            }
        }
    }

    fun getSelectedIndex(): Int {
        return selectedIndex
    }

    fun attachDataSource(@NonNull list: List<Any>) {
        adapter = SpinnerMapAdapter(list, textColor1, backgroundSelector, spinnerTextFormatter, horizontalAlignment!!)
        setAdapterInternal(adapter!!)
    }

    private fun setAdapterInternal(adapter: SpinnerMapBaseAdapter) {
        if (adapter.count > 0) {
            // If the adapter needs to be set again, ensure to reset the selected index as well
            selectedIndex = 0
            popupWindow?.setAdapter(adapter)
            setTextInternal(adapter.getItemInDataset(selectedIndex))
        }
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isEnabled && event.action == MotionEvent.ACTION_UP) {
            if (!popupWindow!!.isShowing) {
                showDropDown()
            } else {
                dismissDropDown()
            }
        }
        return super.onTouchEvent(event)
    }

    private fun animateArrow(shouldRotateUp: Boolean) {
        val start = if (shouldRotateUp) 0 else MAX_LEVEL
        val end = if (shouldRotateUp) MAX_LEVEL else 0
        arrowAnimator = ObjectAnimator.ofInt(arrowDrawable, "level", start, end)
        arrowAnimator?.interpolator = LinearOutSlowInInterpolator()
        arrowAnimator?.start()
    }

    fun dismissDropDown() {
        if (!isArrowHidden) {
            animateArrow(false)
        }
        popupWindow?.dismiss()
    }

    fun showDropDown() {
        if (!isArrowHidden) {
            animateArrow(true)
        }
        popupWindow?.anchorView = this
        popupWindow?.show()
        val listView = popupWindow?.listView
        if (listView != null) {
            listView.isVerticalScrollBarEnabled = false
            listView.isHorizontalScrollBarEnabled = false
            listView.isVerticalFadingEdgeEnabled = false
            listView.isHorizontalFadingEdgeEnabled = false
        }
    }

    fun performItemClick(position: Int, showDropdown: Boolean) {
        if (showDropdown) showDropDown()
        setSelectedIndex(position)
    }

    /**
     * only applicable when popup is shown .
     * @param view
     * @param position
     * @param id
     */
    fun performItemClick(view: View?, position: Int, id: Int) {
        showDropDown()
        val listView = popupWindow?.listView
        listView?.performItemClick(view, position, id.toLong())
    }

    fun getOnSpinnerItemSelectedListener(): OnSpinnerItemSelectedListener? {
        return onSpinnerItemSelectedListener
    }

    fun setOnSpinnerItemSelectedListener(onSpinnerItemSelectedListener: OnSpinnerItemSelectedListener) {
        this.onSpinnerItemSelectedListener = onSpinnerItemSelectedListener
    }

}