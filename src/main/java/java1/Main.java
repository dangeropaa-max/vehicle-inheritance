package java1;

import java1.service.OrderProcessingService;
import java1.model.Order;

import java.util.Scanner;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        logger.info("Запуск системы обработки заказов...");

        OrderProcessingService service = new OrderProcessingService(3, 50);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutdown hook triggered");
            if (service.isServiceRunning()) {
                service.stop();
            }
        }));

        service.start();

        System.out.println("\n Система обработки заказов");
        System.out.println("Система работает с 3 потребителями");

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running && service.isServiceRunning()) {
            try {
                System.out.print("> ");

                if (!scanner.hasNextLine()) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                    continue;
                }

                String command = scanner.nextLine().trim().toLowerCase();

                switch (command) {
                    case "stats":
                        showStatistics(service);
                        break;
                    case "orders":
                        showOrders(service);
                        break;
                    case "queue":
                        System.out.printf(" Orders in queue: %d%n", service.getQueueSize());
                        break;
                    case "quit":
                        System.out.println("Shutting down...");
                        running = false;
                        break;
                    case "help":
                        showHelp();
                        break;
                    case "":
                        break;
                    default:
                        System.out.println(" ");
                }
            } catch (Exception e) {
                System.err.println("Ошибка считывания входных данных: " + e.getMessage());
                running = false;
            }
        }

        scanner.close();
        System.out.println("Остановка обслуживания");
        service.stop();
        System.out.println("Обслуживание остановлено. До свидания!");
        logger.info("Завершение работы системы завершено");
    }

    private static void showHelp() {
        System.out.println("\nДоступные команды");
        System.out.println(" stats  - Показывать статистику обработки");
        System.out.println(" orders - Показывать обработанные заказы");
        System.out.println(" queue  - Показать размер очереди");
        System.out.println(" quit   - Остановите систему");
        System.out.println(" help   - Покажите эту справку");
    }

    private static void showStatistics(OrderProcessingService service) {
        System.out.println("\nОбработка статистических данных");
        System.out.printf(" Обработанные заказы: %d%n", service.getProcessedOrdersCount());
        System.out.printf(" Размер очереди: %d%n", service.getQueueSize());
        System.out.printf(" Статус обслуживания: %s%n", service.isServiceRunning() ? "Работает" : "Остановлен");
    }

    private static void showOrders(OrderProcessingService service) {
        var orders = service.getProcessedOrdersList();

        if (orders.isEmpty()) {
            System.out.println("Обработанных заказов пока нет.\n");
            return;
        }

        System.out.println("\nОбработанные заказы");
        int count = 0;
        for (Order order : orders) {
            if (count >= 20) {
                System.out.printf("и %d ещё заказы s%n", orders.size() - 20);
                break;
            }
            System.out.printf(" %s | Потребитель %s | Продукт: %s | Количество: %d | Срочный: %s | Статус: %s%n",
                    order.getId(),
                    order.getCustomerName(),
                    order.getProductDescription(),
                    order.getQuantity(),
                    order.isUrgent(),
                    order.getStatus().getDescription());
            count++;
        }
        System.out.printf(" Всего: %d закозов %n", orders.size());
    }
}