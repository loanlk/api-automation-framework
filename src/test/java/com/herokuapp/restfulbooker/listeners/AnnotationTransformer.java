package com.herokuapp.restfulbooker.listeners;

import org.testng.IAnnotationTransformer;

public class AnnotationTransformer implements IAnnotationTransformer{
    @Override
    public void transform(org.testng.annotations.ITestAnnotation annotation, Class testClass, java.lang.reflect.Constructor testConstructor, java.lang.reflect.Method testMethod) {
        annotation.setRetryAnalyzer(RetryAnalyzer.class);
    }

}
