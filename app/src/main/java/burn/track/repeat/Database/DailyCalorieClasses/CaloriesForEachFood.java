package burn.track.repeat.Database.DailyCalorieClasses;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "CaloriesForEachFood")
public class CaloriesForEachFood {
    @PrimaryKey(autoGenerate = true)

    public long caloriesForEachFoodId;
    public long uniqueIdTiedToEachFood;

    String typeOfFood;
    double portionForEachFoodType;
    double caloriesConsumedForEachFoodType;

    public long timeAdded;

    public long getCaloriesForEachFoodId() {
        return caloriesForEachFoodId;
    }

    public void setCaloriesForEachFoodId(long caloriesForEachFoodId) {
        this.caloriesForEachFoodId = caloriesForEachFoodId;
    }

    public long getUniqueIdTiedToEachFood() {
        return uniqueIdTiedToEachFood;
    }

    public void setUniqueIdTiedToEachFood(long uniqueIdTiedToEachFood) {
        this.uniqueIdTiedToEachFood = uniqueIdTiedToEachFood;
    }

    public String getTypeOfFood() {
        return typeOfFood;
    }

    public void setTypeOfFood(String typeOfFood) {
        this.typeOfFood = typeOfFood;
    }

    public double getPortionForEachFoodType() {
        return portionForEachFoodType;
    }

    public void setPortionForEachFoodType(double portionForEachFoodType) {
        this.portionForEachFoodType = portionForEachFoodType;
    }

    public double getCaloriesConsumedForEachFoodType() {
        return caloriesConsumedForEachFoodType;
    }

    public void setCaloriesConsumedForEachFoodType(double caloriesConsumedForEachFoodType) {
        this.caloriesConsumedForEachFoodType = caloriesConsumedForEachFoodType;
    }

    public long getTimeAdded() {
        return timeAdded;
    }

    public void setTimeAdded(long timeAdded) {
        this.timeAdded = timeAdded;
    }
}
