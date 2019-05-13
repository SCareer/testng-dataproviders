package com.github.sergueik.testng;
/**
 * Copyright 2017,2018 Serguei Kouzmine
 */

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import org.testng.IAttributes;
import org.testng.ITestContext;
import org.testng.TestRunner;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

// https://www.programcreek.com/java-api-examples/org.testng.Assert
import org.testng.Assert;

public class JSONProviderTest {

	@Test(enabled = true, singleThreaded = false, threadPoolSize = 1, invocationCount = 1, description = "# of articless for specific keyword", dataProvider = "JSON", dataProviderClass = JSONParametersProvider.class)
	@JSONDataFileParameters(name = "data.json", dataKey = "test", columns = "keyword,count"
	/* columns attribute should not be empty */)
	public void test_with_JSON(String searchKeyword, String expectedCount)
			throws InterruptedException {
		dataTest(searchKeyword, expectedCount);
	}

	// Method JSONProviderTest.test_with_static_JSON_missed_parameter_order should
	// throw an exception of type class java.lang.AssertionError
	@Test(enabled = true, dataProvider = "static disconnected data provider", expectedExceptions = java.lang.AssertionError.class)
	public void test_with_static_JSON_missed_parameter_order(Object expectedCount,
			Object searchKeyword) throws InterruptedException {
		dataTest(searchKeyword.toString(), expectedCount.toString());
	}

	@Test(enabled = true, dataProvider = "static disconnected data provider")
	public void test_with_Static_JSON(Object searchKeyword, Object expectedCount)
			throws InterruptedException {
		dataTest(searchKeyword.toString(), expectedCount.toString());
	}

	// NOTE: cannot change signature of the method to include annotation:
	// handleTestMethodInformation(final ITestContext context, final Method
	// method, IDataProviderAnnotation annotation )
	// runtime TestNGException:
	// Method handleTestMethodInformation requires 3 parameters but 0 were
	// supplied in the @Configuration annotation.
	@BeforeMethod
	public void handleTestMethodInformation(final ITestContext context,
			final Method method) {
		final String suiteName = context.getCurrentXmlTest().getSuite().getName();
		final String methodName = method.getName();
		final String testName = context.getCurrentXmlTest().getName();

		System.err.println("BeforeMethod Suite: " + suiteName);
		System.err.println("BeforeMethod Test: " + testName);
		System.err.println("BeforeMethod Method: " + methodName);
		// String dataProvider = ((IDataProvidable)annotation).getDataProvider();
		// System.err.println("Data Provider: " + dataProvider);
		@SuppressWarnings("deprecation")
		final Map<String, String> parameters = (((TestRunner) context).getTest())
				.getParameters();
		final Set<String> keys = parameters.keySet();
		for (String key : keys) {
			System.err.println(
					"BeforeMethod Parameter: " + key + " = " + parameters.get(key));
		}
		final Set<java.lang.String> attributeNames = ((IAttributes) context)
				.getAttributeNames();
		if (attributeNames.size() > 0) {
			for (String attributeName : attributeNames) {
				System.err.print("BeforeMethod Attribute: " + attributeName + " = "
						+ ((IAttributes) context).getAttribute(attributeName));
			}
		}
	}

	@AfterClass(alwaysRun = true)
	public void cleanupSuite() {
	}

	// static disconnected data provider
	@DataProvider(parallel = true, name = "static disconnected data provider")
	public Object[][] dataProviderInline() {
		return new Object[][] { { "junit", 100.0 }, { "testng", 30.0 },
				{ "spock", 10.0 }, };
	}

	private void dataTest(String keyword, String strCount) {
		Assert.assertNotNull(keyword);
		// System.err.println("verifying keyword: " + keyword);
		Assert.assertTrue(keyword.matches("(?:junit|testng|spock)"));
		double count = Double.valueOf(strCount);
		Assert.assertTrue((int) count > 0);
		System.err.println(
				String.format("Search keyword:'%s'\tExpected minimum link count: %s",
						keyword, strCount));
	}

}
