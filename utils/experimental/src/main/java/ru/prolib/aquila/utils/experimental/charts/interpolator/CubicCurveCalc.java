package ru.prolib.aquila.utils.experimental.charts.interpolator;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TiM on 31.01.2017.
 */
public class CubicCurveCalc {
    private static double C = 0.33;

    public static List<Segment> calc(List<Pair<Double, Double>> points){
        List<Double> tgA = new ArrayList<>();
        List<Segment> result = new ArrayList<>();
        if(points.size()<2){
            return result;
        }
        for(int i=0; i< points.size(); i++){
            double x1, y1, x2, y2, x3, y3;
            if(i==0){
                x1 = points.get(i).getLeft();
                y1 = points.get(i).getRight();
                x2 = points.get(i+1).getLeft();
                y2 = points.get(i+1).getRight();
                tgA.add((y1-y2)/(x2-x1));
            } else if(i==points.size()-1){
                x1 = points.get(i-1).getLeft();
                y1 = points.get(i-1).getRight();
                x2 = points.get(i).getLeft();
                y2 = points.get(i).getRight();
                tgA.add((y1-y2)/(x2-x1));
            } else {
                x1 = points.get(i-1).getLeft();
                y1 = points.get(i-1).getRight();
                x2 = points.get(i).getLeft();
                y2 = points.get(i).getRight();
                x3 = points.get(i+1).getLeft();
                y3 = points.get(i+1).getRight();
                if((y1<y2 && y3<y2)||(y1>y2 && y3>y2)){
                    tgA.add(0.);
                } else {
                    tgA.add(((y1-y2)/(x2-x1) + (y2-y3)/(x3-x2))/2);
                }
            }

        }
        for(int i=0; i< points.size()-1; i++){
            double x1 = points.get(i).getLeft();
            double y1 = points.get(i).getRight();
            double x2 = points.get(i+1).getLeft();
            double y2 = points.get(i+1).getRight();

            double xc1 = x1 + (x2 - x1)*C;
            double yc1 = y1 - (xc1 - x1)*tgA.get(i);
            double xc2 = x2 - (x2 - x1)*C;
            double yc2 = y2 + (x2 - xc2)*tgA.get(i+1);

            Segment segment = new Segment(x1, y1, x2, y2, xc1, yc1, xc2, yc2);
            result.add(segment);
        }
        return result;
    }
}
