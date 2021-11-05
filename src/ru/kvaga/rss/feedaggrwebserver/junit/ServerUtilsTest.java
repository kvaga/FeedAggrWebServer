package ru.kvaga.rss.feedaggrwebserver.junit;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;

import javax.xml.bind.JAXBException;

import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.apache.logging.log4j.LogManager;
import junit.framework.TestCase;
import ru.kvaga.rss.feedaggrwebserver.ConfigMap;
import ru.kvaga.rss.feedaggrwebserver.ServerUtils;
import ru.kvaga.rss.feedaggrwebserver.StartStopListener;
import ru.kvaga.rss.feedaggrwebserver.monitoring.MonitoringUtils;
import ru.kvaga.rss.feedaggrwebserver.objects.user.User;
import ru.kvaga.rss.feedaggrwebserver.objects.user.UserFeed;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ServerUtilsTest{
	static {
		MonitoringUtils.disable();
	}
	private static String userName=JUnitConfigMap.user1Name;
	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
	private final PrintStream originalOut = System.out;
	private final PrintStream originalErr = System.err;
	
	public static void main(String args[]) throws JAXBException {
		StartStopListener ssl = new StartStopListener();
		ssl.contextInitialized(null);
		System.out.println("file : " + ConfigMap.usersPath.getAbsoluteFile() + File.separator + userName + ".xml");
		File userFile = new File(ConfigMap.usersPath.getAbsoluteFile() + File.separator + userName + ".xml");
		System.out.println("exist: " + userFile.exists());
		User user = User.createUser(userName);

	}
	String getTextFromException(Exception e) {
		e.printStackTrace();
		return "Exception: " + e.getMessage() + ", cause: " + e.getCause();
	}
	@Before
	public void setUpStreams() {
	    System.setOut(new PrintStream(outContent));
	    System.setErr(new PrintStream(errContent));
	}

	@After
	public void restoreStreams() {
	    System.setOut(originalOut);
	    System.setErr(originalErr);
	}
	public ServerUtilsTest() {
		super();
	}
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		JUnitUtils.serverInit();
		User user = User.createUser(userName);
//		MonitoringUtils.disable();
		assertNotNull(user);
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		//User user = User.getXMLObjectFromXMLFileByUserName(userName);
		assertTrue(User.deleteUser(userName));
	}

	@BeforeEach
	void setUp() throws Exception {
		
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testGetNewFeedId() {
	     assertNotNull(ServerUtils.getNewFeedId());
	}

	
	
//	@Test
//	void testClearSessionFromFeedAttributes() {
//		fail("Not yet implemented");
//	}
//
	@Test
	void testGetUserFeedListByUser() {
		try {
			ArrayList<UserFeed> userFeedList = ServerUtils.getUserFeedListByUser(userName);
			assertNotNull(userFeedList);
			assertNotEquals(userFeedList.size(), 0);
		} catch (Exception e) {
			System.err.println(getTextFromException(e));
			fail(getTextFromException(e));
		}
	}
//
//	@Test
//	void testGetFeedsList() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testGetFeedById() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testGetFeedByUserAndId() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testEscapeHTML() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testStringToHTMLString() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testGetSessionAttribute() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testEncodeString() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testConvertStringToUTF8() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testUpdateCompositeRSSStringStringStringArrayListOfStringBoolean() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testUpdateCompositeRSSStringStringStringArrayListOfString() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testCreateCompositeRSSStringStringArrayListOfString() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testCreateCompositeRSSStringStringStringArrayListOfStringBoolean() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testUpdateCompositeRSSFilesOfUser() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testMergeRSS() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testAddRSSFeedByURLAutomaticlyStringStringStringHashMapOfStringStringLong() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testAddRSSFeedByURLAutomaticlyStringStringString() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testAddRSSFeedByURLAutomaticlyStringString() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testAddRSSFeedByURLAutomaticlyStringStringHashMapOfStringStringLong() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testGetUserFileByLogin() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testGetRssFeedFileByFeedId() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testGetDateSinceToday() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testDeleteOldFeedItems() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testObject() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testGetClass() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testHashCode() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testEquals() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testClone() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testToString() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testNotify() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testNotifyAll() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testWait() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testWaitLong() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testWaitLongInt() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testFinalize() {
//		fail("Not yet implemented");
//	}

	
}
