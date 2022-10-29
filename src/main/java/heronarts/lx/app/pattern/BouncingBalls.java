package heronarts.lx.app.pattern;

import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.app.FixtureMap;
import heronarts.lx.color.ColorParameter;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXModel;
import heronarts.lx.model.LXPoint;
import heronarts.lx.modulator.LinearEnvelope;
import heronarts.lx.modulator.SinLFO;
import heronarts.lx.modulator.TriangleLFO;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.parameter.DiscreteParameter;
import heronarts.lx.parameter.FunctionalParameter;
import heronarts.lx.parameter.LXParameter;
import heronarts.lx.pattern.LXPattern;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@LXCategory("20mish")
public class BouncingBalls extends LXPattern {
    public final DiscreteParameter ballCount =
            new DiscreteParameter("Count", 1, 1, 100)
                    .setDescription("Ball count");

    protected final CompoundParameter rate = (CompoundParameter)
            new CompoundParameter("Rate", .25, .01, 5)
                    .setExponent(2)
                    .setUnits(LXParameter.Units.HERTZ)
                    .setDescription("Rate of movement");

    protected final TriangleLFO phase = new TriangleLFO(0, 1, new FunctionalParameter() {
        public double getValue() {
            return 1000 / rate.getValue();
        }
    });

    public final ColorParameter color = new ColorParameter("Color", 0xffFFFFFF)
            .setDescription("Color of the balls");

    ArrayList<List<Integer>> ballCoords = new ArrayList<List<Integer>>();
    ArrayList<List<Integer>> ballDirections = new ArrayList<List<Integer>>();
    float lastPhase = (float)0.0;

    public BouncingBalls(LX lx) {
        super(lx);
        startModulator(this.phase);
        addParameter("color", this.color);
        addParameter("rate", this.rate);
        addParameter("width", this.ballCount);
        this.ballCoords = new ArrayList<List<Integer>>(Arrays.asList(Arrays.asList(0,0)));
        this.ballDirections = new ArrayList<List<Integer>>(Arrays.asList(Arrays.asList(getRandomXScalar(),1)));
    }

    public int getRandomDirection(){
        return (Math.random() >= 0.5 ? 1 : -1);
    }

    public int getRandomXScalar(){
        int scalar = (int) (Math.random() * 20);
        // don't let it be zero
        return scalar == 0 ? 1 : scalar;
    }

    public void moveBall(int ballIndex) {
//        System.out.println("Moving ball " + ballIndex);

        if (ballIndex >= this.ballCoords.size()) {
            // add a new ball to a random location
            this.ballCoords.add(Arrays.asList((int)(Math.random() * (FixtureMap.maxCoords[0] - 1)), (int)(Math.random() * (FixtureMap.maxCoords[1] - 1))));
            this.ballDirections.add(Arrays.asList(getRandomDirection() * getRandomXScalar(), getRandomDirection()));
            return;
        }

        List<Integer> ballDirection = this.ballDirections.get(ballIndex);
        List<Integer> ballCoord = this.ballCoords.get(ballIndex);


//        System.out.println("ballCoord: " + ballCoord);
//        System.out.println("ballDirection: " + ballDirection);

        // overflow check, bounce if will overflow
        if (ballCoord.get(0) + ballDirection.get(0) >= FixtureMap.maxCoords[0] || ballCoord.get(0) + ballDirection.get(0) < 0) {
            ballDirection.set(0, -1 * ballDirection.get(0));
        }
        if (ballCoord.get(1) + ballDirection.get(1) >= FixtureMap.maxCoords[1] || ballCoord.get(1) + ballDirection.get(1) < 0) {
            ballDirection.set(1, -1 * ballDirection.get(1));
        }

        ballCoord.set(0, ballCoord.get(0) + ballDirection.get(0));
        ballCoord.set(1, ballCoord.get(1) + ballDirection.get(1));

        this.ballCoords.set(ballIndex, ballCoord);
        this.ballDirections.set(ballIndex, ballDirection);
    }

    public void run(double deltaMs) {
        float phase = this.phase.getValuef();
        int ballCount = this.ballCount.getValuei();

        // only move the balls if it's time
        if (!(Math.abs(phase - lastPhase) >= 0.1)) {
            return;
        }
        lastPhase = phase;

        // all pixels black
        for (int fixtureNum = 0; fixtureNum <  model.children.length; fixtureNum++) {
            LXModel fixture = model.children[fixtureNum];
            for(int ledNum = 0; ledNum < fixture.points.length; ledNum++) {
                LXPoint point = fixture.points[ledNum];
                colors[point.index] = LXColor.BLACK;
            }

        }

        for(int i = 0; i < ballCount; i++) {
            moveBall(i);
            List<Integer> ballCoord = this.ballCoords.get(i);
            List<Integer> pixelCoords = FixtureMap.getPixelAt(ballCoord.get(0), ballCoord.get(1));
            // light up the pixel at pixelCoords
            int fixtureIndex = pixelCoords.get(0);
            int ledIndex = pixelCoords.get(1);
            LXModel fixture = model.children[fixtureIndex];
            LXPoint point = fixture.points[ledIndex];
            colors[point.index] = this.color.calcColor();
        }

//        List<Integer> pixelCoords = FixtureMap.getPixelAt(ballCoords.get(0), ballCoords.get(1));
//        int fixtureIndex = pixelCoords.get(0);
//        int ledIndex = pixelCoords.get(1);

        // paint the balls

    }
}
