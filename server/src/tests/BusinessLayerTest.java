package tests;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class BusinessLayerTest {
    @org.junit.Test
    public void getPlacesIntintelligently() throws IOException {
        long seed = 0;
        Random rand = new Random(seed);
        int rand100 = 0;
        for(int i = 0; i < 100; i++)
            rand100 = rand.nextInt();
        System.out.println(rand100);
    }

}