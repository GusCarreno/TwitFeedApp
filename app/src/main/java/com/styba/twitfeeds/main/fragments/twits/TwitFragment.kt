package com.styba.twitfeeds.main.fragments.twits

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout
import com.styba.twitfeeds.R
import com.styba.twitfeeds.common.TwitFeedManager
import com.styba.twitfeeds.events.ReloadEvent
import com.styba.twitfeeds.views.RefreshView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

private const val SECTION = "section"

class TwitFragment : Fragment(), TwitContract.View {

    private var refreshLayoutTwit: TwinklingRefreshLayout? = null
    private var frameLoading: FrameLayout? = null
    private var lottieViewLoading: LottieAnimationView? = null
    private var recyclerViewNews: RecyclerView? = null
    private var textViewEmpty: TextView? = null
    private val twitPresenter = TwitPresenter()
    private var twitFeedManager: TwitFeedManager? = null
    private var section: String? = null
    private var isRefresh = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            section = it.getString(SECTION)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_twit, container, false)
        setUpMVP()
        initViews(view)
        showLoading()
        twitPresenter.getTwits(section)
        return view
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        twitPresenter.detachView()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param section Parameter 1.
         * @return A new instance of fragment TwitFragment.
         */
        @JvmStatic
        fun newInstance(section: String) =
                TwitFragment().apply {
                    arguments = Bundle().apply {
                        putString(SECTION, section)
                    }
                }
    }

    override fun provideContext(): Context? {
        return context
    }

    override fun initTwitsList() {
        textViewEmpty?.visibility = View.GONE
        if (isRefresh) {
            isRefresh = false
            refreshLayoutTwit?.finishRefreshing()
        }
        initList()
    }

    override fun initEmptyView() {
        textViewEmpty?.visibility = View.VISIBLE
    }

    override fun showLoading() {
        frameLoading?.visibility = View.VISIBLE
        lottieViewLoading?.playAnimation()
    }

    override fun hideLoading() {
        lottieViewLoading?.cancelAnimation()
        frameLoading?.visibility = View.GONE
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshed(event: ReloadEvent) {
        twitPresenter.getTwits(section)
    }

    private fun setUpMVP() {
        twitPresenter.attachView(this)
        val twitModel = TwitModel()
        twitFeedManager = TwitFeedManager(context)
        twitFeedManager?.setActivity(activity)
        twitModel.initManager(twitFeedManager)
        twitModel.attachPresenter(twitPresenter)
        twitPresenter.initModel(twitModel)
    }

    private fun initViews(view: View) {
        refreshLayoutTwit = view.findViewById(R.id.refreshLayoutTwit)
        frameLoading = view.findViewById(R.id.frameLoading)
        lottieViewLoading = view.findViewById(R.id.lottieViewLoading)
        recyclerViewNews = view.findViewById(R.id.recyclerViewNews)
        textViewEmpty = view.findViewById(R.id.textViewEmpty)
        lottieViewLoading?.imageAssetsFolder = "images/"
        if (context != null) {
            val headerView = RefreshView(context)
            refreshLayoutTwit?.setHeaderView(headerView)
        }
        refreshLayoutTwit?.setEnableLoadmore(false)
        refreshLayoutTwit?.setOnRefreshListener(object : RefreshListenerAdapter() {
            override fun onRefresh(refreshLayout: TwinklingRefreshLayout) {
                isRefresh = true
                EventBus.getDefault().post(ReloadEvent())
            }
        })
        recyclerViewNews?.setHasFixedSize(true)
    }

    private fun initList() {
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerViewNews?.layoutManager = linearLayoutManager
        recyclerViewNews?.adapter = NewsAdapter(twitPresenter)
    }
}
