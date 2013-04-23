package com.kthcorp.radix.domain.service.routing;


import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;
import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;

import com.kthcorp.radix.domain.service.routing.RoutingMethodSelector.DynamicResourceIndicator;

public class RoutingMethodSelectorTest {

	private final Logger LOG = UuidViewableLoggerFactory.getLogger(this.getClass());
	
	private DynamicResourceIndicator indicator;
	
	class PathPair {
		String path;
		String expected;
		PathPair(String path, String expected) {
			this.path = path;
			this.expected = expected;
		}
	}
	
	@Test
	public void testPhotos() {
		
		indicator = new DynamicResourceIndicator(Arrays.asList("/photos/{photoid}"));
		List<PathPair> paths = Arrays.asList(
				new PathPair("/photos/tom", "/photos/{photoid}")
				,new PathPair("/photos/tom/", "/photos/{photoid}")
				,new PathPair("/photos/tom/apple", null)
				);
		for(PathPair path : paths) {
			String resolved = indicator.parsePath(path.path);
			LOG.info("{}->{}", path.path, resolved);
			Assert.assertEquals(path.expected, resolved);
		}
	}
	
	@Test
	public void testPhotosWithSlashEnd() {
		
		indicator = new DynamicResourceIndicator(Arrays.asList("/photos/{photoid}/"));
		List<PathPair> paths = Arrays.asList(
				new PathPair("/photos/tom", "/photos/{photoid}/")
				,new PathPair("/photos/tom/", "/photos/{photoid}/")
				,new PathPair("/photos/tom/apple", null)
				);
		for(PathPair path : paths) {
			String resolved = indicator.parsePath(path.path);
			LOG.info("{}->{}", path.path, resolved);
			Assert.assertEquals(path.expected, resolved);
		}
	}
	
	@Test
	public void testComments() {
		
		indicator = new DynamicResourceIndicator(Arrays.asList("/photos/comments"));
		List<PathPair> paths = Arrays.asList(
				new PathPair("/photos/comments", "/photos/comments")
				,new PathPair("/photos/comments/", "/photos/comments")
				,new PathPair("/photos/comments1234", null)
				,new PathPair("/photos/comments/1234", null)
				);
		for(PathPair path : paths) {
			String resolved = indicator.parsePath(path.path);
			LOG.info("{}->{}", path.path, resolved);
			Assert.assertEquals(path.expected, resolved);
		}
	}
	
	@Test
	public void testCommentId() {
		
		indicator = new DynamicResourceIndicator(Arrays.asList("/photos/{photoId}/comments/{commentId}"));
		List<PathPair> paths = Arrays.asList(
				new PathPair("/photos/1234/comments/apple", "/photos/{photoId}/comments/{commentId}")
				,new PathPair("/photos/1234/comments/apple/123", null)
				,new PathPair("/photos/1234/comment/apple", null)
				,new PathPair("/Photos/1234/Comments/apple", null)
				);
		for(PathPair path : paths) {
			String resolved = indicator.parsePath(path.path);
			LOG.info("{}->{}", path.path, resolved);
			Assert.assertEquals(path.expected, resolved);
		}
	}
	
	@Test
	public void testPhotosUserId() {
		
		indicator = new DynamicResourceIndicator(Arrays.asList("/photos/{photoId}/{userId}"));
		List<PathPair> paths = Arrays.asList(
				new PathPair("/photos/1234/apple", "/photos/{photoId}/{userId}")
				);
		for(PathPair path : paths) {
			String resolved = indicator.parsePath(path.path);
			LOG.info("{}->{}", path.path, resolved);
			Assert.assertEquals(path.expected, resolved);
		}
	}
	
	@Test
	public void testPhotoAndComment() {
		
		indicator = new DynamicResourceIndicator(Arrays.asList("/photos/{photoId}", "/photos/comments"));
		List<PathPair> paths = Arrays.asList(
				new PathPair("/photos/comment", "/photos/{photoId}")
				);
		for(PathPair path : paths) {
			String resolved = indicator.parsePath(path.path);
			LOG.info("{}->{}", path.path, resolved);
			Assert.assertEquals(path.expected, resolved);
		}
	}
	
	@Test
	public void testPhotoAndStaticComment() {
		
		indicator = new DynamicResourceIndicator(Arrays.asList("/photos/{photoId}", "/photos/comments"));
		List<PathPair> paths = Arrays.asList(
				new PathPair("/photos/comments", "/photos/comments")
				);
		for(PathPair path : paths) {
			String resolved = indicator.parsePath(path.path);
			LOG.info("{}->{}", path.path, resolved);
			Assert.assertEquals(path.expected, resolved);
		}
	}
	
	@Test
	public void testPhotoAndLocation() {
		
		indicator = new DynamicResourceIndicator(Arrays.asList("/photos/{photoId}/{location}", "/photos/comments/{commentId}"));
		List<PathPair> paths = Arrays.asList(
				new PathPair("/photos/comment/paris", "/photos/{photoId}/{location}")
				,new PathPair("/photos/comments/paris", "/photos/comments/{commentId}")
				,new PathPair("/photos/comments/paris/tom", null)
				);
		for(PathPair path : paths) {
			String resolved = indicator.parsePath(path.path);
			LOG.info("{}->{}", path.path, resolved);
			Assert.assertEquals(path.expected, resolved);
		}
	}
	
