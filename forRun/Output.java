/**
 * Output class
 */
public class Output {
    int requestId;
    int deviceId;
    int primaryPort;
    int vlanId;

    public Output(int requestId){
        this.requestId = requestId;
    }

    public Output(int requestId, int deviceId, int primaryPort, int vlanId){
        this.requestId = requestId;
        this.deviceId = deviceId;
        this.primaryPort = primaryPort;
        this.vlanId = vlanId;
    }

    public void setDeviceId(int deviceId){
        this.deviceId = deviceId;
    }
    public void setPrimaryPort(int primaryPort){
        this.primaryPort = primaryPort;
    }
    public void setVlanId(int vlanId){
        this.vlanId = vlanId;
    }

    @Override
    public String toString() {
        return  requestId +
                "," + deviceId +
                "," + primaryPort +
                "," + vlanId;
    }
}
