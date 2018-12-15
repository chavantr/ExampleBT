package imei.mywings.com.bustrackingapp


import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_registration.*
import org.json.JSONObject

class RegistrationActivity : AppCompatActivity(), OnRegistrationListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

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
                jFirst.put("registration", jsonLogin)
                loginTask.setRegistrationListener(this, jFirst)
            }
        }
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

    override fun onRegistrationSuccess(result: Int) {
        if (result > 0) {
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
}
