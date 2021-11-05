package ru.kvaga.rss.feedaggrwebserver.junit;

import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ru.kvaga.rss.feedaggr.Exec;
import ru.kvaga.rss.feedaggr.objects.GUID;
import ru.kvaga.rss.feedaggr.objects.Item;
import ru.kvaga.rss.feedaggr.objects.RSS;
import ru.kvaga.rss.feedaggrwebserver.ServerUtils;
import ru.kvaga.rss.feedaggrwebserver.objects.user.CompositeUserFeed;
import ru.kvaga.rss.feedaggrwebserver.objects.user.User;
import ru.kvaga.rss.feedaggrwebserver.objects.user.UserFeed;
import ru.kvaga.rss.feedaggrwebserver.servlets.mergeRSSServlet;

class mergeRSSServletTest {
	final private static Logger log = LogManager.getLogger(mergeRSSServletTest.class);

	private static User user1, user2, user3, user4;
	//private static String userName = "user" + mergeRSSServletTest.class.toString();
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		JUnitUtils.serverInit();
		user1 = User.createUser();
		user2 = User.createUser();
		user3 = User.createUser();
		user4 = User.createUser();
		
		assertNotNull(user1);
		assertNotNull(user2);
		assertNotNull(user3);
		assertNotNull(user4);

	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		assertTrue(user1.deleteUser());
		assertTrue(user2.deleteUser());
		assertTrue(user3.deleteUser());
		assertTrue(user4.deleteUser());
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void mergeRSSServletExec() {
		try {
			mergeRSSServlet servlet = new mergeRSSServlet();
			
			/*
			// Case1: create composite with null feedIdList
			ArrayList<String> case1_feedIdList = null;
			String case1_compositeRSSTitle = "compositeRSSTitle";
			String case1Result = servlet.mergeRSSServletExec(null, case1_compositeRSSTitle, user1.getName(), case1_feedIdList); 
			String case1ExpectedResult = Exec.getHTMLSuccessText("Composite feed ["+case1_compositeRSSTitle+"] successfully created and updated with new feed ids ["+ServerUtils.getStringFromArrayListWithItemsDelimeteredByComma(case1_feedIdList))+"]<br>";
			log.debug("Case1 result:          " + case1Result);
			log.debug("Case1 expected result: " + case1ExpectedResult);
			Assert.assertTrue(
					case1Result
					.contains(case1ExpectedResult)
			);
					
			
			// Case2: create composite with real feedIdList
			String case2_compositeRSSTitle = "compositeRSSTitle2";
			ArrayList<String> case2_feedIdList = new ArrayList<String>();
			int case2_countOfFeeds=3;

				// create feeds
			for(int i=0; i<case2_countOfFeeds; i++) {
				UserFeed uf = JUnitUtils.createUserFeedTestAndBindToUser(user2.getName());
				assertNotNull(uf);
				RSS rss = RSS.getRSSObjectByFeedId(uf.getId());
				Item item = new Item();
				item.setPubDate(ServerUtils.getDateSinceToday(-5));
				rss.getChannel().getItem().add(item);
				case2_feedIdList.add(uf.getId());
			}
			String case2Result = servlet.mergeRSSServletExec(null, case2_compositeRSSTitle, user2.getName(), case2_feedIdList); 
			String case2ExpectedResult = Exec.getHTMLSuccessText("Composite feed ["+case2_compositeRSSTitle+"] successfully created and updated with new feed ids ["+ServerUtils.getStringFromArrayListWithItemsDelimeteredByComma(case2_feedIdList))+"]<br>";
			log.debug("Case2 result:          " + case2Result);
			log.debug("Case2 expected result: " + case2ExpectedResult);
			
			Assert.assertTrue(
					case2Result
					.contains(case2ExpectedResult)
			);
			*/
			
			// Case3: adding to existed compositeFeed
			String case3_userName=user3.getName();
			String case3_compositeRSSTitleOld="compositeRSSTitle";
			String case3_compositeRSSTitleNew="RSSTitle New";
			CompositeUserFeed case3_cuf = CompositeUserFeed.createCompositeRSS(case3_userName, case3_compositeRSSTitleOld);
			String case3_compositeFeedId=case3_cuf.getId();
			
			ArrayList<String> case3_feedIdList = new ArrayList<String>();
			ArrayList<String> case3_feedIdListToSave= new ArrayList<String>();
			ArrayList<String> case3_feedIdListToDelete= new ArrayList<String>();

			
			int case3_countOfFeeds = 10;
			// create feeds
			for(int i=0; i<case3_countOfFeeds; i++) {
				UserFeed uf = JUnitUtils.createUserFeedTestAndBindToUser(user3.getName());
				assertNotNull(uf);
				RSS rss = RSS.getRSSObjectByFeedId(uf.getId());
				Item item = new Item();
				item.setPubDate(ServerUtils.getDateSinceToday(-5));
				rss.getChannel().getItem().add(item);
				case3_feedIdList.add(uf.getId());
				if(i%2==0) {
					case3_feedIdListToSave.add(uf.getId());
				}else {
					case3_feedIdListToDelete.add(uf.getId());
				}
			}
			String case3Result1 = servlet.mergeRSSServletExec(case3_compositeFeedId, case3_compositeRSSTitleNew, case3_userName, case3_feedIdList); 
			String case3ExpectedResult1CheckForAddingNewlyAddedFeedIdsToACompositeFeed = Exec.getHTMLSuccessText("Composite feed ["+case3_compositeRSSTitleNew+"] successfully updated with new feed ids ["+ServerUtils.getStringFromArrayListWithItemsDelimeteredByComma(case3_feedIdList)+"]<br>"); 
			log.debug("Case3 result:          " + case3Result1);
          
			// Check for adding newly added feed ids to a composite feed
			log.debug("Case3 expected result1.1: " + case3ExpectedResult1CheckForAddingNewlyAddedFeedIdsToACompositeFeed);
			Assert.assertTrue(
					case3Result1
					.contains(case3ExpectedResult1CheckForAddingNewlyAddedFeedIdsToACompositeFeed)
			);
			
			
			// Check for update RSS title
			String case3ExpectedResult2CheckForUpdateRSSTitle = Exec.getHTMLSuccessText("Composite feed's title was changed to the ["+case3_compositeRSSTitleNew+"]<br>");
			log.debug("Case3 expected result1.2: " + case3ExpectedResult2CheckForUpdateRSSTitle);
			Assert.assertTrue(
					case3Result1
					.contains(case3ExpectedResult2CheckForUpdateRSSTitle)
			);
			
			// Check for deleted feeds from composite feed
//			ArrayList<String> deletedFeedIdsFromCompositeFeed = new ArrayList<String>() {{
//				add("");
//			}};
			String case3Result2 = servlet.mergeRSSServletExec(case3_compositeFeedId, case3_compositeRSSTitleNew, case3_userName, case3_feedIdListToSave); 

			String case3ExpectedCheckForDeletedFeedsFromCompositeFeed = Exec.getHTMLSuccessText("Deleted ["+ServerUtils.getStringFromArrayListWithItemsDelimeteredByComma(case3_feedIdListToDelete)+"] feed ids from the composite feed ["+case3_compositeRSSTitleNew+"]<br>");
			log.debug("Case3 expected result2: " + case3ExpectedCheckForDeletedFeedsFromCompositeFeed);
			Assert.assertTrue(
					case3Result2
					.contains(case3ExpectedCheckForDeletedFeedsFromCompositeFeed)
			);
			
			/*
			//Case4: update and set null list in the composite
			String case4_compositeFeedId="";
			String case4_compositeRSSTitle="RSSTitle New";
			String case4_userName=user4.getName();
			ArrayList<String> case4_feedIdList = null;
			String case4Result = servlet.mergeRSSServletExec(case4_compositeFeedId, case4_compositeRSSTitle, case4_userName, case4_feedIdList);
			Assert.assertTrue(
					case4Result
					.contains(Exec.getHTMLSuccessText("Composite feed ["+case4_compositeRSSTitle+"] successfully updated with new feed ids ["+ServerUtils.getStringFromArrayListWithItemsDelimeteredByComma(case4_feedIdList)+"]<br>"))
			);
			*/
		}catch(Exception e) {
			fail("Exception on mergeRSSServletExec", e);
			e.printStackTrace();
		}
	}

