package net.geant.nsi.contest.platform.web.data.convert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConverterImpl implements Converter {
	Map<Class, ConvertType> converters = new HashMap<Class, ConvertType>();
	
	public ConverterImpl() {
	}
	
	public void register(ConvertType converter) {
		if(converter == null)
			throw new IllegalArgumentException("Cannot register null converter.");
		
		converter.setConverter(this);
		converters.put(converter.supports(), converter);
	}
	
	public Object convert(Object object) {
		if(object == null) return null;
		
		ConvertType converter = converters.get(object.getClass());
		if(converter == null) {
			if(object instanceof List<?>)
				return convertList((List<Object>) object);
			else
				throw new IllegalArgumentException("Cannot find converter for type " + object.getClass());
		} else
			return converter.convert(object);
	}
	
	private List<Object> convertList(List<Object> list) {
		List<Object> results = new ArrayList<Object>();
		for(Object l : list) {
			ConvertType converter = converters.get(l.getClass());
			results.add(converter.convert(l));
		}
		return results;
	}
	
	public Object convertDetailed(Object object) {
		if(object == null) return null;
		
		ConvertType converter = converters.get(object.getClass());
		if(converter == null)
			throw new IllegalArgumentException("Cannot find converter for type " + object.getClass());
		
		return converter.convertDetailed(object);		
	}
	
	private List<Object> convertDetailedList(List<Object> list) {
		List<Object> results = new ArrayList<Object>();
		for(Object l : list) {
			ConvertType converter = converters.get(l.getClass());
			results.add(converter.convertDetailed(l));
		}
		return results;
	}
}
