package imei.mywings.com.bustrackingapp

import android.os.AsyncTask
import org.json.JSONArray
import org.json.JSONObject

class GetRoute : AsyncTask<JSONObject, Void, ArrayList<Route>>() {

    private var httpConnectionUtil = HttpConnectionUtil()

    private lateinit var onRouteListener: OnRouteListener


    override fun doInBackground(vararg param: JSONObject?): ArrayList<Route> {

        var lstRoutes = ArrayList<Route>()

        var response = httpConnectionUtil.requestPost(Constants.URL + "GetRoutes", param[0])

        var jsonArray = JSONArray(response)

        for (i in 0..(jsonArray.length() - 1)) {

            var node = jsonArray[i] as JSONObject

            var route = Route()

            route.id = node.getInt("id")

            route.routename = node.getString("routename")

            route.source = node.getString("source")

            route.destination = node.getString("destination")

            route.srclat = node.getString("srclat")

            route.srclng = node.getString("srclng")

            route.destlat = node.getString("destlat")

            route.destlng = node.getString("destlng")

            lstRoutes.add(route)

        }

        return lstRoutes

    }

    override fun onPostExecute(result: ArrayList<Route>?) {
        super.onPostExecute(result)
        onRouteListener.onRouteSuccess(result!!)
    }

    fun setRouteListener(routeListener: OnRouteListener, jsonObject: JSONObject) {
        this.onRouteListener = routeListener
        super.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, jsonObject)
    }


}