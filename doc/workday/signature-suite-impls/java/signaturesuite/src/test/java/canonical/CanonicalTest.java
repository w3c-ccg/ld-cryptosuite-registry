package canonical;

import org.junit.Assert;
import org.junit.Test;

public class CanonicalTest {

    @Test
    public void canonicalTest() throws Exception {
        final String flatStructure = "{\"B\":\"Value2\",\"A\":\"Value1\"}";
        final String flatStructureExpected = "{\"A\":\"Value1\",\"B\":\"Value2\"}";
        final String result = Canonical.canonicalize(flatStructure);
        Assert.assertEquals(flatStructureExpected, result);
    }

    @Test
    public void arrayCanonicalTest() throws Exception {
        final String orderedArrayStructure = "{\"B\":[\"a\",\"z\",\"b\"],\"A\":\"Value1\"}";
        final String orderedArrayStructureExpected = "{\"A\":\"Value1\",\"B\":[\"a\",\"z\",\"b\"]}";
        final String result = Canonical.canonicalize(orderedArrayStructure);
        Assert.assertEquals(orderedArrayStructureExpected, result);
    }

    @Test
    public void hierarchicalCanonicalTest() throws Exception {
        final String hierarchicalStructure = "{\"B\":[{\"D\":\"2\",\"C\":\"1\"},{\"C\":\"5\",\"D\":\"6\"},{\"D\":\"4\",\"C\":\"3\"}],\"A\":\"Value1\"}";
        final String hierarchicalStructureExpected = "{\"A\":\"Value1\",\"B\":[{\"C\":\"1\",\"D\":\"2\"},{\"C\":\"5\",\"D\":\"6\"},{\"C\":\"3\",\"D\":\"4\"}]}";
        final String result = Canonical.canonicalize(hierarchicalStructure);
        Assert.assertEquals(hierarchicalStructureExpected, result);
    }
}
