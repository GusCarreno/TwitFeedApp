package com.styba.twitfeeds.sources

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import com.styba.twitfeeds.R
import com.styba.twitfeeds.common.BaseActivity
import com.styba.twitfeeds.common.TwitFeedManager
import com.styba.twitfeeds.common.Utils
import com.styba.twitfeeds.events.SourceEvent
import kotlinx.android.synthetic.main.activity_source.*
import kotlinx.android.synthetic.main.loading_view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class SourceActivity : BaseActivity(), SourceContract.View {

    private val sourcePresenter = SourcePresenter()
    private var twitFeedManager: TwitFeedManager? = null
    private var sourceAdapter: SourceAdapter? = null
    private var isUpdating = false

    override fun getLayout(): Int {
        return R.layout.activity_source
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpMVP()
        initView()
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
        sourcePresenter.detachView()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            val selected = sourcePresenter.countSelected()
            if (selected!! > 0)
                finish()
            else
                Utils.showSnackBar(this, relativeSource, resources.getString(R.string.source_no_selected_error))
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun getContext(): Context {
        return this
    }

    override fun showLoading() {
        frameLoading.visibility = View.VISIBLE
        lottieViewLoading.playAnimation()
    }

    override fun hideLoading() {
        lottieViewLoading.cancelAnimation()
        frameLoading.visibility = View.GONE
    }

    override fun initSources() {
        if (!isUpdating)
            initList()
    }

    override fun initEmptyView() {
        Utils.showSnackBar(this, relativeSource, getString(R.string.source_error))
    }

    override fun notifyAdapter(position: Int) {
        sourceAdapter?.notifyItemChanged(position)
    }

    override fun gotoHome() {
        setResult(Activity.RESULT_OK, Intent())
        finish()
    }

    override fun onBackPressed() {
        val selected = sourcePresenter.countSelected()
        if (selected!! > 0) {
            setResult(Activity.RESULT_CANCELED, Intent())
            finish()
            super.onBackPressed()
        } else
            Utils.showSnackBar(this, relativeSource, resources.getString(R.string.source_no_selected_error))
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSourceChange(event: SourceEvent) {
        isUpdating = true
        if (!event.enabled) {
            switchSourceAll.setOnCheckedChangeListener(null)
            switchSourceAll.isChecked = false
            switchSourceAll.setOnCheckedChangeListener { _, isChecked ->
                isUpdating = false
                if (isChecked)
                    sourcePresenter.selectAll()
                else
                    sourcePresenter.desSelectAll()
            }
        }
        sourcePresenter.updateSource(event.position, event.enabled, event.source)
    }

    private fun setUpMVP() {
        sourcePresenter.attachView(this)
        val sourceModel = SourceModel()
        twitFeedManager = TwitFeedManager(this)
        twitFeedManager?.setActivity(this)
        sourceModel.initManager(twitFeedManager)
        sourceModel.attachPresenter(sourcePresenter)
        sourcePresenter.initModel(sourceModel)
        sourcePresenter.getSources()
    }

    private fun initView() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        lottieViewLoading.imageAssetsFolder = "images/"
        textViewSourceLocation.text = getString(R.string.source_title_value, twitFeedManager?.getLocationCountry())
        buttonDone.setOnClickListener {
            val selected = sourcePresenter.countSelected()
            if (selected!! > 0)
                sourcePresenter.saveSourceSelected()
            else
                Utils.showSnackBar(this, relativeSource, resources.getString(R.string.source_no_selected_error))
        }
        switchSourceAll.isChecked = twitFeedManager?.isSelectAll()!!
        switchSourceAll.setOnCheckedChangeListener { _, isChecked ->
            isUpdating = false
            if (isChecked)
                sourcePresenter.selectAll()
            else
                sourcePresenter.desSelectAll()
        }
    }

    private fun initList() {
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerViewSources.layoutManager = linearLayoutManager
        sourceAdapter = SourceAdapter(sourcePresenter)
        recyclerViewSources.adapter = sourceAdapter
    }

}
