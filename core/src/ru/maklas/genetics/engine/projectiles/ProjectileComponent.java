package ru.maklas.genetics.engine.projectiles;

import ru.maklas.mengine.Component;
import ru.maklas.mengine.Entity;

public class ProjectileComponent implements Component {

    public Entity source;

    public ProjectileComponent(Entity source) {
        this.source = source;
    }
}
