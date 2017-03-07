package edu.ksu.cis.waterquality;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.opencv.android.Utils;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import static java.lang.Math.abs;
import static org.opencv.core.Core.*;
import static org.opencv.imgproc.Imgproc.*;

/* Supplies the methods to perform the image processing required to gather color information of the
 * individual tests on the card. */
public class ImageProc {
    private static final double LOWER_BOUND = 0.015;
    private static final double UPPER_BOUND = 0.03;

    /* Takes in the image taken by the camera and converts it to a Mat that can then be used to
     * perform the image processing needed.
     * Parameters:
     *      String fileName: name of the picture file to perform the processing on.
     */
    public static List<Scalar> readImage(Bitmap bitmap) {
        try {
            System.loadLibrary("opencv_java3");

            Mat image = new Mat();
            Utils.bitmapToMat(bitmap, image);

            Mat imageGray = new Mat();
            cvtColor(image, imageGray, COLOR_BGR2GRAY);

            Mat imageHSV = new Mat(imageGray.size(), CvType.CV_8UC4);
            Mat imageBlurr = new Mat(imageGray.size(), CvType.CV_8UC4);
            Mat imageA = new Mat(imageGray.size(), CvType.CV_32F);

            Imgproc.cvtColor(image, imageHSV, Imgproc.COLOR_BGR2GRAY);
            Imgproc.GaussianBlur(imageHSV, imageBlurr, new Size(5, 5), 0);
            Imgproc.adaptiveThreshold(imageBlurr, imageA, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 7, 5);

            List<Rect> squares = findTestSquares(imageA);
            squares = sortTestSquares(squares);
            if (squares.size() != 12)
            {
                throw new IllegalArgumentException("Did not find all squares.");
            }

            return findColor(image, squares);

        } catch (Exception ex)
        {
            return new ArrayList<Scalar>();
        }
    }

    /** Finds all the contours in the image, then removes any non-square contours and contours that
     * are the incorrect size range.
     * Parameters:
     *      Mat img: the grayscale Mat of an image, allowing us to find the contours and narrow them
     *               down to the ones we want.
     * Returns:
     *      List<Rect>: holds the 12 test rectangles that need further processing.
     */
    private static List<Rect> findTestSquares(Mat img) {
        /* Uses findContours to find all contours, or closed shapes, in the image. */
        List<MatOfPoint> contours = new ArrayList<>();
        List<MatOfPoint> squares = new ArrayList<>();
        Mat hierarchy = new Mat();
        findContours(img, contours, hierarchy, RETR_CCOMP, CHAIN_APPROX_SIMPLE);
        int buffer[] = new int[(int)(hierarchy.total()*hierarchy.channels())];
        hierarchy.get(0, 0, buffer);

        /* Removes all none-square and improperly sized contours from the list, leaving only
         * the outside and inside contours of each test square. */
        for(int i = 0; i < contours.size(); i++) {
            if (buffer[(i*4)+3] >= 0) {
                MatOfPoint2f tempMat = new MatOfPoint2f(contours.get(i).toArray());
                MatOfPoint2f polyMat = new MatOfPoint2f();
                approxPolyDP(tempMat, polyMat, 10, true);
                if (polyMat.total() == 4) {
                    squares.add(contours.get(i));
                }
            }
        }

        List<Double> areas = new ArrayList<>();
        for (int i = 0; i < squares.size(); i++) {
            Double area = contourArea(squares.get(i));
            areas.add(area);
        }
        Collections.sort(areas);

        Collections.sort(squares, new Comparator<MatOfPoint>() {
            @Override
            public int compare(MatOfPoint o1, MatOfPoint o2) {
                Double a1 = contourArea(o1);
                Double a2 = contourArea(o2);
                return a1.compareTo(a2);
            }
        });

        Double borderArea = contourArea(squares.get(squares.size()-1));
        Double minArea = (LOWER_BOUND*borderArea);
        Double maxArea = (UPPER_BOUND*borderArea);

        List<MatOfPoint> samples = new ArrayList<>();
        for(int i = 0; i < squares.size(); i++) {
            MatOfPoint square = squares.get(i);
            Double area = contourArea(square);
            if (area >= minArea && area <= maxArea) {
                samples.add(squares.get(i));
            }
        }

        List<Rect> rectangles = new ArrayList<>();
        MatOfPoint2f approxCurve = new MatOfPoint2f();
        for (int i=0; i<samples.size(); i++)
        {
            //Convert contours(i) from MatOfPoint to MatOfPoint2f
            MatOfPoint2f contour2f = new MatOfPoint2f( samples.get(i).toArray() );
            //Processing on mMOP2f1 which is in type MatOfPoint2f
            double approxDistance = Imgproc.arcLength(contour2f, true)*0.02;
            Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);

            //Convert back to MatOfPoint
            MatOfPoint points = new MatOfPoint( approxCurve.toArray() );

            // Get bounding rect of contour
            Rect rect = Imgproc.boundingRect(points);

            rectangles.add(rect);
        }

