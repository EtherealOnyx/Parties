package io.sedu.mc.parties.client.overlay.effects;

import io.sedu.mc.parties.client.overlay.ClientPlayerData;
import io.sedu.mc.parties.client.overlay.PEffectsBoth;
import io.sedu.mc.parties.client.overlay.RenderItem;
import net.minecraft.world.effect.MobEffect;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static io.sedu.mc.parties.client.overlay.PEffectsBoth.bLim;

public class EffectHolder {

    HashMap<Integer, ClientEffect> effects = new HashMap<>();
    public List<Integer> sortedEffectAll = new ArrayList<>();
    public List<Integer> sortedEffectBene = new ArrayList<>();
    public List<Integer> sortedEffectBad = new ArrayList<>();

    public static void setValues(int buff, int debuff, boolean dFirst) {
        bLim = buff;
        PEffectsBoth.dLim = debuff;
        PEffectsBoth.debuffFirst = dFirst;
    }

    public EffectHolder() {
    }

    public boolean getEffect(int maxSize, List<Integer> sortedEffects, int buffIndex, Consumer<ClientEffect> action) {
        if (buffIndex < sortedEffects.size()) {
            if (buffIndex == maxSize - 1) {
                if (sortedEffects.size() > maxSize)
                    return true;
                else
                    action.accept(effects.get(sortedEffects.get(buffIndex)));
            } else {
                action.accept(effects.get(sortedEffects.get(buffIndex)));
            }
        }
        return false;
    }

    public static RenderItem.SmallBound updatebLim(int data) {
        bLim = data;
        PEffectsBoth.dLim = PEffectsBoth.maxAll - 1 - data;
        ClientPlayerData.markEffectsDirty();
        return null;
    }

    public static RenderItem.SmallBound updatedLim(int data) {
        PEffectsBoth.dLim = data;
        bLim = PEffectsBoth.maxAll - 1 - data;
        ClientPlayerData.markEffectsDirty();
        return null;
    }


    public void forEachAll(Consumer<ClientEffect> action) {
        sortedEffectAll.forEach(integer -> action.accept(effects.get(integer)));
    }

    public void forEachAllLim(int max, Consumer<ClientEffect> action) {
        for (int i = 0; i < max-1; i++)
            action.accept(effects.get(sortedEffectAll.get(i)));
    }

    public void forEachBeneLim(int max, Consumer<ClientEffect> action) {
        for (int i = 0; i < max-1; i++)
            action.accept(effects.get(sortedEffectBene.get(i)));
    }

    public void forEachBadLim(int max, Consumer<ClientEffect> action) {
        for (int i = 0; i < max-1; i++)
            action.accept(effects.get(sortedEffectBad.get(i)));
    }

    public void forAllRemainder(int max, Consumer<ClientEffect> action) {
        for (int i = max-1; i < sortedEffectAll.size(); i++)
            action.accept(effects.get(sortedEffectAll.get(i)));
    }

    public void forBeneRemainder(int max, Consumer<ClientEffect> action) {
        for (int i = max-1; i < sortedEffectBene.size(); i++)
            action.accept(effects.get(sortedEffectBene.get(i)));
    }

    public void forBadRemainder(int max, Consumer<ClientEffect> action) {
        for (int i = max-1; i < sortedEffectBad.size(); i++)
            action.accept(effects.get(sortedEffectBad.get(i)));
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

    public void refresh() {
        if (PEffectsBoth.prioDur)
            sortAll();
        else
            sortRefresh();
    }

    private void sortRefresh() {
        sortedEffectAll.clear();
        if (sortedEffectBad.size() + sortedEffectBene.size() > PEffectsBoth.maxAll) {
            int mx;
            if (PEffectsBoth.debuffFirst) {
                mx = Math.max(PEffectsBoth.dLim, (PEffectsBoth.maxAll - sortedEffectBene.size())-1);
                for(int i = 0; i < mx && i < sortedEffectBad.size(); i++) {
                    sortedEffectAll.add(sortedEffectBad.get(i));
                }
                for(int i = 0; i < bLim && i < sortedEffectBene.size(); i++) {
                    sortedEffectAll.add(sortedEffectBene.get(i));
                }
                for(int i = mx; i < sortedEffectBad.size(); i++) {
                    sortedEffectAll.add(sortedEffectBad.get(i));
                }
                for(int i = bLim; i < sortedEffectBene.size(); i++) {
                    sortedEffectAll.add(sortedEffectBene.get(i));
                }
            } else {
                mx = Math.max(bLim, (PEffectsBoth.maxAll - sortedEffectBad.size())-1);
                for(int i = 0; i < mx && i < sortedEffectBene.size(); i++) {
                    sortedEffectAll.add(sortedEffectBene.get(i));
                }
                for(int i = 0; i < PEffectsBoth.dLim && i < sortedEffectBad.size(); i++) {
                    sortedEffectAll.add(sortedEffectBad.get(i));
                }
                for(int i = mx; i < sortedEffectBene.size(); i++) {
                    sortedEffectAll.add(sortedEffectBene.get(i));
                }
                for(int i = PEffectsBoth.dLim; i < sortedEffectBad.size(); i++) {
                    sortedEffectAll.add(sortedEffectBad.get(i));
                }
            }
        } else {
            if (PEffectsBoth.debuffFirst) {
                sortedEffectAll.addAll(sortedEffectBad);
                sortedEffectAll.addAll(sortedEffectBene);
            } else {
                sortedEffectAll.addAll(sortedEffectBene);
                sortedEffectAll.addAll(sortedEffectBad);
            }
        }
    }

    private void sort() {
        sortBene();
        sortBad();
        refresh();
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
            return false;
        if (effects.get(right).isInstant() && !effects.get(left).isInstant())
            return true;
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

    public boolean largerAll(int max) {
        return sortedEffectAll.size() > max;
    }

    public boolean largerBene(int max) {
        return sortedEffectAll.size() > max;
    }

    public boolean largerBad(int max) {
        return sortedEffectAll.size() > max;
    }


}
