package by.antonkablash.stemulator.model;

/**
 * Created by Anton_Kablash on 6/26/2018.
 */
public class SensorsConfig {
    public static SensorsConfig BRUSH = new SensorsConfig("393755a1-d748-4292-9bbb-077bcf362808", "8b7ca5c2528998f73c93979937137");//f
    public static SensorsConfig SALT_SPREADER = new SensorsConfig("33910279-53a6-492f-a85f-f230e496823c", "fbc7d52621b4237b18fae11accf8");//0
    public static SensorsConfig SNOWPLOW = new SensorsConfig("d0785055-0787-42e7-bb0b-14f383a5f0ea", "7d4ea6344b922e4aa344e0573b511ec");//0
    public static SensorsConfig GPS = new SensorsConfig("1a00b02f-116c-46db-abd9-09c0ec36a36e", "6efd23e5836ac842bd3b78eac96ae27");//f

    private String sensorId;
    private String oAuthToken;

    SensorsConfig(String sensorId, String oAuthToken){
        this.sensorId = sensorId;
        this.oAuthToken = oAuthToken;
    }

    public String getoAuthToken() {
        return oAuthToken;
    }

    public String getSensorId() {
        return sensorId;
    }

    public void setoAuthToken(String oAuthToken) {
        this.oAuthToken = oAuthToken;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    @Override
    public String toString() {
        return "SensorsConfig{" +
                "oAuthToken='" + oAuthToken + '\'' +
                ", sensorId='" + sensorId + '\'' +
                '}';
    }
}
