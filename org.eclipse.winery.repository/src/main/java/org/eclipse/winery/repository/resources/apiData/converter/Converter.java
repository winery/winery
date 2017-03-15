package org.eclipse.winery.repository.resources.apiData.converter;

/**
 * inspired by XmlAdapter, but without exceptions
 */
public interface Converter<ValueType, BoundType> {

    public abstract BoundType unmarshal(ValueType v);

    public abstract ValueType marshal(BoundType v);

}
