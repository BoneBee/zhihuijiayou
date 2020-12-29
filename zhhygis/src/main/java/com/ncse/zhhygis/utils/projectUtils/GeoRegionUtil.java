package com.ncse.zhhygis.utils.projectUtils;

import java.text.NumberFormat;

import org.jboss.logging.Logger;

import com.supermap.data.GeoPoint;
import com.supermap.data.GeoRegion;
import com.supermap.data.Geometrist;
import com.supermap.data.Point2D;
import com.supermap.data.Point2Ds;

/**
 * 空间数据工具
 * 
 * @author Administrator
 *
 */
public class GeoRegionUtil {

	private final Logger log = Logger.getLogger(this.getClass());

	public static double pi = 3.1415926535897932384626;

	public static double ee = 0.00669342162296594323;

	public static double a = 6378245.0;

	/**
	 * 坐标点转面
	 * 
	 * @param pointss
	 * @return
	 */
	public static GeoRegion pointToGeoRegion(String[] pointss) {
		Point2D point2D;
		String[] point;
		double[] coord;
		Point2D[] point2DArray = new Point2D[pointss.length];
		int i = 0;
		for (String points : pointss) {
			point = points.split(",");
			coord = Topoint.lonLatToMercator(Double.parseDouble(point[1]), Double.parseDouble(point[0]));
			point2D = new Point2D(coord[0], coord[1]);
			point2DArray[i] = point2D;
			i++;
		}
		GeoRegion geoRegion = new GeoRegion(new Point2Ds(point2DArray));
		return geoRegion;
	}

	public static GeoRegion pointToGeoRegion(String pointsss) {
		String[] pointss = pointsss.split(";");
		return pointToGeoRegion(pointss);
	}

	public static Point2Ds getPoint2ds(String pointsss) {
		String[] pointss = pointsss.split(";");
		return getPoint2ds(pointss);
	}

	public static Point2Ds getPoint2ds(String[] pointss) {
		Point2D point2D;
		String[] point;
		double[] coord;
		Point2D[] point2DArray = new Point2D[pointss.length];
		int i = 0;
		for (String points : pointss) {
			point = points.split(",");
			coord = Topoint.lonLatToMercator(Double.parseDouble(point[1]), Double.parseDouble(point[0]));
			point2D = new Point2D(coord[0], coord[1]);
			point2DArray[i] = point2D;
			i++;
		}
		Point2Ds point2ds = new Point2Ds(point2DArray);
		return point2ds;
	}

	/**
	 * 判断是否点面相交
	 * 
	 * @param x
	 *            维度
	 * @param y
	 *            经度
	 * @param geoRegion
	 * @return
	 */
	public static boolean istrue(Double x, Double y, GeoRegion geoRegion) {
		double[] coord = Topoint.lonLatToMercator(x, y);
		return Geometrist.hasIntersection(geoRegion, new GeoPoint(coord[0], coord[1]));
	}

	public static double[] geoLoc2MeterXY(double x, double y) {
		double earthCircumferenceInMeters = 40075016.685578488;

		double halfEarthCircumferenceInMeters = earthCircumferenceInMeters / 2;

		double geoX = x;
		double geoY = y;

		double mx = geoX / 180.0 * halfEarthCircumferenceInMeters;

		double my = geoY / halfEarthCircumferenceInMeters * 180.0;
		my = 180 / Math.PI * (2 * Math.atan(Math.exp(my * Math.PI / 180.0)) - Math.PI / 2);

		return new double[] { mx, my };
	}

	

	// kafka转经纬度坐标转换
	public static double GpsJWStrToDouble(String str) {
		double res = 0;// 结果
		String s = "";
		int i;
		for (i = 0; i < str.length(); i++) {
			if (str.charAt(i + 2) == '.') {
				try {
					res = Integer.parseInt(s);
				} catch (Exception e) {
					res = 0;
				}
				break;
			} else {
				s = s + str.charAt(i);
			}
		}
		s = "";
		for (int j = i; j < str.length(); j++) {
			s = s + str.charAt(j);
		}
		try {
			res = res + Double.valueOf(s) / 60;

		} catch (Exception e) {
			res = 0;
		}
		return res;
	}
	
	
	
	public static Point realGPSToGcj02ll(Point realGPSPoint) {
		if (realGPSPoint != null && realGPSPoint.getX() > 0 && realGPSPoint.getY() > 0) {
			double dLat = transformLat(realGPSPoint.getX() - 105.0D, realGPSPoint.getY() - 35.0D);
			double dLon = transformLon(realGPSPoint.getX() - 105.0D, realGPSPoint.getY() - 35.0D);
			double radLat = realGPSPoint.getY() / 180.0D * 3.141592653589793D;
			double magic = Math.sin(radLat);
			magic = 1.0D - 0.00669342162296594D * magic * magic;
			double sqrtMagic = Math.sqrt(magic);

			dLat = dLat * 180.0D / (6335552.717000426D / (magic * sqrtMagic) * 3.141592653589793D);
			dLon = dLon * 180.0D / (6378245.0D / sqrtMagic * Math.cos(radLat) * 3.141592653589793D);
			double resultX = dLon + realGPSPoint.getX();
			double resultY = dLat + realGPSPoint.getY();
			return new Point (resultX, resultY);
		}
		return null;
	}
	
