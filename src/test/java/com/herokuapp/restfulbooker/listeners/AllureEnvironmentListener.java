package com.herokuapp.restfulbooker.listeners;

import com.herokuapp.restfulbooker.config.EnvironmentConfig;
import org.testng.ISuite;
import org.testng.ISuiteListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

// Ghi target/allure-results/environment.properties khi suite bắt đầu — Allure tự đọc file này
// để hiện lên widget "Environment" ở trang Overview. Không có file này, widget luôn trống rỗng,
// và khi mở lại report vài ngày sau sẽ không biết report đó chạy trên env nào / thread-count bao nhiêu.
public class AllureEnvironmentListener implements ISuiteListener {

    @Override
    public void onStart(ISuite suite) {
        Properties props = new Properties();
        props.setProperty("environment", EnvironmentConfig.getEnvironment());
        props.setProperty("base.url", EnvironmentConfig.getBaseUrl());
        props.setProperty("java.version", System.getProperty("java.version"));
        props.setProperty("parallel", String.valueOf(suite.getXmlSuite().getParallel()));
        props.setProperty("thread-count", String.valueOf(suite.getXmlSuite().getThreadCount()));

        File resultsDir = new File("target/allure-results");
        resultsDir.mkdirs();

        try (FileOutputStream out = new FileOutputStream(new File(resultsDir, "environment.properties"))) {
            props.store(out, null);
        } catch (IOException e) {
            // Không làm fail cả suite chỉ vì không ghi được metadata cho report
            System.err.println("Could not write Allure environment.properties: " + e.getMessage());
        }
    }
}
