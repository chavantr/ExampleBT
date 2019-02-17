package imei.mywings.com.bustrackingapp.update

import android.os.AsyncTask
import imei.mywings.com.bustrackingapp.Constants
import imei.mywings.com.bustrackingapp.HttpConnectionUtil
import org.json.JSONObject

class UpdateProfileAsync : AsyncTask<JSONObject, Void, String>() {

    private lateinit var onUpdateProfileListener: OnUpdateProfileListener

    private val httpConnectionUtil = HttpConnectionUtil()

    override fun doInBackground(vararg param: JSONObject?): String {
        return httpConnectionUtil.requestPost(Constants.URL + Constants.UPDATE_PROFILE, param[0])
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        onUpdateProfileListener.onUpdateProfileSuccess(result!!)
    }

    fun setOnUpdateProfileListener(onUpdateProfileListener: OnUpdateProfileListener, request: JSONObject) {
        this.onUpdateProfileListener = onUpdateProfileListener
        super.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, request)
    }

}