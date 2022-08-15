package com.styba.twitfeeds.main.fragments.settings

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SwitchCompat
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.airbnb.lottie.LottieAnimationView
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.styba.twitfeeds.R
import com.styba.twitfeeds.common.Constants
import com.styba.twitfeeds.common.TwitFeedManager
import com.styba.twitfeeds.common.Utils
import com.styba.twitfeeds.events.LocationEvent
import com.styba.twitfeeds.events.SettingEvent
import com.styba.twitfeeds.location.LocationAdapter
import com.styba.twitfeeds.main.MainActivity
import com.styba.twitfeeds.profile.ProfileActivity
import com.styba.twitfeeds.sources.SourceActivity
import com.styba.twitfeeds.terms.TermsActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class SettingsFragment : Fragment(), SettingsContract.View {

    private var frameLoading: FrameLayout? = null
    private var lottieViewLoading: LottieAnimationView? = null
    private var slidingLayoutSettings: SlidingUpPanelLayout? = null
    private var recyclerViewSettings: RecyclerView? = null
    private var textViewVersion: TextView? = null
    private var imageButtonBack: ImageButton? = null
    private var editTextSearch: EditText? = null
    private var recyclerViewLocations: RecyclerView? = null
    private val settingsPresenter = SettingsPresenter()
    private var twitFeedManager: TwitFeedManager? = null
    private var locationAdapter: LocationAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        setUpMVP()
        initViews(view)
        if (context != null)
            settingsPresenter.getSettings(context!!)
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() = SettingsFragment()
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
        settingsPresenter.detachView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((requestCode == Constants.REQUEST_SOURCES || requestCode == Constants.REQUEST_PROFILE) && resultCode == Activity.RESULT_OK) {
            startActivity(Intent(context, MainActivity::class.java).putExtra("isExplore", false))
            activity?.finish()
        }
    }

    override fun showLoading() {
        frameLoading?.visibility = View.VISIBLE
        lottieViewLoading?.playAnimation()
    }

    override fun hideLoading() {
        lottieViewLoading?.cancelAnimation()
        frameLoading?.visibility = View.GONE
    }

    override fun initSettings() {
        initList()
    }

    override fun initEmptyView() {

    }

    override fun initLocations() {
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerViewLocations?.layoutManager = linearLayoutManager
        locationAdapter = LocationAdapter(settingsPresenter)
        recyclerViewLocations?.adapter = locationAdapter
        slidingLayoutSettings?.panelState = SlidingUpPanelLayout.PanelState.EXPANDED
    }

    override fun onFilterList() {
        locationAdapter?.notifyDataSetChanged()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSettingClicked(event: SettingEvent) {
        when (event.position) {
            0 -> {
                showLoading()
                settingsPresenter.getLocations()
            }
            1 -> startActivityForResult(Intent(context, ProfileActivity::class.java), Constants.REQUEST_PROFILE)
            2 -> startActivityForResult(Intent(context, SourceActivity::class.java), Constants.REQUEST_SOURCES)
            3 -> {
                val dialogData = MaterialDialog.Builder(context!!)
                        .title("Data option")
                        .titleColorRes(R.color.colorBlue2)
                        .customView(R.layout.dialog_data, true)
                        .positiveText(R.string.source_button_done)
                        .positiveColorRes(R.color.colorBlue2)
                        .onPositive { dialog, _ ->
                            dialog.dismiss()
                        }
                        .cancelable(false)
                        .show()
                val view = dialogData.customView
                val switchImageDownload = view?.findViewById<SwitchCompat>(R.id.switchImageDownload)
                val switchReaderMode = view?.findViewById<SwitchCompat>(R.id.switchReaderMode)
                switchImageDownload?.isChecked = twitFeedManager?.isOnlyWifi()!!
                switchImageDownload?.setOnCheckedChangeListener { _, isChecked -> twitFeedManager?.setIsOnlyWifi(isChecked) }
                switchReaderMode?.isChecked = twitFeedManager?.isReaderMode()!!
                switchReaderMode?.setOnCheckedChangeListener { _, isChecked -> twitFeedManager?.setIsReaderMode(isChecked) }
            }
            4 -> startActivity(Intent(context, TermsActivity::class.java).putExtra("URL", Constants.FAQ_URL))
            5 -> startActivity(Intent(context, TermsActivity::class.java).putExtra("URL", Constants.TERMS_URL))
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLocationClicked(event: LocationEvent) {
        twitFeedManager?.setExploreLocationId(event.locationId)
        twitFeedManager?.setExploreLocationName(event.locationName)
        twitFeedManager?.setExploreLocationCountry(event.locationCountry)
        twitFeedManager?.setHasExploreMenu(event.hasMenu == 1)
        twitFeedManager?.setExploreFlag(event.flag)
        startActivity(Intent(context, MainActivity::class.java).putExtra("isExplore", true))
        activity?.finish()
    }

    private fun setUpMVP() {
        settingsPresenter.attachView(this)
        val settingsModel = SettingsModel()
        twitFeedManager = TwitFeedManager(context)
        twitFeedManager?.setActivity(activity)
        settingsModel.initManager(twitFeedManager)
        settingsModel.attachPresenter(settingsPresenter)
        settingsPresenter.initModel(settingsModel)
    }

    private fun initViews(view: View) {
        frameLoading = view.findViewById(R.id.frameLoading)
        lottieViewLoading = view.findViewById(R.id.lottieViewLoading)
        slidingLayoutSettings = view.findViewById(R.id.slidingLayoutSettings)
        recyclerViewSettings = view.findViewById(R.id.recyclerViewSettings)
        textViewVersion = view.findViewById(R.id.textViewVersion)
        imageButtonBack = view.findViewById(R.id.imageButtonBack)
        editTextSearch = view.findViewById(R.id.editTextSearch)
        recyclerViewLocations = view.findViewById(R.id.recyclerViewLocations)
        slidingLayoutSettings?.isTouchEnabled = false
        lottieViewLoading?.imageAssetsFolder = "images/"
        textViewVersion?.text = Utils.getAppVersion()
        imageButtonBack?.setOnClickListener {
            Utils.hideSoftKeyboard(activity!!, editTextSearch!!)
            val handler = Handler()
            handler.postDelayed({ slidingLayoutSettings?.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED }, 150)
        }
        initEditTextSearch()
    }

    private fun initEditTextSearch() {
        editTextSearch?.addTextChangedListener(object : TextWatcher {
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
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerViewSettings?.layoutManager = linearLayoutManager
        recyclerViewSettings?.adapter = SettingsAdapter(settingsPresenter)
    }
}
