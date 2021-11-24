package guru.springframework.mocks;

import org.assertj.core.util.Lists;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by jt on 2018-10-29.
 */
public class AnnotationMocksTest {

    @Mock
    Map<String, Object> mapMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testMock() {
        mapMock.put("keyvalue", "foo");
        System.out.println("mapMock = " + mapMock);
        assertEquals(mapMock.size(), 0);
    }

    // Verify order of interactions:
    @Test
    void testVerifyOrder() {
        List<String> mockedList = mock(List.class);
        mockedList.size();
        mockedList.add("a parameter");
        mockedList.clear();

        InOrder inOrder = Mockito.inOrder(mockedList);
        inOrder.verify(mockedList).size();
        inOrder.verify(mockedList).add("a parameter");
        inOrder.verify(mockedList).clear();

        // Verify interaction with exact argument:
        verify(mockedList).add("a parameter");
        // Verify interaction with flexible/any argument:
        verify(mockedList).add(anyString());

        //Verify interaction using argument capture:
        mockedList.addAll(Lists.<String> newArrayList("someElement"));
        ArgumentCaptor<List> argumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(mockedList).addAll(argumentCaptor.capture());
        List<String> capturedArgument = argumentCaptor.<List<String>> getValue();
        assertThat(capturedArgument).contains("someElement");
        MatcherAssert.assertThat(capturedArgument, hasItem("someElement"));
    }
}
