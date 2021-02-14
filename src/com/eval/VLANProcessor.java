package com.eval;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Class that process the VLAN data file
 */
public class VLANProcessor {
    static final String COMMA_DELIMITER = ",";
    PriorityQueue<VLAN> sourceQueue; // Used to store the vlan data with primary and secondary ports
    PriorityQueue<VLAN> onlyPrimaryQueue; // used to store the vlan data with only primary ports

    public VLANProcessor(String pathToVlan) throws FileNotFoundException {
        sourceQueue = getVlanPriorityQueue();
        onlyPrimaryQueue = getVlanPriorityQueue();
        loadVlans(pathToVlan);
    }

    private PriorityQueue<VLAN> getVlanPriorityQueue() {
        return new PriorityQueue<VLAN>((a, b) -> {
            if (a.vlanId == b.vlanId) return Integer.compare(a.deviceId, b.deviceId);
            return (Integer.compare(a.vlanId, b.vlanId));
        });
    }

    public PriorityQueue<VLAN> getSourceQueue(){
        return sourceQueue;
    }

    public PriorityQueue<VLAN> getOnlyPrimaryQueue(){
        return onlyPrimaryQueue;
    }

    /**
     * Process the vlan file save it the map and the process to priority queue
     * @param pathToVlan
     * @throws FileNotFoundException
     */
    private void loadVlans(String pathToVlan) throws FileNotFoundException {
        Map<String, VLAN> allVlans = new HashMap<>();
        try (Scanner scanner = new Scanner(new File(pathToVlan))) {
            //Skip csv Header
            if(scanner.hasNextLine()) scanner.nextLine();
            while (scanner.hasNextLine()) {
                VLAN curr = getVLANFromLine(scanner.nextLine());
                processVLAN(allVlans, curr);
            }
        }
        loadValuesToQueue(allVlans, sourceQueue, onlyPrimaryQueue);
    }

    /**
     * Pre-process the vlan data file record by record and store in a intermediary map, where the key is deviceId and VlanId
     *
     * @param allVlans
     * @param curr
     */
    private void processVLAN(Map<String, VLAN> allVlans, VLAN curr) {
        if(curr != null) {
            if(allVlans.containsKey(curr.toString())){
             if(curr.isPrimaryPort){
                 allVlans.get(curr.toString()).setPrimaryPort();
             }else if(curr.isSecondaryPort){
                    allVlans.get(curr.toString()).setSecondaryPort();
                }
            }else {
                allVlans.put(curr.toString(), curr);
            }
        }
    }

    /**
     * Convert the line from file to VLAN object
     * @param line
     * @return
     */
    private VLAN getVLANFromLine(String line){
        VLAN vlan = null;
        try (Scanner rowScanner = new Scanner(line)) {
            rowScanner.useDelimiter(COMMA_DELIMITER);
            if(line.isBlank()) return vlan;
            vlan = new VLAN();
            int idx = 0;
            while (rowScanner.hasNext()) {
                int curCol = Integer.parseInt(rowScanner.next());
                switch (idx++){
                    // device Id
                    case 0:
                        vlan.setDeviceId(curCol);
                        break;
                    // isPrimary
                    case 1:
                        // TODO Handle wrong values
                        if(curCol == 1){
                            vlan.setPrimaryPort();
                        }else {
                            vlan.setSecondaryPort();
                        }
                        break;
                    // vlan Id
                    case 2:
                        vlan.setVlanId(curCol);
                }
            }
        }
        return vlan;
    }

    /**
     * Load the pre-process map data to the appropriate queue
     * @param allVlans
     * @param priorityQueue
     * @param onlyPrimaryQueue
     */
    private void loadValuesToQueue(Map<String, VLAN> allVlans, PriorityQueue priorityQueue, PriorityQueue onlyPrimaryQueue){
        for(Map.Entry<String, VLAN> e : allVlans.entrySet()) {
            VLAN vlan = e.getValue();
            if(vlan.isPrimaryPort && vlan.isSecondaryPort){
                priorityQueue.add(vlan);
            }else if(vlan.isPrimaryPort){
                onlyPrimaryQueue.add(vlan);
            }else{
                // assume is a bad data since it has only secondary port
            }
        }
    }
}
