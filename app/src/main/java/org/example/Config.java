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

    private Config(int alu, int compare, int agu, int load, int multiply, int fetchDecodeWidth, int commitWidth) {
        this.alu = alu;
        this.compare = compare;
        this.agu = agu;
        this.load = load;
        this.multiply = multiply;
        this.fetchDecodeWidth = fetchDecodeWidth;
        this.commitWidth = commitWidth;
    }

    public static Config getInstance() {
        return Instance;
    }

    /// Create a config instance if one does not already exist
    public static Config init(String fileName) {
        Properties props = new Properties();

        String alu;
        String compare;
        String agu;
        String load;
        String multiply;
        String fetchDecodeWidth;
        String commitWidth;

        try(FileInputStream fis = new FileInputStream(fileName)) {
            props.load(fis);

            alu = props.getProperty("alu");
            compare = props.getProperty("compare");
            agu = props.getProperty("agu");
            load = props.getProperty("load");
            multiply = props.getProperty("multiply");
            fetchDecodeWidth = props.getProperty("fetchDecodeWidth");
            commitWidth = props.getProperty("commitWidth");

        } catch (IOException e) {
            System.out.printf("Failed to load config file \"%s\", using defaults\n", fileName);
            alu = null;
            compare = null;
            agu = null;
            load = null;
            multiply = null;
            fetchDecodeWidth = null;
            commitWidth = null;
        }



        Instance = new Config(
                alu != null ? Integer.parseInt(alu) : 4,
                compare != null ? Integer.parseInt(compare) : 2,
                agu != null ? Integer.parseInt(agu) : 2,
                load != null ? Integer.parseInt(load) : 2,
                multiply != null ? Integer.parseInt(multiply) : 2,
                fetchDecodeWidth != null ? Integer.parseInt(fetchDecodeWidth) : 4,
                commitWidth != null ? Integer.parseInt(commitWidth) : 4
        );

        return Instance;
    }
}
