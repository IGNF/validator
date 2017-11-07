package fr.ign.validator.xml.binding;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;
import com.sun.xml.bind.v2.WellKnownNamespace;

public class JaxbNamespacePrefixMapper extends NamespacePrefixMapper {

	 @Override
     public String[] getPreDeclaredNamespaceUris() {
         return new String[] { WellKnownNamespace.XML_SCHEMA_INSTANCE };
     }

     @Override
     public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix) {
         if (namespaceUri.equals(WellKnownNamespace.XML_SCHEMA_INSTANCE))
             return "xsi";
         if (namespaceUri.equals(WellKnownNamespace.XML_SCHEMA))
             return "xs";
         if (namespaceUri.equals(WellKnownNamespace.XML_MIME_URI))
             return "xmime";
         return suggestion;
     }
	
}
