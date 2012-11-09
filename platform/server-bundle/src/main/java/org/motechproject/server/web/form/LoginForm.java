package org.motechproject.server.web.form;

/**
 * Created with IntelliJ IDEA.
 * User: lukasz
 * Date: 30.10.12
 * Time: 15:31
 * To change this template use File | Settings | File Templates.
 */
public class LoginForm {
    private String userName;
    private String password;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
