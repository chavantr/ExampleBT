package imei.mywings.com.bustrackingapp

data class Route(
    var id: Int = 0,
    var routename: String = "",
    var source: String = "",
    var destination: String = "",
    var srclat: String = "",
    var srclng: String = "",
    var destlat: String = "",
    var destlng: String = ""
)