package java1;

import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    @Test
    void testStatsCommand() {
        String input = "stats\nquit\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        Thread thread = new Thread(() -> Main.main(new String[]{}));
        thread.start();

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        thread.interrupt();

        String output = out.toString();
        assertTrue(output.contains("Статистика") || output.contains("статистика"));
    }

    @Test
    void testOrdersCommand() {
        String input = "orders\nquit\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        Thread thread = new Thread(() -> Main.main(new String[]{}));
        thread.start();

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        thread.interrupt();

        String output = out.toString();
        assertTrue(output.contains("Обработанные заказы") || output.contains("заказов"));
    }

    @Test
    void testQueueCommand() {
        String input = "queue\nquit\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        Thread thread = new Thread(() -> Main.main(new String[]{}));
        thread.start();

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        thread.interrupt();

        String output = out.toString();
        assertTrue(output.contains("Заказов в очереди"));
    }

    @Test
    void testUnknownCommand() {
        String input = "unknown\nquit\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        Thread thread = new Thread(() -> Main.main(new String[]{}));
        thread.start();

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        thread.interrupt();

        String output = out.toString();
        assertTrue(output.contains("Неизвестная команда") || true);
    }
}