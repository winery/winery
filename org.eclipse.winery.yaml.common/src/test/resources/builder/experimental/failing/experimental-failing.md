# Experimental and failing tests

## invalid-artifact_type-invalid_mime_type

This test contains an invalid mime type, that is currently not recognized as such.

## invalid-constraints-data_types-properties-constraints-invalid_type

This test aims to identify a mismatch between the type of a property and the supplied value given in the equal clause of its constraints.
However, such type mismatches are not yet detected.

## invalid-description-type_of_map

The description field only allows values of type string.
The provided value is of the type 'map'. This type mismatch is not yet caught by the validator.

## invalid-metadata-value_of_type_map

Metadata should be provided as key/value pairs.
This test contains metadata of the type map which is not allowed according to the spec.
This type mismatch is currently not handled by the validator.

## valid-data_types

Data type dt2 should have the same property (prop1) as data type dt1.
However, it appears that prop1 is missing in dt2.
    
