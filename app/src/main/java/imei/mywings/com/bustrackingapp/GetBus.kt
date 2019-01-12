package imei.mywings.com.bustrackingapp

import android.os.AsyncTask
import org.json.JSONArray
import org.json.JSONObject

class GetBus : AsyncTask<JSONObject, Void, List<Bus>>() {

    private var httpConnectionUtil = HttpConnectionUtil()

    private lateinit var onBusListener: OnBusListener

    override fun doInBackground(vararg param: JSONObject?): List<Bus> {

        var lstBuses = ArrayList<Bus>()

        var response = httpConnectionUtil.requestPost(Constants.URL + "GetBuses", param[0])

        var jsonArray = JSONArray(response)


        if (null != jsonArray && jsonArray.length() > 0)
            for (i in 0..(jsonArray.length() - 1)) {

                var node = jsonArray[i] as JSONObject

                var bus = Bus()

                bus.id = node.getInt("id")

                bus.name = node.getString("name")

                bus.drivename = node.getString("drivername")

                bus.driverphone = node.getString("driverphone")

                bus.clat = node.getString("clat")

                bus.clng = node.getString("clng")

                bus.rid = node.getInt("rid")

                lstBuses.add(bus)

            }

        return lstBuses

    }

    override fun onPostExecute(result: List<Bus>?) {
        super.onPostExecute(result)
        onBusListener.onBusSuccess(result!!)
    }

    fun setBusListener(onBusListener: OnBusListener, jsonObject: JSONObject) {
        this.onBusListener = onBusListener
        super.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, jsonObject)
    }
}