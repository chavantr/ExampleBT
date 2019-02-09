package imei.mywings.com.bustrackingapp.update

import android.os.AsyncTask
import imei.mywings.com.bustrackingapp.Bus
import imei.mywings.com.bustrackingapp.Constants
import imei.mywings.com.bustrackingapp.HttpConnectionUtil
import org.json.JSONObject

class GetUpdateLocationAsync : AsyncTask<Int, Void, Bus>() {

    private val httpConnectionUtil = HttpConnectionUtil()

    private lateinit var onLocationUpdateListener: OnLocationUpdateListener


    override fun doInBackground(vararg param: Int?): Bus? {

        val response = httpConnectionUtil.requestGet(Constants.URL + Constants.GET_CURRENT_LOCATION_BY_ID + "?id=${param[0]}")

        val jLocation = JSONObject(response)

        if (null != jLocation) {

            var bus = Bus()

            bus.id = jLocation.getInt("id")
            bus.name = jLocation.getString("name")
            bus.drivename = jLocation.getString("drivername")
            bus.driverphone = jLocation.getString("driverphone")
            bus.rid = jLocation.getInt("rid")
            bus.clat = jLocation.getString("clat")
            bus.clng = jLocation.getString("clng")

            return bus
        }
        return null
    }

    override fun onPostExecute(result: Bus?) {
        super.onPostExecute(result)
        onLocationUpdateListener.onUpdateLocationSuccess(result!!)
    }

    fun setOnLocationUpdateListener(onLocationUpdateListener: OnLocationUpdateListener, id: Int) {
        this.onLocationUpdateListener = onLocationUpdateListener
        super.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, id)
    }

}