        return rectangles;
    }

    /** Sorts the list of rectangles in order from upper left to bottom right.
     *
     * @param squares The list of Rects to sort, should only contain 12 squares
     * @return        holds the sorted squares.
     */
    private static List<Rect> sortTestSquares(List<Rect> squares) {
        Collections.sort(squares, new Comparator<Rect>() {
            @Override
            public int compare(Rect o1, Rect o2) {
                Double diffX = o1.tl().x - o2.tl().x;
                Double diffY = o1.tl().y - o2.tl().y;

                if (abs(diffY) < 10.0)
                {
                    if (abs(diffX) < 10.0)
                    {
                        return 0;
                    }
                    else if (diffX < 0)
                    {
                        return -1;
                    }
                    else
                    {
                        return 1;
                    }
                }
                else if (diffY < 0)
                {
                    return -1;
                }
                else
                {
                    return 1;
                }
            }
        });
        return squares;
    }

    /** Finds the colors inside each square by first shrinking the Rect object for the square to be
     * inside the square on the test card, then finds the average color in each square and stores
     * it as a Scalar in BGR colorspace.
     *
     * @param img     the original, color image that was grabbed initially
     * @param squares the sorted list of the test squares
     * @return        holds the average color of each square in the same sort as the squares in the
     *                list. It is stored in the BGRA colorspace ('A' will sometimes be 0 depending
     *                on if the initial image has an alpha)
     */
    private static List<Scalar> findColor(Mat img, List<Rect> squares) {
        List<Scalar> colors = new ArrayList<Scalar>();
        /* This initially shrinks the squares by a percentage in order to only grab the internal
         * color of each square, and not grab the outline itself. Default set to 1.5% for x and 1%
         * for y. */
        for(int i = 0; i < squares.size(); i++) {
            squares.get(i).x = (int)(squares.get(i).x + img.width() * LOWER_BOUND);
            squares.get(i).y = (int)(squares.get(i).y + img.height() * UPPER_BOUND);
            squares.get(i).height = (int)(squares.get(i).height - img.height() * (2*UPPER_BOUND));
            squares.get(i).width = (int)(squares.get(i).width - img.width() * (2*LOWER_BOUND));
        }
        /* Uses a mask to find the average color of a single square, then stores it in the List of
         * Scalars. */
        for(int i = 0; i < squares.size(); i++) {
            Mat mask = new Mat(img.height(), img.width(), 0);
            List<MatOfPoint> poly = new ArrayList<MatOfPoint>();
            Point[] points = new Point[4];

            points[0] = new Point(squares.get(i).x, squares.get(i).y);
            points[1] = new Point(squares.get(i).x + squares.get(i).width, squares.get(i).y);
            points[2] = new Point(squares.get(i).x + squares.get(i).width, squares.get(i).y + squares.get(i).height);
            points[3] = new Point(squares.get(i).x, squares.get(i).y + squares.get(i).height);
            poly.add(new MatOfPoint(points));

            fillPoly(mask, poly, new Scalar(255));

            colors.add(mean(img, mask));
        }
        return colors;
    }

    /** Performs simple linear regression on the color values of the squares and their corresponding
     * strength values. It will then find the strength of the test squares. Currently uses just
     * saturation to perform the linear regression.
     *
     * @param colorVals: the color values of the test squares.
     */
    public static double linearRegression(List<Scalar> colorVals) {
        double[] colorArr = new double[colorVals.size() - 4]; //minus 4 as we take off the two test squares and the positve and negative squares
        double[] percVals = { 90, 85, 80, 75, 70, 65, 60, 55 }; //these percvals are preset for now, but will eventually be loaded by the app.
        for(int i = 2; i < colorVals.size() - 2; i++) {
            float[] tempHSV = new float[3];
            Color.RGBToHSV((int)colorVals.get(i).val[2], (int)colorVals.get(i).val[1], (int)colorVals.get(i).val[0], tempHSV);
            colorArr[i-2] = tempHSV[1];
        }
        LinearRegression test = new LinearRegression(colorArr, percVals);
        float[] temp = new float[3];
        Color.RGBToHSV((int)colorVals.get(10).val[2], (int)colorVals.get(10).val[1], (int)colorVals.get(10).val[0], temp);
        double testVal1 = temp[1];
        Color.RGBToHSV((int)colorVals.get(11).val[2], (int)colorVals.get(11).val[1], (int)colorVals.get(11).val[0], temp);
        double testVal2 = temp[1];
        double predictPerc1 = test.predict(testVal1);
        double predictPerc2 = test.predict(testVal2);
        double avg = (predictPerc1 + predictPerc2) / 2;
        System.out.print(avg);
        return avg;
    }

    /** Creates a line graph giving the linear curve of the test results, showing concentration
     * on the Y-axis and the squares the test was taken from on the X-axis.
     *
     * @param colorVals the Scalar values of the colors from each square in the RGB colorspace.
     * @throws Exception Throws IllegalArgumentException if there is not the correct number of
     *                   colors.
     */
    public static LineData createData(List<Scalar> colorVals) throws Exception {
        if(colorVals.size() != 12) {
            throw new IllegalArgumentException();
        }

        float[][] hsvColors = new float[colorVals.size()][3];
        for(int i = 0; i < colorVals.size(); i++) {
            Color.RGBToHSV((int)colorVals.get(i).val[2], (int)colorVals.get(i).val[1], (int)colorVals.get(i).val[0], hsvColors[i]);
        }

        double[] percVals = { 90, 85, 80, 75, 70, 65, 60, 55 }; //defaulted to this, will implement variable values

        //Creating the data set for the line graph here.
        ArrayList<Entry> lineGraphEntries = new ArrayList<Entry>();
        ArrayList<String> labels = new ArrayList<String>();
        for (int i = 2; i < colorVals.size() - 2; i++) {
            lineGraphEntries.add(i - 2, new Entry(i-2, hsvColors[i][1]));
            labels.add(i - 2, Double.toString(percVals[i - 2]));
        }

        LineDataSet dataset = new LineDataSet(lineGraphEntries, "Values");
        LineData data = new LineData(dataset);
        return data;
    }
}