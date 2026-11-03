//Task-1
//import java.nio.charset.StandardCharsets;
//import java.util.Base64;
//
//interface ReportFormatter {
//    String format(String title, String[] headers, String[][] rows);
//}
//
//class JsonFormatter implements ReportFormatter {
//
//    private String escape(String value) {
//        return value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
//    }
//
//    @Override
//    public String format(String title, String[] headers, String[][] rows) {
//        StringBuilder sb = new StringBuilder();
//
//        sb.append("{\n  \"report\": \"").append(escape(title)).append("\",\n  \"records\": [\n");
//
//        for (int i = 0; i < rows.length; i++) {
//            if (rows[i].length != headers.length) {
//                throw new IllegalArgumentException("Invalid row length");
//            }
//
//            sb.append("    {");
//
//            for (int j = 0; j < headers.length; j++) {
//                if (j > 0) sb.append(", ");
//
//                sb.append("\"").append(escape(headers[j])).append("\": \"").append(escape(rows[i][j])).append("\"");
//            }
//
//            sb.append("}");
//
//            if (i < rows.length - 1) sb.append(",");
//            sb.append("\n");
//        }
//
//        sb.append("  ]\n}");
//
//        return sb.toString();
//    }
//}
//
//class CsvFormatter implements ReportFormatter {
//
//    private String quote(String value) {
//        return "\"" + value.replace("\"", "\"\"") + "\"";
//    }
//
//    @Override
//    public String format(String title, String[] headers, String[][] rows) {
//        StringBuilder sb = new StringBuilder();
//
//        sb.append("# TITLE: ").append(title).append("\n");
//
//        for (int i = 0; i < headers.length; i++) {
//            if (i > 0) sb.append(",");
//            sb.append(quote(headers[i]));
//        }
//        sb.append("\n");
//
//        for (String[] row : rows) {
//            if (row.length != headers.length) {
//                throw new IllegalArgumentException("Invalid row length");
//            }
//
//            for (int i = 0; i < row.length; i++) {
//                if (i > 0) sb.append(",");
//                sb.append(quote(row[i]));
//            }
//            sb.append("\n");
//        }
//
//        return sb.toString();
//    }
//}
//
//interface Exporter {
//    void export(String destination, String data);
//}
//
//class BaseFileExporter implements Exporter {
//
//    @Override
//    public void export(String destination, String data) {
//        int bytes = data.getBytes(StandardCharsets.UTF_8).length;
//
//        System.out.printf("[DISK WRITE] Writing %d bytes to file://%s%n", bytes, destination);
//
//        System.out.println("----- FILE PAYLOAD BEGIN -----");
//        System.out.println(data);
//        System.out.println("----- FILE PAYLOAD END -----");
//    }
//}
//
//abstract class ExporterDecorator implements Exporter {
//    protected final Exporter wrappedExporter;
//
//    public ExporterDecorator(Exporter wrappedExporter) {
//        if (wrappedExporter == null) {
//            throw new IllegalArgumentException("Wrapped exporter cannot be null");
//        }
//
//        this.wrappedExporter = wrappedExporter;
//    }
//
//    @Override
//    public void export(String destination, String data) {
//        wrappedExporter.export(destination, data);
//    }
//}
//
//class HeaderFooterDecorator extends ExporterDecorator {
//    private final String timestamp;
//
//    public HeaderFooterDecorator(Exporter wrappedExporter, String timestamp) {
//        super(wrappedExporter);
//        this.timestamp = timestamp;
//    }
//
//    @Override
//    public void export(String destination, String data) {
//        String enrichedData = "=== METADATA HEADER: " + timestamp + " ===\n" + data + "\n" + "=== INTEGRITY CHECK: SHA-256 OK | END RECORD ===";
//
//        super.export(destination, enrichedData);
//    }
//}
//
//class Base64EncryptDecorator extends ExporterDecorator {
//
//    public Base64EncryptDecorator(Exporter wrappedExporter) {
//        super(wrappedExporter);
//    }
//
//    @Override
//    public void export(String destination, String data) {
//        String encoded = Base64.getEncoder().encodeToString(data.getBytes(StandardCharsets.UTF_8));
//
//        String payload = "[BASE64_CIPHERTEXT:" + encoded + "]";
//
//        super.export(destination, payload);
//    }
//}
//
//class Task1Runner {
//
//    public static void main(String[] args) {
//        String[] headers = {"ID", "User", "Role"};
//
//        String[][] rows = {{"101", "sufyan", "Admin"}, {"102", "alex", "Dev"}};
//
//        ReportFormatter jsonStrategy = new JsonFormatter();
//
//        String rawJson = jsonStrategy.format("UserAuditReport", headers, rows);
//
//        System.out.println(">>> TESTING DECORATOR STACK: " + "Base64(HeaderFooter(BaseFileExporter)) <<<");
//
//        Exporter securePipeline = new Base64EncryptDecorator(new HeaderFooterDecorator(new BaseFileExporter(), "2026-11-02 10:00:00 UTC"));
//
//        securePipeline.export("/var/logs/audit.sec", rawJson);
//    }
//}

