package ru.kvaga.rss.feedaggrwebserver.junit.suites;

import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.*;
import org.junit.platform.suite.api.SuiteDisplayName;

import ru.kvaga.rss.feedaggrwebserver.junit.cases.mergeRSSServletTest;

@Suite
@SuiteDisplayName("JUnit Platform Suite Demo")
//@SelectPackages("example")
//@IncludeClassNamePatterns(".*Tests")
@SelectClasses({mergeRSSServletTest.class})
class AllUnitTest {
}