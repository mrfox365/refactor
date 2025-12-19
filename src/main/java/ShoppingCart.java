import java.util.*;
import java.text.*;

public class ShoppingCart {

    public static enum ItemType { NEW, REGULAR, SECOND_FREE, SALE };

    private List<Item> items = new ArrayList<Item>();

    // --- MAIN для демонстрації ---
    public static void main(String[] args) {
        ShoppingCart cart = new ShoppingCart();
        cart.addItem("Apple", 0.99, 5, ItemType.NEW);
        cart.addItem("Banana", 20.00, 4, ItemType.SECOND_FREE);
        cart.addItem("A long piece of toilet paper", 17.20, 1, ItemType.SALE);
        cart.addItem("Nails", 2.00, 500, ItemType.REGULAR);
        System.out.println(cart.formatTicket());
    }

    public void addItem(String title, double price, int quantity, ItemType type) {
        if (title == null || title.length() == 0 || title.length() > 32)
            throw new IllegalArgumentException("Illegal title");
        if (price < 0.01)
            throw new IllegalArgumentException("Illegal price");
        if (quantity <= 0)
            throw new IllegalArgumentException("Illegal quantity");
        Item item = new Item();
        item.setTitle(title);
        item.setPrice(price);
        item.setQuantity(quantity);
        item.setType(type);
        items.add(item);
    }

    public String formatTicket() {
        if (items.size() == 0)
            return "No items.";

        List<String[]> lines = convertItemsToTableLines();
        String[] header = {"#", "Item", "Price", "Quan.", "Discount", "Total"};
        String[] footer = {String.valueOf(items.size()), "", "", "", "", MONEY.format(calculateTotal())};

        int[] width = new int[]{0, 0, 0, 0, 0, 0};
        int[] align = new int[]{1, -1, 1, 1, 1, 1};

        for (String[] line : lines) adjustColumnWidth(width, line);
        adjustColumnWidth(width, header);
        adjustColumnWidth(width, footer);

        int lineLength = width.length - 1;
        for (int w : width) lineLength += w;

        StringBuilder sb = new StringBuilder();
        appendFormattedLine(sb, header, align, width, true);
        appendSeparator(sb, lineLength);
        for (String[] line : lines) {
            appendFormattedLine(sb, line, align, width, true);
        }
        if (lines.size() > 0) appendSeparator(sb, lineLength);
        appendFormattedLine(sb, footer, align, width, false);

        return sb.toString();
    }

    // Extracted method (3.8)
    private List<String[]> convertItemsToTableLines() {
        List<String[]> lines = new ArrayList<String[]>();
        int index = 0;
        for (Item item : items) {
            item.setDiscount(calculateDiscount(item.getType(), item.getQuantity()));
            item.setTotalPrice(item.getPrice() * item.getQuantity() * (100.00 - item.getDiscount()) / 100.00);

            lines.add(new String[]{
                    String.valueOf(++index),
                    item.getTitle(),
                    MONEY.format(item.getPrice()),
                    String.valueOf(item.getQuantity()),
                    (item.getDiscount() == 0) ? "-" : (String.valueOf(item.getDiscount()) + "%"),
                    MONEY.format(item.getTotalPrice())
            });
        }
        return lines;
    }

    private double calculateTotal() {
        double total = 0.0;
        for(Item item : items) {
            total += item.getTotalPrice();
        }
        return total;
    }

    // --- Допоміжні методи ---
    private static final NumberFormat MONEY;
    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        MONEY = new DecimalFormat("$#.00", symbols);
    }

    public static void appendFormatted(StringBuilder sb, String value, int align, int width) {
        if (value.length() > width)
            value = value.substring(0, width);
        int before = (align == 0)
                ? (width - value.length()) / 2
                : (align == -1) ? 0 : width - value.length();
        int after = width - value.length() - before;
        while (before-- > 0) sb.append(" ");
        sb.append(value);
        while (after-- > 0) sb.append(" ");
        sb.append(" ");
    }

    public static int calculateDiscount(ItemType type, int quantity) {
        int discount = 0;
        switch (type) {
            case NEW: return 0;
            case REGULAR: discount = 0; break;
            case SECOND_FREE: if (quantity > 1) discount = 50; break;
            case SALE: discount = 70; break;
        }
        if (discount < 80) {
            discount += quantity / 10;
            if (discount > 80) discount = 80;
        }
        return discount;
    }

    private void appendSeparator(StringBuilder sb, int lineLength) {
        for (int i = 0; i < lineLength; i++) sb.append("-");
        sb.append("\n");
    }

    private void adjustColumnWidth(int[] width, String[] columns) {
        for (int i = 0; i < width.length; i++)
            width[i] = (int) Math.max(width[i], columns[i].length());
    }

    private void appendFormattedLine(StringBuilder sb, String[] line, int[] align, int[] width, Boolean newLine) {
        for (int i = 0; i < line.length; i++)
            appendFormatted(sb, line[i], align[i], width[i]);
        if (newLine) sb.append("\n");
    }

    public static class Item {
        private String title;
        private double price;
        private int quantity;
        private ItemType type;
        private int discount;
        private double total;

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public ItemType getType() { return type; }
        public void setType(ItemType type) { this.type = type; }
        public int getDiscount() { return discount; }
        public void setDiscount(int discount) { this.discount = discount; }
        public double getTotalPrice() { return total; }
        public void setTotalPrice(double total) { this.total = total; }
    }
}