package net.geant.nsi.contest.platform.validation;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;


public class FieldMatchValidatorTest {

	FieldMatchValidator validator;
	
	@FieldMatch(field="field1", matchWith="field2")
	public class TestForm {
		String field1;
		String field2;
		
		public TestForm(String field1, String field2) {
			this.field1 = field1;
			this.field2 = field2;
		}
		
		public String getField1() { return field1;}
		public String getField2() { return field2;}
	};
	
	@Before
	public void init() {
		validator = new FieldMatchValidator();
	}
	
	@Test
	public void testShouldMatch() {
		TestForm form = new TestForm("aaa", "aaa");
		
		validator.initialize(form.getClass().getAnnotation(FieldMatch.class));
		assertTrue(validator.isValid(form, null));
	}
	
	@Test
	public void testShouldNotMatch() {
		TestForm form = new TestForm("aaa", "aaaa");
		
		validator.initialize(form.getClass().getAnnotation(FieldMatch.class));
		assertFalse(validator.isValid(form, null));
	}
	
	@Test
	public void testShouldEmptyMatch() {
		TestForm form = new TestForm("", "");
		
		validator.initialize(form.getClass().getAnnotation(FieldMatch.class));
		assertTrue(validator.isValid(form, null));
	}

	@Test
	public void testShouldNullMatch() {
		TestForm form = new TestForm(null, null);
		
		validator.initialize(form.getClass().getAnnotation(FieldMatch.class));
		assertTrue(validator.isValid(form, null));
	}
	
	@Test
	public void testShouldNullNotMatch() {
		TestForm form = new TestForm(null, "");
		
		validator.initialize(form.getClass().getAnnotation(FieldMatch.class));
		assertFalse(validator.isValid(form, null));
	}

	
}
