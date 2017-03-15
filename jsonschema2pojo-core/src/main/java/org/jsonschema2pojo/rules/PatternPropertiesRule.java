/**
 * Copyright © 2010-2014 Nokia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jsonschema2pojo.rules;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jsonschema2pojo.Schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassContainer;
import com.sun.codemodel.JType;

/**
 * Applies the "pattern-properties" schema rule. This is a work in progress.
 * 
 * @see <a href=
 *      "http://tools.ietf.org/html/draft-zyp-json-schema-03#section-5.2">http://tools.ietf.org/html/draft-zyp-json-schema-03#section-5.2</a>
 */
public class PatternPropertiesRule implements Rule<JClassContainer, JType> {

    private final RuleFactory ruleFactory;

    protected PatternPropertiesRule(RuleFactory ruleFactory) {
        this.ruleFactory = ruleFactory;
    }

    /**
     * Applies this schema rule to take the required code generation steps.
     * <p>
     * For each property present within the properties node, this rule will
     * invoke the 'property' rule provided by the given schema mapper.
     *
     * @param nodeName
     *            the name of the node for which properties are being added
     * @param node
     *            the properties node, containing property names and their
     *            definition
     * @param jclass
     *            the Java type which will have the given properties added
     * @return the given jclass
     */
    @Override
    public JType apply(String nodeName, JsonNode node, JClassContainer jclassContainer, Schema schema) {
        JsonNode patternProperties = node.get("patternProperties");
        String shortFieldName = null;
        if (nodeName.endsWith("s")) {
            shortFieldName = nodeName.substring(0, nodeName.length() - 1);
        } else {
            shortFieldName = nodeName;
        }
        Iterator<String> elements = patternProperties.fieldNames();
        List<JType> valueMapTypes = new ArrayList<JType>();
        while (elements.hasNext()) {
            String fieldName = elements.next();
            JsonNode n = patternProperties.get(fieldName);
            String javaName = ruleFactory.getNameHelper().getFieldName(shortFieldName, n);
            JType valueMapType = ruleFactory.getSchemaRule().apply(javaName, n, jclassContainer, schema);
            valueMapTypes.add(valueMapType);
        }

        JType valueMapType = jclassContainer.owner().ref(Object.class);
        if (!valueMapTypes.isEmpty()) {
            valueMapType = valueMapTypes.iterator().next();
        }

        JClass propertiesMapType = jclassContainer.owner().ref(Map.class);
        propertiesMapType = propertiesMapType.narrow(jclassContainer.owner().ref(String.class), valueMapType.boxify());

        return propertiesMapType;
    }

}