//Task-2
//import java.util.HashMap;
//import java.util.Locale;
//import java.util.Map;
//
//class LegacyStockFeed {
//
//    public String fetchRawTickerPacket(String ticker) {
//        try {
//            Thread.sleep(250);
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//        }
//
//        if ("AAPL".equalsIgnoreCase(ticker)) {
//            return "TICKER=AAPL;BID=182.50;ASK=183.50;" + "STATUS=OPEN;TIMESTAMP=17300000";
//        }
//
//        if ("GOOGL".equalsIgnoreCase(ticker)) {
//            return "TICKER=GOOGL;BID=164.20;ASK=164.80;" + "STATUS=OPEN;TIMESTAMP=17300000";
//        }
//
//        return "TICKER=" + ticker + ";BID=0.00;ASK=0.00;" + "STATUS=UNKNOWN;TIMESTAMP=0";
//    }
//}
//
//interface StockProvider {
//    double getMarketPrice(String ticker);
//    String getStatus(String ticker);
//}
//
//class StockFeedAdapter implements StockProvider {
//    private final LegacyStockFeed legacyFeed;
//
//    public StockFeedAdapter(LegacyStockFeed legacyFeed) {
//        if (legacyFeed == null) {
//            throw new IllegalArgumentException("Legacy feed cannot be null");
//        }
//
//        this.legacyFeed = legacyFeed;
//    }
//
//    private Map<String, String> parsePacket(String raw) {
//        Map<String, String> result = new HashMap<>();
//
//        for (String token : raw.split(";")) {
//            String[] pair = token.split("=", 2);
//
//            if (pair.length == 2) {
//                result.put(pair[0].trim(), pair[1].trim());
//            }
//        }
//
//        return result;
//    }
//
//    @Override
//    public double getMarketPrice(String ticker) {
//        String raw = legacyFeed.fetchRawTickerPacket(ticker);
//        Map<String, String> data = parsePacket(raw);
//
//        double bid = Double.parseDouble(data.getOrDefault("BID", "0.0"));
//
//        double ask = Double.parseDouble(data.getOrDefault("ASK", "0.0"));
//
//        return (bid + ask) / 2.0;
//    }
//
//    @Override
//    public String getStatus(String ticker) {
//        String raw = legacyFeed.fetchRawTickerPacket(ticker);
//
//        return parsePacket(raw).getOrDefault("STATUS", "UNAVAILABLE");
//    }
//}
//
//
//class CachedStockProxy implements StockProvider {
//
//    private static class CacheEntry {
//        final double price;
//        final long timestamp;
//
//        CacheEntry(double price, long timestamp) {
//            this.price = price;
//            this.timestamp = timestamp;
//        }
//    }
//
//    private final StockProvider realProvider;
//    private final long ttlMillis;
//
//    private final Map<String, CacheEntry> cache = new HashMap<>();
//
//    public CachedStockProxy(StockProvider realProvider, long ttlMillis) {
//
//        if (realProvider == null) {
//            throw new IllegalArgumentException("Real provider cannot be null");
//        }
//
//        if (ttlMillis <= 0) {
//            throw new IllegalArgumentException("TTL must be positive");
//        }
//
//        this.realProvider = realProvider;
//        this.ttlMillis = ttlMillis;
//    }
//
//    @Override
//    public double getMarketPrice(String ticker) {
//        String key = ticker.toUpperCase(Locale.ROOT);
//        long now = System.currentTimeMillis();
//
//        CacheEntry entry = cache.get(key);
//
//        if (entry != null && now - entry.timestamp < ttlMillis) {
//            System.out.printf("[CACHE HIT] Returning cached price for %s: " + "$%.2f (Age: %d ms)%n", key, entry.price, now - entry.timestamp);
//
//            return entry.price;
//        }
//
//        System.out.printf("[NETWORK FETCH] Cache miss or expired for %s. " + "Calling upstream adapter...%n", key);
//
//        double freshPrice = realProvider.getMarketPrice(key);
//
//        cache.put(key, new CacheEntry(freshPrice, System.currentTimeMillis()));
//
//        return freshPrice;
//    }
//
//    @Override
//    public String getStatus(String ticker) {
//        return realProvider.getStatus(ticker);
//    }
//}
//
//class Task2Runner {
//
//    public static void main(String[] args)
//            throws InterruptedException {
//
//        LegacyStockFeed legacyFeed = new LegacyStockFeed();
//
//        StockProvider adapter = new StockFeedAdapter(legacyFeed);
//
//        StockProvider proxy = new CachedStockProxy(adapter, 1500);
//
//        System.out.println(">>> 1. Initial Request (Cold Cache) <<<");
//
//        System.out.printf("AAPL Price: $%.2f%n", proxy.getMarketPrice("AAPL"));
//
//        System.out.println("\n>>> 2. Immediate Repeat Request (Hot Cache) <<<");
//
//        System.out.printf("AAPL Price: $%.2f%n", proxy.getMarketPrice("AAPL"));
//
//        System.out.println("\n>>> 3. Sleeping 1600ms to invalidate cache... <<<");
//
//        Thread.sleep(1600);
//
//        System.out.println(">>> 4. Subsequent Request (Expired Cache) <<<");
//
//        System.out.printf("AAPL Price: $%.2f%n", proxy.getMarketPrice("AAPL"));
//    }
//}


