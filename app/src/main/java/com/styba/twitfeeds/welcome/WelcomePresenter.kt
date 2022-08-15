package com.styba.twitfeeds.welcome

import com.styba.twitfeeds.common.BasePresenter

class WelcomePresenter : BasePresenter<WelcomeContract.View>(), WelcomeContract.Presenter {

    private var model: WelcomeModel? = null

    override fun initModel(model: WelcomeModel) {
        this.model = model
    }

    override fun getSources() {
        view?.showLoading()
        model?.getSources()
    }

    override fun onSourceSuccess() {
        model?.saveSourceSelected()
    }

    override fun onSourcesFail() {
        view?.hideLoading()
        view?.showError()
    }

    override fun onSaveFinished() {
        view?.hideLoading()
        view?.gotoMain()
    }

    override fun onSaveFail() {
        view?.hideLoading()
        view?.showError()
    }

    override fun detachView() {
        super.detachView()
        model?.detachPresenter()
    }

}