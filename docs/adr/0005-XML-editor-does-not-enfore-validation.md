# Optional XML validation

**UserStory:** Winery offers editing the stored XML of the TOSCA definitions. What to do with validation?

## Considered Alternatives

* Winery never creates an non-schema-conforming XML. For instance, the user has to create a topology template first before he is allowed to save the service template
* Winery generate random data to gain schema-conforming XML
* Winery generates non-schema-conforming XML, but assumes that the user makes it eventually valid. In casea the user uses the XML tab, the user knows what he does. Winery forces the user to generate schema-conforming in the XML editor.
* Winery generates non-schema-conforming XML and warns the user when the user uses the XML editor. Winery does NOT force the user to generate schema-conforming XML in the XML editor.
## Conclusion

* *Chosen Alternative: D*
* This is in line with other editors: They allow to save, but warn if the file has compile errors, validation errors, ...

## License

Copyright (c) 2017 University of Stuttgart.

All rights reserved. Made available under the terms of the [Eclipse Public License v1.0] and the [Apache License v2.0] which both accompany this distribution.

 [Apache License v2.0]: http://www.apache.org/licenses/LICENSE-2.0.html
 [Eclipse Public License v1.0]: http://www.eclipse.org/legal/epl-v10.html
