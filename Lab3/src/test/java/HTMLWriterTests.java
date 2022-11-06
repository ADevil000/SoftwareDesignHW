import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.akirakozov.sd.refactoring.html.HTMLWriter;

import java.io.PrintWriter;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class HTMLWriterTests {
    private PrintWriter writer;
    private ArrayList<String> result;

    @BeforeEach
    public void setUp() {
        writer = mock(PrintWriter.class);

        result = new ArrayList<>();
        doAnswer(invocation -> {
            String s = invocation.getArgument(0, String.class);
            result.add(s);
            return null;
        }).when(writer).println(anyString());
    }

    @Test
    public void testNoAdding() {
        final HTMLWriter testObj = new HTMLWriter(writer);
        testObj.close();

        String[] expected = new String[]{
                "<html><body>",
                "</body></html>"
        };
        assertArrayEquals(expected, result.toArray());
    }

    @Test
    public void testAddParagraph() {
        String text = "iphone \t 600";

        try(final HTMLWriter testObj = new HTMLWriter(writer)) {
            testObj.addParagraph(text);
        }

        String[] expected = new String[]{
                "<html><body>",
                text + "</br>",
                "</body></html>"
        };
        assertArrayEquals(expected, result.toArray());
    }

    @Test
    public void testAddHeader() {
        String text = "Product with min price: ";

        try(final HTMLWriter testObj = new HTMLWriter(writer)) {
            testObj.addHeader1(text);
        }

        String[] expected = new String[]{
                "<html><body>",
                "<h1>" + text + "</h1>",
                "</body></html>"
        };
        assertArrayEquals(expected, result.toArray());
    }

    @Test
    public void testAddLine() {
        String text = "Some text.";

        try(final HTMLWriter testObj = new HTMLWriter(writer)) {
            testObj.addLine(text);
        }

        String[] expected = new String[]{
                "<html><body>",
                text,
                "</body></html>"
        };
        assertArrayEquals(expected, result.toArray());
    }
}