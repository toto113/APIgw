package foo;

import java.util.Arrays;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DynamicResourceIndicatorTest {

	private final Logger LOG = LoggerFactory.getLogger(this.getClass());
	
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
}
