package com.styba.twitfeeds.common

import com.google.gson.GsonBuilder
import org.xml.sax.InputSource
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory

abstract class BaseModel<P> {

    protected var presenter: P? = null
    protected var gSon = GsonBuilder().setPrettyPrinting().create()!!
    protected var twitFeedManager: TwitFeedManager? = null

    abstract fun initManager(twitFeedManager: TwitFeedManager?)

    open fun attachPresenter(presenter: P) {
        this.presenter = presenter
    }

    open fun detachPresenter() {
        presenter = null
    }

    open fun parseResponse(response: String) : String {
        val dbf = DocumentBuilderFactory.newInstance()
        val db = dbf.newDocumentBuilder()
        val doc = db.parse(InputSource(StringReader(response)))
        var parseResponse = ""
        doc.documentElement.normalize()

        val nodeList = doc.getElementsByTagName("string")
        for (i in 0 until nodeList.length) {
            val node = nodeList.item(i)
            for (j in 0 until node.childNodes.length) {
                val temp = node.childNodes.item(j)
                parseResponse = temp.textContent
            }
        }
        return parseResponse
    }

    protected val isPresenterAttached: Boolean
        get() = presenter != null
}