//Task-4
public static class LegacyForeignOrder {
    private final String orderId;
    private final double gbpAmount;
    public LegacyForeignOrder(String orderId, double gbpAmount) {
        this.orderId = orderId;
        this.gbpAmount = gbpAmount;
    }
    public String getOrderId() { return orderId; }
    public double getGbpAmount() { return gbpAmount; }
}
public static class LegacyCurrencyConverter {
    public double convert(double amount, String from, String to) {
        if ("GBP".equalsIgnoreCase(from) && "USD".equalsIgnoreCase(to)) {
            return amount * 1.25; // 1 GBP = 1.25 USD
        }
        return amount;
    }
}

public interface PricingService {
    double calculateTotal();
    String getReceiptBreakdown();
}

static class CurrencyOrderAdapter implements PricingService {
    private final LegacyForeignOrder foreignOrder;
    private final LegacyCurrencyConverter converter;
    private final String targetCurrency;
    public CurrencyOrderAdapter(LegacyForeignOrder order, LegacyCurrencyConverter converter, String targetCurrency) {
        this.foreignOrder = order;
        this.converter = converter;
        this.targetCurrency = targetCurrency;
    }
    @Override
    public double calculateTotal() {
        return converter.convert(foreignOrder.getGbpAmount(), "GBP", targetCurrency);
    }

    @Override
    public String getReceiptBreakdown() {
        return "";
    }
}
@Override
public String getReceiptBreakdown() {
    return String.format("Order [%s] Base Item: $%.2f %s (Converted from %.2f GBP)",
            foreignOrder.getOrderId(), calculateTotal(), targetCurrency, foreignOrder.getGbpAmount());
}

public abstract class PricingDecorator implements PricingService {
    protected final PricingService wrappedService;
    public PricingDecorator(PricingService wrappedService) {
        if (wrappedService == null) throw new IllegalArgumentException("Wrapped service cannot be null");
        this.wrappedService = wrappedService;
    }
    @Override
    public double calculateTotal() {
        return wrappedService.calculateTotal();
    }
    @Override
    public String getReceiptBreakdown() {
        return wrappedService.getReceiptBreakdown();
    }
}
public static class GiftWrapDecorator extends PricingDecorator {
    public GiftWrapDecorator(PricingService wrapped) { super(wrapped); }
    @Override
    public double calculateTotal() {
        return super.calculateTotal() + 5.00; // Flat $5
    }
    @Override
    public String getReceiptBreakdown() {
        return super.getReceiptBreakdown() + " + [Gift Wrap: $5.00]";
    }
}
public static class ExpressCourierDecorator extends PricingDecorator {
    public ExpressCourierDecorator(PricingService wrapped) { super(wrapped); }
    @Override
    public double calculateTotal() {
        return super.calculateTotal() + 15.00; // Flat $15
    }
    @Override
    public String getReceiptBreakdown() {
        return super.getReceiptBreakdown() + " + [Express Courier: $15.00]";
    }
}
public static class CustomsDutyDecorator extends PricingDecorator {
    public CustomsDutyDecorator(PricingService wrapped) { super(wrapped); }
    @Override
    public double calculateTotal() {
        double subtotal = super.calculateTotal();
        return Math.round((subtotal * 1.085) * 100.0) / 100.0; // 8.5% customs tax
    }
    @Override
    public String getReceiptBreakdown() {
        return super.getReceiptBreakdown() + " + [Customs Duty: 8.5%]";
    }
}

public class Task4Runner {

    public static void main(String[] args) {
        LegacyForeignOrder order = new LegacyForeignOrder("ORD-9821", 100.00);

        LegacyCurrencyConverter converter = new LegacyCurrencyConverter();

        PricingService baseOrder = new CurrencyOrderAdapter(order, converter, "USD");

        System.out.printf("Base Converted: $%.2f%n", baseOrder.calculateTotal());

        PricingService completeCheckout = new CustomsDutyDecorator(new ExpressCourierDecorator(new GiftWrapDecorator(baseOrder)));

        System.out.println("\n>>> INVOICE BREAKDOWN <<<");

        System.out.println(completeCheckout.getReceiptBreakdown());

        System.out.printf("FINAL CHARGE TOTAL: $%.2f%n", completeCheckout.calculateTotal());
    }
}