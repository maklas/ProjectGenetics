package ru.maklas.genetics.engine;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import ru.maklas.mengine.BundlerKey;
import ru.maklas.genetics.utils.Profiler;
import ru.maklas.genetics.utils.TimeSlower;
import ru.maklas.genetics.utils.gsm_lib.State;

@SuppressWarnings("all")
public class B {

    public static final BundlerKey<Batch> batch = BundlerKey.of("batch");
    public static final BundlerKey<OrthographicCamera> cam = BundlerKey.of("cam");
    public static final BundlerKey<Float> dt = BundlerKey.of("dt");
    public static final BundlerKey<TimeSlower> timeSlower = BundlerKey.of("timeSlower");
    public static final BundlerKey<Profiler> profiler = BundlerKey.of("profiler");
    public static final BundlerKey<State> gsmState = BundlerKey.of("state");

}
