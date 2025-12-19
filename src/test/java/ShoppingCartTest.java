import org.junit.Test;
import static org.junit.Assert.*;

public class ShoppingCartTest {

    @Test
    public void testAppendFormatted() {
        StringBuilder sb = new StringBuilder();
        ShoppingCart.appendFormatted(sb, "SomeLine", 0, 14);
        assertEquals(sb.toString(), "   SomeLine    ");
    }

    @Test
    public void testCalculateDiscount() {
        // Тепер ми тестуємо метод через об'єкт Item
        assertEquals(80, createItem(ShoppingCart.ItemType.SALE, 500).calculateDiscount());
        assertEquals(73, createItem(ShoppingCart.ItemType.SALE, 30).calculateDiscount());
        assertEquals(71, createItem(ShoppingCart.ItemType.SALE, 10).calculateDiscount());
        assertEquals(70, createItem(ShoppingCart.ItemType.SALE, 9).calculateDiscount());
        assertEquals(70, createItem(ShoppingCart.ItemType.SALE, 1).calculateDiscount());

        assertEquals(0, createItem(ShoppingCart.ItemType.NEW, 20).calculateDiscount());
        assertEquals(0, createItem(ShoppingCart.ItemType.NEW, 10).calculateDiscount());
        assertEquals(0, createItem(ShoppingCart.ItemType.NEW, 1).calculateDiscount());

        assertEquals(80, createItem(ShoppingCart.ItemType.SECOND_FREE, 500).calculateDiscount());
        assertEquals(53, createItem(ShoppingCart.ItemType.SECOND_FREE, 30).calculateDiscount());
        assertEquals(51, createItem(ShoppingCart.ItemType.SECOND_FREE, 10).calculateDiscount());
        assertEquals(50, createItem(ShoppingCart.ItemType.SECOND_FREE, 9).calculateDiscount());
        assertEquals(50, createItem(ShoppingCart.ItemType.SECOND_FREE, 2).calculateDiscount());
        assertEquals(0, createItem(ShoppingCart.ItemType.SECOND_FREE, 1).calculateDiscount());
    }

    // Допоміжний метод для тестів, щоб не писати багато коду
    private ShoppingCart.Item createItem(ShoppingCart.ItemType type, int quantity) {
        ShoppingCart.Item item = new ShoppingCart.Item();
        item.setType(type);
        item.setQuantity(quantity);
        return item;
    }
}