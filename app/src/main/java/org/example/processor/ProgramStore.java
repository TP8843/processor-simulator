package org.example.processor;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProgramStore {
    private int currentPC;
    
    /// Stores all the labels and their associated locations
    private Map<String, Integer> labelMap;
    
    private List<String> program;
    private final Decode decode;
    
    private String latestInstruction;
    
    private boolean halted = false;
    
    public ProgramStore(Decode decode, String file){
        this.currentPC = 0;
        
        this.decode = decode;
        this.program = new ArrayList<>();
        
        
        
        try(BufferedReader br = new BufferedReader(new FileReader(file))){
            String line = br.readLine();
            
            while (line != null) {
                line = line.trim().toLowerCase();

                // If a blank line or if line just a comment, skip
                if (line.isBlank() || line.startsWith("#")) continue;
                
                program.add(line);
                
                line = br.readLine();
            }
            
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // TODO: Handle searching and replacing labels in assembly
    }
    
    public boolean getHalted() {
        return halted;
    }
    
    public void getInstruction() {
        if(currentPC >= program.size()) {
            halted = true;
            return;
        }
        
        latestInstruction = program.get(currentPC);
        decode.input = latestInstruction;
        decode.inputPC = currentPC;
        
        currentPC += 1;
    }
    
    public int getCurrentPC() {
        return currentPC;
    }
    
    public void updatePC(int value){
        currentPC = value;
        
        // TODO: For pipelining, remember to flush the pipeline when this is run
    }
    
    /// Perform simple assembly steps to process labels, comments, and whitespace
    private List<String> assemble(String file) {
        List<String> output = new ArrayList<>();

        try(BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            
            boolean labelToProcess = false;
            String currentLabel = "";
            
            int lineCount = 0;

            while ((line = br.readLine()) != null) {
                line = line.trim().toLowerCase();
                int labelPosition = line.lastIndexOf(':');

                // Do not process blank or commented line
                if (line.isBlank() || line.startsWith("#")) continue;
                
                // Check for a label and process
                if (labelPosition != -1) {
                    // Get label and any extra text
                    String[] labelArgs = line.split(":");
                    
                    currentLabel = labelArgs[0];
                    labelToProcess = true;
                    
                    // Remove label and process as normal line
                    line = labelArgs[1].trim();
                }

                // Do not process blank or commented line
                if (line.isBlank() || line.startsWith("#")) continue;
                
                // If instruction, set label to line and add line to program
                output.add(line);
                
                if (labelToProcess) {
                    labelMap.put(currentLabel, lineCount);
                    labelToProcess = false;
                }
                
                lineCount += 1;
            }
            
            
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        return output;
    }
    
    @Override
    public String toString() {
        return String.format("""
                Program Store:
                    Latest Instruction: %s
                """, latestInstruction);
    }
}
