package fr.ign.validator.io.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import fr.ign.validator.model.AttributeConstraints;
import fr.ign.validator.model.AttributeType;

@XmlType(propOrder = {
    "name", "type", "definition", "regexp", "size", "nullable", "listOfValues", "identifiant", "reference"
})
public class AttributeTypeAdapter extends XmlAdapter<AttributeTypeAdapter.AdaptedAttributeType, AttributeType<?>> {

    /**
     * Legacy XML format
     * 
     * @author MBorne
     */
    public static class AdaptedAttributeType {
        public String name;
        public String type;
        public String definition;
        public String regexp;
        public Integer size;
        public boolean nullable;
        public boolean identifier;
        public String reference;
        @XmlElementWrapper(name = "listOfValues")
        @XmlElement(name = "value")
        public List<String> listOfValues;
    }

    @Override
    public AdaptedAttributeType marshal(AttributeType<?> attributeType) throws Exception {
        if (null == attributeType) {
            return null;
        }

        AdaptedAttributeType adaptedAttributeType = new AdaptedAttributeType();
        adaptedAttributeType.name = attributeType.getName();
        adaptedAttributeType.type = attributeType.getTypeName();
        adaptedAttributeType.definition = attributeType.getDescription();

        AttributeConstraints constraints = attributeType.getConstraints();
        adaptedAttributeType.nullable = !constraints.isRequired();
        adaptedAttributeType.identifier = constraints.isUnique();
        adaptedAttributeType.regexp = constraints.getPattern();
        adaptedAttributeType.size = constraints.getMaxLength();
        adaptedAttributeType.listOfValues = constraints.getEnumValues();
        adaptedAttributeType.reference = constraints.getReference();
        return adaptedAttributeType;
    }

    @Override
    public AttributeType<?> unmarshal(AttributeTypeAdapter.AdaptedAttributeType adaptedValueType) throws Exception {
        if (null == adaptedValueType) {
            return null;
        }

        AttributeType<?> attributeType = AttributeType.forName(adaptedValueType.type);
        attributeType.setName(adaptedValueType.name);
        attributeType.setDescription(adaptedValueType.definition);

        AttributeConstraints constraints = attributeType.getConstraints();
        constraints.setRequired(!adaptedValueType.nullable);
        constraints.setUnique(adaptedValueType.identifier);
        constraints.setPattern(adaptedValueType.regexp);
        constraints.setMaxLength(adaptedValueType.size);
        constraints.getEnumValues(adaptedValueType.listOfValues);
        constraints.setReference(adaptedValueType.reference);

        return attributeType;
    }

}
