import java.io.IOException;
import java.util.List;

public class Main {
    /**
     *
     * @param args pathToVlan , pathToRequest and outputPath
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        /**
         *
         * VLAN Processing:
         * -----------------
         * The main idea behind solving this problem is that using a priority queue to store the VLAN data
         * which are sorted by the vlanId and if equal sorted by deviceId.
         *
         * We maintain 2 priority queue
         *
         *  -onlyPrimaryQueue contains the vlanId which has only Primary port
         *  -sourceQueue contains all the vlanId which contains Primary and secondary port
         *
         * This mean when ever we need to assign a vlanId we just check the top of the queue which will give a expected value
         *
         *
         * Request processing:
         * -------------------
         *  - non-redundant request we check the onlyPrimaryQueue for data, if it's empty we then check the sourceQueue
         *    once consumed we delete the entry form the queue. if both are empty then we throw exception
         *  - redundant request we just check the onlyPrimaryQueue for data if found we output that in the required format
         *    else if empty we throw an exception.
         *
         * Assumption:
         * ----------
         *  - that it's ok to take the vlan which has both primary and secondary for non-redundant request.
         *  - all the input data are valid (but implemented little bit of error handling)
         *
         */

        if(args.length != 3){
            System.out.println("Usage <PathToVlan> <PathToRequest> <outputPath>");
            return;
        }
        String pathToVlan = args[0];
        String pathToRequest = args[1];
        String outputPath = args[2];

        RequestProcessor requestProcessor = new RequestProcessor();
        List<Output> result = requestProcessor.processRequest(pathToVlan, pathToRequest);
        requestProcessor.writeOutputToFile(result, outputPath);

    }
}
