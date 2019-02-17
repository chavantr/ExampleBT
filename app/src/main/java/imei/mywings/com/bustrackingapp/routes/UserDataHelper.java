package imei.mywings.com.bustrackingapp.routes;

import imei.mywings.com.bustrackingapp.LoginResult;

public class UserDataHelper {


    private LoginResult loginResult;


    public static UserDataHelper getInstance() {
        return InnerUserDataHelper.INSTANCE;
    }

    public LoginResult getLoginResult() {
        return loginResult;
    }

    public void setLoginResult(LoginResult loginResult) {
        this.loginResult = loginResult;
    }

    private static class InnerUserDataHelper {

        static UserDataHelper INSTANCE = new UserDataHelper();

    }

}
