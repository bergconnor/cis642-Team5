package edu.ksu.cis.waterquality;

import org.opencv.core.Scalar;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Jacob on 3/7/2017.
 */
public class ImageProcTest {
    @org.junit.Test
    public void testCreateDataOnlyAcceptsTwelve() throws Exception {
        try {
            List<Scalar> testList = new ArrayList<Scalar>();
            for(int i = 0; i < 13; i++) {
                testList.add(new Scalar(Math.random(), Math.random(), Math.random()));
            }
        } catch(IllegalArgumentException e) {
            assertTrue("createData throws IllegalArgumentException", true);
        }
    }

}