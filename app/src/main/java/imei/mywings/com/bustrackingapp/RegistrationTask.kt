package imei.mywings.com.bustrackingapp

import android.os.AsyncTask
import org.json.JSONObject

class RegistrationTask : AsyncTask<JSONObject, Void, Int>() {

    private var connectionUtil: HttpConnectionUtil = HttpConnectionUtil()

    private lateinit var registrationListener: OnRegistrationListener

    override fun doInBackground(vararg param: JSONObject?): Int {

        var response = connectionUtil.requestPost(Constants.URL+ "Register", param[0])

        //if (response.contentEquals("1")) return 1

        return response.toInt()
    }

    override fun onPostExecute(result: Int?) {
        super.onPostExecute(result)
        registrationListener.onRegistrationSuccess(result!!)
    }

    fun setRegistrationListener(registrationListener: OnRegistrationListener, jsonObject: JSONObject) {
        this.registrationListener = registrationListener
        super.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, jsonObject)
    }


}