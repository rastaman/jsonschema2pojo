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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jsonschema2pojo.Schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

/**
 * Applies the "properties" schema rule.
 *
 * @see <a
 *      href="http://tools.ietf.org/html/draft-zyp-json-schema-03#section-5.2">http://tools.ietf.org/html/draft-zyp-json-schema-03#section-5.2</a>
 */
public class PatternPropertiesRule implements Rule<JDefinedClass, JDefinedClass> {

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
    public JDefinedClass apply(String nodeName, JsonNode node, JDefinedClass jclass, Schema schema) {
        if (!this.ruleFactory.getGenerationConfig().isIncludePatternProperties()) {
            // no pattern properties allowed
            return jclass;
        }

        if ( node != null ) {
            JType propertyType;
            if (node != null && node.size() != 0) {
                propertyType = ruleFactory.getSchemaRule().apply(nodeName, node, jclass, schema);
            } else {
                propertyType = jclass.owner().ref(Map.class);
            }
            JClass propertiesMapType = jclass.owner().ref(Map.class);
            propertiesMapType = propertiesMapType.narrow(jclass.owner().ref(String.class), propertyType.boxify());

            JClass propertiesMapImplType = jclass.owner().ref(LinkedHashMap.class);
            propertiesMapImplType = propertiesMapImplType.narrow(jclass.owner().ref(String.class), propertyType.boxify());

            JFieldVar field = jclass.field(JMod.PRIVATE, propertiesMapType, nodeName);

            field.init(JExpr._new(propertiesMapImplType));
            
            addGetter(jclass, field);

            addSetter(jclass, propertyType, field);
        }

        return jclass;
    }

    private void addSetter(JDefinedClass jclass, JType propertyType, JFieldVar field) {
        JMethod setter = jclass.method(JMod.PUBLIC, void.class, "setAdditionalProperty");

        ruleFactory.getAnnotator().anySetter(setter);

        JVar nameParam = setter.param(String.class, "name");
        JVar valueParam = setter.param(propertyType, "value");

        JInvocation mapInvocation = setter.body().invoke(JExpr._this().ref(field), "put");
        mapInvocation.arg(nameParam);
        mapInvocation.arg(valueParam);
    }

    private JMethod addGetter(JDefinedClass jclass, JFieldVar field) {
        JMethod getter = jclass.method(JMod.PUBLIC, field.type(), "getAdditionalProperties");

        ruleFactory.getAnnotator().anyGetter(getter);

        getter.body()._return(JExpr._this().ref(field));
        return getter;
    }

}
