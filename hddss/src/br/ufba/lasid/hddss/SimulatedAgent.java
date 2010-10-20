package br.ufba.lasid.hddss;

/**
 * SimulatedAgent encapsules the specific behavior of the Agent that is done by
 * simulation
 * @author aliriosa
 */
public class SimulatedAgent extends Agent{

    /**
     * createMessage builds a new message that will be sent by the simulator
     * infra-structure
     * @param realClock - physical (simulated) clock
     * @param sender - id of the agent that sends the message
     * @param destination - id of the agent that will receive the message
     * @param type - a number that labels the class of the message
     * @param content - content of the message
     * @param logicalClock - logical clock, if used
     */
    public final void createMessage(int realClock, int sender, int destination, int type, Object content, int logicalClock) {
        Message msg = new Message(sender, destination, type, logicalClock, realClock, content);
        if (infra.faultModel == null) {
          infra.nic_out.add(realClock, msg);
        }
        else infra.faultModel.sendMessage(realClock,msg);
    }

    /**
     * createMessage builds a new message that will be sent by the simulator
     * infra-structure
     * @param realClock - physical (simulated) clock
     * @param sender - id of the agent that sends the message
     * @param destination - id of the agent that will receive the message
     * @param type - a number that labels the class of the message
     * @param content - content of the message
     * @param logicalClock - logical clock, if used
     * @param payload - indicates if the message is from the application (true)
     * or is from the protocol (false)
     */
    public final void createMessage(int realClock, int sender, int destination,
            int type, Object content, int logicalClock, boolean payload) {
        Message msg = new Message(sender, destination, type, logicalClock, realClock, content);
        msg.payload = payload;
        if (infra.faultModel == null) {
            infra.nic_out.add(realClock, msg);
        }
        else infra.faultModel.sendMessage(realClock,msg);
    }


    /**
     * relayMessage forwards a message that will be sent by the simulator
     * infra-structure from another agent to a final destination agent,
     * using this agent
     * @param realClock - physical (simulated) clock
     * @param msg - the message itself
     * @param to - id of the final destination agent
     */
    public final void relayMessage(int realClock, Message msg, int to) {
        msg.relayFrom = id;
        msg.relayTo = to;
        if (infra.faultModel == null) {
            infra.nic_out.add(realClock, msg);
        }
        else infra.faultModel.sendMessage(realClock,msg);
    }

}
