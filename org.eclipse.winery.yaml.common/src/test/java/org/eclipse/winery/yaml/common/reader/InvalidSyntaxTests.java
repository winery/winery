package org.eclipse.winery.yaml.common.reader;

import org.eclipse.winery.yaml.common.exception.MultiException;
import org.eclipse.winery.yaml.common.reader.yaml.Reader;
import org.junit.Test;

/**
 * Intention of this test class is to test the robustness of the parser
 * Goal is to see no unexpected exceptions
 */
public class InvalidSyntaxTests {
    
    private static String PREFIX = "src/test/resources/builder/invalid_syntax";
    
    @Test(expected = MultiException.class)
    public void missingLineBreakThrowsException() throws MultiException {
       Reader reader = new Reader(); 
       reader.parse(PREFIX, "missing_linebreak.yaml");
    }
}
