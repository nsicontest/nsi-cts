package net.geant.nsi.contest.platform.web.data.convert;

public abstract class AbstractConvertType<FROM, TO> implements ConvertType<FROM, TO> {

	private final Class<FROM> supports;
	
	protected Converter converter;
	
	public AbstractConvertType(Class<FROM> type) {
		this.supports = type;
	}

	@Override
	public Class<FROM> supports() {
		return supports;
	}

	public Converter getConverter() {
		return converter;
	}

	public void setConverter(Converter converter) {
		this.converter = converter;
	}
	
}
