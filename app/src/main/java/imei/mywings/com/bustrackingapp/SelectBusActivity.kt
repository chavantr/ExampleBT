package imei.mywings.com.bustrackingapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import imei.mywings.com.bustrackingapp.joint.JointAdapter
import kotlinx.android.synthetic.main.activity_select_bus.*
import org.json.JSONObject
import android.R.id.message
import android.content.Intent


class SelectBusActivity : AppCompatActivity(), OnBusListener, OnSelectedBusListener {

    private lateinit var jointAdapter: JointAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_bus)
        lstSelect.layoutManager = LinearLayoutManager(this)
        getBuses()
    }

    override fun onBusSuccess(result: List<Bus>) {
        if (result.isNotEmpty()) {
            jointAdapter = JointAdapter(result)
            jointAdapter.setOnSelectedBusListener(this@SelectBusActivity)
            lstSelect.adapter = jointAdapter
        }
    }

    override fun onBusSelected(item: Bus) {
        val mIntent = Intent()
        mIntent.putExtra("id", item.id)
        setResult(RESULT_OK, mIntent)
        finish()
    }

    private fun getBuses() {
        val getVehicles = GetBus()
        getVehicles.setBusListener(this, JSONObject())
    }
}
