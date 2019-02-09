package imei.mywings.com.bustrackingapp.update

import android.os.AsyncTask
import imei.mywings.com.bustrackingapp.Constants
import imei.mywings.com.bustrackingapp.HttpConnectionUtil
import org.json.JSONObject

class UpdateLocationAsync : AsyncTask<JSONObject, Void, String>() {

    private val httpConnectionUtil = HttpConnectionUtil()

    private lateinit var onUpdateListener: OnUpdateListener

    override fun doInBackground(vararg param: JSONObject?): String {

        return httpConnectionUtil.requestPost(Constants.URL, param[0])

    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        onUpdateListener.onUpdateSuccess(result!!)

    }

    fun setOnUpdateListener(onUpdateListener: OnUpdateListener, request: JSONObject) {
        this.onUpdateListener = onUpdateListener
        super.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, request)
    }
}