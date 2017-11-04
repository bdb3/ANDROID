import android.content.Context;
import android.location.Location;

import com.example.edwin.photoarchive.GPSTracker;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.junit.runner.RunWith;


/**
 * Created by aracelilopez on 11/3/17.
 */

@RunWith(MockitoJUnitRunner.class)
public class GPSTrackerTest {

    @Mock
    GPSTracker gps;

    @Mock
    private GPSTracker isGPSEnabled;

    @Mock
    private GPSTracker isNetworkEnabled;

    @Mock
    private GPSTracker isGPSNotEnabled;

    @Mock
    private GPSTracker isNetworkNotEnabled;

    @Mock
    private Location location;

    @Mock
    private Context context;


    public GPSTrackerTest(Context context) {
        this.context = context;


    }

    public Location getLocation(){
        return location;
    }

    public void setIsGPSEnabled(GPSTracker isGPSEnabled) {
        this.isGPSEnabled = isGPSEnabled;
    }

    public GPSTracker getIsGPSEnabled() {
        return isGPSEnabled;
    }

    public void setIsGPSNotEnabled(GPSTracker isGPSNotEnabled) {
        this.isGPSNotEnabled = isGPSNotEnabled;

    }

    public GPSTracker getIsGPSNotEnabled() {
        return isGPSNotEnabled;
    }

    public void setIsNetworkEnabled(GPSTracker isNetworkEnabled) {
        this.isNetworkEnabled = isNetworkEnabled;
    }

    public GPSTracker getIsNetworkEnabled() {
        return isNetworkEnabled;
    }

    public void setIsNetworkNotEnabled(GPSTracker isNetworkNotEnabled) {
        this.isNetworkNotEnabled = isNetworkNotEnabled;
    }

    public GPSTracker getIsNetworkNotEnabled() {
        return isNetworkNotEnabled;
    }

    @Test
    public void GpsValidator_GPSisEnabled_ReturnTrue() {

        assertTrue(gps.equals(isGPSEnabled) && gps.equals(isNetworkEnabled));
    }

    @Test
     public void GpsValidator_GPSisNotEnabled_ReturnTrue() {

    assertFalse(gps.equals(isGPSNotEnabled) && gps.equals(isNetworkNotEnabled));
}


}
