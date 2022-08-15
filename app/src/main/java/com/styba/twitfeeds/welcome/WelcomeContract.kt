package com.styba.twitfeeds.welcome

interface WelcomeContract {
    interface View {
        fun showLoading()
        fun hideLoading()
        fun gotoMain()
        fun showError()
    }

    interface Presenter {
        fun initModel(model: WelcomeModel)
        fun getSources()
        fun onSourceSuccess()
        fun onSourcesFail()
        fun onSaveFinished()
        fun onSaveFail()
    }

    interface Model {
        fun getSources()
        fun saveSourceSelected()
    }
}