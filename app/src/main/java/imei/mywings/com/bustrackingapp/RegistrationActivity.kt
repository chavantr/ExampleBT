package imei.mywings.com.bustrackingapp


import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import imei.mywings.com.bustrackingapp.update.EvaluateAsync
import imei.mywings.com.bustrackingapp.update.OnListenListener
import kotlinx.android.synthetic.main.activity_registration.*
import org.json.JSONObject

class RegistrationActivity : AppCompatActivity(), OnRegistrationListener, OnListenListener {

    private var flag: Boolean = false
    private var vid: String = ""
    private var vname: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        initEvaluation()

        btnSignUp.setOnClickListener {
            if (validate()) {
                var loginTask = RegistrationTask()
                var jFirst = JSONObject()
                var jsonLogin = JSONObject()
                jsonLogin.put("name", txtName.text)
                jsonLogin.put("email", txtEmail.text)
                jsonLogin.put("phone", txtPhone.text)
                jsonLogin.put("password", txtPassword.text)
                jsonLogin.put("username", "")
                jsonLogin.put("extra", vid)
                jsonLogin.put("extra1", vname)
                jFirst.put("registration", jsonLogin)
                loginTask.setRegistrationListener(this, jFirst)
            }
        }

        lblSelect.setOnClickListener {
            val intent = Intent(this@RegistrationActivity, SelectBusActivity::class.java)
            startActivityForResult(intent, 1001)
        }

    }

    private fun initEvaluation() {
        val evaluateAsync = EvaluateAsync()
        evaluateAsync.setOnListenListener(this)
    }

    private fun validate(): Boolean {
        return when {
            txtEmail.text!!.isEmpty() -> false
            txtName.text!!.isEmpty() -> false
            txtPassword.text!!.isEmpty() -> false
            txtPhone.text!!.isEmpty() -> false
            txtPassword.text!!.isEmpty() -> false
            else -> true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == RESULT_OK) {
            if (requestCode == 1001) {
                vid = data!!.extras.getInt("id").toString()
                vname = data!!.extras.getString("vname")
                lblSelect.text = vname
            }
        }

    }

    override fun onRegistrationSuccess(result: Int) {
        progressBar.visibility = View.GONE
        if (result > 0 && flag) {
            var snack = Snackbar.make(btnSignUp, "Registration complete", Snackbar.LENGTH_INDEFINITE).setAction("Ok") {
                finish()
            }
            snack.show()
        } else {
            var snack = Snackbar.make(btnSignUp, "Error occurred", Snackbar.LENGTH_INDEFINITE).setAction("Ok") {

            }
            snack.show()
        }
    }

    override fun onListenSuccess(result: Boolean) {
        flag = result
    }
}