	void createCompositeFeedAndAddNewFeeds() {
		log.debug("User user ["+user1.getName()+"] for test CreateCompositeFeedAndAddNewFeeds");
		String compositeRSSTitle = "title_" + user1.getName();
		try {
			CompositeUserFeed cuf = CompositeUserFeed.createCompositeRSS(user1.getName(), compositeRSSTitle);
			assertNotNull(cuf);
			assertNotNull(cuf.getId());
			assertNotNull(cuf.getFeedIds());
			assertEquals(cuf.getFeedIds().size(), 0);
			assertTrue(CompositeUserFeed.deleteCompositeRSS(user1.getName(), cuf.getId()));
		} catch (Exception e) {
			fail("Exception on createCompositeFeedAndAddNewFeeds", e);
			e.printStackTrace();
		}
	}
	
	
//	@Test
//	void testServiceHttpServletRequestHttpServletResponse() {
//		fail("Not yet implemented");
//		// создать новый пустой compose
//		
//		// добавить в существующий пустой compose новые feed ids и при этом для новых feed items выставить текущую pubDate
//		
//		// добавить в существующий НЕ пустой compose новые feed ids и при этом для новых feed items выставить текущую pubDate
//		// при этом старые items не должны изменить pubDate или удалиться
//		
//		// проверка удаление старых items 
//		
//		// убавление из существующего компоуза определенных feed ids
//		
//		// удаление компоуза
//			// самого файла
//			// из файла user
//		
//		// обновление когда решулярный джоб работает
//
//	}

}
