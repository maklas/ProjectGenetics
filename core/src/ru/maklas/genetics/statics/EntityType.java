package ru.maklas.genetics.statics;


import ru.maklas.mengine.Entity;

public class EntityType {

    public static final int BACKGROUND = 1;
    public static final int CREEP = 2;
    public static final int TOWER = 3;
    public static final int PROJECTILE = 4;
    public static final int SOCKET = 5;
    private static final EntityType[] map;

    static {

        //masks
        int none = 1;
        int background = 2;
        int creep = 4;
        int tower = 8;
        int projectile = 16;


        int placeHolder10 = 32;
        int placeHolder9 = 64;
        int placeHolder8 = 128;
        int placeHolder7 = 256;
        int placeHolder6 = 512;
        int placeHolder5 = 1024;
        int placeHolder4 = 2048;
        int placeHolder3 = 4096;
        int placeHolder2 = 8192;
        int placeHolder1 = 16384;




        map = new EntityType[16];
        map[BACKGROUND]       = new EntityType(background, none);
        map[CREEP]            = new EntityType(creep, tower | projectile);
        map[TOWER]            = new EntityType(tower, creep);
        map[PROJECTILE]       = new EntityType(projectile, creep);
        map[SOCKET]     = new EntityType(none, none);
        //Маски можно перечислять через "|", чтобы их комбинировать: new EntityType(bird, pipe | coin | feather)
    }

    public static EntityType of(int code){
        if (code == 0){
            throw new RuntimeException("Code must be 1 or larger");
        }
        return map[code];
    }

    public static EntityType of(int code, EntityType def){
        if (code > 0 && code < map.length) {
            return map[code];
        }
        return def;
    }

    public static boolean isOneOf(Entity e, int... types){
        for (int type : types) {
            if (e.type == type) return true;
        }
        return false;
    }

    public final short category;
    public final short mask;

    public EntityType(int category, int mask) {
        this.category = (short) category;
        this.mask = (short) mask;
    }


    public static String typeToString(int code) {
        switch (code) {
            case BACKGROUND: return "BACKGROUND";
            case CREEP: return "CREEP";
            case TOWER: return "TOWER";
            case PROJECTILE: return "PROJECTILE";
            case SOCKET: return "SOCKET";
            default: return "UNKNOWN";
        }
    }
}
