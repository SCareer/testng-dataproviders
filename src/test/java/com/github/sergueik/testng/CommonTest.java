package com.github.sergueik.testng;
/**
 * Copyright 2019 Serguei Kouzmine
 */

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import org.testng.IAttributes;
import org.testng.ITestContext;
import org.testng.TestRunner;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;

// https://www.programcreek.com/java-api-examples/org.testng.Assert
import org.testng.Assert;

//NOTE: needed to switch to hamcrest-all.jar and Matchers 
//just for resolving method 'containsInAnyOrder'
import static org.hamcrest.Matchers.*;

import static org.hamcrest.MatcherAssert.assertThat;

public class CommonTest {

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

	protected void dataTest(String keyword, String strCount) {
		Assert.assertNotNull(keyword);
		// System.err.println("verifying keyword: " + keyword);
		Assert.assertTrue(keyword.matches("(?:junit|testng|spock)"));
		double count = Double.valueOf(strCount);
		Assert.assertTrue((int) count > 0);
		System.err.println(
				String.format("Search keyword:'%s'\tExpected minimum link count: %s",
						keyword, strCount));
	}

	protected void dataTestWithMethod(Method method, String keyword,
			double count) {
		System.err.println("Method name: " + method.getName() + " Parameter count: "
				+ method.getParameterCount());
		dataTest(keyword, count);
	}

	protected void dataTest(String keyword, double count) {
		Assert.assertNotNull(keyword);
		System.err.println("keyword: " + keyword);
		// NOTE: remove one of the alternatives e.g. the "whatever" to trigger
		// assertionError
		Assert.assertTrue(keyword.matches("(?:junit|testng|spock|whatever)"));
		/*
		Object[] expected = new Object[] { "junit", "testng", "spock" };
		HashSet<Object> resultHashset = new HashSet<Object>();
		resultHashset.add(keyword);
		assertThat(resultHashset, containsInAnyOrder(expected));
		*/
		// NOTE: remove one of the alternatives e.g. the "whatever" to trigger
		// assertionError
		assertThat(keyword,
				org.hamcrest.Matchers.isOneOf("junit", "testng", "spock", "whatever"));
		// NOTE: remove one of the alternatives e.g. the "whatever" to trigger
		// assertionError

		// NOTE: change to greater to trigger assertionError
		Assert.assertTrue(((int) count >= 0));
		System.err.println(
				String.format("Search keyword:'%s'\tExpected minimum link count:%d",
						keyword, (int) count));
	}

}
