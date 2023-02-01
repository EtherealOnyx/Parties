package io.sedu.mc.parties.client.overlay.effects;

import net.minecraft.world.effect.MobEffect;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class EffectHolder {

    //TODO: Add config
    boolean seperate = true;
    HashMap<Integer, ClientEffect> effects = new HashMap<>();
    List<Integer> sortedEffectAll = new ArrayList<>();
    List<Integer> sortedEffectBene = new ArrayList<>();
    List<Integer> sortedEffectBad = new ArrayList<>();


    public EffectHolder() {

    }


    public void forEachAll(Consumer<ClientEffect> action) {
        Objects.requireNonNull(action);
        sortedEffectAll.forEach(integer -> action.accept(effects.get(integer)));
    }

    public int sizeAll() {
        return sortedEffectAll.size();
    }

    public int sizeBene() {
        return sortedEffectBene.size();
    }

    public int sizeBad() {
        return sortedEffectBad.size();
    }

    public void forEachBene(Consumer<ClientEffect> action) {
        Objects.requireNonNull(action);
        sortedEffectBene.forEach(integer -> action.accept(effects.get(integer)));
    }

    public void forEachBad(Consumer<ClientEffect> action) {
        Objects.requireNonNull(action);
        sortedEffectBad.forEach(integer -> action.accept(effects.get(integer)));
    }

    public void add(int type, int duration, int amp) {
        if (effects.get(type) == null) {
            effects.put(type, new ClientEffect(type, duration, amp));
            addVal(type);
        }
        else {
            effects.get(type).addEffect(duration, amp);

            sort();
        }

    }

    private void sort() {
        sortBene();
        sortBad();
        if(seperate) {
            sortedEffectAll.clear();
            sortedEffectAll.addAll(sortedEffectBad);
            sortedEffectAll.addAll(sortedEffectBene);
        } else {
            sortAll();
        }

    }

    private void addVal(int type) {
        sortedEffectAll.add(type);
        if (MobEffect.byId(type).isBeneficial()) {
            sortedEffectBene.add(type);
        } else {
            sortedEffectBad.add(type);
        }

        sort();
    }

    private void sortBad() {
        sort(sortedEffectBad);
    }

    private void sortBene() {
        sort(sortedEffectBene);
    }

    private void sortAll() {
        sort(sortedEffectAll);
    }

    public void sort(List<Integer> array) {
        int i, j;
        for (i = 1; i < array.size(); i++) {
            Integer tmp = array.get(i);
            j = i;
            while ((j > 0) && leftMoreThanRight(array.get(j - 1), tmp)) {
                array.set(j, array.get(j - 1));
                j--;
            }
            array.set(j, tmp);
        }
    }

    public boolean leftMoreThanRight(int left, int right) {

        if (effects.get(left).isInstant() && !effects.get(right).isInstant())
            return true;
        if (effects.get(right).isInstant() && !effects.get(left).isInstant())
            return false;
        if (effects.get(right).isInstant() && effects.get(left).isInstant())
                return !effects.get(right).bene() && effects.get(left).bene();
        if (effects.get(left).cur.duration > effects.get(right).cur.duration) {
            return true;
        }
        return effects.get(left).cur.duration == effects.get(right).cur.duration
                && (!effects.get(right).bene() && effects.get(left).bene());
    }


    public boolean removeIf(Predicate<ClientEffect> filter) {
        Objects.requireNonNull(filter);
        boolean removed = false;
        Iterator<ClientEffect> each = effects.values().iterator();
        ClientEffect e;
        while(each.hasNext()) {
            e = each.next();
            if (filter.test(e)) {
                remove(e.bene(), e.getId());
                each.remove();
                removed = true;
            }
        }

        if (removed) {
            sort();
        }
        return removed;
    }

    private void remove(boolean beneficial, Integer id) {
        sortedEffectAll.remove(id);
        if (beneficial)
            sortedEffectBene.remove(id);
        else
            sortedEffectBad.remove(id);
    }


    public void markForRemoval(int type) {
        if (effects.get(type) != null && !effects.get(type).isInstant())
            effects.get(type).markForRemoval();
    }

    public void markForRemoval() {
        effects.values().forEach(ClientEffect::markForRemoval);
    }
}