	@Test
	public void testPhotoAndLocationAndCommentAndName() {
		
		indicator = new DynamicResourceIndicator(Arrays.asList("/photos/{photoId}/{location}", "/photos/comments/{commentId}", "/photos/comments/{location}/{name}"));
		List<PathPair> paths = Arrays.asList(
				new PathPair("/photos/comments/paris/tom", "/photos/comments/{location}/{name}")
				);
		for(PathPair path : paths) {
			String resolved = indicator.parsePath(path.path);
			LOG.info("{}->{}", path.path, resolved);
			Assert.assertEquals(path.expected, resolved);
		}
	}
	
	@Test
	public void pathTemplate에_맞는_uri일때_처리_확인() {
		indicator = new DynamicResourceIndicator(Arrays.asList("/photos/{photoid}"));
		List<PathPair> paths = Arrays.asList(
				new PathPair("/photos/tom/INVALID", null)
				);
		for(PathPair path : paths) {
			String resolved = indicator.parsePath(path.path);
			LOG.info("{}->{}", path.path, resolved);
			Assert.assertEquals(path.expected, resolved);
		}
	}

	
	@Test
	public void uri가_null이거나_빈문자열_일때의_처리_확인() {
		indicator = new DynamicResourceIndicator(Arrays.asList("/photos/{photoid}"));
		List<PathPair> paths = Arrays.asList(
				new PathPair("", null),
				new PathPair(null, null)
				);
		for(PathPair path : paths) {
			try {
				String resolved = indicator.parsePath(path.path);
				LOG.info("{}->{}", path.path, resolved);
				Assert.assertEquals(path.expected, resolved);
			} catch(Throwable e) {
				e.printStackTrace();
				fail("processing path failed. e="+e);
			}
		}
	}
	

	@Test
	public void pathTemplate이_빈문자열일때의_예외처리_확인() {
		indicator = new DynamicResourceIndicator(Arrays.asList(""));
		List<PathPair> paths = Arrays.asList(
				new PathPair("/photos/tom", null)
				);
		for(PathPair path : paths) {
			try {
				String resolved = indicator.parsePath(path.path);
				LOG.info("{}->{}", path.path, resolved);
				Assert.assertEquals(path.expected, resolved);
			} catch(Throwable e) {
				e.printStackTrace();
				fail("processing path failed. e="+e);
			}
		}
	}

	
	@Test
	public void 전달되는_pathTemplate_배열이_null일_경우_예외처리_확인() {
		try {
			indicator = new DynamicResourceIndicator(null);
		} catch(Throwable e) {
			e.printStackTrace();
			fail("initializing DynamicResourceIndicator failed. e="+e);
		}
		
		List<PathPair> paths = Arrays.asList(
				new PathPair("/photos/tom", null)
				);
		for(PathPair path : paths) {
			try {
				String resolved = indicator.parsePath(path.path);
				LOG.info("{}->{}", path.path, resolved);
				Assert.assertEquals(path.expected, resolved);
			} catch(Throwable e) {
				e.printStackTrace();
				fail("processing path failed. e="+e);
			}
		}
	}
	
	
	@Test
	public void 전달되는_pathTemplate_중의_하나가_null일_경우_예외처리_확인() {
		try {
			indicator = new DynamicResourceIndicator(Arrays.asList("/photos/{photoid}", null));
		} catch(Throwable e) {
			e.printStackTrace();
			fail("initializing DynamicResourceIndicator failed. e="+e);
		}
		
		List<PathPair> paths = Arrays.asList(
				new PathPair("/photos/tom", "/photos/{photoid}")
				);
		for(PathPair path : paths) {
			try {
				String resolved = indicator.parsePath(path.path);
				LOG.info("{}->{}", path.path, resolved);
				Assert.assertEquals(path.expected, resolved);
			} catch(Throwable e) {
				e.printStackTrace();
				fail("processing path failed. e="+e);
			}
		}
	}
	
	
	@Test
	@Ignore	// FIXME : 여러개의 pathTemplate이 매칭이 되는 경우에는 path template이 가장 많은 것을 채택해야 한다.
	public void 하나의_uri가_복수의_pathTemplate에_매칭될때의_처리_확인() {
		
		indicator = new DynamicResourceIndicator(Arrays.asList("/map/a/b/c", "/map/{a}/b/c/", "/map/{a}/{b}/c/", "/map/{a}/{b}/{c}/"));
		List<PathPair> paths = Arrays.asList(
				new PathPair("/map/a/b/c", "/map/{a}/{b}/{c}/")
				);
		for(PathPair path : paths) {
			String resolved = indicator.parsePath(path.path);
			LOG.info("{}->{}", path.path, resolved);
			Assert.assertEquals(path.expected, resolved);
		}
	}
	
	
	@Test
	public void pathTemplate에_물음표가_포함된_경우_처리_확인() {
		
		indicator = new DynamicResourceIndicator(Arrays.asList("/photos/{photoid}?some=other"));
		List<PathPair> paths = Arrays.asList(
				new PathPair("/photos/tom", "/photos/{photoid}")
				,new PathPair("/photos/tom/", "/photos/{photoid}")
				,new PathPair("/photos/tom/apple", null)
				);
		for(PathPair path : paths) {
			String resolved = indicator.parsePath(path.path);
			LOG.info("{}->{}", path.path, resolved);
			Assert.assertEquals(path.expected, resolved);
		}
	}
	
}
