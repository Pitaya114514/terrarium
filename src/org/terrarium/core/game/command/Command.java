package org.terrarium.core.game.command;

import java.util.Set;

public abstract class Command {
    private final Set<Command> commandSet;
    public final String identifier;

    public Command(String identifier, Command... subCommands) {
        this.commandSet = Set.of(subCommands);
        this.identifier = identifier;
    }

    public abstract int execute(String[] args);

    @Override
    public String toString() {
        return identifier;
    }

    public Set<Command> getCommandSet() {
        return Set.copyOf(commandSet);
    }

    public static int execute(String context, Set<Command> commandSet) {
        for (Command command : commandSet) {
            String[] contexts = context.split(" ");
            if (contexts[0].equals(command.identifier)) {
                String[] args = new String[contexts.length - 1];
                System.arraycopy(contexts, 1, args, 0, contexts.length - 1);
                return command.execute(args);
            }
        }
        return -1;
    }
}
