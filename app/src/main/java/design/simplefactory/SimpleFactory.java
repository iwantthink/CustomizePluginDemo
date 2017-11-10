package design.simplefactory;

/**
 * Created by renbo on 2017/11/9.
 */

public class SimpleFactory {

    public static Product createProduct(String productType) {
        if (productType.equals("A")) {
            return new ProductA();
        } else if (productType.equals("B")) {
            return new ProductB();
        } else {
            return null;
        }
    }

}

