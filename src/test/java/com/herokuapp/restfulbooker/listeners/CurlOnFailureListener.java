package com.herokuapp.restfulbooker.listeners;

import com.herokuapp.restfulbooker.utils.CurlLoggingFilter;
import io.qameta.allure.Allure;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

public class CurlOnFailureListener implements ITestListener {
    @Override
    public void onTestFailure(ITestResult result) {
        String curl = CurlLoggingFilter.getLastCurl();
        if(curl == null) return;

        System.out.println( "Repo curl for " + result.getName() +  ":\n" + curl);
        Allure.addAttachment("Repo curl", "text/plain",
                new ByteArrayInputStream(curl.getBytes(StandardCharsets.UTF_8)),".txt");

    }
}
