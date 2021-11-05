package ru.kvaga.rss.feedaggrwebserver.junit;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.catalina.connector.Connector;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ru.kvaga.rss.feedaggrwebserver.ServerUtils;
import ru.kvaga.rss.feedaggrwebserver.objects.user.User;
import ru.kvaga.rss.feedaggrwebserver.objects.user.UserFeed;
//import org.apache.ju
import ru.kvaga.rss.feedaggrwebserver.servlets.deleteFeed;

class deleteFeedTest {
	private static deleteFeed _deleteFeed;
	static User user = null;
	static String feedId=null;
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		JUnitUtils.serverInit();
		_deleteFeed = new deleteFeed();
		user = User.createUser("testUser_DeleteFeed");
		assertNotNull(user);
		Set<UserFeed> userFeeds = new HashSet<UserFeed>();
		UserFeed userFeed = JUnitUtils.createUserFeedTestAndBindToUser(user.getName());
		feedId = userFeed.getId();
		userFeeds.add(userFeed);
		user.setUserFeeds(userFeeds);
		File userFile = user.saveXMLObjectToFileByLogin();
		if(!userFile.exists()) {
			fail("File ["+userFile+"] doesn't exists");
			return;
		}
	}

	
	@AfterAll
	static void tearDownAfterClass() throws Exception {
		if(user!=null) {
			user.deleteUser();
		}
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testServiceHttpServletRequestHttpServletResponse() {
		PrintWriter out = new PrintWriter(System.out);
		StringBuilder sb = new StringBuilder();
		String userName = user.getName();
		String[] parameterValuesExisted = {
			feedId,
		};
		try {
			Thread.sleep(50000);
			// Existed
			HashMap<String, Boolean> res = _deleteFeed.delete(userName, parameterValuesExisted, sb, out);
			int successfull=0;
			for(String feedId : res.keySet()) {
				if(res.get(feedId)) {
					successfull++;
				}
			}
			Thread.sleep(5000);

			assertEquals(parameterValuesExisted.length, successfull);
			// NotExisted
			// ...
		} catch (Exception e) {
			e.printStackTrace();
			fail(e);
		}
	}

}
