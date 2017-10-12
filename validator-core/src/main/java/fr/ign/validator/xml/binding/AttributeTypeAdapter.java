package fr.ign.validator.xml.binding;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import fr.ign.validator.model.AttributeType;

@XmlType(propOrder = { "name", "type", "definition", "regexp", "size", "nullable", "listOfValues" })
public class AttributeTypeAdapter extends XmlAdapter<AttributeTypeAdapter.AdaptedAttributeType, AttributeType<?>>{

	@Override
	public AdaptedAttributeType marshal(AttributeType<?> attributeType) throws Exception {
		if ( null == attributeType ){
			return null ;
		}
		
		AdaptedAttributeType adaptedAttributeType = new AdaptedAttributeType();
		adaptedAttributeType.name = attributeType.getName() ;
		adaptedAttributeType.type = attributeType.getTypeName() ;
		adaptedAttributeType.definition = attributeType.getDefinition() ;
		adaptedAttributeType.regexp = attributeType.getRegexp() ;
		adaptedAttributeType.size = attributeType.getSize() ;
		adaptedAttributeType.nullable = attributeType.isNullable() ;
		adaptedAttributeType.listOfValues = attributeType.getListOfValues() ;
		return adaptedAttributeType ;
	}

	
	@Override
	public AttributeType<?> unmarshal(AttributeTypeAdapter.AdaptedAttributeType adaptedValueType) throws Exception {
		if ( null == adaptedValueType ){
			return null ;
		}
		
		AttributeType<?> attributeType = AttributeType.forName( adaptedValueType.type ) ;
		attributeType.setName(adaptedValueType.name);
		attributeType.setDefinition(adaptedValueType.definition);
		attributeType.setRegexp(adaptedValueType.regexp);
		attributeType.setSize(adaptedValueType.size);
		attributeType.setNullable(adaptedValueType.nullable) ;
		attributeType.setListOfValues(adaptedValueType.listOfValues);
		
		return attributeType ;
	}

	public static class AdaptedAttributeType {
		public String name ;
		public String type ;
		public String definition ;
		public String regexp ;
		public Integer size ;
		public boolean nullable ;
		@XmlElementWrapper(name = "listOfValues")
		@XmlElement(name = "value")
		public List<String> listOfValues ;
	}

}
