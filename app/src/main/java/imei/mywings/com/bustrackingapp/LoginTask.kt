package imei.mywings.com.bustrackingapp

import android.os.AsyncTask
import org.json.JSONObject

class LoginTask : AsyncTask<JSONObject, Void, LoginResult>() {


    private var connectionUtil: HttpConnectionUtil = HttpConnectionUtil()

    lateinit var loginLoginListener: OnLoginListener

    private var name = "Login"


    override fun doInBackground(vararg param: JSONObject?): LoginResult {
        var loginResult = LoginResult()
        var response = connectionUtil.requestPost(Constants.URL + name, param[0])
        var jsonResponse = JSONObject(response)
        return loginResult
    }

    override fun onPostExecute(result: LoginResult?) {
        super.onPostExecute(result)
        loginLoginListener.onLoginSuccess(result!!)
    }

    fun setLoginListener(loginListener: OnLoginListener, jsonObject: JSONObject) {
        this.loginLoginListener = loginListener
        super.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, jsonObject)
    }

}