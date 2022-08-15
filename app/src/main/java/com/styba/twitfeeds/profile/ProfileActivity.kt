package com.styba.twitfeeds.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import co.ceryle.radiorealbutton.library.RadioRealButton
import co.ceryle.radiorealbutton.library.RadioRealButtonGroup
import com.afollestad.materialdialogs.MaterialDialog
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.TwitterAuthProvider
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.styba.twitfeeds.R
import com.styba.twitfeeds.common.BaseActivity
import com.styba.twitfeeds.common.TwitFeedManager
import com.styba.twitfeeds.common.Utils
import com.styba.twitfeeds.events.LocationEvent
import com.styba.twitfeeds.location.LocationAdapter
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.TwitterSession
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.loading_view.*
import kotlinx.android.synthetic.main.panel_location.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class ProfileActivity : BaseActivity(), ProfileContract.View, RadioRealButtonGroup.OnPositionChangedListener {

    private val profilePresenter = ProfilePresenter()
    private var twitFeedManager: TwitFeedManager? = null
    private var locationAdapter: LocationAdapter? = null
    private var fireBaseAuth: FirebaseAuth? = null
    private val tag = "ProfileActivity"
    private var needRefresh = false

    override fun getLayout(): Int {
        return R.layout.activity_profile
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //FirebaseAuth.getInstance().signOut()
        fireBaseAuth = FirebaseAuth.getInstance()
        setUpMVP()
        initViews()
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
        val currentUser = fireBaseAuth?.currentUser
        if (currentUser != null)
            updateUI(currentUser)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        profilePresenter.detachView()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        twitterButton.onActivityResult(requestCode, resultCode, data)
    }

    override fun showLoading() {
        linearChooseLocation.visibility = View.GONE
        frameLoading.visibility = View.VISIBLE
        lottieViewLoading.playAnimation()
    }

    override fun hideLoading() {
        lottieViewLoading.cancelAnimation()
        frameLoading.visibility = View.GONE
        linearChooseLocation.visibility = View.VISIBLE
    }

    override fun initLocations() {
        initList()
    }

    override fun onFilterList() {
        locationAdapter?.notifyDataSetChanged()
    }

    override fun initEmptyView() {
        Utils.showSnackBar(this, slidingLayoutProfile, getString(R.string.location_error))
    }

    override fun gotoMain() {
        setResult(Activity.RESULT_OK, Intent())
        finish()
    }

    override fun showError() {
        Utils.showSnackBar(this, slidingLayoutProfile, resources.getString(R.string.profile_error))
    }

    override fun onPositionChanged(button: RadioRealButton?, position: Int) {
        val lang = if (position == 0) "es" else "en"
        needRefresh = true
        //twitFeedManager?.setLanguage(lang)
        twitFeedManager?.setLanguageApp(lang)
    }

    override fun onBackPressed() {
        if (slidingLayoutProfile.panelState == SlidingUpPanelLayout.PanelState.EXPANDED) {
            Utils.hideSoftKeyboard(this, editTextSearch)
            val handler = Handler()
            handler.postDelayed({ slidingLayoutProfile?.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED }, 150)
        } else {
            if (needRefresh) {
                setResult(Activity.RESULT_OK, Intent())
                finish()
            } else
                super.onBackPressed()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLocationClicked(event: LocationEvent) {
        slidingLayoutProfile.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
        MaterialDialog.Builder(this)
                .title("Location")
                .titleColorRes(R.color.colorBlue2)
                .content(R.string.profile_dialog_location_message)
                .positiveText(R.string.yes_label)
                .positiveColorRes(R.color.colorBlue2)
                .onPositive { dialog, _ ->
                    twitFeedManager?.setLocationId(event.locationId)
                    twitFeedManager?.setLocationCountry(event.locationCountry)
                    twitFeedManager?.setLocationName(event.locationName)
                    twitFeedManager?.setHasMenu(event.hasMenu == 1)
                    twitFeedManager?.setFlag(event.flag)
                    profilePresenter.getSources()
                    dialog.dismiss()
                }
                .negativeText(R.string.no_label)
                .negativeColorRes(R.color.colorBlack)
                .onNegative { dialog, _ ->
                    dialog.dismiss()
                }
                .cancelable(false)
                .show()
    }

    private fun setUpMVP() {
        profilePresenter.attachView(this)
        val profileModel = ProfileModel()
        twitFeedManager = TwitFeedManager(this)
        twitFeedManager?.setActivity(this)
        profileModel.initManager(twitFeedManager)
        profileModel.attachPresenter(profilePresenter)
        profilePresenter.initModel(profileModel)
        twitFeedManager?.setOldLocationId(twitFeedManager?.getLocationId()!!)
    }

    private fun initViews() {
        slidingLayoutProfile.isTouchEnabled = false
        lottieViewLoading.imageAssetsFolder = "images/"
        textViewLocation.text = twitFeedManager?.getLocationName()
        textViewLocation.setOnClickListener { profilePresenter.getLocations() }
        buttonNotification.setOnClickListener {
            val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:$packageName")
            startActivity(intent)
        }
        twitterButton.callback = object : Callback<TwitterSession>() {
            override fun success(result: Result<TwitterSession>) {
                Log.d(tag, "twitterLogin:success$result")
                handleTwitterSession(result.data)
            }

            override fun failure(exception: TwitterException) {
                Log.w(tag, "twitterLogin:failure", exception)
            }
        }
        if (TextUtils.equals(twitFeedManager?.getLanguageApp(), "es"))
            radioGroupLanguage.position = 0
        else
            radioGroupLanguage.position = 1
        radioGroupLanguage.setOnPositionChangedListener(this)

        imageButtonBack.setOnClickListener {
            Utils.hideSoftKeyboard(this, editTextSearch)
            val handler = Handler()
            handler.postDelayed({ slidingLayoutProfile?.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED }, 150)
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

    private fun handleTwitterSession(session: TwitterSession) {
        Log.d(tag, "handleTwitterSession:$session")
        val credential = TwitterAuthProvider.getCredential(
                session.authToken.token,
                session.authToken.secret)
        signInWithCredential(credential)
    }

    private fun signInWithCredential(credential: AuthCredential) {
        fireBaseAuth?.signInWithCredential(credential)
                ?.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d(tag, "signInWithCredential:success")
                        val user = fireBaseAuth?.currentUser
                        updateUI(user)
                    } else {
                        Log.w(tag, "signInWithCredential:failure", task.exception)
                    }
                }
    }

    private fun initList() {
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerViewLocations.layoutManager = linearLayoutManager
        locationAdapter = LocationAdapter(profilePresenter)
        recyclerViewLocations.adapter = locationAdapter
        slidingLayoutProfile.panelState = SlidingUpPanelLayout.PanelState.EXPANDED
    }

    private fun updateUI(user: FirebaseUser?) {
        linearProfile.visibility = View.VISIBLE
        textViewLabel1.visibility = View.GONE
        textViewLabel2.visibility = View.GONE
        twitterButton.visibility = View.GONE
        textViewUser.text = user?.displayName
    }

}
