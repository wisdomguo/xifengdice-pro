package com.wisdomguo.xifeng.modules.game.seedbag.enums;

/**
 * SeedSpecies
 * 种子类型
 * @author wisdom-guo
 * @since 2021/7/9
 */
public enum SeedSpecies {

    WHEAT("小麦", 1,1,50,55,60,1,"颗"),
    RICE("水稻", 2,1,100,115,120,1,"颗"),
    CHINESECABBAGE("大白菜", 3,1,150,170,175,1,"颗"),
    CABBAGE("圆白菜", 1,2,1,120,140,1,"颗"),
    KOHLRABI("大头菜", 2,2,5,600,650,1,"颗"),
    WATERMELON("西瓜", 3,2,10,1000,1100,1,"颗"),
    PEACH("桃子", 1,3,5,3000,3500,10,"斤"),
    CHERRY("樱桃", 2,3,10,5000,5500,10,"斤");

    private String title;
    private int code;
    private int type;
    private int price;
    private int minSell;
    private int maxSell;
    private int times;
    private String unit;
    public String getTitle() {
        return title;
    }

    public int getCode() {
        return code;
    }

    public int getType() {
        return type;
    }

    public int getPrice() {
        return price;
    }

    public int getMinSell() {
        return minSell;
    }

    public int getMaxSell() {
        return maxSell;
    }

    public int getTimes() {
        return times;
    }

    public String getUnit() {
        return unit;
    }

    private SeedSpecies(String title, int code,int type,int price,int minSell,int maxSell,int times,String unit) {
        this.title = title;
        this.code = code;
        this.type = type;
        this.price = price;
        this.minSell = minSell;
        this.maxSell = maxSell;
        this.times=times;
        this.unit=unit;
    }

    public static SeedSpecies valueOf(int code,int type) {
        for (SeedSpecies seedSpecies : SeedSpecies.values()) {
            if (seedSpecies.getCode() == code && seedSpecies.getType() == type) {
                return seedSpecies;
            }
        }
        return null;
    }
}
