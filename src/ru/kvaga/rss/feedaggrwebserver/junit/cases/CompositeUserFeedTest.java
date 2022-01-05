package ru.kvaga.rss.feedaggrwebserver.junit.cases;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;
import javax.xml.bind.JAXBException;

import org.apache.catalina.connector.Connector;
import org.apache.catalina.connector.Request;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ru.kvaga.rss.feedaggr.objects.GUID;
import ru.kvaga.rss.feedaggr.objects.Item;
import ru.kvaga.rss.feedaggr.objects.RSS;
import ru.kvaga.rss.feedaggrwebserver.ServerUtils;
import ru.kvaga.rss.feedaggrwebserver.junit.util.JUnitUtils;
import ru.kvaga.rss.feedaggrwebserver.objects.user.CompositeUserFeed;
import ru.kvaga.rss.feedaggrwebserver.objects.user.User;
import ru.kvaga.rss.feedaggrwebserver.objects.user.UserFeed;


class CompositeUserFeedTest {
	final private static Logger log = LogManager.getLogger(CompositeUserFeedTest.class);

	private static User user1, user2, user3, user4;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		JUnitUtils.serverInit();
//		user1 = User.createUser();
//		user2 = User.createUser();
//		user3 = User.createUser();
		user4 = User.createUser();
		
//		assertNotNull(user1);
//		assertNotNull(user2);
//		assertNotNull(user3);
		assertNotNull(user4);
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
//		assertTrue(user1.deleteUser());
//		assertTrue(user2.deleteUser());
//		assertTrue(user3.deleteUser());
//		assertTrue(user4.deleteUser());
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	/*
	@Test
	void testCreateCompositeFeedAndAddNewFeeds() {
		log.debug("User user ["+user1.getName()+"] for test testCreateCompositeFeedAndAddNewFeeds");
		String compositeRSSTitle = "title_" + user1.getName();
		try {
			CompositeUserFeed cuf = CompositeUserFeed.createCompositeRSS(user1.getName(), compositeRSSTitle);
			assertNotNull(cuf);
			assertNotNull(cuf.getId());
			assertNotNull(cuf.getFeedIds());
			assertEquals(cuf.getFeedIds().size(), 0);
			assertTrue(CompositeUserFeed.deleteCompositeRSS(user1.getName(), cuf.getId()));
		} catch (Exception e) {
			fail("Exception on testCreateCompositeFeedAndAddNewFeeds", e);
			e.printStackTrace();
		}
	}
	
	@Test
	void testAddNewFeedIds2ExistedCompositeFeed() {
		log.debug("Use user ["+user2.getName()+"] for test testAddNewFeedIds2ExistedCompositeFeed");

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH");
		String compositeRSSTitle = "title_" + user2.getName();
		ArrayList<String> feedIdList = new ArrayList<String>();
				
		try {
			int countOfFeeds=3;
			for(int i=0; i<countOfFeeds; i++) {
				UserFeed uf = JUnitUtils.createUserFeedTestAndBindToUser(user2.getName());
				assertNotNull(uf);
				RSS rss = RSS.getRSSObjectByFeedId(uf.getId());
				Item item = new Item();
				item.setPubDate(ServerUtils.getDateSinceToday(-5));
				rss.getChannel().getItem().add(item);
				feedIdList.add(uf.getId());
			}
			// Create a new Composite feed
			CompositeUserFeed cuf = CompositeUserFeed.createCompositeRSS(user2.getName(), compositeRSSTitle);
			// Add feedId with old pubDate
			Item itemWithOldPubDate = new Item();
			itemWithOldPubDate.setGuid(new GUID("false", GUID.generateGUID("itemWithOldPubDate")));
			Date oldPubDate = ServerUtils.getDateSinceToday(-25);
			itemWithOldPubDate.setPubDate(oldPubDate);
			RSS cufRSSForAddingItemWithOldPubDate = RSS.getRSSObjectByFeedId(cuf.getId());
			cufRSSForAddingItemWithOldPubDate.getChannel().getItem().add(itemWithOldPubDate);
			cufRSSForAddingItemWithOldPubDate.saveXMLObjectToFileByFeedId(cuf.getId());
			
			// Adding new Feeds with recent pubDates 
			CompositeUserFeed.addNewFeeds2CompositeFeed(cuf.getId(), feedIdList, user2.getName());

			// Checking composite user feed contains newly added feed ids
			User user = User.getXMLObjectFromXMLFileByUserName(user2.getName());
			for(String feedId : feedIdList) {
				assertTrue(user.getCompositeUserFeedById(cuf.getId()).getFeedIds().contains(feedId));
			}
			// Checking that item with oldPubDate has oldPubDate but newly added feeds with recent pubDates have the currentPubDate
			RSS cufRSS = RSS.getRSSObjectByFeedId(cuf.getId());
			assertEquals(cufRSS.getChannel().getItem().size(), countOfFeeds+1);
			for(Item item : cufRSS.getChannel().getItem()) {
				if(item.equals(itemWithOldPubDate)) {
					assertEquals(item.getPubDate(), oldPubDate);
				}else {
					assertEquals(sdf.format(item.getPubDate()), sdf.format(new Date()));
				}
			}
			
			// Delete Composite user feed
			assertTrue(CompositeUserFeed.deleteCompositeRSS(user2.getName(), cuf.getId()));
			 
			 // Delete Feeds from User and delete Feeds RSS files
			 for(String feedId : feedIdList) {
				 assertTrue(RSS.deleteRSSFile(feedId));
			 }
			 
		} catch (Exception e) {
			fail("Exception on testAddNewFeedIds2ExistedCompositeFeed", e);
			e.printStackTrace();
		}
	}

	@Test
	void testDeleteFeedIdsFromCompositeUserFeedDespiteFinalList() {
		ArrayList<String> feedIdsForDeletion = new ArrayList<String>();
		ArrayList<String> feedIdsForSaving = new ArrayList<String>();

		// Create a new Composite feed
		try {
			CompositeUserFeed cuf = CompositeUserFeed.createCompositeRSS(user3.getName(), "compositeRSSTitle");
			User userBeforeChanges = User.getXMLObjectFromXMLFileByUserName(user3.getName());

			for(int i=0; i<10; i++) {
				String feedId = ServerUtils.getNewFeedId();
				Thread.sleep(1);
				userBeforeChanges.getCompositeUserFeedById(cuf.getId()).getFeedIds().add(feedId);
				if(i%2==0) {
					feedIdsForDeletion.add(feedId);
				}else {
					feedIdsForSaving.add(feedId);
				}
			}
			userBeforeChanges.saveXMLObjectToFileByLogin();
			
			ArrayList<String> finalList = CompositeUserFeed.deleteFeedIdsFromCompositeUserFeedDespiteFinalList(user3.getName(), cuf.getId(), feedIdsForSaving);
			User afterChanges = User.getXMLObjectFromXMLFileByUserName(user3.getName());
			//Check that all feeds from the list feedIdsForDeletion were deleted
			assertEquals(finalList.size(), feedIdsForDeletion.size());
			assertEquals(finalList.size(), afterChanges.getCompositeUserFeedById(cuf.getId()).getFeedIds().size());
			for(String feedId : feedIdsForSaving) {
				assertTrue(afterChanges.getCompositeUserFeedById(cuf.getId()).getFeedIds().contains(feedId));
			}
			
			// Delete Composite user feed
			assertTrue(CompositeUserFeed.deleteCompositeRSS(user3.getName(), cuf.getId()));
		} catch (Exception e) {
			fail("Exception on testDeleteFeedIdsFromCompositeUserFeedDespiteFinalList", e);
			e.printStackTrace();
		}
	}

	*/
	@Test
	void testUpdateItemsInCompositeRSSFilesOfUser() {
		String userName =user4.getName();
		ArrayList<Item> itemsForDeletion = new ArrayList<Item>();
		ArrayList<Item> itemsForSaving = new ArrayList<Item>();
		ArrayList<String> clearRSSFeedIds = new ArrayList<String>();
		CompositeUserFeed cuf = null;
		int countOfAllFeeds = 10;
		try {
			cuf = CompositeUserFeed.createCompositeRSS(userName, "compositeRSSTitle");
			// reload user's information
			user4 = User.getXMLObjectFromXMLFileByUserName(userName);
			for(int i=0; i<countOfAllFeeds; i++) {
				UserFeed uf = JUnitUtils.createUserFeedTestAndBindToUser(userName);
				assertNotNull(uf);
				RSS rss = RSS.getRSSObjectByFeedId(uf.getId());
				Item item = JUnitUtils.createItem();
				item.setPubDate(ServerUtils.getDateSinceToday(-5));
				if(i%2==0) {
					item.setPubDate(ServerUtils.getDateSinceToday(-(new Random().nextInt(100 - 35) + 35)));
					itemsForDeletion.add(item);
				}else {
					itemsForSaving.add(item);
				}
				rss.getChannel().getItem().add(item);
				rss.saveXMLObjectToFileByFeedId(uf.getId());
				clearRSSFeedIds.add(uf.getId());
				user4.getCompositeUserFeedById(cuf.getId()).getFeedIds().add(uf.getId());
			}
			RSS cufRSS = RSS.getRSSObjectByFeedId(cuf.getId());
			for(Item item : itemsForDeletion) {
				cufRSS.getChannel().getItem().add(item);
			}
			cufRSS.saveXMLObjectToFileByFeedId(cuf.getId());
			user4.saveXMLObjectToFileByLogin();
			
			int result[] = CompositeUserFeed.updateItemsInCompositeRSSFilesOfUser(userName);
			assertEquals(1, result[0]); 
			assertEquals(1, result[1]);
			assertEquals(0, result[2]);
			assertEquals(itemsForDeletion.size(), result[3]);
		} catch (Exception e) {
			fail("Exception on testUpdateItemsInCompositeRSSFilesOfUser", e);
			e.printStackTrace();
		}finally {
//			for(String feedId : clearRSSFeedIds) {
//				try {
//					assertTrue(RSS.deleteRSSFile(feedId));
//				} catch (JAXBException e) {
//					fail("Exception on cleaning after testUpdateItemsInCompositeRSSFilesOfUser", e);
//					e.printStackTrace();
//				}
//			}
//			if(cuf!=null) {
//				try {
//					assertTrue(RSS.deleteRSSFile(cuf.getId()));
//				} catch (JAXBException e) {
//					fail("Exception on cleaning after testUpdateItemsInCompositeRSSFilesOfUser", e);
//					e.printStackTrace();
//				}
//			}
		}
	}

	
}
