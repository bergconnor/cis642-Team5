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
    public void testCreateDataFailsOnLargerThanTwelveList() throws Exception {
        try {
            List<Scalar> testList = new ArrayList<Scalar>();
            for(int i = 0; i < 13; i++) {
                testList.add(new Scalar(Math.random(), Math.random(), Math.random()));
            }
            ImageProc.createData(testList);
            assertFalse("Method should have thrown IllegalArgumentException", false);
        } catch(IllegalArgumentException e) {
            assertTrue("createData throws IllegalArgumentException", true);
        }
    }

    @org.junit.Test
    public void testCreateDataFailsOnSmallerThanTwelveList() throws Exception {
        try {
            List<Scalar> testList = new ArrayList<Scalar>();
            for (int i = 0; i < 8; i++) {
                testList.add(new Scalar((int)(Math.random() * 251), (int)(Math.random() * 251), (Math.random() * 251));
            }
            ImageProc.createData(testList);
            assertFalse("Method should have thrown IllegalArgumentException", false);
        } catch (IllegalArgumentException e) {
            assertTrue("createData throws IllegalArgumentException", true);
        }
    }

    /*@org.junit.Test
    public void testCreateDataDoesntAcceptScalarValuesBeyondBoundsOfRGB() {
        try {
            List<Scalar> testList = new
        }
    }
    */


}