package com.styba.twitfeeds.calligraphy;

import android.view.View;
import com.styba.twitfeeds.viewpump.InflateResult;
import com.styba.twitfeeds.viewpump.Interceptor;

public class CalligraphyInterceptor implements Interceptor {

    private final Calligraphy calligraphy;

    public CalligraphyInterceptor(CalligraphyConfig calligraphyConfig) {
        this.calligraphy = new Calligraphy(calligraphyConfig);
    }

    @Override
    public InflateResult intercept(Chain chain) {
        InflateResult result = chain.proceed(chain.request());
        View viewWithTypeface = calligraphy.onViewCreated(result.view(), result.context(), result.attrs());
        return result.toBuilder().view(viewWithTypeface).build();
    }
}
