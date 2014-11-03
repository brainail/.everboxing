package org.brainail.Everboxing.tool;

import org.brainail.Everboxing.utils.ToolCollections;
import org.fest.assertions.api.Assertions;
import org.junit.Test;

/**
 * User: brainail<br/>
 * Date: 03.11.14<br/>
 * Time: 23:37<br/>
 */
public class ToolCollectionsTest {

    @Test
    public void testNullIterable() {
        Assertions.assertThat(ToolCollections.emptyIfNull((Iterable) null)).isNotNull();
    }

}
