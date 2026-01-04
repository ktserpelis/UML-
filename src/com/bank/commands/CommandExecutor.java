package com.bank.commands;

import java.util.ArrayList;
import java.util.List;

public class CommandExecutor{
    private static CommandExecutor instance;
    private List<Command> history = new ArrayList<>();

    private CommandExecutor() {}

    public static CommandExecutor getInstance() {
        if (instance == null) instance = new CommandExecutor();
        return instance;
    }

    public void executeCommand(Command cmd) throws Exception {
        cmd.execute();
        history.add(cmd); //  ιστορικό συναλλαγών
        System.out.println("[COMMAND] Executed: " + cmd.getClass().getSimpleName());
    }

    public List<Command> getHistory() {
        return history;
    }
}