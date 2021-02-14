package javatest;

import org.junit.Before;
import org.junit.Test;
import com.eval.*;
import org.junit.Assert;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class RequestProcessorTest {

    private RequestProcessor requestProcessor;

    @Before
    public void setUp() {
        requestProcessor = new RequestProcessor();
    }

    @Test
    public void testSampleRequest() throws IOException {
        String pathToVlan = "res/test_vlans.csv";
        String pathToRequest = "res/test_requests.csv";
        List<Output> result = requestProcessor.processRequest(pathToVlan, pathToRequest);
        File tmpFile = File.createTempFile("TelnyxCodingChallenge.csv", null);
        requestProcessor.writeOutputToFile(result, tmpFile);
        byte[] file1Bytes = Files.readAllBytes(Paths.get(tmpFile.getPath()));
        byte[] file2Bytes = Files.readAllBytes(Paths.get("res/test_output.csv"));

        String actual = new String(file1Bytes, StandardCharsets.UTF_8);
        String expected = new String(file2Bytes, StandardCharsets.UTF_8);

        Assert.assertEquals(actual, expected);

        tmpFile.delete();
    }

}
