package com.herokuapp.restfulbooker.models.testdata;

import com.herokuapp.restfulbooker.models.request.AuthRequest;

public class AuthTestData {
    private AuthRequest validUser;
    private AuthRequest wrongPassword;
    private AuthRequest nonExistentUser;
    private AuthRequest missingPasswordField;
    private AuthRequest sqlInjectionUsername;

    public AuthRequest getValidUser() {
        return validUser;
    }

    public void setValidUser(AuthRequest validUser) {
        this.validUser = validUser;
    }

    public AuthRequest getWrongPassword() {
        return wrongPassword;
    }

    public void setWrongPassword(AuthRequest wrongPassword) {
        this.wrongPassword = wrongPassword;
    }

    public AuthRequest getNonExistentUser() {
        return nonExistentUser;
    }

    public void setNonExistentUser(AuthRequest nonExistentUser) {
        this.nonExistentUser = nonExistentUser;
    }

    public AuthRequest getMissingPasswordField() {
        return missingPasswordField;
    }

    public void setMissingPasswordField(AuthRequest missingPasswordField) {
        this.missingPasswordField = missingPasswordField;
    }

    public AuthRequest getSqlInjectionUsername() {
        return sqlInjectionUsername;
    }

    public void setSqlInjectionUsername(AuthRequest sqlInjectionUsername) {
        this.sqlInjectionUsername = sqlInjectionUsername;
    }
}
