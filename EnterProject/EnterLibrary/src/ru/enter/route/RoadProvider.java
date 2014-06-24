
package ru.enter.route;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.google.android.maps.GeoPoint;

public class RoadProvider {

        public static Road getRoute(InputStream is) {
                KMLHandler handler = new KMLHandler();
                try {
                        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
                        if (is != null)//TODO
                        	parser.parse(is, handler);
                } catch (ParserConfigurationException e) {
                	handler.mRoad.result=Road.CONNECTION_ERROR;
                        e.printStackTrace();
                } catch (SAXException e) {
                	handler.mRoad.result=Road.CONNECTION_ERROR;
                        e.printStackTrace();
                } catch (IOException e) {
                	handler.mRoad.result=Road.CONNECTION_ERROR;
                        e.printStackTrace();
                }
                catch (NullPointerException e) {
					handler.mRoad.result=Road.CONNECTION_ERROR;
				}
                return handler.mRoad;
        }

        public static String getUrl(double fromLat, double fromLon, double toLat,
                        double toLon) {// connect to map web service
        	StringBuffer urlString = new StringBuffer();
            urlString.append("https://maps.googleapis.com/maps/api/directions/xml?");
            urlString.append("origin=");// from
            urlString.append(Double.toString(fromLat));
            urlString.append(",");
            urlString.append(Double.toString(fromLon));
            urlString.append("&destination=");// to
            urlString.append(Double.toString(toLat));
            urlString.append(",");
            urlString.append(Double.toString(toLon));
            urlString.append("&language=en&sensor=true");
                return urlString.toString();
        }
}

class KMLHandler extends DefaultHandler {
        Road mRoad;
        boolean isStatus;
        boolean isOverviewPolyline;
        private String points;   
        private String mString;

        public KMLHandler() {
                mRoad = new Road();
        }

        public void startElement(String uri, String localName, String name,
                        Attributes attributes) throws SAXException {
        	if (localName.equalsIgnoreCase("status")) 
                isStatus = true;
        if (localName.equalsIgnoreCase("overview_polyline")) 
            isOverviewPolyline = true;
                mString = new String();
        }

        public void characters(char[] ch, int start, int length)
                        throws SAXException {
                String chars = new String(ch, start, length).trim();
                mString = mString.concat(chars);
        }

        public void endElement(String uri, String localName, String name)
                        throws SAXException {
        	if(localName.equals("status")&&isStatus)
    		{if(mString.equals("OK")) mRoad.result=Road.OK;
    		
    		else mRoad.result=Road.NOT_FOUND;
    			isStatus=false;
    		}
	    	if(localName.equals("overview_polyline"))
	    	{
	    		isOverviewPolyline=false;
	    		mRoad.mGeoPoint = (ArrayList<GeoPoint>) PolylineDecoder.decodePoints(points);
	    	}
	    	if(localName.equals("points")&&isOverviewPolyline)
	    	{
	    		points=mString;
	    		
	    	}
        }
        
}

class PolylineDecoder {
	/**
	 * Transform a encoded PolyLine to a Array of GeoPoints.
	 * Java implementation of the original Google JS code.
	 * @see Original encoding part: <a href="http://code.google.com/apis/maps/documentation/polylinealgorithm.html">http://code.google.com/apis/maps/documentation/polylinealgorithm.html</a>
	 * @return Array of all GeoPoints decoded from the PolyLine-String.
	 * @param encoded_points String containing the encoded PolyLine. 
	 * @param countExpected Number of points that are encoded in the PolyLine. Easiest way is to use the length of the ZoomLevels-String. 
	 * @throws DecodingException 
	 */
	public static List <GeoPoint> decodePoints(String encoded_points){
		int index = 0;
		int lat = 0;
		int lng = 0;
		List <GeoPoint> out = new ArrayList<GeoPoint>();

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
		        out.add(new GeoPoint((lat*10),(lng*10)));
		    }
		    return out;
		}catch(Exception e) {
		    e.printStackTrace();
		}
		return out;
	}
	
}
