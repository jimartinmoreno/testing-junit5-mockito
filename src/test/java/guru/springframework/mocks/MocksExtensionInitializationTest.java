package guru.springframework.mocks;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
/**
 * @ExtendWith(MockitoExtension.class) indica que se inicialicen los mosk
 */
@ExtendWith(MockitoExtension.class)
public class MocksExtensionInitializationTest {

    /**
     * @Mock indica que cree un mock
     */
    @Mock
    Map<String, Object> mapMock;

    @Test
    void testMock() {
        mapMock.put("keyvalue", "foo");
        System.out.println("mapMock = " + mapMock);
        assertEquals(mapMock.size(), 0);
    }
}
