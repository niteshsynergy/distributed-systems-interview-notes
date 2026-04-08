Complex Java 8 Stream Example (E-commerce Analytics)

/*Scenario:
You have orders with items and need to generate a complex analytics report.

Operations used:

filter
        map
flatMap
        groupingBy
partitioningBy
        collectingAndThen
mapping
        reducing
sorted
        limit
distinct
        peek
Optional handling
statistics collectors*/

import java.util.*;
import java.util.stream.*;

//Complex Stream Pipeline
public class java8_complex_streamApiCode
{
	public static void main(String[] args)
	{
        List<Order> orders = getOrders();

        Map<String, DoubleSummaryStatistics> result =
                orders.stream()

                        // 1 filter completed orders
                        .filter(o -> "COMPLETED".equals(o.getStatus()))

                        // 2 debug stream
                        .peek(o -> System.out.println("Processing order: " + o.getCustomer()))

                        // 3 remove duplicates
                        .distinct()

                        // 4 flatten items
                        .flatMap(order -> order.getItems().stream())

                        // 5 filter expensive items
                        .filter(item -> item.getPrice() > 50)

                        // 6 normalize category
                        .map(item -> new Item(
                                item.name,
                                item.getCategory().toUpperCase(),
                                item.getPrice()))

                        // 7 debug
                        .peek(i -> System.out.println("Item: " + i.getCategory()))

                        // 8 sort by price
                        .sorted(Comparator.comparing(Item::getPrice).reversed())

                        // 9 limit top items
                        .limit(100)

                        // 10 group by category
                        .collect(Collectors.groupingBy(
                                Item::getCategory,

                                // 11 collect statistics
                                Collectors.summarizingDouble(Item::getPrice)
                        ));

        //Another Very Advanced Stream Example
        //Calculate Top 3 customers per category by revenue.
        Map<String, List<String>> report =
                orders.stream()

                        .filter(o -> o.getTotalAmount() > 100)

                        .flatMap(order ->
                                order.getItems().stream()
                                        .map(item -> new AbstractMap.SimpleEntry<>(item.getCategory(), order))
                        )

                        .collect(Collectors.groupingBy(
                                Map.Entry::getKey,

                                Collectors.mapping(
                                        Map.Entry::getValue,

                                        Collectors.collectingAndThen(
                                                Collectors.toList(),

                                                list -> list.stream()
                                                        .collect(Collectors.groupingBy(
                                                                Order::getCustomer,
                                                                Collectors.summingDouble(Order::getTotalAmount)
                                                        ))
                                                        .entrySet()
                                                        .stream()
                                                        .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                                                        .limit(3)
                                                        .map(Map.Entry::getKey)
                                                        .collect(Collectors.toList())
                                        )
                                )
                        ));

        //Ultra-Advanced Stream Pipeline (Practice)
        //Try to understand this one carefully:
        List<String> topCategories =
                orders.stream()

                        .filter(o -> o.getStatus().equals("COMPLETED"))
                        .flatMap(o -> o.getItems().stream())
                        .filter(i -> i.getPrice() > 20)
                        .map(Item::getCategory)
                        .map(String::toLowerCase)
                        .distinct()
                        .sorted()
                        .peek(System.out::println)
                        .collect(Collectors.groupingBy(
                                c -> c,
                                Collectors.counting()
                        ))
                        .entrySet()
                        .stream()
                        .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                        .limit(5)
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList());
        List<Order> orders = getSampleOrders(); // 1️⃣ Find top 5 customers by spending List<Map.Entry<String, Double>> topCustomers = orders.stream() .collect(Collectors.groupingBy( Order::getCustomer, Collectors.summingDouble(Order::getTotalAmount))) .entrySet() .stream() .sorted(Map.Entry.<String, Double>comparingByValue().reversed()) .limit(5) .collect(Collectors.toList()); System.out.println("Top Customers: " + topCustomers); // 2️⃣ Find average order value per customer Map<String, Double> avgOrderValue = orders.stream() .collect(Collectors.groupingBy( Order::getCustomer, Collectors.averagingDouble(Order::getTotalAmount) )); System.out.println("Average Order Value: " + avgOrderValue); // 3️⃣ Find most sold product category Optional<Map.Entry<String, Long>> mostSoldCategory = orders.stream() .flatMap(order -> order.getItems().stream()) .collect(Collectors.groupingBy( Item::getCategory, Collectors.counting())) .entrySet() .stream() .max(Map.Entry.comparingByValue()); System.out.println("Most Sold Category: " + mostSoldCategory); // 4️⃣ Find customers who bought in multiple categories List<String> customersMultipleCategories = orders.stream() .collect(Collectors.groupingBy( Order::getCustomer, Collectors.flatMapping( order -> order.getItems().stream() .map(Item::getCategory), Collectors.toSet()))) .entrySet() .stream() .filter(entry -> entry.getValue().size() > 1) .map(Map.Entry::getKey) .collect(Collectors.toList()); System.out.println("Customers buying multiple categories: " + customersMultipleCategories); // 5️⃣ Find highest value order per day Map<LocalDate, Optional<Order>> highestOrderPerDay = orders.stream() .collect(Collectors.groupingBy( Order::getDate, Collectors.maxBy( Comparator.comparingDouble(Order::getTotalAmount) ) )); System.out.println("Highest Order per Day: " + highestOrderPerDay);

    }
    // Sample data generator
    static List<Order> getSampleOrders() {

        Item phone = new Item("Phone", "Electronics", 700);
        Item laptop = new Item("Laptop", "Electronics", 1200);
        Item shoes = new Item("Shoes", "Fashion", 120);
        Item shirt = new Item("Shirt", "Fashion", 60);
        Item book = new Item("Book", "Books", 30);

        return Arrays.asList(
                new Order(1, "Alice", Arrays.asList(phone, book), 730, LocalDate.of(2024,1,1)),
                new Order(2, "Bob", Arrays.asList(shoes, shirt), 180, LocalDate.of(2024,1,1)),
                new Order(3, "Alice", Arrays.asList(laptop), 1200, LocalDate.of(2024,1,2)),
                new Order(4, "Charlie", Arrays.asList(book, shirt), 90, LocalDate.of(2024,1,2)),
                new Order(5, "Bob", Arrays.asList(phone), 700, LocalDate.of(2024,1,3)),
                new Order(6, "David", Arrays.asList(laptop, shoes), 1320, LocalDate.of(2024,1,3))
        );
    }
}
//Data Classes
class Order {
    int id;
    String customer;
    List<Item> items;
    double totalAmount;
    String status;

    public Order(int id, String customer, List<Item> items, double totalAmount, String status) {
        this.id = id;
        this.customer = customer;
        this.items = items;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public List<Item> getItems() { return items; }
    public String getCustomer() { return customer; }
    public double getTotalAmount() { return totalAmount; }
    public String getStatus() { return status; }
}

class Item {
    String name;
    String category;
    double price;

    public Item(String name, String category, double price) {
        this.name = name;
        this.category = category;
        this.price = price;
    }

    public String getCategory() { return category; }
    public double getPrice() { return price; }
}

/*
Important Stream Operations You Should Master

Practice chaining these.

Intermediate operations

filter
map
flatMap
distinct
sorted
peek
limit
skip

Terminal operations

collect
reduce
count
forEach
findFirst
findAny
min
max

Collectors

groupingBy
partitioningBy
mapping
joining
summarizingInt
collectingAndThen

 */