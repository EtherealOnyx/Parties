package eomods.combatoverhaul.eoparties.data.server;

import eomods.combatoverhaul.eoparties.network.ClientPacketEntity;
import eomods.combatoverhaul.eoparties.network.Handler;
import net.minecraftforge.fml.network.NetworkDirection;

import java.util.*;

public class QueuePacket {
//Potion effects will be sent right away, so those don't go here.
    private UUID entityToUpdate;
    private int typeToUpdate;
    private float valueToUpdate;

    private static LinkedList<QueuePacket> queueList = new LinkedList<>();
    private static HashMap<QueuePacket, QueuePacket> queueSet = new HashMap<>();

    QueuePacket(UUID id, int type, float value) {
        this.entityToUpdate = id;
        this.typeToUpdate = type;
        this.valueToUpdate = value;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof QueuePacket))
            return false;
        return (((QueuePacket) o).entityToUpdate.equals(this.entityToUpdate)
                && ((QueuePacket) o).typeToUpdate == this.typeToUpdate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityToUpdate, typeToUpdate);
    }

    private static HashSet<UUID> getListeners(QueuePacket q) {
        return ServerData.trackers.get(q.entityToUpdate);
    }

    private ClientPacketEntity getPacket() {
        return new ClientPacketEntity(entityToUpdate, typeToUpdate, valueToUpdate);
    }

    static void enqueue(QueuePacket q) {
        if (!queueSet.containsKey(q)) {
            queueSet.put(q, q);
            queueList.add(q);
        } else {
            combineQueue(q);
        }
    }

    private static void combineQueue(QueuePacket q) {
        if ((queueSet.get(q).valueToUpdate += q.valueToUpdate) == 0) {
            queueList.remove(q);
            queueSet.remove(q);
        }
    }

    static void dequeue() {
        QueuePacket q = queueSet.get(queueList.removeLast());
        queueSet.remove(q);
        ClientPacketEntity packet = q.getPacket();
        for (UUID player : getListeners(q)) {
            Handler.network.sendTo(packet,
                    Util.getNet(player), NetworkDirection.PLAY_TO_CLIENT);
        }
    }
}

