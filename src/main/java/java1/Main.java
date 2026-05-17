package java1;

import java1.service.OrderProcessingService;
import java1.model.Order;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Запуск системы обработки заказов ===\n");
        OrderProcessingService service = new OrderProcessingService(3, 50);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nПолучен сигнал завершения...");
            if (service.isRunning()) {
                service.stop();
            }
        }));

        service.start();

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running && service.isRunning()) {
            System.out.print("> ");
            String command = scanner.nextLine().trim().toLowerCase();

            switch (command) {
                case "stats":
                    showStatistics(service);
                    break;
                case "orders":
                    showOrders(service);
                    break;
                case "queue":
                    System.out.printf("Заказов в очереди: %d%n%n", service.getQueueSize());
                    break;
                case "quit":
                    running = false;
                    break;
                default:
                    System.out.println(" ");
            }
        }

        scanner.close();
        service.stop();
        System.out.println("=== Система завершила работу ===");
    }

    private static void showStatistics(OrderProcessingService service) {
        System.out.println("\n=== СТАТИСТИКА ===");
        System.out.printf("Обработано заказов: %d%n", service.getProcessedOrdersCount());
        System.out.printf("Заказов в очереди: %d%n", service.getQueueSize());
        System.out.printf("Статус сервиса: %s%n%n", service.isRunning() ? "РАБОТАЕТ" : "ОСТАНОВЛЕН");
    }

    private static void showOrders(OrderProcessingService service) {
        java.util.List<Order> orders = service.getProcessedOrdersList();
        if (orders.isEmpty()) {
            System.out.println("Нет обработанных заказов.\n");
            return;
        }
        System.out.println("\n=== ОБРАБОТАННЫЕ ЗАКАЗЫ ===");
        for (Order order : orders) {
            System.out.printf("ID: %s | Клиент: %s | Товар: %s | Кол-во: %d | Срочный: %s | Статус: %s%n",
                    order.getId(), order.getCustomerName(), order.getProductDescription(),
                    order.getQuantity(), order.isUrgent() ? "ДА" : "НЕТ", order.getStatus().getDescription());
        }
        System.out.println();
    }
}