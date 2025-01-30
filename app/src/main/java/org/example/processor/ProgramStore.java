package org.example.processor;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProgramStore {
    private List<String> program;
    private final Decode decode;
    private final ProgramCountUpdater programCountUpdater;
    
    private boolean halted = false;
    
    public ProgramStore(Decode decode, ProgramCountUpdater programCountUpdater, String file){
        this.decode = decode;
        this.programCountUpdater = programCountUpdater;
        this.program = new ArrayList<>();
        
        try(BufferedReader br = new BufferedReader(new FileReader(file))){
            String line = br.readLine();
            
            while (line != null) {
                line = line.trim().toLowerCase();
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
        if(programCountUpdater.getCurrentPC() >= program.size()) {
            halted = true;
            return;
        }
        
        decode.input = program.get(programCountUpdater.getCurrentPC());
    }
}
