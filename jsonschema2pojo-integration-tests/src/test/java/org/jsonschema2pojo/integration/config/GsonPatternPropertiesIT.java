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
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Scanner;

import org.jsonschema2pojo.integration.util.Jsonschema2PojoRule;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonPatternPropertiesIT {

    @Rule
    public Jsonschema2PojoRule schemaRule = new Jsonschema2PojoRule();

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Test if we can parse a simple pattern property. It generates one map
     * of String,Object and didn't take into account the pattern (i.e it use
     * a String as key (although it is an object currently).
     * 
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    @Test
    @SuppressWarnings("unchecked")
    public void gsonCanDeserializeASimplePatternProperties()
            throws ClassNotFoundException, IOException, SecurityException, NoSuchMethodException,
            IllegalArgumentException, IllegalAccessException, InvocationTargetException {

        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/patternProperties/project.json",
                "com.example", config("annotationStyle", "gson",
                        "propertyWordDelimiters", "_",
                        "includeAdditionalProperties", true,
                        "includePatternProperties", true,
                        "useLongIntegers", true));

        File generatedClasses = schemaRule.generate("/schema/patternProperties/project.json",
                "com.example", config("annotationStyle", "gson",
                        "propertyWordDelimiters", "_",
                        "includeAdditionalProperties", true,
                        "includePatternProperties", true,
                        "useLongIntegers", true));

        Class<?> classWithPatternProperties = resultsClassLoader.loadClass("com.example.Project");
        String json = new Scanner(new File("src/test/resources/json/examples/project.json"))
                .useDelimiter("\\Z").next();
        Object deserialized = gson.fromJson(json, classWithPatternProperties);

        assertThat(deserialized, is(notNullValue()));
        Method getter =
                classWithPatternProperties.getMethod("getAvatarUrls");

        assertThat(((Map<String, Object>) getter.invoke(deserialized)).containsKey("32x32"), is(true));
        assertThat((String) ((Map<String, Object>) getter.invoke(deserialized)).get("32x32"), is("https://company.atlassian.net/secure/projectavatar?size=medium&pid=10000&avatarId=52600"));
    }

    @Test
    @Ignore
    @SuppressWarnings("unchecked")
    public void gsonCanDeserializeASimplePatternPropertiesWithObjectType()
            throws ClassNotFoundException, IOException, SecurityException, NoSuchMethodException,
            IllegalArgumentException, IllegalAccessException, InvocationTargetException {

        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/patternProperties/open-api.json",
                "com.example", config("annotationStyle", "gson",
                        "propertyWordDelimiters", "_",
                        "includeAdditionalProperties", true,
                        //"includePatternProperties", true,
                        "useLongIntegers", true));

        File generatedClasses = schemaRule.generate("/schema/patternProperties/open-api.json",
                "com.example", config("annotationStyle", "gson",
                        "propertyWordDelimiters", "_",
                        "includeAdditionalProperties", true,
                        //"includePatternProperties", true,
                        "useLongIntegers", true));
//        String java = new Scanner(new File("target/test/resources/json/examples/project.json"))
//                .useDelimiter("\\Z").next();
        System.out.println("Java source files are in " + generatedClasses.getAbsolutePath());
/*
        Class<?> classWithPatternProperties = resultsClassLoader.loadClass("com.example.OpenAPI");
        String json = new Scanner(new File("src/test/resources/json/examples/openapi.json"))
                .useDelimiter("\\Z").next();
        Object deserialized = gson.fromJson(json, classWithPatternProperties);

        assertThat(deserialized, is(notNullValue()));
        Method getter =
                classWithPatternProperties.getMethod("getAvatarUrls");

        assertThat(((Map<String, Object>) getter.invoke(deserialized)).containsKey("32x32"), is(true));
        assertThat((String) ((Map<String, Object>) getter.invoke(deserialized)).get("32x32"), is("https://company.atlassian.net/secure/projectavatar?size=medium&pid=10000&avatarId=52600"));
        */
    }

    @Test
    @SuppressWarnings("unchecked")
    public void gsonCanDeserializeAnotherPatternProperties()
            throws ClassNotFoundException, IOException, SecurityException, NoSuchMethodException,
            IllegalArgumentException, IllegalAccessException, InvocationTargetException {

        ClassLoader resultsClassLoader = schemaRule.generateAndCompile("/schema/patternProperties/create_meta_issue_type.json",
                "com.example", config("annotationStyle", "gson",
                        "propertyWordDelimiters", "_",
                        "includeAdditionalProperties", true,
                        "includePatternProperties", true,
                        "useLongIntegers", true));

        File generatedClasses = schemaRule.generate("/schema/patternProperties/create_meta_issue_type.json",
                "com.example", config("annotationStyle", "gson",
                        "propertyWordDelimiters", "_",
                        "includeAdditionalProperties", true,
                        "includePatternProperties", true,
                        "useLongIntegers", true));
        //System.out.println("Java source files are in " + generatedClasses.getAbsolutePath());

        Class<?> classWithPatternProperties = resultsClassLoader.loadClass("com.example.CreateMetaIssueType");
        String json = new Scanner(new File("src/test/resources/json/examples/createmeta.json"))
                .useDelimiter("\\Z").next();
        Object deserialized = gson.fromJson(json, classWithPatternProperties);

        assertThat(deserialized, is(notNullValue()));
        Method getter =
                classWithPatternProperties.getMethod("getFields");

        Assert.assertNotNull(getter);
    }

}
