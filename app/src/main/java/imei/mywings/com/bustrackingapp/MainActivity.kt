package imei.mywings.com.bustrackingapp

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import imei.mywings.com.bustrackingapp.routes.UserDataHelper
import imei.mywings.com.bustrackingapp.update.EvaluateAsync
import imei.mywings.com.bustrackingapp.update.OnListenListener
import kotlinx.android.synthetic.main.activity_main.*

import org.json.JSONObject

class MainActivity : AppCompatActivity(), OnLoginListener, OnListenListener {


    private var flag: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initEvaluation()

        btnSignUp.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }

        btnSignIn.setOnClickListener {
            //txtUserName.setText("test@test.com")
            //txtPassword.setText("test")
            login()
        }

    }

    private fun login() {
        if (validate()) {
            progressBar.visibility = View.VISIBLE
            var jFirst = JSONObject()
            var jLogin = JSONObject()
            jLogin.put("email", txtUserName.text)
            jLogin.put("password", txtPassword.text)
            jFirst.put("login", jLogin)
            val loginTask = LoginTask()
            loginTask.setLoginListener(this, jFirst)
        } else {
            Snackbar.make(btnSignIn, "Enter username or password", Snackbar.LENGTH_LONG).show()
        }
    }

    private fun validate(): Boolean {
        return when {
            txtUserName.text!!.isEmpty() -> false
            txtPassword.text!!.isEmpty() -> false
            else -> true
        }
    }

    private fun initEvaluation() {
        val evaluateAsync = EvaluateAsync()
        evaluateAsync.setOnListenListener(this)
    }

    override fun onLoginSuccess(loginResult: LoginResult) {
        progressBar.visibility = View.GONE
        if (null != loginResult && loginResult.id!! > 0 && flag) {
            UserDataHelper.getInstance().loginResult = loginResult
            val intent = Intent(this, TrackerDashboardWithMenu::class.java)
            startActivity(intent)
        } else {
            var snack = Snackbar.make(btnSignIn, "Login failed,try again", Snackbar.LENGTH_INDEFINITE)
            snack.setAction("Ok") {
                login()
            }
            snack.show()
        }
    }

    override fun onListenSuccess(result: Boolean) {
        flag = result
    }
}
