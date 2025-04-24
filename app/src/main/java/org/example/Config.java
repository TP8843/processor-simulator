package org.example;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {
    /// Single instance of the config singleton
    private static Config Instance;

    /// Number of ALUs to have
    public final int alu;

    /// Number of Compare Units to have
    public final int compare;

    /// Number of AGUs to have
    public final int agu;

    /// Number of Load Units to have
    public final int load;

    /// Number of Multiply Units to have
    public final int multiply;

    /// Number of instructions to fetch and decode each cycle
    public final int fetchDecodeWidth;

    /// Number of instructions to commit each cycle
    public final int commitWidth;

    // Buffer Sizes
    public final int aluRs;
    public final int multiplyRs;
    public final int compareRs;
    public final int aguRs;
    public final int aguLoadBuffer;
    public final int fetchDecodeBuffer;
    public final int decodeIssueBuffer;
    public final int issueBranchBuffer;
    public final int rob;


    private Config(int alu,
                   int compare,
                   int agu,
                   int load,
                   int multiply,
                   int fetchDecodeWidth,
                   int commitWidth,
                   int aluRs,
                   int multiplyRs,
                   int compareRs,
                   int aguRs,
                   int aguLoadBuffer,
                   int fetchDecodeBuffer,
                   int decodeIssueBuffer,
                   int issueBranchBuffer,
                   int rob) {
        this.alu = alu;
        this.compare = compare;
        this.agu = agu;
        this.load = load;
        this.multiply = multiply;
        this.fetchDecodeWidth = fetchDecodeWidth;
        this.commitWidth = commitWidth;
        this.aluRs = aluRs;
        this.multiplyRs = multiplyRs;
        this.compareRs = compareRs;
        this.aguRs = aguRs;
        this.aguLoadBuffer = aguLoadBuffer;
        this.fetchDecodeBuffer = fetchDecodeBuffer;
        this.decodeIssueBuffer = decodeIssueBuffer;
        this.issueBranchBuffer = issueBranchBuffer;
        this.rob = rob;
    }

    public static Config getInstance() {
        return Instance;
    }

    /// Create a config instance
    public static Config init(String fileName) {
        Properties props = new Properties();

        String alu;
        String compare;
        String agu;
        String load;
        String multiply;
        String fetchDecodeWidth;
        String commitWidth;
        String aluRs;
        String multiplyRs;
        String compareRs;
        String aguRs;
        String aguLoadBuffer;
        String fetchDecodeBuffer;
        String decodeIssueBuffer;
        String issueBranchBuffer;
        String rob;

        try(FileInputStream fis = new FileInputStream(fileName)) {
            props.load(fis);

            alu = props.getProperty("alu");
            compare = props.getProperty("compare");
            agu = props.getProperty("agu");
            load = props.getProperty("load");
            multiply = props.getProperty("multiply");
            fetchDecodeWidth = props.getProperty("fetchDecodeWidth");
            commitWidth = props.getProperty("commitWidth");
            aluRs = props.getProperty("aluRs");
            multiplyRs = props.getProperty("multiplyRs");
            compareRs = props.getProperty("compareRs");
            aguRs = props.getProperty("aguRs");
            aguLoadBuffer = props.getProperty("aguLoadBuffer");
            fetchDecodeBuffer = props.getProperty("fetchDecodeBuffer");
            decodeIssueBuffer = props.getProperty("decodeIssueBuffer");
            issueBranchBuffer = props.getProperty("issueBranchBuffer");
            rob = props.getProperty("rob");

        } catch (IOException e) {
            System.out.printf("Failed to load config file \"%s\", using defaults\n", fileName);
            alu = null;
            compare = null;
            agu = null;
            load = null;
            multiply = null;
            fetchDecodeWidth = null;
            commitWidth = null;
            aluRs = null;
            multiplyRs = null;
            compareRs = null;
            aguRs = null;
            aguLoadBuffer = null;
            fetchDecodeBuffer = null;
            decodeIssueBuffer = null;
            issueBranchBuffer = null;
            rob = null;
        }



        Instance = new Config(
                alu != null ? Integer.parseInt(alu) : 4,
                compare != null ? Integer.parseInt(compare) : 2,
                agu != null ? Integer.parseInt(agu) : 2,
                load != null ? Integer.parseInt(load) : 2,
                multiply != null ? Integer.parseInt(multiply) : 2,
                fetchDecodeWidth != null ? Integer.parseInt(fetchDecodeWidth) : 4,
                commitWidth != null ? Integer.parseInt(commitWidth) : 4,
                aluRs != null ? Integer.parseInt(aluRs) : 32,
                multiplyRs != null ? Integer.parseInt(multiplyRs) : 32,
                compareRs != null ? Integer.parseInt(compareRs) : 32,
                aguRs != null ? Integer.parseInt(aguRs) : 32,
                aguLoadBuffer != null ? Integer.parseInt(aguLoadBuffer) : 32,
                fetchDecodeBuffer != null ? Integer.parseInt(fetchDecodeBuffer) : 16,
                decodeIssueBuffer != null ? Integer.parseInt(decodeIssueBuffer) : 16,
                issueBranchBuffer != null ? Integer.parseInt(issueBranchBuffer) : 2,
                rob != null ? Integer.parseInt(rob) : 128
        );

        return Instance;
    }
}