	private static double transformLon(double x, double y) {
		double ret = 300.0D + x + 2.0D * y + 0.1D * x * x + 0.1D * x * y + 0.1D * Math.sqrt(Math.abs(x));
		ret += (20.0D * Math.sin(6.0D * x * 3.141592653589793D) + 20.0D * Math.sin(2.0D * x * 3.141592653589793D)) * 2.0D / 3.0D;
		ret += (20.0D * Math.sin(x * 3.141592653589793D) + 40.0D * Math.sin(x / 3.0D * 3.141592653589793D)) * 2.0D / 3.0D;
		ret += (150.0D * Math.sin(x / 12.0D * 3.141592653589793D) + 300.0D * Math.sin(x / 30.0D * 3.141592653589793D)) * 2.0D / 3.0D;
		return ret;
	}

	// realGPSToLngLat调用
	private static double transformLat(double x, double y) {
		double ret = -100.0D + 2.0D * x + 3.0D * y + 0.2D * y * y + 0.1D * x * y + 0.2D * Math.sqrt(Math.abs(x));
		ret += (20.0D * Math.sin(6.0D * x * 3.141592653589793D) + 20.0D * Math.sin(2.0D * x * 3.141592653589793D)) * 2.0D / 3.0D;
		ret += (20.0D * Math.sin(y * 3.141592653589793D) + 40.0D * Math.sin(y / 3.0D * 3.141592653589793D)) * 2.0D / 3.0D;
		ret += (160.0D * Math.sin(y / 12.0D * 3.141592653589793D) + 320.0D * Math.sin(y * 3.141592653589793D / 30.0D)) * 2.0D / 3.0D;
		return ret;
	}


	public static void main(String[] args) {
		String[] points= {"10255.1482,2506.0092", 
				"10255.0807,2506.0515", 
				"10255.0294,2505.9960", 
				"10254.9844,2506.0194", 
				"10254.9265,2505.9651", 
				"10254.8582,2506.0157", 
				"10254.9033,2506.0742", 
				"10254.8310,2506.1349", 
				"10255.2656,2506.5600", 
				"10256.2737,2507.7204", 
				"10256.2679,2507.7901", 
				"10256.4524,2507.9987", 
				"10257.5987,2507.4609", 
				"10257.6651,2507.4114", 
				"10257.6209,2507.3469", 
				"10257.6929,2507.2884", 
				"10257.5718,2507.1483", 
				"10257.3232,2506.8661", 
				"10257.2507,2506.8569", 
				"10256.0655,2505.4945", 
				"10256.0719,2505.4276", 
				"10255.8866,2505.2152", 
				"10255.8076,2505.2667", 
				"10255.7533,2505.2135", 
				"10255.6831,2505.2653", 
				"10255.7269,2505.3227", 
				"10255.6913,2505.3575", 
				"10255.7402,2505.4194", 
				"10255.6649,2505.4814", 
				"10255.2446,2506.0733", 
				"10255.6730,2506.1661", 
				"10255.8072,2505.6647", 
				"10256.0400,2506.4381", 
				"10255.8486,2506.7492", 
				"10256.5423,2507.5835", 
				"10256.3231,2506.3925", 
				"10256.2860,2506.9097"
};
		for(String s:points) {
			String[] p=s.split(",");
			Point po=new Point(GpsJWStrToDouble(p[0]),GpsJWStrToDouble(p[1]));
			Point t=realGPSToGcj02ll(po);
			double[] coord = Topoint.lonLatToMercator(t.getX(), t.getY());
			
			System.out.println(s+"；转换后："+coord[0]+","+formatDouble(coord[0])+","+coord[1]);
		}
		/*Point p=new Point(GpsJWStrToDouble("10253.2603"),GpsJWStrToDouble("2503.0953"));
		Point t=realGPSToGcj02ll(p);*/
		//double[] t = gps84_To_Gcj02(GpsJWStrToDouble("2503.0953"),GpsJWStrToDouble("10253.2603"));
		
	}
	
	 private static String formatDouble(double d) {
         NumberFormat nf = NumberFormat.getInstance();
         //设置保留多少位小数
         nf.setMaximumFractionDigits(20);
          // 取消科学计数法
         nf.setGroupingUsed(false);
         //返回结果
         return nf.format(d);
     }
}
