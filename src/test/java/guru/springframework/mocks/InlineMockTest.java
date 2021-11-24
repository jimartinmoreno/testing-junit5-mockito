package guru.springframework.mocks;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * Created by jt on 2018-10-29.
 */
public class InlineMockTest {

    @Test
    void testInlineMock() {
        Map<String, String> mapMock = mock(Map.class);
        mapMock.put("keyvalue", "foo");
        assertEquals(mapMock.size(), 0);
    }
}
