package imei.mywings.com.bustrackingapp

import android.app.Application

class TrackGloabal : Application() {

    init {
        instance = this
    }

    companion object {
        private var instance: TrackGloabal? = null
        fun getInstance(): TrackGloabal {
            return instance as TrackGloabal
        }

        public var loginResult: LoginResult? = null
    }
}