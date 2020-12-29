package com.ncse.zhhygis.utils.projectUtils;

import org.springframework.util.StringUtils;

import com.ncse.zhhygis.utils.projectUtils.Point;

/**
 * ClassName:  [功能名称]
 * Description:  [一句话描述该类的功能]
 * Date:  2018/11/17 16 51
 *
 * @author Xugn
 * @version 1.0.0
 */
public class Topoint {

        /**
         * 经纬度转墨卡托
         * @param LonLat 经纬度坐标
         * @return
         */
        public static Point lonLatToMercator(Point LonLat){
           /* Point mercator = new Point();
            double x = (LonLat.getX() * 20037508.342789 / 180);
            double y = (Math.log(Math.tan((90 + LonLat.getY()) * Math.PI / 360)) / (Math.PI / 180));
            y = (double)(y * 20037508.342789 / 180);
            mercator.setX(x);
            mercator.setY(y);
            return mercator;*/
        	 Point mercator = new Point();
             double x = (LonLat.getX() * 20037508.342789 / 180);
             double y = (Math.log(Math.tan((90 + LonLat.getY()) * Math.PI / 360)) / (Math.PI / 180));
             y = (double)(y * 20037508.342789 / 180);
             mercator.setX(x);
             mercator.setY(y);
             return mercator;
        }

        /**
         * 经纬度转墨卡托
         * @return
         */
        public static double[] lonLatToMercator(double x,double y){
            Point mercator = new Point();
            double mx = (x * 20037508.342789 / 180);
            double mly=Math.tan((90 + y) * Math.PI / 360);
            double my = (Math.log(mly)) / (Math.PI / 180);
            my = (double)(my * 20037508.342789 / 180);
           /* mercator.setX(mx);
            mercator.setY(my);*/
            return new double[]{ mx, my };
        }


        /**
         * 墨卡托转经纬度
         * @param mercator 墨卡托坐标
         * @return
         */
        public static Point mercatorToLonLat(Point mercator){
            Point lonlat = new Point();
            double x = (mercator.getX() / 20037508.342789 * 180);
            double y =(mercator.getY() / 20037508.342789 * 180);
            y = (double) (180 / Math.PI * (2 * Math.atan(Math.exp(y * Math.PI / 180)) - Math.PI / 2));
            lonlat.setX(x);
            lonlat.setY(y);
            return lonlat;
        }

    public static void main(String[] args) {
        /*Point point = new Point();
        Point point1 = new Point();
        Double x =new Double("25.10976997234533");
        Double y =new Double("102.9366159439087");

        Double y1 =new Double("2887491.2398054646");
        Double x1 =new Double("11458007.310169352");
        point.setX(y);
        point.setY(x);
        Point point2 = lonLatToMercator(point);
        System.out.println("经纬度转墨卡托："+point2.getX()+","+point2.getY());
        point1.setX(x1);
        point1.setY(y1);
        Point point3 = mercatorToLonLat(point1);
        System.out.println("墨卡托转经纬度："+point3.getX()+","+point3.getY());*/
    	
    	double x=0.0;
    	double y=0.0;
    	System.out.println(x==0);
    }
}
