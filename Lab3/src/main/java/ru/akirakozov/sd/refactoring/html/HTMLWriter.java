package ru.akirakozov.sd.refactoring.html;

import java.io.PrintWriter;

public class HTMLWriter implements AutoCloseable {
    public final PrintWriter writer;

    public HTMLWriter(PrintWriter writer) {
        this.writer = writer;
        this.writer.println("<html><body>");
    }

    public void addLine(String line) {
        writer.println(line);
    }

    public void addHeader1(String text) {
        writer.println(String.format("<h1>%s</h1>", text));
    }

    public void addParagraph(String text) {
        writer.println(String.format("%s</br>", text));
    }

    @Override
    public void close() {
        writer.println("</body></html>");
    }
}
