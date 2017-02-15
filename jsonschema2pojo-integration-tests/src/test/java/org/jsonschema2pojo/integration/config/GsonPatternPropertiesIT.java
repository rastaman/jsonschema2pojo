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

package org.jsonschema2pojo.integration.config;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.jsonschema2pojo.integration.util.CodeGenerationHelper.config;
import static org.jsonschema2pojo.integration.util.JsonAssert.assertEqualsJson;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.io.IOUtils;
import org.jsonschema2pojo.integration.util.Jsonschema2PojoRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonPatternPropertiesIT {

    @Rule
    public Jsonschema2PojoRule schemaRule = new Jsonschema2PojoRule();

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Test
    @SuppressWarnings("unchecked")
    public void gsonCanDeserializeOurAdditionalProperties()
            throws ClassNotFoundException, IOException, SecurityException, NoSuchMethodException,
            IllegalArgumentException, IllegalAccessException, InvocationTargetException {

        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/patternProperties/project.json",
                "com.example", config("annotationStyle", "gson",
                        "propertyWordDelimiters", "_",
                        "includeAdditionalProperties", true,
                        "includePatternProperties", true,
                        "useLongIntegers", true));

        Class<?> classWithAdditionalProperties = resultsClassLoader.loadClass("com.example.Project");

        String json = new String(Files.readAllBytes(Paths.get("src/test/resources/json/examples/project.json")));
        System.out.println("Original JSON:");
        System.out.println(json);
        Object deserialized = gson.fromJson(json, classWithAdditionalProperties);

        assertThat(deserialized, is(notNullValue()));
        System.out.println("Deserialized JSON:");
        String toJson = gson.toJson(deserialized);
        System.out.println(toJson);
        // classWithAdditionalPropertieMethod getter =
        // classWithAdditionalProperties.getMethod("getAdditionalProperties");

        // assertThat(((Map<String, Object>) getter.invoke(deserialized)).containsKey("a"), is(true));
        // assertThat((String) ((Map<String, Object>) getter.invoke(deserialized)).get("a"), is("1"));
        // assertThat(((Map<String, Object>) getter.invoke(deserialized)).containsKey("b"), is(true));
        // assertThat((Integer) ((Map<String, Object>) getter.invoke(deserialized)).get("b"), is(2));
    }

}
