package net.geant.nsi.contest.platform.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.beanutils.BeanUtils;


/**
 * 
 * @author mikus
 *
 */
public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {

	private String field;
	private String matchWith;
	
	@Override
	public void initialize(FieldMatch constraintAnnotation) {
		this.field = constraintAnnotation.field();
		this.matchWith = constraintAnnotation.matchWith();
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		try {
			String fieldObj = BeanUtils.getProperty(value, field);
			String matchWithObj = BeanUtils.getProperty(value, matchWith);
			
			return (fieldObj == matchWithObj || (fieldObj != null && fieldObj.equals(matchWithObj)));
		} catch (Exception e) {
			//TODO: add log debug if you need
		} 
		
		return false;
	}

}
