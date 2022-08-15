package com.styba.twitfeeds.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.airbnb.lottie.LottieAnimationView
import com.lcodecore.tkrefreshlayout.IHeaderView
import com.lcodecore.tkrefreshlayout.OnAnimEndListener
import com.styba.twitfeeds.R

class RefreshView: FrameLayout, IHeaderView {

    private var frameLoading: FrameLayout? = null
    private var lottieViewLoading: LottieAnimationView? = null

    constructor(context: Context?) : super(context) {
        initView(context)
    }

    constructor(context: Context?, attrs: AttributeSet) : super(context, attrs) {
        initView(context)
    }

    constructor(context: Context?, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView(context)
    }

    override fun onFinish(animEndListener: OnAnimEndListener) {
        lottieViewLoading?.cancelAnimation()
        animEndListener.onAnimEnd()
    }

    override fun getView(): View {
        return this
    }

    override fun onPullingDown(fraction: Float, maxHeadHeight: Float, headHeight: Float) {
        frameLoading?.visibility = View.VISIBLE
        lottieViewLoading?.cancelAnimation()
    }

    override fun onPullReleasing(fraction: Float, maxHeadHeight: Float, headHeight: Float) {
        lottieViewLoading?.playAnimation()
    }

    override fun reset() {
        frameLoading?.visibility = View.GONE
        invalidate()
    }

    override fun startAnim(maxHeadHeight: Float, headHeight: Float) {

    }

    private fun initView(context: Context?) {
        LayoutInflater.from(context).inflate(R.layout.refresh_loading, this, true)
        frameLoading = findViewById(R.id.frameLoading)
        lottieViewLoading = findViewById(R.id.lottieViewLoading)
        lottieViewLoading?.imageAssetsFolder = "images/"
        frameLoading?.visibility = View.VISIBLE
    }
}