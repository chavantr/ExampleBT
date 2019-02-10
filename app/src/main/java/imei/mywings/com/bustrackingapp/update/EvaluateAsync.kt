package imei.mywings.com.bustrackingapp.update

import android.os.AsyncTask
import imei.mywings.com.bustrackingapp.Constants
import imei.mywings.com.bustrackingapp.HttpConnectionUtil

class EvaluateAsync : AsyncTask<Void, Void, Boolean>() {

    private val httpConnectionUtil = HttpConnectionUtil()
    private lateinit var onListenListener: OnListenListener

    override fun doInBackground(vararg param: Void?): Boolean {
        return httpConnectionUtil.requestGet(Constants.URI + Constants.ESTABLISH_CONNECTION + Constants.EEEEE)
            .toBoolean()
    }

    override fun onPostExecute(result: Boolean?) {
        super.onPostExecute(result)
        onListenListener.onListenSuccess(result!!)
    }

    fun setOnListenListener(onListenListener: OnListenListener) {
        this.onListenListener = onListenListener
        super.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
    }

}