package imei.mywings.com.bustrackingapp

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import imei.mywings.com.bustrackingapp.routes.UserDataHelper
import imei.mywings.com.bustrackingapp.update.OnUpdateProfileListener
import imei.mywings.com.bustrackingapp.update.UpdateProfileAsync
import kotlinx.android.synthetic.main.activity_profile.*
import org.json.JSONObject

class ProfileActivity : AppCompatActivity(), OnUpdateProfileListener {

    private val user = UserDataHelper.getInstance().loginResult
    private var vid: String = ""
    private var vname: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        txtName.setText(user.name)
        txtEmail.setText(user.email)
        txtPassword.setText(user.password)
        txtPhone.setText(user.phone)
        lblSelect.text = user.vname
        vname = user!!.vname!!
        vid = user!!.vid!!
        lblSelect.setOnClickListener {
            val intent = Intent(this@ProfileActivity, SelectBusActivity::class.java)
            startActivityForResult(intent, 1001)
        }
        btnSignUp.setOnClickListener {
            initUpdateProfile()
        }
    }

    private fun initUpdateProfile() {
        val updateProfileAsync = UpdateProfileAsync()
        var jFirst = JSONObject()
        var jsonLogin = JSONObject()
        jsonLogin.put("id", user.id)
        jsonLogin.put("name", txtName.text)
        jsonLogin.put("email", txtEmail.text)
        jsonLogin.put("phone", txtPhone.text)
        jsonLogin.put("password", txtPassword.text)
        jsonLogin.put("username", "")
        jsonLogin.put("extra", vid)
        jsonLogin.put("extra1", vname)
        jFirst.put("registration", jsonLogin)
        updateProfileAsync.setOnUpdateProfileListener(this, jFirst)
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

    override fun onUpdateProfileSuccess(result: String) {

        if (!result.isNullOrEmpty()) {
            var snack = Snackbar.make(btnSignUp, "Profile updated", Snackbar.LENGTH_INDEFINITE).setAction("Ok") {
                finish()
            }
            snack.show()
        } else {
            var snack = Snackbar.make(btnSignUp, "An error has occurred. try later", Snackbar.LENGTH_LONG)
                .setAction("Ok") {

                }
            snack.show()
        }

    }
}