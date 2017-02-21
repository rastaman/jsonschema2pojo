This patch introduce limited support for pattern properties. It only create a map of objects keyed by strings as attribute for the property containing the pattern properties.

For instance schema describing a property "roles" defined as follow :
```javascript
        "roles": {
            "type": "object",
            "patternProperties": {
                ".+": {
                    "type": "string",
                    "format": "uri"
                }
            },
            "additionalProperties": false
        }
```
Will be generated like this with the gson annotator:
```java
private Map roles;
```
