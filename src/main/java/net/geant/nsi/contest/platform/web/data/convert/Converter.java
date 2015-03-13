package net.geant.nsi.contest.platform.web.data.convert;

public interface Converter {

	@SuppressWarnings("rawtypes")
	void register(ConvertType converter);
	
	Object convert(Object object);
	Object convertDetailed(Object object);
}
