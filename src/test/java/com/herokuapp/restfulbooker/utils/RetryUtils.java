package com.herokuapp.restfulbooker.utils;

import java.util.function.Supplier;

public class RetryUtils {
    public static void retryUntilSuccess(Supplier<Integer> action,
                                         int maxAttempts,
                                         int waitMs) {

        for (int i = 0; i < maxAttempts; i++) {
            if (action.get() == 200) break;
            if (i < maxAttempts - 1) {
                try {
                    Thread.sleep(waitMs);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
}