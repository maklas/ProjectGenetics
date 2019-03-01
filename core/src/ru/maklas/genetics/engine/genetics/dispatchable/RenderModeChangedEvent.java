package ru.maklas.genetics.engine.genetics.dispatchable;

import ru.maklas.genetics.engine.genetics.ChromosomeRenderMode;
import ru.maklas.genetics.engine.other.Event;

public class RenderModeChangedEvent implements Event {

    ChromosomeRenderMode renderMode;

    public RenderModeChangedEvent(ChromosomeRenderMode renderMode) {
        this.renderMode = renderMode;
    }

    public ChromosomeRenderMode getRenderMode() {
        return renderMode;
    }
}
