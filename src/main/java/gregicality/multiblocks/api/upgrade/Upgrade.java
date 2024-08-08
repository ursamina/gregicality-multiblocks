package gregicality.multiblocks.api.upgrade;

public class Upgrade {

    public int currentValue;
    public int maximumValue;
    public int minimumValue;

    public int incrementValue;
    public int upgradeCost;

    public Upgrade(int currentValue, int minimumValue, int maximumValue, int incrementValue, int upgradeCost) {
        this.currentValue = currentValue;
        this.maximumValue = maximumValue;
        this.minimumValue = minimumValue;
        this.incrementValue = incrementValue;
        this.upgradeCost = upgradeCost;
    }

    public final int getCurrentValue() {
        return currentValue;
    }

    public final void setCurrentValue(int amount) {
        this.currentValue = amount;
    }

    public final int getMemoryConsumption() {
        return this.getAllocatedPoints() * this.upgradeCost;
    }

    public final int getMax() {
        return maximumValue;
    }

    public final int getMin() {
        return minimumValue;
    }

    public final int getIncrementValue() {
        return incrementValue;
    }

    public final int getAllocatedPoints() {
        return Math.abs((currentValue - minimumValue) / incrementValue);
    }

    public final int getMaxPoints() {
        return maximumValue / incrementValue;
    }

    public boolean isEnabled() {
        return currentValue != 0;
    }
}
