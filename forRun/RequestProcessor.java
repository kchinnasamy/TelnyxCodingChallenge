
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Class which process the request file line by line for the given Vlan data
 */
public class RequestProcessor {
    static final String COMMA_DELIMITER = ",";

    /**
     * Pre-process the vlan data and process the request line by line
     * @param pathToVlan
     * @param pathToRequest
     * @return
     * @throws FileNotFoundException
     */
    public List<Output> processRequest(String pathToVlan, String pathToRequest) throws FileNotFoundException {
        VLANProcessor VLANProcessor = new VLANProcessor(pathToVlan);

        PriorityQueue sourceData = VLANProcessor.getSourceQueue(); // This contains vlan's that has both primary and secondary
        PriorityQueue onlyPrimaryQueue = VLANProcessor.getOnlyPrimaryQueue(); // This contains vlans that has only secondary

        List<Output> outputs = new ArrayList<>(); // Has the result for the data given
        try (Scanner scanner = new Scanner(new File(pathToRequest))) {
            //Skip csv Header
            if (scanner.hasNextLine()) scanner.nextLine();
            while (scanner.hasNextLine()) {
                Request curRequest = getRequestFromLine(scanner.nextLine());
                processRequest(sourceData, onlyPrimaryQueue, curRequest, outputs);
            }
        }
        Comparator<Output> cmp = Comparator
                .comparing((Output output)-> output.requestId)
                .thenComparing((Output output)-> output.primaryPort);
        Collections.sort(outputs, cmp);
        return outputs;
    }

    /**
     * For debugging purpose
     * @param outputs
     */
    private void printOutput(List<Output> outputs) {
        for(Output output : outputs){
            System.out.println(output.toString());
        }
    }

    /**
     * Writing the output to a file.
     * @param outputs
     * @param outputFilePath
     * @throws IOException
     */
    public void writeOutputToFile(List<Output> outputs, String outputFilePath) throws IOException{
        writeOutputToFile(outputs, new File(outputFilePath));
    }
    public void writeOutputToFile(List<Output> outputs, File outputFile) throws IOException {
        // Write headers
        FileWriter csvWriter = new FileWriter(outputFile);
        csvWriter.append("request_id");
        csvWriter.append(",");
        csvWriter.append("device_id");
        csvWriter.append(",");
        csvWriter.append("primary_port");
        csvWriter.append(",");
        csvWriter.append("vlan_id");
        csvWriter.append("\n");

        for(Output output : outputs){
            csvWriter.append(String.valueOf(output.requestId));
            csvWriter.append(",");
            csvWriter.append(String.valueOf(output.deviceId));
            csvWriter.append(",");
            csvWriter.append(String.valueOf(output.primaryPort));
            csvWriter.append(",");
            csvWriter.append(String.valueOf(output.vlanId));
            csvWriter.append("\n");
        }
        csvWriter.flush();
        csvWriter.close();
    }

    /**
     * For the given request check
     * if its redundant request if so check
     *          if there are any data in the sourcequeue that matches the constrain and add it to the output
     *          else throw an exception
     * else if its a non redundant data if so check
     *          if there are any data in the onlyPrimaryQueue that matches the given constrain and add it to output
     *          else throw an exception
     * @param sourceQueue
     * @param onlyPrimaryQueue
     * @param curr
     * @param outputs
     * @return
     */
    private boolean processRequest(PriorityQueue<VLAN> sourceQueue, PriorityQueue<VLAN> onlyPrimaryQueue, Request curr,
                                          List<Output> outputs) {

        if (!curr.isRedundant) {
            //first check onlyPrimary queue
            VLAN vlan = null;
            if (!onlyPrimaryQueue.isEmpty()) {
                vlan = onlyPrimaryQueue.poll();
            } else {
                while (!sourceQueue.isEmpty() && !sourceQueue.peek().isPrimaryPort) {
                    sourceQueue.poll();
                }
                // This means there is no vlan with primary port
                if (sourceQueue.isEmpty()) {
                    throw new IllegalStateException("Unable to find a valid com.eval.VLAN with " +
                            "primary port for non-redundant data");
                }
                vlan = sourceQueue.poll();
            }
            processOutput(curr, outputs, vlan, true);

        } // if data in onlyPrimaryQueue the check the sourceQueue
        else {
            while (!sourceQueue.isEmpty() && (!sourceQueue.peek().isSecondaryPort || !sourceQueue.peek().isPrimaryPort)) {
                VLAN vlan = sourceQueue.poll();
                if (vlan.isPrimaryPort) {
                    onlyPrimaryQueue.add(vlan);
                }
            }
            if (sourceQueue.isEmpty()) {
                throw new IllegalStateException("Unable to find a valid com.eval.VLAN with " +
                        "primary & secondary port for redundant data");
            }
            VLAN vlan = sourceQueue.poll();
            processOutput(curr, outputs, vlan, true);
            processOutput(curr, outputs, vlan, false);
        }
        return true;
    }


    /**
     * Convert the line of data from the file to Request object
     * @param line
     * @return
     */
    public Request getRequestFromLine(String line){
        Request request = null;
        try (Scanner rowScanner = new Scanner(line)) {
            rowScanner.useDelimiter(COMMA_DELIMITER);
            if(line.isBlank()) return request;
            request = new Request();
            int idx = 0;
            while (rowScanner.hasNext()) {
                int curCol = Integer.parseInt(rowScanner.next());
                switch (idx++){
                    // request Id
                    case 0:
                        request.setRequestId(curCol);
                        break;
                    case 1:
                        // TODO Handle wrong values
                        request.setIsRedundant(curCol);
                        break;
                }
            }
        }
        return request;
    }

    private void processOutput(Request curr, List<Output> outputs, VLAN vlan, boolean isPrimaryPort) {
        int requestId = curr.requestId;
        int deviceId = vlan.deviceId;
        int primaryPort = isPrimaryPort ? 1 : 0;
        int vlanId = vlan.vlanId;
        Output output = new Output(requestId, deviceId, primaryPort, vlanId);
        outputs.add(output);
    }
}
