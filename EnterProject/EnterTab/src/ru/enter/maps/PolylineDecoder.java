package ru.enter.maps;

import java.util.ArrayList;
import java.util.List;
import com.google.android.maps.GeoPoint;

public class PolylineDecoder {
	/**
	 * Transform a encoded PolyLine to a Array of GeoPoints. Java implementation
	 * of the original Google JS code.
	 * 
	 * @see Original encoding part: <a href=
	 *      "http://code.google.com/apis/maps/documentation/polylinealgorithm.html"
	 *      >http://code.google.com/apis/maps/documentation/polylinealgorithm.
	 *      html</a>
	 * @return Array of all GeoPoints decoded from the PolyLine-String.
	 * @param encoded_points
	 *            String containing the encoded PolyLine.
	 * @param countExpected
	 *            Number of points that are encoded in the PolyLine. Easiest way
	 *            is to use the length of the ZoomLevels-String.
	 * @throws DecodingException
	 */
	public static List<GeoPoint> decodePoints(String encoded_points) {
		int index = 0;
		int lat = 0;
		int lng = 0;
		List<GeoPoint> out = new ArrayList<GeoPoint>();

		try {
			int shift;
			int result;
			while (index < encoded_points.length()) {
				shift = 0;
				result = 0;
				while (true) {
					int b = encoded_points.charAt(index++) - '?';
					result |= ((b & 31) << shift);
					shift += 5;
					if (b < 32)
						break;
				}
				lat += ((result & 1) != 0 ? ~(result >> 1) : result >> 1);

				shift = 0;
				result = 0;
				while (true) {
					int b = encoded_points.charAt(index++) - '?';
					result |= ((b & 31) << shift);
					shift += 5;
					if (b < 32)
						break;
				}
				lng += ((result & 1) != 0 ? ~(result >> 1) : result >> 1);
				/* Add the new Lat/Lng to the Array. */
				out.add(new GeoPoint((lat * 10), (lng * 10)));
			}
			return out;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return out;
	}

}