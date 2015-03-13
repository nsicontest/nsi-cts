package net.geant.nsi.contest.platform.web.data.convert;

import java.util.List;

/**
 * Interface for converting generic FROM type into TO type
 * @author mikus
 *
 * @param <FROM>
 * @param <TO>
 */
public interface ConvertType<FROM, TO> {
	TO convert(FROM object);
	TO convertDetailed(FROM object);
	
	Class<FROM> supports();
	
	/**
	 * Nested types can reuse converters already registered
	 * @param converter
	 */
	void setConverter(Converter converter);
}
