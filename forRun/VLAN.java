/**
 * VLAN class
 */
public class VLAN {
    int deviceId;
    boolean isPrimaryPort;
    boolean isSecondaryPort;
    int vlanId;

    public VLAN(int deviceId, int vlanId) {
        this.deviceId = deviceId;
        this.vlanId = vlanId;
    }
    public void setPrimaryPort(){
        isPrimaryPort = true;
    }
    public void setSecondaryPort(){
        isSecondaryPort = true;
    }
    public VLAN(){

    }
    public void setDeviceId(int deviceId){
        this.deviceId = deviceId;
    }

    public void setVlanId(int vlanId){
        this.vlanId = vlanId;
    }

    @Override
    public String toString() {
        return "com.eval.VLAN{" +
                "deviceId=" + deviceId +
                ", vlanId=" + vlanId +
                '}';
    }
}

