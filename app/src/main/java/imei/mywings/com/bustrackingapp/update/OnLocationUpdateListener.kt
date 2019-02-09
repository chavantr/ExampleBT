package imei.mywings.com.bustrackingapp.update

import imei.mywings.com.bustrackingapp.Bus

interface OnLocationUpdateListener {
    fun onUpdateLocationSuccess(find: Bus)
}