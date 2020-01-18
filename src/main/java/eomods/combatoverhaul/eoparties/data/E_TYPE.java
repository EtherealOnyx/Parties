package eomods.combatoverhaul.eoparties.data;

public enum E_TYPE {
    HEALTH(1),
    MAX_HEALTH(2),
    HUNGER(3),
    SATURATION(4),
    ARMOR(5);
    private int type;
    E_TYPE(int type) {
        this.type = type;
    }

    public int value() {
        return type;
    }

}
