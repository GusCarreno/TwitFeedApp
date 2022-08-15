package com.styba.twitfeeds.location

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.styba.twitfeeds.login.LoginActivity
import com.styba.twitfeeds.R
import com.styba.twitfeeds.common.BaseActivity
import com.styba.twitfeeds.common.TwitFeedManager
import com.styba.twitfeeds.common.Utils
import com.styba.twitfeeds.events.LocationEvent
import kotlinx.android.synthetic.main.activity_location.*
import kotlinx.android.synthetic.main.loading_view.*
import kotlinx.android.synthetic.main.panel_location.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class LocationActivity : BaseActivity(), LocationContract.View {

    private val locationPresenter = LocationPresenter()
    private var twitFeedManager: TwitFeedManager? = null
    private var locationAdapter: LocationAdapter? = null

    override fun getLayout(): Int {
        return R.layout.activity_location
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setUpMVP()
        initViews()
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
        locationPresenter.detachView()
    }

    override fun showLoading() {
        frameLoading.visibility = View.VISIBLE
        lottieViewLoading.playAnimation()
    }

    override fun hideLoading() {
        lottieViewLoading.cancelAnimation()
        frameLoading.visibility = View.GONE
    }

    override fun initLocations() {
        buttonChooseLocation.isEnabled = true
        initList()
    }

    override fun initEmptyView() {
        buttonChooseLocation.isEnabled = true
        Utils.showSnackBar(this, slidingLayoutLocation, getString(R.string.location_error))
    }

    override fun onFilterList() {
        locationAdapter?.notifyDataSetChanged()
    }

    override fun onBackPressed() {
        if (slidingLayoutLocation.panelState == SlidingUpPanelLayout.PanelState.EXPANDED) {
            Utils.hideSoftKeyboard(this, editTextSearch)
            val handler = Handler()
            handler.postDelayed({ slidingLayoutLocation?.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED }, 150)
        } else
            super.onBackPressed()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLocationClicked(event: LocationEvent) {
        slidingLayoutLocation.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
        buttonChooseLocation.text = resources.getString(R.string.location_item, event.locationName, event.locationCountry)
        twitFeedManager?.setLocationId(event.locationId)
        twitFeedManager?.setLocationName(event.locationName)
        twitFeedManager?.setLocationCountry(event.locationCountry)
        twitFeedManager?.setHasMenu(event.hasMenu == 1)
        twitFeedManager?.setFlag(event.flag)
        textViewContent2.visibility = View.VISIBLE
        buttonContinue.setBackgroundColor(ActivityCompat.getColor(this, R.color.colorBlue2))
    }

    private fun setUpMVP() {
        locationPresenter.attachView(this)
        val locationModel = LocationModel()
        twitFeedManager = TwitFeedManager(this)
        twitFeedManager?.setActivity(this)
        locationModel.initManager(twitFeedManager)
        locationModel.attachPresenter(locationPresenter)
        locationPresenter.initModel(locationModel)
    }

    private fun initViews() {
        slidingLayoutLocation.isTouchEnabled = false
        lottieViewLoading.imageAssetsFolder = "images/"
        buttonChooseLocation.setOnClickListener {
            if (Utils.verifyConnection(this)) {
                buttonChooseLocation.isEnabled = false
                locationPresenter.getLocations()
            } else
                Toast.makeText(this, "you don't have an internet connection", Toast.LENGTH_LONG).show()
        }
        imageButtonBack.setOnClickListener {
            Utils.hideSoftKeyboard(this, editTextSearch)
            val handler = Handler()
            handler.postDelayed({ slidingLayoutLocation?.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED }, 150)
        }
        buttonContinue.setOnClickListener{
            if (!TextUtils.isEmpty(twitFeedManager?.getLocationCountry())) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else
                Utils.showSnackBar(this, slidingLayoutLocation, getString(R.string.location_alert))
        }
        initEditTextSearch()
    }

    private fun initEditTextSearch() {
        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                locationAdapter?.filter?.filter(s.toString())
            }
        })
    }

    private fun initList() {
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerViewLocations.layoutManager = linearLayoutManager
        locationAdapter = LocationAdapter(locationPresenter)
        recyclerViewLocations.adapter = locationAdapter
        slidingLayoutLocation.panelState = SlidingUpPanelLayout.PanelState.EXPANDED
    }
}
