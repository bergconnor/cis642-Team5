package edu.ksu.cis.waterquality;


import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.opencv.android.Utils;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

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
    public static Bitmap readImage(Bitmap bitmap) {
        System.loadLibrary("opencv_java3");

        Mat image = new Mat();
        Utils.bitmapToMat(bitmap, image);

        Mat imageGray = new Mat();
        cvtColor(image, imageGray, COLOR_BGR2GRAY);

        Mat imageHSV = new Mat(imageGray.size(), CvType.CV_8UC4);
        Mat imageBlurr = new Mat(imageGray.size(), CvType.CV_8UC4);
        Mat imageA = new Mat(imageGray.size(), CvType.CV_32F);

        Imgproc.cvtColor(image, imageHSV, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(imageHSV, imageBlurr, new Size(5,5), 0);
        Imgproc.adaptiveThreshold(imageBlurr, imageA, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY,7, 5);

        List<MatOfPoint> contours = findTestSquares(imageA);

        MatOfPoint2f approxCurve = new MatOfPoint2f();

        //For each contour found
        for (int i=0; i<contours.size(); i++)
        {
            //Convert contours(i) from MatOfPoint to MatOfPoint2f
            MatOfPoint2f contour2f = new MatOfPoint2f( contours.get(i).toArray() );
            //Processing on mMOP2f1 which is in type MatOfPoint2f
            double approxDistance = Imgproc.arcLength(contour2f, true)*0.02;
            Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);

            //Convert back to MatOfPoint
            MatOfPoint points = new MatOfPoint( approxCurve.toArray() );

            // Get bounding rect of contour
            Rect rect = Imgproc.boundingRect(points);

            // draw enclosing rectangle (all same color, but you could use variable i to make them unique)
            rectangle(image, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height), new Scalar(0, 255, 0, 255), 2);
        }

        Utils.matToBitmap(image, bitmap);
        return bitmap;
    }

    /* Finds all the contours in the image, then removes any non-square contours and contours that
     * are the incorrect size range.
     * Parameters:
     *      Mat img: the grayscale Mat of an image, allowing us to find the contours and narrow them
     *               down to the ones we want.
     * Returns:
     *      List<Rect>: holds the 12 test rectangles that need further processing.
     */
    private static List<MatOfPoint> findTestSquares(Mat img) {
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

        return samples;
    }

    /* Sorts the list of rectangles in order from upper left to bottom right.
     * Parameters:
     *      List<Rect> squares: The list of Rects to sort, should only contain 12 squares
     * Returns:
     *      List<Rect>: holds the sorted squares.
     */
    private static List<Rect> sortTestSquares(List<Rect> squares) {
        List<Rect> initSort = new ArrayList<Rect>();
        Rect next = new Rect();
        int size = squares.size();

        /* Runs through the list of squares and pulls the highest square (lowest y value) out from
         * the current list and puts it in a new list. This sorts all of the squares by y, but will
         * still need sorted by x. Overall, this puts the first column as the first 3, second column
         * as the second three and so forth. */
        for(int i = 0; i < size; i++) {
            for (int j = 0; j < squares.size(); j++) {
                if (next.y > squares.get(j).y || j == 0) {
                    next = squares.get(j);
                }
            }
            initSort.add(next);
            squares.remove(next);
            next = new Rect();
        }
        /* This will sort each set of three by their x value (lowest to the left, highest on right).
         * This will complete the overall sort, giving us a top-to-bottom, left-to-right sort. */
        int moves = 0;
        for(int i = 0; i < 4; i++) {
            do {
                for(int j = i*3; j < (i+1)*3; j++) {
                    if(j+1 < (i+1)*3 && initSort.get(j).x > initSort.get(j+1).x) {
                        next = initSort.get(j);
                        initSort.set(j, initSort.get(j+1));
                        initSort.set(j+1, next);
                        moves++;
                    }
                }
            } while(moves > 0);
        }
        return initSort;
    }

    /* Finds the colors inside each square by first shrinking the Rect object for the square to be
     * inside the square on the test card. It then finds the average color in each square and stores
     * it as a Scalar in BGR colorspace.
     * Parameters:
     *      Mat img: the original, color image that was grabbed initially
     *      List<Rect> squares: the sorted list of the test squares
     * Returns:
     *      List<Scalar>: holds the average color of each square in the same sort as the squares in
     *                    the list. It is stored in the BGRA colorspace (A will sometimes be 0
     *                    depending on if the initial image has an alpha)
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
}