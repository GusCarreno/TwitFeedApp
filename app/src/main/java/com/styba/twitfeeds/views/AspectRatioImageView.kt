package com.styba.twitfeeds.views

import android.content.Context
import android.support.annotation.IntDef
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet
import com.styba.twitfeeds.R
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

class AspectRatioImageView : AppCompatImageView {

    private var aspect: Int = 0
    private var aspectRatio: Float = 0.toFloat()

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val height = measuredHeight
        val width = measuredWidth

        when (aspect) {
            AUTO -> if (height > width) {
                if (width == 0) {
                    return
                }
                aspect = WIDTH
                aspectRatio = Math.round(height.toDouble() / width).toFloat()
                setMeasuredDimensionByHeight(height)
            } else {
                if (height == 0) {
                    return
                }
                aspect = HEIGHT
                aspectRatio = Math.round(width.toDouble() / height).toFloat()
                setMeasuredDimensionByWidth(width)
            }
            WIDTH -> setMeasuredDimensionByHeight(height)
            HEIGHT -> setMeasuredDimensionByWidth(width)
            else -> setMeasuredDimensionByWidth(width)
        }
    }

    private fun init(attrs: AttributeSet) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.AspectRatioImageView)
        try {
            aspect = a.getInt(R.styleable.AspectRatioImageView_ari_aspect, HEIGHT)
            aspectRatio = a.getFloat(R.styleable.AspectRatioImageView_ari_ratio, DEFAULT_RATIO.toFloat())
        } finally {
            a.recycle()
        }
    }

    private fun setMeasuredDimensionByWidth(width: Int) {
        setMeasuredDimension(width, (width * aspectRatio).toInt())
    }

    private fun setMeasuredDimensionByHeight(height: Int) {
        setMeasuredDimension((height * aspectRatio).toInt(), height)
    }

    fun getAspectRatio(): Double {
        return aspectRatio.toDouble()
    }

    fun setAspectRatio(ratio: Float) {
        aspectRatio = ratio
        requestLayout()
    }

    @Aspect
    fun getAspect(): Int {
        return aspect
    }

    fun setAspect(@Aspect aspect: Int) {
        this.aspect = aspect
        requestLayout()
    }

    @IntDef(WIDTH, HEIGHT)
    @Retention(RetentionPolicy.SOURCE)
    annotation class Aspect

    companion object {

        const val DEFAULT_RATIO = 1
        private const val WIDTH = 0
        private const val HEIGHT = 1
        private const val AUTO = 2
    }
}