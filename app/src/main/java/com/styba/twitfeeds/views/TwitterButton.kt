package com.styba.twitfeeds.views

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import com.styba.twitfeeds.R
import com.twitter.sdk.android.core.identity.TwitterLoginButton

class TwitterButton : TwitterLoginButton {
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init()
    }

    private fun init() {
        if (isInEditMode)
            return
        //setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context, R.mipmap.ic_twitter), null, null, null)
        setBackgroundColor(ContextCompat.getColor(context,android.R.color.transparent))
        textSize = 16f
        text = resources.getString(R.string.login_twitter)
        setTextColor(ContextCompat.getColor(context, R.color.colorGray1))
    }
}