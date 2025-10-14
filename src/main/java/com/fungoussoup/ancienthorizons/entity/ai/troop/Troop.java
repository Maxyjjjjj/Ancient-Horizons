package com.fungoussoup.ancienthorizons.entity.ai.troop;

import net.minecraft.world.entity.LivingEntity;

import java.util.*;
import java.util.stream.Collectors;

public class Troop {

    private final UUID id;
    private final Set<TroopMember> members = new HashSet<>();
    private final Map<UUID, TroopRelation> relations = new HashMap<>();

    private int territoryCenterX;
    private int territoryCenterZ;
    private int territoryRadius = 64;

    public Troop(UUID id, TroopMember founder) {
        this.id = id;
        addMember(founder);
        this.territoryCenterX = (int) founder.getEntity().getX();
        this.territoryCenterZ = (int) founder.getEntity().getZ();
    }

    public UUID getId() {
        return id;
    }

    public void addMember(TroopMember member) {
        members.add(member);
        member.setTroopId(id);
    }

    public void removeMember(TroopMember member) {
        members.remove(member);
        member.setTroopId(null);
    }

    public List<TroopMember> getMembers() {
        return new ArrayList<>(members);
    }

    public void tick() {
        handleDiplomacy();
        handleAlphaPromotion();
    }

    private void handleDiplomacy() {
        for (TroopMember member : members) {
            for (LivingEntity nearby : member.getEntity().level().getEntitiesOfClass(LivingEntity.class, member.getEntity().getBoundingBox().inflate(32))) {
                if (nearby instanceof TroopMember otherMember && !otherMember.getTroopId().equals(id)) {
                    relations.putIfAbsent(otherMember.getTroopId(), TroopRelation.NEUTRAL);
                    double dist = member.getEntity().distanceTo(otherMember.getEntity());
                    if (dist < territoryRadius) relations.put(otherMember.getTroopId(), TroopRelation.ENEMY);
                }
            }
        }
    }

    private void handleAlphaPromotion() {
        boolean hasAlpha = members.stream().anyMatch(m -> m.getTroopRank() == TroopRank.ALPHA);
        if (!hasAlpha && !members.isEmpty()) {
            // Promote random adult to alpha
            List<TroopMember> adults = members.stream()
                    .filter(m -> m.getTroopRank() == TroopRank.ADULT)
                    .toList();
            if (!adults.isEmpty()) {
                TroopMember chosen = adults.get(new Random().nextInt(adults.size()));
                chosen.setTroopRank(TroopRank.ALPHA);
            }
        }
    }

    public TroopRelation getRelation(UUID otherTroopId) {
        return relations.getOrDefault(otherTroopId, TroopRelation.NEUTRAL);
    }

    public int getTerritoryCenterX() {
        return territoryCenterX;
    }

    public int getTerritoryCenterZ() {
        return territoryCenterZ;
    }

    public int getTerritoryRadius() {
        return territoryRadius;
    }

    public void setTerritoryCenter(int x, int z) {
        this.territoryCenterX = x;
        this.territoryCenterZ = z;
    }

    public boolean isInsideTerritory(int x, int z) {
        int dx = x - territoryCenterX;
        int dz = z - territoryCenterZ;
        return dx * dx + dz * dz <= territoryRadius * territoryRadius;
    }

    public int getTerritoryX() {
        return territoryCenterX;
    }

    public int getTerritoryZ() {
        return territoryCenterZ;
    }

}


