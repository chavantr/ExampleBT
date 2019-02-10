package imei.mywings.com.bustrackingapp

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import imei.mywings.com.bustrackingapp.joint.JointAdapter
import imei.mywings.com.bustrackingapp.update.EvaluateAsync
import imei.mywings.com.bustrackingapp.update.OnListenListener
import kotlinx.android.synthetic.main.activity_select_bus.*
import org.json.JSONObject


class SelectBusActivity : AppCompatActivity(), OnBusListener, OnSelectedBusListener, OnListenListener {

    private lateinit var jointAdapter: JointAdapter

   // private var flag: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_bus)
        lstSelect.layoutManager = LinearLayoutManager(this)
        //initEvaluation()
        getBuses()
    }

    override fun onBusSuccess(result: List<Bus>) {
        if (result.isNotEmpty() ) {
            jointAdapter = JointAdapter(result)
            jointAdapter.setOnSelectedBusListener(this@SelectBusActivity)
            lstSelect.adapter = jointAdapter
        }
    }

    private fun initEvaluation() {
        val evaluateAsync = EvaluateAsync()
        evaluateAsync.setOnListenListener(this)
    }

    override fun onListenSuccess(result: Boolean) {
       // flag = result
    }

    override fun onBusSelected(item: Bus) {
      ////  if (flag) {
            val mIntent = Intent()
            mIntent.putExtra("id", item.id)
            setResult(RESULT_OK, mIntent)
            finish()
      //  }
    }

    private fun getBuses() {
        val getVehicles = GetBus()
        getVehicles.setBusListener(this, JSONObject())
    }
}
