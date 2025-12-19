import java.util.*;
import java.text.*;

public class ShoppingCart {

    // --- Константи для усунення Magic Numbers ---
    private static final int MAX_TITLE_LENGTH = 32;
    private static final double MIN_PRICE = 0.01;
    private static final int MAX_DISCOUNT_PERCENT = 80;
    private static final int SALE_DISCOUNT = 70;
    private static final int SECOND_FREE_DISCOUNT = 50;
    private static final int QUANTITY_DISCOUNT_THRESHOLD = 10;

    public static enum ItemType { NEW, REGULAR, SECOND_FREE, SALE };

    private List<Item> items = new ArrayList<Item>();

    public static void main(String[] args) {
        ShoppingCart cart = new ShoppingCart();
        cart.addItem("Apple", 0.99, 5, ItemType.NEW);
        cart.addItem("Banana", 20.00, 4, ItemType.SECOND_FREE);
        cart.addItem("A long piece of toilet paper", 17.20, 1, ItemType.SALE);
        cart.addItem("Nails", 2.00, 500, ItemType.REGULAR);
        System.out.println(cart.formatTicket());
    }

    public void addItem(String title, double price, int quantity, ItemType type) {
        if (title == null || title.length() == 0 || title.length() > MAX_TITLE_LENGTH)
            throw new IllegalArgumentException("Illegal title");
        if (price < MIN_PRICE)
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

    private List<String[]> convertItemsToTableLines() {
        List<String[]> lines = new ArrayList<String[]>();
        int index = 0;
        for (Item item : items) {
            // Виправлено Feature Envy: Item сам рахує свою знижку
            item.setDiscount(item.calculateDiscount());

            // Розрахунок ціни
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
        for (Item item : items) {
            total += item.getTotalPrice();
        }
        return total;
    }

    // --- Helper methods ---
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

    // --- Item Class (Updated) ---
    public static class Item {
        private String title;
        private double price;
        private int quantity;
        private ItemType type;
        private int discount;
        private double total;

        // Метод перенесено сюди (Feature Envy fix)
        public int calculateDiscount() {
            int result = 0;
            switch (type) {
                case NEW:
                    return 0;
                case REGULAR:
                    result = 0;
                    break;
                case SECOND_FREE:
                    if (quantity > 1)
                        result = SECOND_FREE_DISCOUNT;
                    break;
                case SALE:
                    result = SALE_DISCOUNT;
                    break;
            }
            if (result < MAX_DISCOUNT_PERCENT) {
                result += quantity / QUANTITY_DISCOUNT_THRESHOLD;
                if (result > MAX_DISCOUNT_PERCENT)
                    result = MAX_DISCOUNT_PERCENT;
            }
            return result;
        }

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