import org.apache.commons.lang3.RandomStringUtils;

import java.util.List;

public class Order {

    private List<String> ingredients;

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public static String getSomeRandomIngredientHash(){

        return "61c0c5a71d1f82001bdaaa6d" + RandomStringUtils.randomAlphabetic(1);
    }
}
